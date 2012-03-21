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

import pika
import logging
from pika.credentials import PlainCredentials
from net.totem.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration

class WorkerChannel(object):
    
    def __init__(self, state):
        self.state = state
        credentials = PlainCredentials(RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"),
                                       RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"))
        parameters = pika.ConnectionParameters(host=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerBrokerHost"),
                                               virtual_host=self.state.vhost,
                                               credentials=credentials)
        # instantiate a connection
        logging.debug("GameLogicServer] WorkerChannel - Starting a connection")
        self.connection = pika.BlockingConnection(parameters=parameters)
        logging.debug("GameLogicServer] WorkerChannel - Creating a channel")
        self.channel = self.connection.channel()
            
    def stop(self):
        self.channel.close()
        self.connection.close()

    def publish(self, consumer, state, action, message):
        logging.debug("GameLogicServer] Worker - publish message " + message + " with action " + action + " to consumer " + consumer)
        routingKey = RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + "." + consumer + "." + action
        self.channel.basic_publish(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                   routing_key=routingKey,
                                   body=message);
    
    # actions
    def joinMaster(self, masterLogin):
        logging.info("GameLogicServer] Joining of a master " + masterLogin)
        self.channel.queue_declare(queue=masterLogin)
        logging.debug("GameLogicServer] Queue declared for master" + masterLogin)
        observationKey = "*." + masterLogin + ".*.*"
        logging.debug("GameLogicServer] master\'s binding key is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=masterLogin,
                                routing_key=observationKey)
        logging.debug("GameLogicServer] First queue bound for master" + masterLogin)
        observationKey = "*.all.*.*"
        logging.debug("GameLogicServer] master\'s binding key for broadcasts is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=masterLogin,
                                routing_key=observationKey)
        logging.debug(" GameLogicServer] Second queue bound for master " + masterLogin)
        self.publish(masterLogin, self.state, "join.joinMasterOK", masterLogin + " has joined as a master")
        
       
    def joinSpectator(self, spectatorLogin, spectatorKey):
        logging.info("GameLogicServer] Joining of a spectator "+ spectatorLogin)
        self.channel.queue_declare(queue=spectatorLogin)
        logging.debug("GameLogicServer] Queue declared for spectator " + spectatorLogin)
        logging.debug("GameLogicServer] Spectator\'s observation key is " + spectatorKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=spectatorLogin,
                                routing_key=spectatorKey)
        logging.debug("GameLogicServer] First queue bound on for spectator " + spectatorLogin)
        observationKey = "*.all.*.*"
        logging.debug("GameLogicServer] Spectator\'s binding key for broadcasts is " + spectatorKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=spectatorLogin,
                                routing_key=observationKey)
        logging.debug(" GameLogicServer] Second queue bound on for spectator " + spectatorLogin)
        self.publish(spectatorLogin, self.state, "join.joinSpectatorOK", spectatorLogin + " has joined as a spectator")
        
    
    def joinPlayer(self, playerLogin):
        logging.info("GameLogicServer] Joining of a player "+playerLogin)
        self.channel.queue_declare(queue=playerLogin)
        logging.debug("GameLogicServer] Queue declared for player " + playerLogin)
        observationKey = "*." + playerLogin + ".*.*"
        logging.debug("GameLogicServer] player\'s binding key is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=playerLogin,
                                routing_key=observationKey)
        logging.debug("GameLogicServer] First queue bound for player " + playerLogin)
        observationKey = "*.all.*.*"
        logging.debug("GameLogicServer] player\'s binding key for broadcasts is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=playerLogin,
                                routing_key=observationKey)
        logging.debug("GameLogicServer] Second queue bound for player " + playerLogin)
        self.publish(playerLogin, self.state, "join.joinPlayerOK", playerLogin + " has joined as a player")
    
    
    def terminate(self):
        logging.info("GameLogicServer] Terminating")
        self.state.exiting = True
        routingKey = RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + ".all.lifecycle.terminate"
        message = "all the participants must terminate"
        self.channel.basic_publish(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                   routing_key=routingKey,
                                   body=message);
        logging.debug("GameLogicServer] Stopping gamelogic channel ioloop")
        self.state.gamelogicchannel.stop()
        logging.debug("GameLogicServer] Stopping worker channel")
        self.stop()
                                     