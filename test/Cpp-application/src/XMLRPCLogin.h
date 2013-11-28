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

#ifndef XMLRPCLOGIN_H_
#define XMLRPCLOGIN_H_

#include <vector>
#include <unistd.h>
#include "XmlRpc.h"

using namespace XmlRpc;

class XMLRPCLogin {
public:
	static bool executeXmlRpcCall(std::string method, std::vector<std::string> params, XmlRpcValue result);
	static bool executeXmlRpcCall(std::string method, XmlRpcValue result);
	static bool createAndJoinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName);
	static bool joinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName);
	static bool joinGameInstance(std::string login, std::string password, std::string gameName, std::string gameInstanceName, std::string observationKey);
	static std::vector<std::string> listGameInstances(std::string gameName);
	static bool terminateGameInstance(std::string gameName, std::string gameInstanceName);
	static bool terminate();

	~XMLRPCLogin() {}
};

#endif /* XMLRPCLOGIN_H_ */
