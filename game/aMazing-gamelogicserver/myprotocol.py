import string
import threading
from mazecorner import MazeCorner
from gps import * 
from item import Item
import gameSession
import xmlrpclib
from net.totem.configuration.xmlrpc.xmlrpcconfig import XMLRPCConfiguration
from subprocess import call

gpsListP1 = []
proximityViolationP1 = False
lastCorrectMazeCornerP1 = []
lastCorrectMazeCornerP1.append(None)
sentP1SetupData = False
locationP1Accurate = False
lastPosP1 = [0,0]

gpsListP2 = []
proximityViolationP2 = False
lastCorrectMazeCornerP2 = []
lastCorrectMazeCornerP2.append(None)
sentP2SetupData = False
locationP2Accurate = False
lastPosP2 = [0,0]

latDistance = 0
lngDistance = 0

updateCrownClaimFlag = False

timerThread = None
terminationTimer = None

'''
1. Get the gps coordinates of a player
2. Check each coordinate with the other players points
    a. Add it because it is fine
    b. Do not add a point. Set a flag that all other 
       points will be checked for a return point.
    c. Because a flag is set check if a new point 
       not violating the others players point and 
       is close to the last added point. 
3. Send a message to the other player if everything 
   is fine or send a warning to the player if the 
   points are violating the other points 

'''
# This method is called when users tell the server they are ready.
def playerIsReady(state, header, body):
   
    global sentP1SetupData
    global sentP2SetupData
    if header[0] == 'PLAYER_1':
        sentP1SetupData = True
        #read out setup data from P1 and set up a game instance with it
        setupData = string.split(body, '*' )        
        gameSession.itemAmount = int(setupData[0])
        gameSession.goalAmount = int(setupData[1])
        gameSession.gameDuration = int(setupData[2])
        gameSession.halfAreaSize = int(setupData[3])/2
        
        #TODO maybe in future work make these  advanced settings 
        gameSession.rechargeRate = 6.5
        gameSession.crownClaimRadius = 50.0
        gameSession.itemPickupRadius = 25.0
        gameSession.mazePoolLimit = 50.0
        gameSession.breakerRadius = gameSession.crownClaimRadius*0.5 #TODO change this. 
        gameSession.expandRate = 1.5 
        
        #We start a timer here to shut down the task if players do not really start playing in 5 minutes.
        global terminationTimer 
        terminationTimer =  TimerThread(state)
        terminationTimer.setInterval(600)
        terminationTimer.maxCounter = 0
        terminationTimer.executionCounter = 1
        terminationTimer.start()
        
        
    else:
        sentP2SetupData = True
    
    
    #Once both players are ready send them the game information and let them start the game.
    if sentP1SetupData and sentP2SetupData:
        state.gamelogicchannel.publish("PLAYER_1", state, "amazingActionKind.playerReady", "")
        state.gamelogicchannel.publish("PLAYER_2", state, "amazingActionKind.playerReady", "")


#This is used to wait for the players to get an accurate GPS fix.        
def playerLocationAccurate(state, header, body):
    if header[0] == 'PLAYER_1':
        global locationP1Accurate
        locationP1Accurate = True     
        latlng = string.split(body, '*' ) 
        global lastPosP1 
        lastPosP1 = [float(latlng[0]), float(latlng[1])]
        
    else:
        global locationP2Accurate
        locationP2Accurate = True
        latlng = string.split(body, '*' ) 
        global lastPosP2 
        lastPosP2 = [float(latlng[0]), float(latlng[1])]
        
    #If both players have accurate positions     
    if locationP1Accurate and locationP2Accurate:
        # We check if the distance between the players is far enough. 
        if gpsCoordinatesToDistance(lastPosP1[0], lastPosP1[1], lastPosP2[0], lastPosP2[1]) > 10:
            #Then we decide the center of the area and send the game data. The game can start!
            
            global lastCorrectMazeCornerP1
            global lastCorrectMazeCornerP2
            
            lastCorrectMazeCornerP1[0] =MazeCorner( lastPosP1[0], lastPosP1[1], False, False, True, True, -1)
            lastCorrectMazeCornerP2[0] =MazeCorner( lastPosP2[0], lastPosP2[1], False, False, True, True, -1)
            
            
            gameSession.centerLat = (lastPosP1[0]+ lastPosP2[0])/2.0
            gameSession.centerLng = (lastPosP1[1]+ lastPosP2[1])/2.0
            gameInfo = gameSession.createNewGameData()
            state.gamelogicchannel.publish("PLAYER_1", state, "amazingActionKind.locationIsAccurate", gameInfo )
            state.gamelogicchannel.publish("PLAYER_2", state, "amazingActionKind.locationIsAccurate", gameInfo)
            #Shutdown the old termination thread
            global terminationTimer
            terminationTimer.shutdown()
            
            #Start the real game timer
            global timerThread
            timerThread = TimerThread(state)
            timerThread.start()
            
            
         
