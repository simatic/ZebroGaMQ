"""
 ZebroGaMQ: Communication Middleware for Mobile Gaming
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
from pika.adapters import SelectConnection
from pika.credentials import PlainCredentials
from zebrogamq.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration
import listofactions

class GameLogicChannel(object):
    
    def __init__(self, appstate):
        self.state = appstate
        
    def start(self):
        # parameters require for the AMQP connection: user name and password...
        credentials = PlainCredentials(RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"),
                                       RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"))
        parameters = pika.ConnectionParameters(host=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerBrokerHost"),
                                               virtual_host=self.state.vhost,
                                               credentials=credentials)
        # instantiate a connection
        connection = SelectConnection(parameters=parameters,
                                      on_open_callback=self.on_connected)
        # required behavior on close
        connection.add_on_close_callback(self.on_close)
        # start the connection
        connection.ioloop.start()
        
    def stop(self):
        self.connection.ioloop.stop()
            
    def on_close(self, conn):
        self.channel.close()
        self.connection.close()
        if self.state.exiting == False:
            self.connection.ioloop.start()

    def on_connected(self, new_connection):
        logging.debug("GameLogicServer] Connected")
        self.connection = new_connection
        self.connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, new_channel):
        logging.debug("GameLogicServer] Channel on /instance opened")
        self.channel = new_channel
        self.channel.exchange_declare(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                      type=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeType"),
                                      callback=self.on_exchange_declared)

    def on_exchange_declared(self, new_exchange):
        logging.debug("GameLogicServer] Exchange on /instance declared")
        self.channel.queue_declare(exclusive=True,
                                   callback=self.on_queue_declared)

    def on_queue_declared(self, new_queue):
        logging.debug("GameLogicServer] Queue on /instance declared")
        self.queue = new_queue.method.queue
        gameLogicServerBindingKey = "*." + RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + ".*.*"
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                        queue=self.queue,
                                        routing_key=gameLogicServerBindingKey,
                                        callback=self.on_queue_bind)

    def on_queue_bind(self, new_queue):
        logging.debug("GameLogicServer] Binding declared")
        gameLogicServerBindingKey = "*.all.*.*"
        self.channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                  queue=self.queue,
                                  routing_key=gameLogicServerBindingKey,
                                  callback=self.on_queue_bind_all)

    def on_queue_bind_all(self, new_channel):
        logging.debug("GameLogicServer] Binding for all declared, releasing gameLogicSemaphore for vhost "+ self.state.vhost)
        self.state.createGameLogicSemaphore.release()
        logging.debug("GameLogicServer] Waiting for messages on /instance ")
        self.channel.basic_consume(consumer_callback=self.handle_delivery_gamelogic,
                                           queue=self.queue,
                                           no_ack=True)

    def handle_delivery_gamelogic(self, channel, method, header, body):
        seqBody = body.split(RabbitMQConfiguration().getRabbitMQProperty("bodySeparator"))
        try:
            key = method.routing_key
        except:
            logging.debug("GameLogicServer] message ignored" +  body + " " + method)
        logging.debug("GameLogicServer] Received on /instance " + body + " with key " + key)
        seqKey = key.split(RabbitMQConfiguration().getRabbitMQProperty("routingKeySeparator"))
        actionKind = seqKey[2]
        action = seqKey[3]
        for i in listofactions.ListOfActions.reverseLookup:
            if actionKind == listofactions.ListOfActions.whatis(i):
                for j in listofactions.ListOfActions.whichenuminstance(i).reverseLookup:
                    if action == listofactions.ListOfActions.whichenuminstance(i).whatis(j):
                        listofactions.ListOfActions.whichenuminstance(i).whichfunction(j)(self.state, seqKey, body)
                        return
        for i in self.state.gamelogiclistofactions.reverseLookup:
            if actionKind == self.state.gamelogiclistofactions.whatis(i):
                for j in self.state.gamelogiclistofactions.whichenuminstance(i).reverseLookup:
                    if action == self.state.gamelogiclistofactions.whichenuminstance(i).whatis(j):
                        self.state.gamelogiclistofactions.whichenuminstance(i).whichfunction(j)(self.state, seqKey, body)
                        return

    def publish(self, consumer, state, action, message):
        logging.debug("GameLogicServer] publish message " + message + " with action " + action + " to consumer " +consumer)
        routingKey = RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + "." + consumer + "." + action
        self.channel.basic_publish(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                     routing_key=routingKey,
                                     body=message);

    def publishToAll(self, state, action, message):
        logging.debug("GameLogicServer] publish message " + message + " with action " + action + " to all consumers")
        routingKey = RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + ".all." + action
        self.channel.basic_publish(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                                     routing_key=routingKey,
                                     body=message);
                                           