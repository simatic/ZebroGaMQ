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

#include "XMLRPCLogin.h"
#include "XmlRpc.h"
#include <iostream>
#include <fstream>
#include <stdlib.h>
#include "ZebroGamqUtil.h"

using namespace std;
using namespace XmlRpc;

bool XMLRPCLogin::executeXmlRpcCall(std::string method, std::vector<std::string> params, XmlRpcValue result) {
	std::string GAME_SERVER_URL = ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCHost");
	int GAME_SERVER_PORT = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCPort").c_str());
	int MAX_RETRY = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("maxRetry").c_str());

	bool answer = false;
	XmlRpcValue noArgs, paramarrs;
	XmlRpcClient client(GAME_SERVER_URL.c_str(), GAME_SERVER_PORT);
	for (int i = 0; i < MAX_RETRY; i++) {
		try {
			for(unsigned int i=0; i<params.size(); i++) {
				paramarrs[i] = params.at(i);
			}
			answer = client.execute(method.c_str(), paramarrs, result);
			if (answer) break;
            else continue;
		} catch (XmlRpcException &e) {
            ofstream filestr;
            bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
            if ( isTempFileUsed ) {
                filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
                filestr << e.getMessage() << endl;
                filestr.close();
            } else {
                cout << "XMLRPC error: " << e.getMessage() << endl;
            }
            client.close();
		}
		// sleep 1 second before retrying
		usleep(1 * 1000 * 1000);
	}
	return answer;
}

bool XMLRPCLogin::executeXmlRpcCall(std::string method, XmlRpcValue result) {
	std::string GAME_SERVER_URL = ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCHost");
	int GAME_SERVER_PORT = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCPort").c_str());
	int MAX_RETRY = atoi(ZebroGamqUtil::getXMLRPCProperties()->getProperty("maxRetry").c_str());

	bool answer = false;
	XmlRpcValue noArgs, paramarrs;
	XmlRpcClient client(GAME_SERVER_URL.c_str(), GAME_SERVER_PORT);
	for (int i = 0; i < MAX_RETRY; i++) {
		try {
			answer = client.execute(method.c_str(), paramarrs, result);
			break;
		} catch (XmlRpcException &e) {
            ofstream filestr;
            bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
            if ( isTempFileUsed ) {
                filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
                filestr << e.getMessage() << endl;
                filestr.close();
            } else {
                cout << e.getMessage() << endl;
            }
		}
		// sleep 1 second before retrying
		usleep(1 * 1000 * 1000);
	}
	return answer;
}

bool XMLRPCLogin::createAndJoinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName) {
	bool res = false;
	bool answer = false;
	XmlRpcValue result;

	std::vector<std::string> params;
	params.push_back(login);
	params.push_back(password);
	params.push_back(gameName);
	params.push_back(gameInstanceName);
	answer = executeXmlRpcCall("createAndJoinGameInstance", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call createAndJoinGameInstance has succeded for " << login <<
                 " in " << gameName << "/" << gameInstanceName << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call createAndJoinGameInstance has succeded for " << login <<
                    " in " << gameName << "/" << gameInstanceName << endl;
        }

		res = true;
	}
	return res;
}

bool XMLRPCLogin::joinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName) {
	bool res = false;
	bool answer = false;
	XmlRpcValue result;

	std::vector<std::string> params;
	params.push_back(login);
	params.push_back(password);
	params.push_back(gameName);
	params.push_back(gameInstanceName);
	answer = executeXmlRpcCall("joinGameInstance", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call joinGameInstance has succeded for "
                << login << " in " << gameName << "/" << gameInstanceName
                << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call joinGameInstance has succeded for "
                 << login << " in " << gameName << "/" << gameInstanceName
                 << endl;
        }

		res = true;
	}
	return res;
}

bool XMLRPCLogin::joinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName, std::string observationKey) {
	bool res = false;
	bool answer = false;
	XmlRpcValue result;

	std::vector<std::string> params;
	params.push_back(observationKey);
	params.push_back(gameInstanceName);
	params.push_back(gameName);
	params.push_back(password);
	params.push_back(login);
	answer = executeXmlRpcCall("joinGameInstance", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call joinGameInstance has succeded for " << login <<
                       " in " << gameName << "/" << gameInstanceName << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call joinGameInstance has succeded for " << login <<
                    " in " << gameName << "/" << gameInstanceName << endl;
        }
		res = true;
	}
	return res;
}

std::vector<std::string> XMLRPCLogin::listGameInstances(std::string gameName) {
	std::vector<std::string> res;
	bool answer = false;
	XmlRpcValue result;
	std::vector<std::string> params;
	params.push_back(gameName);
	answer = executeXmlRpcCall("listGameInstances", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call to listGameInstances " << gameName << " has succeded." << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call to listGameInstances " << gameName << " has succeded." << endl;
        }
		
		res = (std::vector<string>)params;
	}
	return res;
}

bool XMLRPCLogin::terminateGameInstance(std::string gameName, std::string gameInstanceName) {
	bool res = false;
	bool answer = false;
	XmlRpcValue result;
	std::vector<std::string> params;
	params.push_back(gameInstanceName);
	params.push_back(gameName);
	answer = executeXmlRpcCall("terminateGameInstance", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call to terminateGameInstance " << gameName << "/" << gameInstanceName << " has succeded." << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call to terminateGameInstance " << gameName << "/" << gameInstanceName << " has succeded." << endl;
        }
		res = true;
	}
	return res;
}

bool XMLRPCLogin::terminate() {
	bool res = false;
	bool answer = false;
	XmlRpcValue result;
	std::vector<std::string> params;
	answer = executeXmlRpcCall("terminate", params, result);
	if (answer == true) {
        ofstream filestr;
        bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
        if ( isTempFileUsed ) {
            filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
            filestr << "XMLRPC call to terminate has succeeded." << endl;
            filestr.close();
        } else {
            cout << "XMLRPC call to terminate has succeeded." << endl;
        }
		res = true;
	}
	return res;
}