def computeGPSCoordinates(state, header, body):     
    mazeCorners = string.split(body, '*' )
    if header[0] == 'PLAYER_1':
        if proximityViolationP1 == False:
            handleGPSCoordinates(1, 2, gpsListP1, gpsListP2, lastCorrectMazeCornerP1, state, body, mazeCorners)
        else:
            handleGPSCoordinatesDuringViolation(1, 2, gpsListP1, gpsListP2, lastCorrectMazeCornerP1, state, body, mazeCorners)
    else:
        if proximityViolationP2 == False:
            handleGPSCoordinates(2, 1, gpsListP2, gpsListP1, lastCorrectMazeCornerP2, state, body, mazeCorners)
        else:
            handleGPSCoordinatesDuringViolation(2, 1, gpsListP2, gpsListP1, lastCorrectMazeCornerP2,state, body, mazeCorners)
    #printLists()
    
def handleGPSCoordinatesDuringViolation(player, otherPlayer, gpsList, othergpsList, lastCorrectPosition, state, body, mazeCorners):
    forwardList = []
    proximityViolationList = []
    proximityViolationCorrected = False
    for mcs in mazeCorners:         # for all corners
        corner = parseMazeCornerFromString(mcs)
        if corner.id != -1: 
            #If this is an old message we need to tell the player to remove the corners and sync the lists.
            corner.hidden = True
            forwardList.append(corner)
            proximityViolationList.append(corner)
        else:
            #This is mazeCorner created to return to the last correct point.
            if gpsCoordinatesToDistance(corner.lat, corner.lng, lastCorrectPosition[0].lat, lastCorrectPosition[0].lng)< 3:
                #We have returned to an old point. We can send a message to the player
                #If none of the following points are wrong.
                proximityViolationCorrected = True
            if checkProximity(player, othergpsList, corner, state ) == 1:
                proximityViolationCorrected = False
    if proximityViolationCorrected == True :
        #Inform the player that he can go on with the normal gameplay.
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.proximityViolationCorrected", lastCorrectPositionToString(player))
        #Also we reset the flag so that the server returns to normal gameplay.                                    
        if player == 1:
            global proximityViolationP1
            proximityViolationP1 = False
        else:
            global proximityViolationP2
            proximityViolationP2 = False

