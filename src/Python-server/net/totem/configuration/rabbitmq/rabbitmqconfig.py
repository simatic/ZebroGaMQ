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

import re
import sys

class Singleton(object):

    _alreadyInitialized = None
    _instance = None

    def __new__(self, *args, **kw):
        if self._instance is None:
            self._instance = super(Singleton, self).__new__(self)
            self._alreadyInitialized = False
        return self._instance

class RabbitMQConfiguration(Singleton):

    __configurationProperties = {}

    def __init__(self, confDir=None):
        if self._alreadyInitialized == False:
            try:
                if(confDir is None):
                    configurationFile = open('rabbitmq.properties', 'r')
                else:
                    configurationFile = open(confDir+'rabbitmq.properties', 'r')
            except IOError:
                if(confDir is None):
                    msg = "Cannot open rabbitmq.properties file."
                else:
                    msg = "Cannot open "+confDir+"rabbitmq.properties file." 
                sys.exit(msg)
            for line in configurationFile:
                property = re.split(r'\s*', line)
                self.__configurationProperties.setdefault(property[0], property[1])
            self._alreadyInitialized = True

    def getRabbitMQProperty(self, key):
        if self.__configurationProperties.__contains__(key):
            return self.__configurationProperties.get(key)
        else:
            return "No value for the key \'" + key + "\'"
