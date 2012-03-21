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
    def join(self, login):
        logging.info("GameLogicServer] Joining of " + login)
        self.channel.queue_declare(queue=login)
        logging.debug("GameLogicServer] Queue declared " + login)
        observationKey = "*." + login + ".*.*"
        logging.debug("GameLogicServer] Binding key is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=login,
                                routing_key=observationKey)
        logging.debug("GameLogicServer] First queue bound for " + login)
        observationKey = "*.all.*.*"
        logging.debug("GameLogicServer] Binding key for broadcasts is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=login,
                                routing_key=observationKey)
        logging.debug(" GameLogicServer] Second queue bound for " + login)
        self.publish(login, self.state, "join.joinOK", login + " has joined")
        
       
    def joinWithObservationKey(self, login, observationKey):
        logging.info("GameLogicServer] Joining of "+ login)
        self.channel.queue_declare(queue=login)
        logging.debug("GameLogicServer] Queue declared for " + login)
        logging.debug("GameLogicServer] Observation key is " + observationKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=login,
                                routing_key=observationKey)
        logging.debug("GameLogicServer] First queue bound on for " + login)
        observationKey = "*.all.*.*"
        logging.debug("GameLogicServer] Binding key for broadcasts is " + spectatorKey)
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                queue=login,
                                routing_key=observationKey)
        logging.debug(" GameLogicServer] Second queue bound on for " + login)
        self.publish(login, self.state, "join.joinOK", login + " has joined")
    
    
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
                                     