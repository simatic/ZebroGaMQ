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
import logging
from SimpleXMLRPCServer import SimpleXMLRPCServer
from net.totem.configuration.xmlrpc.xmlrpcconfig import XMLRPCConfiguration
from net.totem.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration
import gameserverprotocol

global server
confDir = None


def setLogger():
    loggingLevel = RabbitMQConfiguration(confDir).getRabbitMQProperty("loggingLevel")
    if(loggingLevel == "DEBUG"):
        logging.basicConfig(format='[%(levelname)s - %(message)s', level=logging.DEBUG)
    elif(loggingLevel == "INFO"):
        logging.basicConfig(format='[%(levelname)s - %(message)s', level=logging.INFO)
    elif(loggingLevel == "WARNING"):
        logging.basicConfig(format='[%(levelname)s - %(message)s', level=logging.WARNING)
    elif(loggingLevel == "ERROR"):
        logging.basicConfig(format='[%(levelname)s - %(message)s', level=logging.ERROR)
    elif(loggingLevel == "CRITICAL"):
        logging.basicConfig(format='[%(levelname)s - %(message)s', level=logging.CRITICAL)
    else:
        logging.basicConfig(format='[%(levelname)s - %(message)s')

if __name__ == '__main__':
    # if confDir is used in params
    if len(sys.argv) == 2:
        confDir = sys.argv[1]
    setLogger()
    logging.info("GameServer] Game server started, identifier")
    port = int(XMLRPCConfiguration(confDir).getXMLRPCProperty("gameServerXMLRPCPort"))
    logging.info("GameServer] Listening on port "+str(port))
    server = None
    server = SimpleXMLRPCServer((XMLRPCConfiguration().getXMLRPCProperty("gameServerXMLRPCHost"),
                                 port))
    server.register_introspection_functions()
    server.register_function(gameserverprotocol.createAndJoinGameInstance)
    server.register_function(gameserverprotocol.joinGameInstance)
    server.register_function(gameserverprotocol.listGameInstances)
    server.register_function(gameserverprotocol.terminateGameInstance)
    server.register_function(gameserverprotocol.terminate)
    logging.info("GameServer] List of registered method for xml rpc calls of game server "+str(server.system_listMethods()))
    try:
        server.serve_forever()
    except:
        pass
    logging.info("GameServer] Leaving XML-RPC server loop")
    
    

    