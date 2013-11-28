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

#include "RabbitMQGameInstanceChannel.h"
#include "Properties.h"
#include "GameLogicState.h"
#include "ZebroGamqUtil.h"
#include <fstream>
#include <iostream>

using namespace std;

RabbitMQGameInstanceChannel::RabbitMQGameInstanceChannel(const ChannelsManager *channelsManager, const GameLogicState *state) {
	numberOfRetries = 0;
	this->channelsManager = (ChannelsManager*) channelsManager;
	initCommunicationWithBroker(state);
}

bool RabbitMQGameInstanceChannel::initCommunicationWithBroker(const GameLogicState *state) {
	bool communicationOK = false;
	int max = atoi(ZebroGamqUtil::getRabbitMQProperties()->getProperty("maxRetry").c_str());
	for (int i = 0; i < max; i++) {
		if (!state->hasConnectionExited()) {
			try {
				std::string virtualHost = ZebroGamqUtil::getRabbitMQProperties()->getProperty("virtualHostSeparator")
										  + state->gameName
										  + ZebroGamqUtil::getRabbitMQProperties()->getProperty("virtualHostSeparator")
										  + state->instanceName;
				std::string addr(state->password
								 + ":"
								 + state->login
								 + "@"
								 + ZebroGamqUtil::getRabbitMQProperties()->getProperty("gameLogicBrokerHost")
								 + ":"
								 + ZebroGamqUtil::getRabbitMQProperties()->getProperty("gameLogicBrokerPort")
								 + "/"
								 + virtualHost);
				if (!amqp) {
					amqp = new AMQP(addr);
				}
				ex = amqp->createExchange(state->exchangeName);
				ex->Declare(state->exchangeName, "topic", AMQP_PASSIVE);
				communicationOK = true;
				//state->setNumberOfRetries(0);
				numberOfRetries = 0;
				break;
			} catch (AMQPException &e) {
				ofstream filestr;
				bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
				if ( isTempFileUsed ) {
					filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
					filestr << "AMQPException : " + e.getMessage() << std::endl;
					filestr.close();
				} else {
					cout << "AMQPException : " + e.getMessage() << std::endl;
				}

				// TODO See if it is OK not to have a handleRetryNumber
				handleRetryNumber(i, max, state);
			}
		}
	}
	return communicationOK;
}

void RabbitMQGameInstanceChannel::handleRetryNumber(int retryNumber, int maxRetryNumber, const GameLogicState *state) {
	updateNumberOfRetries(retryNumber, state);
	if(retryNumber == maxRetryNumber - 1) {
		ofstream filestr;
		bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
		if ( isTempFileUsed ) {
			filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
			filestr << " [" << state->role << " " << state->login << "]: " << " stop : too many retries." << endl;
			filestr.close();
		} else {
			cout << " [" << state->role << " " << state->login << "]: " << " stop : too many retries." << endl;
		}

		state->connectionExit();
	} else {
		usleep(1000);
	}
}

void RabbitMQGameInstanceChannel::updateNumberOfRetries(int retryNumber, const GameLogicState *state) {
	//if(retryNumber > state->numberOfRetries){
	if(retryNumber > numberOfRetries){
		//state->setNumberOfRetries(retryNumber);
		numberOfRetries = retryNumber;
	}
}

void RabbitMQGameInstanceChannel::close() {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
		filestr << "Close instance of RabbitMQGameInstanceChannel" << endl;
		filestr.close();
	} else {
		cout << "Close instance of RabbitMQGameInstanceChannel" << endl;
	}
}