def handleGPSCoordinates(player, otherPlayer, gpsList, othergpsList, lastCorrectPosition, state, body, mazeCorners):
    forwardList = []
    proximityViolationList = []
    proximityOke = True
    for mcs in mazeCorners:         # for all corners
        corner = parseMazeCornerFromString(mcs) # parse the corner
        
        if corner.id == -1:
            
            check = checkProximity(player, othergpsList, corner, state )
            if  check == 1:
                    #state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.proximityViolation", lastCorrectPositionToString(player))
                    proximityOke = False
                    break # A violation happened. Ignore the other points. 
            #elif check ==2:
                   # state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.proximityViolation", "")
                    #Otherwise the point is oke so set it as last correct position.
            if proximityOke==True: 
                setLastCorrectPos(player, corner)
            continue #Go on with the next point. 
        if proximityOke==True:                      # if there was no violation
            if not handleMazeCorner(player, otherPlayer, gpsList, othergpsList, corner, state ):     #handle the corner
                # this one is not oke modify the last added entry of the list and forward
                if len(forwardList) >=1:
                    lastMazeCorner = forwardList.pop()
                    lastMazeCorner.generated = False
                    forwardList.append(lastMazeCorner)
                #We need to tell the other player that there was a violation 
                #and we need to tell which corners to remove from the stored list.
                #for this set proximityOke to false so that we  add the id's to 
                #the message.   
                proximityViolationList.append(corner)
                proximityOke = False
                #They are also forwarded to the other player for list consistency.
                corner.hidden = True
                forwardList.append(corner)
            
            else:
                # this is fine add it to the list and proceed
                forwardList.append(corner)
                #Also set is as last correct position.
                setLastCorrectPos(player, corner)
        else:
            #A proximity Violation has been detected all further points are added
            #to the proximityViolationList and sent back to the player. 
            proximityViolationList.append(corner)
            #They are also forwarded to the other player and stored in the list for list consistency.
            corner.hidden = True
            forwardList.append(corner)
            handleMazeCorner(player, otherPlayer, gpsList, othergpsList, corner, state )
            
    #All points have been checked and added to lists where needed.
    
    #Forward the forwardList to the other player.
    if len(forwardList)>=1:
        message = ""
        for mcs in forwardList:
            message = message  + mcs.toString() + "*"
        message = message[0:-1] # remove last "*"
        state.gamelogicchannel.publish("PLAYER_"+ repr(otherPlayer), state, "amazingActionKind.sendGPSCoordinates", message)
    
    #Check if there was a violation and sent back a message to the player.
    if not proximityOke:
      
        message = ""
        for mcs in proximityViolationList:
            message = message  + repr(mcs.id) + "*"
        message = message[0:-1] # remove last "*"
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.proximityViolation", lastCorrectPositionToString(player) + "|" +message)
        
        if player == 1:
            global proximityViolationP1 
            proximityViolationP1 = True
        else:
            global proximityViolationP2 
            proximityViolationP2 = True
        
    if updateCrownClaimFlag:
        #Crown claims were changed. We need to send the information to both players
        
        messageP1 = ""
        messageP2 = ""
        
        for key in gameSession.mapGoals:
            x= gameSession.mapGoals[key]
            
            # player 1  just send messages like stored.
            messageP1 = messageP1 + repr(x.id) +","+ repr(x.owner) + "*"
            
            #player 2 invert the messages
            if x.owner == 0:
                messageP2 = messageP2 + repr(x.id) +","+ repr(x.owner) + "*"
            if x.owner == 1:
                messageP2 = messageP2 + repr(x.id) +",2*"
            if x.owner == 2:
                messageP2 = messageP2 + repr(x.id) +",1*"
        
        messageP1 = messageP1[0:-1]
        messageP2 = messageP2[0:-1]
        state.gamelogicchannel.publish("PLAYER_1", state, "amazingActionKind.updateCrownClaims", messageP1)
        state.gamelogicchannel.publish("PLAYER_2", state, "amazingActionKind.updateCrownClaims", messageP2)
        global updateCrownClaimFlag
        updateCrownClaimFlag = False
         
        """
            1. Collect the last point which was wrong and all the following points in this package. 
            2. Send it to the player.
            
            3. Set a flag and wait until the player returns to the last well known point. 
            
            player part
            set all points which are in violation to the rules to hidden.
            The point before the last deleted is set to generated = false
            The point after the last deleted is set to generated = false and connected = false
        """            

    
def checkProximity(player, othergpsList, corner, state ):
    lat = corner.lat
    lng = corner.lng
    for x in othergpsList: 
        if x.hidden:
            continue
        if gpsCoordinatesToDistance(lat, lng, x.lat, x.lng)< 3:
            print "Player " + repr(player) + " is too close! to a point" 
            return 1 # Signals a violation happened.
        elif gpsCoordinatesToDistance(lat, lng, x.lat, x.lng)< 5:
            print "Player " + repr(player) + " is getting close to a point" 
            return 2
    
    
def setLastCorrectPos(player, corner):
    if player ==1:
        global lastCorrectMazeCornerP1
        lastCorrectMazeCornerP1.append(corner)
        lastCorrectMazeCornerP1 = lastCorrectMazeCornerP1[-3:]
    else:
        global lastCorrectMazeCornerP2
        lastCorrectMazeCornerP2.append(corner)
        lastCorrectMazeCornerP2 = lastCorrectMazeCornerP2[-3:]
    
  

