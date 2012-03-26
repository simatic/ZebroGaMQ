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

from net.totem.configuration.rabbitmq.rabbitmqconfig import RabbitMQConfiguration
from multiprocessing import Queue, Semaphore

class GameInstanceManagementData(object):
	
	def __init__(self, gameName, instanceName):
		self.gameName = gameName
		self.instanceName = instanceName
		self.vhost = RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + gameName + RabbitMQConfiguration().getRabbitMQProperty("virtualHostSeparator") + instanceName
		self.semaphore = Semaphore(0)
		self.queueRequest = Queue()
		self.queueReturn = Queue()