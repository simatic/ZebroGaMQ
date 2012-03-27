"""
 TCM: TOTEM Communication Middleware
 Copyright: Copyright (C) 2009-2012
 Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 USA

 Developer(s): Denis Conan, Gabriel Adgeg
"""

import sys
import thread
import logging
from multiprocessing import Process
from net.totem.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration
from net.totem.gamelogicserver.creategamelogicserver import createGameLogicServer
from net.totem.gamelogicserver.createloggingserver import createLoggingServer
from net.totem.gamelogicserver.gamelogicvhost import set_permissions_vhost_participant
from net.totem.gamelogicserver.gameinstancemanagementdata import GameInstanceManagementData

gameInstanceManagementDataDict = {}

def createAndJoinGameInstance(login, password, gameName, instanceName, loggingServer=True):
    """
    Create and join GameLogicServer managing a game instance.
    By default, logging parameter is set to True: a process is created to log every relevant message.
    Set the logging parameter to false if you don't need the logging process. 
    """
    logging.info("GameServer] Creation of a game instance "+ gameName+ " " + instanceName)
    gameinstance = GameInstanceManagementData(gameName,instanceName)
    # if instance is already running
    if gameinstance.vhost in gameInstanceManagementDataDict.keys():
        logging.warning("GameServer] Game instance "+ gameName + " " + instanceName + "is already running, creation canceled")
        return False
    else:
        # store the game instance
        gameInstanceManagementDataDict[gameinstance.vhost] = gameinstance
    logging.debug("GameServer] Going to create process for game instance "+ gameName+ " " + instanceName)
    gameinstance.process = Process(target=createGameLogicServer,
                                   args=(gameinstance,
                                        login,
                                        password,))
    gameinstance.process.start()
    logging.debug("GameServer] Acquiring semaphore: wait for end of creation of the game instance")
    gameinstance.semaphore.acquire()
    logging.debug("GameServer] Semaphore released")
    if loggingServer:
        logging.info("GameServer] Creating process for LoggingServer")
        process= Process(target=createLoggingServer,
                        args=(gameName,
                              instanceName,))
        process.start()
    logging.debug("GameServer] Create queue and binding for "+ login)
    gameinstance.queueRequest.put(["join", login])
    item = gameinstance.queueReturn.get()
    return item[0]


def joinGameInstance(login, password, gameName, instanceName, observationKey=None):
    if observationKey is None:
        logging.info("GameServer] "+login+" joining game instance "+ gameName + " "+ instanceName)
    else:
        logging.info("GameServer] "+login+" joining game instance "+ gameName + " "+ instanceName+ " with observation key " + observationKey)
    # if instance doesn't exist
    vhost = RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + gameName + RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + instanceName
    if vhost not in gameInstanceManagementDataDict.keys():
        logging.warning("GameServer] Game instance "+ gameName +" "+ instanceName+ "doesn't exist, joining canceled ")
        return False
    else:
        gameinstance = gameInstanceManagementDataDict[vhost]
        set_permissions_vhost_participant(vhost, login, password)
        if observationKey is None:
            gameinstance.queueRequest.put(["join", login])
        else:
            gameinstance.queueRequest.put(["joinWithObservationKey", login, observationKey])
        item = gameinstance.queueReturn.get()
        logging.info("GameServer] " + login + " has joined the game instance "+gameName+" "+instanceName + ".")
        return item[0]
    
    
def listGameInstances(gameName):
    """
    Return a tuple (for Java client, an Object, that can be cast in Object[], containing String elements)
    with all the instances created for a game.
    """
    print ' [GameServer] Listing game instances for game', gameName
    availableInstances = []
    for gameInstance in gameInstanceManagementDataDict.values():
        if gameInstance.gameName == gameName:
            availableInstances.append(gameInstance.instanceName)
    if len(availableInstances) == 0:
        logging.info("GameServer] Not any instance has been created for game "+ gameName)
    else:
        logging.info("GameServer] Game instances for game " + gameName + ":" + str(availableInstances))
    return availableInstances
    

def terminateGameInstance(gameName, instanceName):
    logging.info("GameServer] Terminating game instance "+ gameName + " " + instanceName)
    # if instance doesn't exist
    vhost = RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + gameName + RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + instanceName
    if vhost not in gameInstanceManagementDataDict.keys():
        logging.warning("GameServer] Game instance "+ gameName +" "+ instanceName+ "doesn't exist, termination canceled ")
        return False
    else:
        # return the game instance, and remove it from the dictionary
        gameinstance = gameInstanceManagementDataDict.pop(vhost)
        gameinstance.queueRequest.put(["terminate"])
        logging.debug("GameServer] Wait for the end of game logic server for "+ gameName + " " + instanceName)
        gameinstance.process.join()
        while not gameinstance.queueRequest.empty():
            #print "queue request:", gameinstance.queueRequest.get()
            logging.debug("GameServer] queue request:" + str(gameinstance.queueRequest.get()))
        while not gameinstance.queueReturn.empty():
            #print "queue return:", gameinstance.queueReturn.get()
            logging.debug("GameServer] queue return:" + str(gameinstance.queueReturn.get()))
        return True


def terminate():
    logging.info("GameServer] Terminating all game instances")
    for gameinstance in gameInstanceManagementDataDict.values():
        terminateGameInstance(gameinstance.gameName, gameinstance.instanceName)
    logging.debug("GameServer] Interrupting main thread")
    try:
        thread.interrupt_main();
    except:
        pass
    logging.debug("GameServer] Interrupting current thread")
    try:
        thread.exit_thread();
    except:
        pass
    logging.info("GameServer] Exiting")
    sys.exit(0)
