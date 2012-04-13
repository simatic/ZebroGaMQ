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
from pika.credentials import PlainCredentials
from zebrogamq.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration

def createLoggingServer(gameName, instanceName):
    logging.info("GameLogicServer] Creating Logging server for " + gameName + " " + instanceName)
    vhost = RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + gameName + RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + instanceName
    credentials = PlainCredentials(RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName"),
                                   RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName"))
    parameters = pika.ConnectionParameters(host=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerBrokerHost"),
                                           virtual_host=vhost,
                                           credentials=credentials)
    logging.debug("LoggingServer] Starting a connection")
    connection = pika.BlockingConnection(parameters=parameters)
    logging.debug("LoggingServer] Creating a channel")
    channel = connection.channel()
    logging.debug("LoggingServer] Declaring a queue " + RabbitMQConfiguration().getRabbitMQProperty("loggingServerQueueName"))
    channel.queue_declare(queue=RabbitMQConfiguration().getRabbitMQProperty("loggingServerQueueName"),
                          exclusive=False,
                          durable=True)
    logging.debug("LoggingServer] Binding the queue with the rooting key " + RabbitMQConfiguration().getRabbitMQProperty("loggingServerBindingKey"))
    channel.queue_bind(exchange=RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerExchangeName"),
                       queue=RabbitMQConfiguration().getRabbitMQProperty("loggingServerQueueName"),
                       routing_key=RabbitMQConfiguration().getRabbitMQProperty("loggingServerBindingKey"))
    channel.basic_consume(consumer_callback = handle_delivery_loggingserver,
                          queue=RabbitMQConfiguration().getRabbitMQProperty("loggingServerQueueName"),
                          no_ack=True)
    logging.info(" LoggingServer] Waiting for messages on " + vhost)
    channel.start_consuming()
    logging.info("LoggingServer] Process terminated")
    
def handle_delivery_loggingserver(channel, method, header, body):
    logging.debug("LoggingServer] Received "+ method.routing_key + " " + body)
    seqBody = method.routing_key.split(',')
    key = seqBody[0]
    seqKey= key.split('.')
    senderName = seqKey[0]
    action = seqKey[3]
    if action == "terminate":
        logging.info("LoggingServer] Terminating...")
        channel.stop_consuming()
        