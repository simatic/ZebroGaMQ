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

import sys
import logging
from subprocess import call
from zebrogamq.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration

grant_all_permissions = " \".*\" \".*\" \".*\""

def create_vhost(vhost):
    return_code = call("rabbitmqctl add_vhost " + vhost, shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in creating virtual host "+ vhost)
        exit(1)
        
def delete_vhost(vhost):
    return_code = call("rabbitmqctl delete_vhost " + vhost, shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in deleting virtual host "+ vhost)
        exit(1)

def set_permissions_vhost_server(vhost):
    return_code = call("rabbitmqctl add_user "
                       + RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName")
                       + " "
                       + RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"),
                       shell=True)
    if return_code != 0:
        if return_code == 2: # User exist
            logging.info("GameLogicServer] User " + RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName") + " already exists")
            pass
        else:
            logging.error("GameLogicServer] Problem (" + str(return_code) + ") in adding user "+ RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName"))
            exit(1)
    return_code = call("rabbitmqctl set_permissions -p "
                       + vhost + " " + RabbitMQConfiguration().getRabbitMQProperty("gameLogicServerUserName")
                       + grant_all_permissions,
                       shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in setting permissions for server")
        exit(1)

def set_permissions_vhost_logginserver(vhost):
    return_code = call("rabbitmqctl add_user "
                       + RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName")
                       + " "
                       + RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName"),
                       shell=True)
    if return_code != 0:
        if return_code == 2: # User exist
            logging.info("GameLogicServer] User " + RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName") + " already exists")
            pass
        else:
            logging.error("GameLogicServer] Problem (" + str(return_code) + ") in adding user "+ RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName"))
            exit(1)
    return_code = call("rabbitmqctl set_permissions -p "
                       + vhost + " " + RabbitMQConfiguration().getRabbitMQProperty("loggingServerUserName")
                       + grant_all_permissions,
                       shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in setting permissions for logging server")
        exit(1)

def set_permissions_vhost_participant(vhost, participantLogin, participantPassword):
    return_code = call("rabbitmqctl add_user " + participantLogin
                       + " " + participantPassword,
                       shell=True)
    if return_code != 0:
        if return_code == 2: # User exist
            logging.info("GameLogicServer] User " + participantLogin + " already exists")
            pass
        else:
            logging.error("GameLogicServer] Problem (" + str(return_code) + ") in adding user "+ participantLogin)
            exit(1)
    return_code = call("rabbitmqctl set_permissions -p "
                       + vhost + " " + participantLogin
                       + grant_all_permissions,
                       shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in setting permissions for participant " + participantLogin)
        exit(1)

def get_permissions_vhost_participant(vhost, participantLogin):
    return_code = call("rabbitmqctl list_user_permissions -p " + vhost +" "+participantLogin,shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in listing permissions of user " + participantLogin + " in vhost " + vhost)
        exit(1)
        
def get_queues_vhost(vhost):
    return_code = call("rabbitmqctl list_queues -p " + vhost,shell=True)
    if return_code != 0:
        logging.error("GameLogicServer] Problem (" + str(return_code) + ") in listing queues of vhost " + vhost)
        exit(1)