def handleMazeCorner(player, otherPlayer, gpsList, othergpsList, corner, state ):
    if corner.hidden == True: # This is not a mazeCorner, just add it to the list. 
        gpsList.append(corner)
    
        return True

    
    #Handling maze corner like normal     
    lat = corner.lat
    lng = corner.lng
    
    for x in othergpsList: 
        if x.hidden == True: 
            continue # This is not a relevant point, just ignore it. 
        if gpsCoordinatesToDistance(lat, lng, x.lat, x.lng)< 3:
            print "Player " + repr(player) + " is too close! to a point"
            return False
        elif gpsCoordinatesToDistance(lat, lng, x.lat, x.lng)< 5:
            print "Player " + repr(player) + " is getting close to a point"
    
    #Adding was fine, we can now check if this point claimed a crown. 
    global updateCrownClaimFlag
    updateCrownClaimFlag = (claimCrowns(player, lat, lng) or updateCrownClaimFlag)
    
    #Add to the list. 
    gpsList.append(corner)
    return True

def parseMazeCornerFromString(mazeCornerString):
    "Parses a MazeCorner Object from the string"
    mcArgs = string.split(mazeCornerString, '/')
    return MazeCorner(float(mcArgs[1])/float(1e6), float(mcArgs[2])/float(1e6), True if mcArgs[3] == "1" else False, True if mcArgs[4] == "1" else False, True if mcArgs[5] == "1" else False, False, int(mcArgs[0]))

def printLists():
    print "In the lists: "
    print "Points for player 1 \n ---------------------------------------------------"
    for x in gpsListP1:
        print x.toString()
    print "Points for player 2 \n ---------------------------------------------------"
    for x in gpsListP2:
        print x.toString()
    
def lastCorrectPositionToString(player):
    if player ==1:
        pos =  lastCorrectMazeCornerP1[0]
    else:
        pos = lastCorrectMazeCornerP2[0]
    
    return repr(int(float(pos.lat)*float(10**6))) +"*"+  repr(int(float(pos.lng)*float(10**6)))

def itemRequest(state, header, body):   
        # for either of the players
        # check if item is still in list
        # if not         -> send failed reason 0
        # else    check violation first 
        #         if violation   -> send failed reason -1
        #         else     check position
        #                  if too far     -> send failed reason 1
        #                  else check inventory is full
        #                       if full     -> send failed reason 2
        #                       else remove item from item list
        #                            Send update to both players that item is gone
        #                            Include for 1 player that he obtained the item. add Item to players list.
        data = string.split(body, '*' )
        if header[0] == 'PLAYER_1':
            handleItemRequest(1, 2, proximityViolationP1, int(data[0]), (float(data[1])/ float(10** 6)), (float(data[2])/ float(10** 6)), state);
        else:
            handleItemRequest(2, 1, proximityViolationP2, int(data[0]), (float(data[1])/ float(10** 6)), (float(data[2])/ float(10** 6)), state);
            
def handleItemRequest(player, otherPlayer, proximityViolation, itemId, lat, lng, state):
    # check if item is still in list
    if itemId in gameSession.mapItems:
        item = gameSession.mapItems[itemId]
        if proximityViolation == False:
            if gpsCoordinatesToDistance(lat, lng, (float(item.lat)/ float(10** 6)), (float(item.lng)/ float(10** 6))) < gameSession.itemPickupRadius:
                if len(gameSession.playerItems[player-1]) < gameSession.maxItems:
                    state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.itemUpdate", "-2*"+repr(itemId))
                    state.gamelogicchannel.publish("PLAYER_"+repr(otherPlayer), state, "amazingActionKind.itemUpdate", "-1*"+repr(itemId))
                    gameSession.playerItems[player-1][itemId] = gameSession.mapItems[itemId]
                    del gameSession.mapItems[itemId]
                else:
                    state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.itemUpdate", "1") # full inventory
            else:
                state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.itemUpdate", "2") # too far away
        else:
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.itemUpdate", "3")# currently in prox violation
    else:
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.itemUpdate", "4")# item was already picked up

