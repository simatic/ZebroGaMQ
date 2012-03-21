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
import threading
import Queue
from multiprocessing import Queue
from workerchannel import WorkerChannel

class Worker(threading.Thread):

    def __init__(self, queueRequest, queueReturn, appstate):
        self.state = appstate
        self.queueRequest = queueRequest
        self.queueReturn = queueReturn
        self.workerchannel = WorkerChannel(self.state)
        threading.Thread.__init__(self)
        
    def run(self):
        while self.state.exiting == False:
            item = self.queueRequest.get()
            if item[0] == "joinMaster":
                logging.debug("GameLogicServer] Forwarding for joining of a master")
                self.workerchannel.joinMaster(item[1])
                self.queueReturn.put([True])     
            if item[0] == "joinSpectator":
                logging.debug("GameLogicServer] Forwarding for joining of a spectator")
                self.workerchannel.joinSpectator(item[1], item[2])
                self.queueReturn.put([True])
            if item[0] == "joinPlayer":
                logging.debug("GameLogicServer] Forwarding for joining of a player")
                self.workerchannel.joinPlayer(item[1])
                self.queueReturn.put([True])
            if item[0] == "terminate":
                logging.debug("GameLogicServer] Forwarding for termination")
                self.workerchannel.terminate()
                self.queueReturn.put([True])