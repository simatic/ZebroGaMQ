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

#include "GameLogicApplication.h"
#include "XMLRPCLogin.h"
#include <ctime>
#include <iostream>
#include <thread>
#include <chrono>
#include <stdio.h>
#include <stdlib.h>
#include "MyListOfGameLogicActions.h"
#include <zebrogamq-ios/ChannelsManager.h>
#include <zebrogamq-ios/JoinAction.h>
#include <zebrogamq-ios/PresenceAction.h>
#include <zebrogamq-ios/ZebroGamqUtil.h>
#include <fstream>

#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <errno.h>
#include <fcntl.h>

using namespace std;

GameLogicState* GameLogicApplication::state = 0;
std::string GameLogicApplication::XMLRPC_PROPERTIES_FILE = "./resources/xmlrpc.properties";
std::string GameLogicApplication::CONFIG_PROPERTIES_FILE = "./resources/config.properties";
std::string GameLogicApplication::RABBITMQ_PROPERTIES_FILE = "./resources/rabbitmq.properties";

bool GameLogicApplication::executeXMLRPCLogin(std::string action) {
    if (action.compare("createAndJoinGameInstance")==0) {
        return XMLRPCLogin::createAndJoinGameInstance(GameLogicApplication::state->login, GameLogicApplication::state->password, GameLogicApplication::state->gameName, GameLogicApplication::state->instanceName);
    }
    if (action.compare("joinGameInstance")==0) {
        return XMLRPCLogin::joinGameInstance(GameLogicApplication::state->login, GameLogicApplication::state->password, GameLogicApplication::state->gameName, GameLogicApplication::state->instanceName);
    }
    return nullptr;
}

/*
 * This method successively:
 * - instantiate the channelsManager of the GameLogicApplication::state in order to
 *   start the consumption and to enable publication of messages.
 * - publish a JOIN message to the GameLogicServer. By default,
 *   on the reception of this message, there is only a message
 *   displayed. If you want to define your own behavior on the
 *   reception of this message, please refer to the javadoc of
 *   the class OptionalDelegationOfStandardActions.
 *   (http://simatic.github.com/ZebroGaMQ/doc/javadoc/zebrogamq/gamelogic/OptionalDelegationOfStandardActions.html)
 */
void GameLogicApplication::initChannelsManager() {
	MyListOfGameLogicActions* myListOfGameLogicActions = new MyListOfGameLogicActions();
	GameLogicApplication::state->channelsManager = ChannelsManager::getInstance(GameLogicApplication::state, myListOfGameLogicActions->m_list);
	// Publish a Join message
	std::string content = GameLogicApplication::state->login + "," + GameLogicApplication::state->gameName + "," + GameLogicApplication::state->instanceName;
	state->channelsManager->publishToGameLogicServer(state, JOIN, content);
}

void GameLogicApplication::startParticipantListThread() {
	std::thread thread (threadStartParticipantListThread);
	thread.detach();
}

void GameLogicApplication::threadStartParticipantListThread() {
	while (!GameLogicApplication::state->hasConnectionExited()) {
        state->channelsManager->publishToGameLogicServer(state, ASK_PARTICIPANTS_LIST, " ");
		// wait for a while
		usleep(15000 * 1000);
	}
}

/*
* TODO: Load properties files
*/
bool GameLogicApplication::loadProperties() {
	bool result = false;
	Properties* xmlrpcProperty = new Properties();
	Properties* configProperty = new Properties();
	Properties* rabbitmqProperty = new Properties();

	result = xmlrpcProperty->load(GameLogicApplication::XMLRPC_PROPERTIES_FILE);
	if (!result) return false;

	result = configProperty->load(GameLogicApplication::CONFIG_PROPERTIES_FILE);
	if (!result) return false;

	result = rabbitmqProperty->load(GameLogicApplication::RABBITMQ_PROPERTIES_FILE);
	if (!result) return false;

	ZebroGamqUtil::setRabbitMQProperties(rabbitmqProperty);
	ZebroGamqUtil::setConfigProperties(configProperty);
	ZebroGamqUtil::setXMLRPCProperties(xmlrpcProperty);

	return result;
}

/*
 * TODO: Update config properties
 */
bool GameLogicApplication::updateConfigProperties(std::string propType, std::string propValue, const std::string &propFilename) {
    bool result = true;
    FILE *f = fopen(propFilename.c_str(), "w");
    if (f == nullptr) return false;
    fprintf(f, "%s    %s", propType.c_str(), propValue.c_str());
    fclose(f);
    return result;
}

/*
 * TODO: Load properties files
 */
bool GameLogicApplication::loadProperties(std::string xmlrpcPropertiesFile, std::string rabbitmqPropertiesFile, std::string configPropertiesFile) {
	bool result = false;
	Properties* xmlrpcProperty = new Properties();
	Properties* rabbitmqProperty = new Properties();
    Properties* configProperty = new Properties();
    
	result = xmlrpcProperty->load(xmlrpcPropertiesFile);
	if (!result) return false;
    
	result = rabbitmqProperty->load(rabbitmqPropertiesFile);
	if (!result) return false;
    
    result = configProperty->load(configPropertiesFile);
	if (!result) return false;
    
	ZebroGamqUtil::setRabbitMQProperties(rabbitmqProperty);
	ZebroGamqUtil::setXMLRPCProperties(xmlrpcProperty);
    ZebroGamqUtil::setConfigProperties(configProperty);
    
	return result;
}

/* Method check the XMLRPC server is available in (host, port) or not */
bool GameLogicApplication::checkXMLRPCServer() {
    std::string GAME_SERVER_URL = ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCHost");
	int GAME_SERVER_PORT = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCPort").c_str());
	int MAX_RETRY = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("maxRetry").c_str());
    
    struct sockaddr_in saddr;
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    
    struct hostent *hp = gethostbyname(GAME_SERVER_URL.c_str());
    if (hp == 0) return false;
    
    saddr.sin_family = hp->h_addrtype;
    memcpy(&saddr.sin_addr, hp->h_addr, hp->h_length);
    saddr.sin_port = htons((u_short) GAME_SERVER_PORT);

    int result = -1;
    for(int i = 0; i < MAX_RETRY; i++) {
    	// For asynch operation, this will return EWOULDBLOCK (windows) or
    	// EINPROGRESS (linux) and we just need to wait for the socket to be writable...
        int sock = ::socket(AF_INET, SOCK_STREAM, 0);
        result = ::connect(sock, (struct sockaddr *)&saddr, sizeof(saddr));
        if (result == 0) {
            close(sock);
            break;
        }
        close(sock);
        usleep(1000 * 1000);
    }
    return (result == 0);
}