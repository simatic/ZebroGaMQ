/**
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

 Developer(s): Michel SIMATIC, LE Van Hung
 */

#include "PublishChannel.h"
#include "AMQPcpp.h"
#include "RabbitMQGameInstanceChannel.h"
#include "Properties.h"
#include "ChannelsManager.h"
#include "GameLogicState.h"
#include "Action.h"
#include "ZebroGamqUtil.h"
#include <fstream>
#include <iostream>

PublishChannel::PublishChannel(const ChannelsManager* channelsManager, const GameLogicState* state) : RabbitMQGameInstanceChannel(channelsManager, state) {
	routingKeyRootGameInstanceServer = 	state->login
										+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
										+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("gameLogicUserName")
										+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator");
	routingKeyRootAll = state->login
						+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
						+ "all"
						+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator");
}

void PublishChannel::publish(const std::string consumer, const GameLogicState *state,
		const Action* action, const std::string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}	
	if (consumer.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : publish tries to publish to null consumer"
				 	<< endl;
		} else {
			cout << "EXCEPTION : publish tries to publish to null consumer"
				 << endl;
		}
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
			 	 	<< endl;
		} else {
			cout << "publish tries to publish with null state"
			 	 << endl;
		}
	}
	if (action->getKind().size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null action"
				 << endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
				 	<< endl;
		} else {
			cout << "publish tries to publish a null message"
				 << endl;
		}
	}
	std::string routingKey = state->login
			+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
			+ consumer
			+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
			+ action->getKind();
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
						<< " with key = " << routingKey << " on vhost "
				 		<< state->virtualHost
				 		<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
					 << " with key = " << routingKey << " on vhost "
				 	 << state->virtualHost
				 	 << endl;
			}
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::publish(const std::string consumer, const GameLogicState *state,
		const std::string action, const std::string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}

	if (consumer.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : publish tries to publish to null consumer"
				 	<< endl;
		} else {
			cout << "EXCEPTION : publish tries to publish to null consumer"
				 	<< endl;
		}
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null state"
				 << endl;
		}
	}
	if (action.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
			 		<< endl;
		} else {
			cout << "publish tries to publish with null action"
			 	<< endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
				 	<< endl;
		} else {
			cout << "publish tries to publish a null message"
				 << endl;
		}
	}
	std::string routingKey = state->login
			+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
			+ consumer
			+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
			+ action;
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
					 	<< " with key = " << routingKey << " on vhost "
				 		<< state->virtualHost
				 		<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
					 << " with key = " << routingKey << " on vhost "
				 	<< state->virtualHost
				 	<< endl;
			}
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::publishToAll(const GameLogicState *state,
					const string action, const string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
			 		<< endl;
		} else {
			cout << "publish tries to publish with null state"
			 	<< endl;
		}	 	
	}
	if (action.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
			 	 	<< endl;
		} else {
			cout << "publish tries to publish with null action"
			 	 << endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
			 		<< endl;
		} else {
			cout << "publish tries to publish a null message"
			 	<< endl;
		}
	}
	std::string routingKey = routingKeyRootAll + action;
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
					 	<< " with key = " << routingKey << " on vhost "
				 	 	<< state->virtualHost
				 	 	<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
					 << " with key = " << routingKey << " on vhost "
				 	 << state->virtualHost
				 	 << endl;
			} 
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::publishToAll(const GameLogicState *state,
				const Action* action, const string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null state"
				 << endl;
		}
	}
	if (action->getKind().size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
					<< endl;
		} else {
			cout << "publish tries to publish with null action"
				 << endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
			 	 	<< endl;
		} else {
			cout << "publish tries to publish a null message"
			 	 << endl;
		}
	}
	std::string routingKey = routingKeyRootAll + action->getKind();
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
					 	<< " with key = " << routingKey << " on vhost "
				 		<< state->virtualHost
				 		<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
					 << " with key = " << routingKey << " on vhost "
				 	 << state->virtualHost
				 	 << endl;
			}
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::publishToGameLogicServer(const GameLogicState *state,
		const Action* action, const std::string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
			 	 	<< endl;
		} else {
			cout << "publish tries to publish with null state"
			 	 << endl;
		}
	}
	if (action->getKind().size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null action"
				 << endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
				 	<< endl;
		} else {
			cout << "publish tries to publish a null message"
				 << endl;
		}
	}
	std::string routingKey = routingKeyRootGameInstanceServer + action->toString();
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
				 	 	<< " with key = " << routingKey << " on vhost "
				 	 	<< state->virtualHost
				 	 	<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
				 	 << " with key = " << routingKey << " on vhost "
				 	 << state->virtualHost
				 	 << endl;			
			}
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = this->initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::publishToGameLogicServer(const GameLogicState *state,
		const string action, const std::string message) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null state"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null state"
				 << endl;
		}
	}
	if (action.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish with null action"
				 	<< endl;
		} else {
			cout << "publish tries to publish with null action"
				 << endl;
		}
	}
	if (message.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "publish tries to publish a null message"
				 	<< endl;
		} else {
			cout << "publish tries to publish a null message"
				 << endl;
		}
	}
	std::string routingKey = routingKeyRootGameInstanceServer + action;
	bool communicationOK = true;
	while(communicationOK) {
		try {
			ex->setHeader("Delivery-mode", 2);
			ex->setHeader("Content-type", "text/text");
			ex->setHeader("Content-encoding", "UTF-8");

			ex->Publish(message, routingKey);
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Sent " << message
					 	<< " with key = " << routingKey << " on vhost "
				 		<< state->virtualHost
				 		<< endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Sent " << message
					 << " with key = " << routingKey << " on vhost "
				 	<< state->virtualHost
				 	<< endl;
			}	 
			return;
		} catch (AMQPException &e) {
			amqp->closeChannel();
			communicationOK = this->initCommunicationWithBroker(state);
			std::cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

void PublishChannel::close() {}

PublishChannel::~PublishChannel() {
	// TODO Auto-generated destructor stub
}