def claimCrowns(player, lat, lng):  
    #called when  we have a correct maze point. Returns True if the claims changed. 
    crownClaimUpdate = False
    #For each maze point we check the distance between the point and the crown
    for key in gameSession.mapGoals:
        x = gameSession.mapGoals[key]
        #Check if this point is closer than the last 
        
        distance = gpsCoordinatesToDistance(lat, lng, float(x.lat)/float(10**6), float(x.lng)/float(10**6))
        if distance < x.closestPoint:
            # If within range try a claim otherwise ignore.
            if distance < x.crownClaimRadius:                
                x.closestPoint = distance
                if x.owner != player:
                    #Need to update
                    crownClaimUpdate = True
                    x.owner = player
                
    return crownClaimUpdate
    

def teleportItem(state, header, body):
    # Check if there is an teleporter in the inventory of the user
    # then check if the desired item is still on the map
    # If not send an error message, (message = 1|fail)
    
    # Otherwise send a message to both players with the new location and update the location of the item.
    # message is of form 1|ID*lat*lng or 0|ID*lat*lng if the message is for the other player. 
    #Body is the item ID
    if header[0] == 'PLAYER_1':
        player = 1
        otherPlayer = 2
    else:
        player = 2
        otherPlayer = 1
    
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 6:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.teleportItem", "1|fail")
        
    else:
        #Check if the item is still on the map. 
        if int(body) not in gameSession.mapItems:
            # send error message
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.teleportItem", "1|fail")
        else:
            # Update item and send an update message to both players.
            del gameSession.playerItems[player-1][usedItemID]
            updatedItem = gameSession.teleportItem(int(body))
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.teleportItem", "1|"+ repr(updatedItem.id) + "*" + repr(updatedItem.lat) + "*" + repr(updatedItem.lng))
            state.gamelogicchannel.publish("PLAYER_"+repr(otherPlayer), state, "amazingActionKind.teleportItem", "0|"+ repr(updatedItem.id) + "*" + repr(updatedItem.lat) + "*" + repr(updatedItem.lng))
        
def drawItem(state, header, body):
    # Check if there is an magnet in the inventory of the user
    # then check if the desired item is still on the map
    # If not send an error message, (message = 1|fail)
    
    # Otherwise send a message to both players with the new location and update the location of the item.
    # message is of form 1|ID*lat*lng or 0|ID*lat*lng if the message is for the other player. 
    #Body is the item ID
    if header[0] == 'PLAYER_1':
        player = 1
        otherPlayer = 2
    else:
        player = 2
        otherPlayer = 1
    
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 3:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.drawItem", "1|fail")
        
    else:
        #Check if the item is still on the map.
        data = string.split(body, '*' )  
        if (int(data[0]) not in gameSession.mapItems) and (int(data[0]) not in gameSession.mapGoals):
            # send error message
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.drawItem", "1|fail")
        else:
            # Update item and send an update message to both players.
            del gameSession.playerItems[player-1][usedItemID]
            updatedItem = gameSession.drawItem(int(data[0]), int(data[1]), int(data[2]))
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.drawItem", "1|"+ repr(updatedItem.id) + "*" + repr(updatedItem.lat) + "*" + repr(updatedItem.lng))
            state.gamelogicchannel.publish("PLAYER_"+repr(otherPlayer), state, "amazingActionKind.drawItem", "0|"+ repr(updatedItem.id) + "*" + repr(updatedItem.lat) + "*" + repr(updatedItem.lng))
        
            #IF the item is a crown, also check if this crown is updated.
            if updatedItem.type==0: 
                oldOwner = updatedItem.owner       
                updatedItem.closestPoint = 100000
                updatedItem.owner = 0
                for point in gpsListP1:
                    if point.hidden == True:
                        continue
                    distance = gpsCoordinatesToDistance(point.lat, point.lng, float(updatedItem.lat)/float(10**6), float(updatedItem.lng)/float(10**6))
                    if distance < x.crownClaimRadius:
                        if distance < updatedItem.closestPoint:
                            updatedItem.closestPoint = distance
                            updatedItem.owner = 1
                for point in gpsListP2:
                    if point.hidden == True:
                        continue
                    distance = gpsCoordinatesToDistance(point.lat, point.lng, float(updatedItem.lat)/float(10**6), float(updatedItem.lng)/float(10**6))
                    if distance < x.crownClaimRadius:
                        if distance < updatedItem.closestPoint:
                            updatedItem.closestPoint = distance
                            updatedItem.owner = 2
                    
                if not oldOwner == updatedItem.owner:
                    #The owner has changed, we need to update the players on this. 
                    messageP1 = ""
                    messageP2 = ""
                    
                    for key in gameSession.mapGoals:
                        x= gameSession.mapGoals[key]
                        
                        # player 1  just send messages like stored.
                        messageP1 = messageP1 + repr(x.id) +","+ repr(x.owner) + "*"
                        
                        #player 2 invert the messages
                        if x.owner == 0:
                            messageP2 = messageP2 + repr(x.id) +","+ repr(x.owner) + "*"
                        if x.owner == 1:
                            messageP2 = messageP2 + repr(x.id) +",2*"
                        if x.owner == 2:
                            messageP2 = messageP2 + repr(x.id) +",1*"
                    
                    messageP1 = messageP1[0:-1]
                    messageP2 = messageP2[0:-1]
                    state.gamelogicchannel.publish("PLAYER_1", state, "amazingActionKind.updateCrownClaims", messageP1)
                    state.gamelogicchannel.publish("PLAYER_2", state, "amazingActionKind.updateCrownClaims", messageP2)
                                
