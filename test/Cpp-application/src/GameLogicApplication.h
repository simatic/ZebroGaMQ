#ifndef GAMELOGICAPPLICATION_H_
#define GAMELOGICAPPLICATION_H_

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

#include <iostream>
#include <stdio.h>
#include <unistd.h>
#include "GameLogicState.h"
#include "ZebroGamqUtil.h"

class GameLogicApplication {
private:
	static int team;
	static std::string payload;

public:
	static GameLogicState* state;

	static std::string XMLRPC_PROPERTIES_FILE;
	static std::string CONFIG_PROPERTIES_FILE;
	static std::string RABBITMQ_PROPERTIES_FILE;

	static bool executeXMLRPCLogin();

	/*
	 * This method successively:
	 * - instantiate the channelsManager of the state in order to
	 *   start the consumption and to enable publication of messages.
	 * - publish a JOIN message to the GameLogicServer. By default,
	 *   on the reception of this message, there is only a message
	 *   displayed. If you want to define your own behavior on the
	 *   reception of this message, please refer to the javadoc of
	 *   the class OptionalDelegationOfStandardActions.
	 *   (http://simatic.github.com/ZebroGaMQ/doc/javadoc/zebrogamq/gamelogic/OptionalDelegationOfStandardActions.html)
	 */
	static void initChannelsManager();
	static void startParticipantListThread();
	static void threadStartParticipantListThread();
	static bool loadProperties();
    static bool loadProperties(std::string xmlrpcPropertiesFile, std::string rabbitmqPropertiesFile, std::string configPropertiesFile);
    static bool updateConfigProperties(std::string propType, std::string propValue, std::string propFilename);
};


#endif /* GAMELOGICAPPLICATION_H_ */
