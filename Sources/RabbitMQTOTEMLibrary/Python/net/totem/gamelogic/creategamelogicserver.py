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

import logging
from net.totem.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration
from gamelogicchannel import GameLogicChannel 
from gamelogicqueueworker import Worker
from mylistofactions import MyListOfActions
from mystate import MyState
import gamelogicvhost

def createGameLogicServer(gameInstance, masterLogin, masterPassword):
    logging.info("GameLogicServer] Creating instance " +gameInstance.gameName + " " + gameInstance.instanceName + " with master = " + masterLogin)
    state = MyState()
    state.createGameLogicSemaphore = gameInstance.semaphore
    state.vhost = gameInstance.vhost 
    gamelogicvhost.create_vhost(state.vhost)
    gamelogicvhost.set_permissions_vhost_server(state.vhost)
    gamelogicvhost.set_permissions_vhost_logginserver(state.vhost)
    gamelogicvhost.set_permissions_vhost_participant(state.vhost, masterLogin, masterPassword)
    state.gameName = gameInstance.gameName
    state.instanceName = gameInstance.instanceName
    state.gamelogiclistofactions = MyListOfActions
    # start the worker in another thread
    logging.debug("GameLogicServer] Creating Worker thread for vhost "+ state.vhost)
    worker = Worker(queueRequest=gameInstance.queueRequest, queueReturn=gameInstance.queueReturn, appstate=state)
    worker.start()
    logging.debug("GameLogicServer] Creating GameLogicChannel for vhost "+ state.vhost)
    # start the game logic channel in current thread 
    state.gamelogicchannel = GameLogicChannel(state)
    state.gamelogicchannel.start()
    # when the gamelogicchannel finish its loop on reception of a terminate message, delete the vhost 
    logging.info("GameLogicServer] Deleting the virtual host of the game instance " + state.vhost)
    gamelogicvhost.delete_vhost(state.vhost)