def freePass(state, header, body):
    if header[0] == 'PLAYER_1':
        player = 1
        checkList = gpsListP2
    else:
        player = 2
        checkList = gpsListP1
        
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 4:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.freePass", "fail|0")
        
    else:
        #Check if the player did indeed violate the maze.
        if (player==1 and proximityViolationP1) or (player==2 and proximityViolationP2):
            latlng = string.split(body, '*' )
            lat = float(latlng[0])/float(10**6) 
            lng = float(latlng[1])/float(10**6)
            violation = False
            for x in checkList:
                if gpsCoordinatesToDistance(lat, lng, x.lat, x.lng)< 3:
                    #This point is too close. 
                    violation = True
                    break
            if violation == True:
                #We send a message that the player is too close
                state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.freePass", "fail|2")
            else:
                #We tell the player everything is fine and the violation is reset.
                del gameSession.playerItems[player-1][usedItemID]
                if player == 1:
                    global proximityViolationP1
                    proximityViolationP1= False
                    state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.freePass", body)
                else:
                    global proximityViolationP2
                    proximityViolationP2 = False
                    state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.freePass", body)
                    
        else:
            #There is no violation.
            state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.freePass", "fail|1")
            
def breakMaze(state, header, body):
    if header[0] == 'PLAYER_1':
        player = 1
        otherPlayer = 2
        gpsList = gpsListP2
    else:
        player = 2
        otherPlayer = 1
        gpsList = gpsListP1
    
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 2:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error item not found in inventory send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.breakMaze", "1|fail")
    else:
        #Check the list of the other players for points in the radius
        
        latlng = string.split(body, '*' )
        lat = float(latlng[0])/float(10**6) 
        lng = float(latlng[1])/float(10**6)
        
        message = "-1*" #We add -1 to the front to ensure we do not send an empty list. 
        
        for i in range(len(gpsList)):
            corner = gpsList[i]
            if gpsCoordinatesToDistance(lat, lng, corner.lat, corner.lng) <= gameSession.breakerRadius:
                # 'remove' the corner from the list. 
                #On the client we also need to edit the other corners. 
                #Here it suffices to just hide them and send the id to the client. 
                corner.hidden = True
                message = message + repr(corner.id) + "*" 
        del gameSession.playerItems[player-1][usedItemID]
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.breakMaze", "1|"+ message[0:-1])
        state.gamelogicchannel.publish("PLAYER_"+repr(otherPlayer), state, "amazingActionKind.breakMaze", "0|"+ message[0:-1])
        
        #Since we removed points we need to recheck the crowns.
        changedOwners = False
        for key in gameSession.mapGoals:
            x = gameSession.mapGoals[key]
            oldOwner = x.owner
            x.owner = 0
            x.closestPoint = 10000
            for point in gpsListP1:
                if point.hidden == True:
                    continue
                distance = gpsCoordinatesToDistance(point.lat, point.lng, float(x.lat)/float(10**6), float(x.lng)/float(10**6))
                if distance < x.closestPoint and distance < x.crownClaimRadius:
                    x.owner = 1
                    x.closestPoint = distance
            for point in gpsListP2:
                if point.hidden == True:
                    continue
                distance = gpsCoordinatesToDistance(point.lat, point.lng, float(x.lat)/float(10**6), float(x.lng)/float(10**6))
                if distance < x.closestPoint and distance < x.crownClaimRadius:
                    x.owner = 2
                    x.closestPoint = distance
            
            if x.owner != oldOwner:
                changedOwners = True
        
        if changedOwners:
            #The owner has changed, we need to update the players on this. 
            messageP1 = ""
            messageP2 = ""
            
            for key in gameSession.mapGoals:
                x= gameSession.mapGoals[key]
                
                # player 1  just send messages like stored.
                messageP1 = messageP1 + repr(x.id) +","+ repr(x.owner) + "*"
                
                #player 2 invert the messages
                if x.owner == 0:
                    messageP2 = messageP2 + repr(x.id) +","+ repr(x.owner) + "*"
                if x.owner == 1:
                    messageP2 = messageP2 + repr(x.id) +",2*"
                if x.owner == 2:
                    messageP2 = messageP2 + repr(x.id) +",1*"
            
            messageP1 = messageP1[0:-1]
            messageP2 = messageP2[0:-1]
            state.gamelogicchannel.publish("PLAYER_1", state, "amazingActionKind.updateCrownClaims", messageP1)
            state.gamelogicchannel.publish("PLAYER_2", state, "amazingActionKind.updateCrownClaims", messageP2)
            
