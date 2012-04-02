'''
Created on Jan 19, 2012
Keeps all the info of the currecnt game session


@author: Alex
'''
from gps import metricDistanceToLatitudeDistance, metricDistanceToLongditudeDistance, gpsCoordinatesToDistance
import random
from item import Item
halfAreaSize = 0
centerLat = 0.0
centerLng = 0.0
itemAmount = 0
goalAmount = 0
areaLeft = 0
areaRight = 0
areaTop = 0
areaBottom = 0
mapGoals = {}
mapItems = {}
maxItems = 6 #Hardcoded since the UI only supports 6.  
rechargeRate = 0.0
gameDuration = 0
crownClaimRadius = 0.0
itemPickupRadius = 0.0
breakerRadius = 0.0
expandRate = 0.0

mazePoolLimit = 0.0



spanID = range(9)
goalSpanID = []
itemSpanID = []



playerItems = [{}, {}]


def calculateAreaBounds():
    latDelta = metricDistanceToLatitudeDistance(centerLat, halfAreaSize)
    lngDelta = metricDistanceToLongditudeDistance(centerLat, halfAreaSize) 
    global areaLeft, areaRight, areaTop, areaBottom  
    areaLeft = centerLng - lngDelta
    areaRight = centerLng + lngDelta
    areaTop = centerLat + latDelta
    areaBottom = centerLat - latDelta
        
def getAreaBoundsString():
    return repr(areaLeft) +","+ repr(areaRight) +","+ repr(areaTop) +","+ repr(areaBottom)+","+repr(rechargeRate)+","+repr(gameDuration)+","+repr(crownClaimRadius)+","+repr(itemPickupRadius)+","+ repr(mazePoolLimit) 

def getGoalsString():
    value = ""
    latSpan = 2* metricDistanceToLatitudeDistance(centerLat, halfAreaSize)
    lngSpan = 2* metricDistanceToLongditudeDistance(centerLat, halfAreaSize)

    global mapGoals
    
    
    if goalAmount<5:
        goalSpanID = random.sample([0,2,6,8],goalAmount)
    if goalAmount>4:
        goalSpanID = [0,2,6,8] + random.sample([1,3,5,7],goalAmount-4)
    
    
    for x in range(goalAmount):
 #       lat = areaBottom + (0.1 +0.8*random())*latSpan
  #      lng = areaLeft + (0.1 +0.8*random())*lngSpan
        lat = areaBottom + (0.05 +0.3*random.random()+0.3*(goalSpanID[x]/3))*latSpan
        lng = areaLeft + (0.05 +0.3*random.random()+0.3*(goalSpanID[x]%3))*lngSpan   
        goal = Item(int(lat*float(10**6)), int( lng*float(10**6)), 0, "I'm a goal state!")
        goal.crownClaimRadius = crownClaimRadius
        value = value + goal.toString()+ ","
        
        #Add the item to the list of items
        mapGoals[goal.id] = goal
    
    return value[0:-2]

def getItemsString():
    value = ""
    latSpan = 2* metricDistanceToLatitudeDistance(centerLat, halfAreaSize)
    lngSpan = 2* metricDistanceToLongditudeDistance(centerLat, halfAreaSize)
    global mapItems
    
    global itemSpanID 
    
    for x in range(int(itemAmount/9)):
        itemSpanID = itemSpanID + spanID
    
    itemSpanID = itemSpanID + random.sample(spanID,itemAmount%9)
    
    for x in range(itemAmount):
   #     lat = areaBottom + (0.1 +0.8*random())*latSpan
    #    lng = areaLeft + (0.1 +0.8*random())*lngSpan
        lat = areaBottom + (0.05 +0.3*random.random()+0.3*(itemSpanID[x]/3))*latSpan
        lng = areaLeft + (0.05 +0.3*random.random()+0.3*(itemSpanID[x]%3))*lngSpan
        
        item = Item(int(lat*float(10**6)), int( lng*float(10**6)), 1+int(random.random()*6), "I'm an item!")
        #item = Item(int(lat*float(10**6)), int( lng*float(10**6)),5, "I'm an item!")
        value = value + item.toString()+ ","
        
        #Add the item to the list of items
        mapItems[item.id] = item
    
    return value[0:-2]

#Only call this function if it was made sure that the item is still in the list. 
def teleportItem(itemId):
    latSpan = 2* metricDistanceToLatitudeDistance(centerLat, halfAreaSize)
    lngSpan = 2* metricDistanceToLongditudeDistance(centerLat, halfAreaSize)
    global mapItems
    lat = areaBottom + (0.05 +0.9*random.random())*latSpan
    lng = areaLeft + (0.05 +0.9*random.random())*lngSpan
    mapItems[itemId].lat = int(lat*float(10**6))
    mapItems[itemId].lng = int(lng*float(10**6))
    
    return mapItems[itemId]

def createNewGameData():
    ''''
    This methods creates the important things for a new game such as items and goals 
    The information is then returned as a string so that it can be forwarded to a player. 
    '''
    calculateAreaBounds()
    return getAreaBoundsString() + "*"+repr(goalAmount)+"*" +repr(itemAmount)+ "*"+getGoalsString()+"*"+ getItemsString()

#Only call this function if it was made sure that the item is still in the list. 
def drawItem(itemId, latE6, lngE6):
    #Check if it is a goal or an item
    if itemId  in mapGoals:
        #Goal
        #Draw the item towards the player.
        #first check the distance6
        global mapGoals
        item = mapGoals[itemId]
        distance = gpsCoordinatesToDistance((latE6/ float(10**6)),(lngE6/ float(10**6)),(item.lat/ float(10**6)),(item.lng/ float(10**6)))
        #TODO check if this should be set to a different value! 
        if  distance<= crownClaimRadius:
            #Just set it to the players location.
            
            mapGoals[itemId].lat = latE6
            mapGoals[itemId].lng = lngE6
            return mapGoals[itemId]
            
        else:
            #Set it to a point  between the original point and the player. 
            latSpan = (item.lat - latE6)
            lngSpan = (item.lng - lngE6)
            ratio = crownClaimRadius / distance 
            mapGoals[itemId].lat = int(mapGoals[itemId].lat - ratio*latSpan)
            mapGoals[itemId].lng = int(mapGoals[itemId].lng - ratio*lngSpan) 
            return mapGoals[itemId]
    else:
        #Item
        #Set the items location to the players location. 
        global mapItems
        mapItems[itemId].lat = latE6
        mapItems[itemId].lng = lngE6
        return mapItems[itemId]
        