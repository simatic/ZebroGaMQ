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

"""
Warning
-------

Messages should not be published in separate threads.
Indeed, the AMQP mechanism used by the Pika library for 
the publication of messages IS NOT thread-safe.
"""

import logging


def myFirstAction(state, header, body):
    """
    header array contains relevant information about the message:
    - header[0] = sender
    - header[1] = recipient
	- header[2] = action kind
	- header[3] = action name
    """
    logging.info("GameLogicServer] React to myFirstAction message")
    state.gamelogicchannel.publishToAll(state, "mySecondActionKind.myFourthAction", "Game logic says Go!")

def mySecondAction(state, header, body):
    logging.info("GameLogicServer] React to mySecondAction message")

def myFourthAction(state, header, body):
    logging.info("GameLogicServer] React to myFourthAction message")

def myFitfhAction(state, header, body):
    logging.info("GameLogicServer] React to myFitfhAction message")