def binoculars(state, header, body):
    if header[0] == 'PLAYER_1':
        player = 1
        otherPlayer = 2
    else:
        player = 2
        otherPlayer = 1
    
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 1:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error item not found in inventory send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.binoculars", "fail")
    else:
        # We need to compile a list of items and send it to the player
        message = ""
        for key in gameSession.playerItems[otherPlayer-1]:
            x = gameSession.playerItems[otherPlayer-1][key]
            message = message +repr(x.type) + "*"

        del gameSession.playerItems[player-1][usedItemID]
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.binoculars", message[0:-1])
        
        
def rocket(state, header, body):
    if header[0] == 'PLAYER_1':
        player = 1
    else:
        player = 2
    
    usedItemID = -1
    #Check if the used item is in the inventory
    for key in gameSession.playerItems[player-1]:
        x = gameSession.playerItems[player-1][key]
        if x.type == 5:
            #This item is the used item
            usedItemID = x.id
            break
    
    if usedItemID == -1:
        # error item not found in inventory send message
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.rocket", "fail")
    else:
        # Everything is fine, remove the item and notify the player.
        del gameSession.playerItems[player-1][usedItemID]
        state.gamelogicchannel.publish("PLAYER_"+repr(player), state, "amazingActionKind.rocket", "")


class TimerThread(threading.Thread):
    """Thread to execute some things every 5 minutes and eventually end the game."""
    
    def __init__(self, state):
        threading.Thread.__init__(self)
        self._finished = threading.Event()
        self.gameState = state
        self._interval = gameSession.gameDuration / 3000
        self.executionCounter = 0
        self.maxCounter =  gameSession.gameDuration / (self._interval * 1000)
    
    def shutdown(self):
        """Stop this thread"""
        self._finished.set()
    
    def setInterval(self, interval):
        self._interval = interval
    
    def run(self):
        while 1:
            if self._finished.isSet(): return
            # sleep for interval or until shutdown
            self._finished.wait(self._interval)
            
            if not self._finished.is_set(): self.task()
            
    def task(self):
        self.executionCounter +=1
        if (self.executionCounter < self.maxCounter):
            #This is a normal run.
            #Expand unclaimed crowns every 5 minutes
            #Check all crowns.
            message = ""
            for key in gameSession.mapGoals:
                x= gameSession.mapGoals[key]
               
                
                #If this is an unclaimed crown
                if (x.owner == 0):
                    # We need to expand this crown.
                    x.crownClaimRadius *= gameSession.expandRate
                    # Needed to update correctly
                    x.closestPoint = 100000
                    #Add the crown to the message.
                    message = message + repr(x.id) + "," + repr(x.crownClaimRadius) + "*"
                    
            message = message[0:-1]
            
            #After checking all crowns we check if crowns have been updated    
            if message != "":
                #We need to send a message to both players about the updated crowns.
                #First we check if the crown claims have changed. 
                for key in gameSession.mapGoals:
                    y = gameSession.mapGoals[key]
                    
                    if y.owner ==0:
                        for point in gpsListP1:
                            if point.hidden == True:    
                                continue
                            distance = gpsCoordinatesToDistance(point.lat, point.lng, float(y.lat)/float(10**6), float(y.lng)/float(10**6))
                            if distance < y.closestPoint:
                                if distance < y.crownClaimRadius:
                                    y.owner = 1
                                    y.closestPoint = distance
                        for point in gpsListP2:
                            if point.hidden == True:
                                continue
                            distance = gpsCoordinatesToDistance(point.lat, point.lng, float(y.lat)/float(10**6), float(y.lng)/float(10**6))
                            if distance < y.closestPoint:
                                if distance < y.crownClaimRadius:
                                    y.owner = 2
                                    y.closestPoint = distance
                                    
                #Crown claims were changed. We need to send the information to both players
                messageP1 = ""
                messageP2 = ""
                
                for key in gameSession.mapGoals:
                    x= gameSession.mapGoals[key]
                    
                    # player 1  just send messages like stored.
                    messageP1 = messageP1 + repr(x.id) +","+ repr(x.owner) + "*"
                    
                    #player 2 invert the messages
                    if x.owner == 0:
                        messageP2 = messageP2 + repr(x.id) +","+ repr(x.owner) + "*"
                    if x.owner == 1:
                        messageP2 = messageP2 + repr(x.id) +",2*"
                    if x.owner == 2:
                        messageP2 = messageP2 + repr(x.id) +",1*"
                
                messageP1 = messageP1[0:-1]
                messageP2 = messageP2[0:-1]
                global updateCrownClaimFlag
                updateCrownClaimFlag = False
                
                self.gameState.gamelogicchannel.publish("PLAYER_1", self.gameState, "amazingActionKind.expandCrowns", message +"|"+ messageP1)
                self.gameState.gamelogicchannel.publish("PLAYER_2", self.gameState, "amazingActionKind.expandCrowns", message +"|"+ messageP2)
                
                
        else:
            if (self.executionCounter == self.maxCounter):
                #This is the last execution.
                #Tell players about the result of the game.
                p1 =0
                p2 =0
                for key in gameSession.mapGoals:
                    x = gameSession.mapGoals[key]
            
                    if x.owner ==1:
                        p1 = p1+1
                    elif x.owner ==2 :
                        p2 = p2+1
                    
                self.gameState.gamelogicchannel.publish("PLAYER_1", self.gameState, "amazingActionKind.finishGame", repr(p1) +"*"+repr(p2))
                self.gameState.gamelogicchannel.publish("PLAYER_2", self.gameState, "amazingActionKind.finishGame", repr(p2) +"*"+repr(p1))

            
                #Reset the interval to 30 seconds after that the game instance will be terminated. 
                self.setInterval(30)
            else:
                
                #Terminate the instance here
                proxy = xmlrpclib.ServerProxy("http://"
                                  + XMLRPCConfiguration().getXMLRPCProperty("gameServerXMLRPCHost")
                                  + ":"
                                  + XMLRPCConfiguration().getXMLRPCProperty("gameServerXMLRPCPort")
                                  + "/")
                try:
                    proxy.terminateGameInstance(self.gameState.gameName , self.gameState.instanceName)
                except:
                    pass
                               
                self.shutdown()
                


#def finishInstance(instanceName):
#    call("(cd ./../TerminationInstanceApplication; ./run.sh "+instanceName+")", shell=True)

