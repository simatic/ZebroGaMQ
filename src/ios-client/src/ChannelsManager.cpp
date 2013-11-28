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

 Developer(s): Michel SIMATIC, Van Hung LE
 */

#include "ChannelsManager.h"
#include "Properties.h"
#include "GameLogicState.h"
#include "ZebroGamqUtil.h"
#include <fstream>
#include <iostream>

using namespace std;

const int ChannelsManager::CONNECTION_ESTABLISHMENT_TIMEOUT 	= 1000;

ChannelsManager *ChannelsManager::getInstance(GameLogicState *state,
		const std::vector<Action *> loai) {
	return new ChannelsManager(state, loai, false);
}

void ChannelsManager::exit() {
	ofstream filestr;
	
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
		filestr << " Exiting: closing channels and connections" << endl;
		filestr.close();
	} else {
		cout << " Exiting: closing channels and connections" << endl;
	}
	
	consumeChannel->close();
	publishChannel->close();
	heartbeatChannel->close();
}

void ChannelsManager::initGameLogicState(GameLogicState *state){
	// TODO Dans méthode initGameLogicState, remplacer tous les cout par des throw exception
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), fstream::in | fstream::out);
	}
	
	if (state->gameName.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 	<< " null game name"
			 	 	<< endl;
		} else {
			cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 << " null game name"
			 	 << endl;
		}
	}
	if (state->instanceName.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 	<< " null game instance name"
					<< endl;
		} else {
			cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 << " null game instance name"
				 << endl;
		}
	}
	state->virtualHost = ZebroGamqUtil::getRabbitMQProperties()->getProperty("virtualHostSeparator")
			+ state->gameName
			+ ZebroGamqUtil::getRabbitMQProperties()->getProperty("virtualHostSeparator")
			+ state->instanceName;

	state->exchangeName = ZebroGamqUtil::getRabbitMQProperties()->getProperty("gameLogicExchangeName");

	if (state->exchangeName.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 	<< " null exchange name"
				 	<< endl;
		} else {
			cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to create"
				 << " null exchange name"
				 << endl;
		}
	}
	if ((ZebroGamqUtil::getRabbitMQProperties()->getProperty("gameLogicBrokerHost")).size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect to"
					<< " null game logic broker host"
			 	 	<< endl;
		} else {
			cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect to"
				 << " null game logic broker host"
			 	 << endl;	
		}
	}
	if (state->login.size() == 0) {
		if ( isTempFileUsed ) {
			if ( isTempFileUsed ) {
				filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect with"
					 	<< " null login"
					 	<< endl;
			} else {
				cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect with"
					 << " null login"
					 << endl;
			}
		}
	}
	if (state->password.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect with"
				 	<< " null password"
				 	<< endl;
		} else {
			cout << "EXCEPTION : RabbitMQGameInstanceChannel tries to connect with"
				 << " null password"
				 << endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

ChannelsManager::ChannelsManager(GameLogicState *state,
		const std::vector<Action *> loai,
		bool enableRawAction) {
	// initialize state
	ChannelsManager::initGameLogicState(state);

	// instantiate attributes
	heartbeatChannel = new HeartbeatChannel(this, state);
	consumeChannel = new ConsumeChannel(this, state, loai, enableRawAction);
	publishChannel = new PublishChannel(this, state);
}

void ChannelsManager::publish(const std::string consumer, const GameLogicState* state,
					const Action* action, const std::string message) {
	publishChannel->publish(consumer, state, action, message);
}

void ChannelsManager::publish(const std::string consumer, const GameLogicState* state,
					const string action, const std::string message) {
	publishChannel->publish(consumer, state, action, message);
}

void ChannelsManager::publishToAll(	const GameLogicState* state,
								const Action* action, const std::string message) {
	publishChannel->publishToAll(state, action, message);
}

void ChannelsManager::publishToAll(	const GameLogicState* state,
								const string action, const std::string message) {
	publishChannel->publishToAll(state, action, message);
}

/**
 * Publish a message to the Game Logic server, and specify the action
 * that it should triggered on the reception of this message.
 *
 * @param 	state		the GameLogicState of the game logic
 * @param 	action		the action to trigger on the recipient side
 * @param 	message		the message
 *
 * @throws 	IOException if an exception occurs during the publish.
 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
 * 			The method PlayerState.exit() should be called on the handling of this exception.
 */
void ChannelsManager::publishToGameLogicServer(const GameLogicState *state,
		  const Action* action, const std::string message) {
	publishChannel->publishToGameLogicServer(state, action, message);
}

void ChannelsManager::publishToGameLogicServer(const GameLogicState *state,
		  const string action, const std::string message) {
	publishChannel->publishToGameLogicServer(state, action, message);
}

