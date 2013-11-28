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

 Developer(s): LE Van Hung, Van Hung LE
 */

#include "ZebroGamqUtil.h"
#include <stdexcept>
#include <string.h>
#include <stdio.h>
#include <vector>
#include <sys/time.h>

using namespace std;

Properties* ZebroGamqUtil::rabbitMQProperties = NULL;
Properties* ZebroGamqUtil::xmlrpcProperties = NULL;
Properties* ZebroGamqUtil::configProperties = NULL;
Log* ZebroGamqUtil::logger =  new Log();

void ZebroGamqUtil::setRabbitMQProperties(Properties* prop) {
	if (rabbitMQProperties == NULL) {
		rabbitMQProperties = prop;
	}
}

Properties* ZebroGamqUtil::getRabbitMQProperties() {
	return rabbitMQProperties;
}

void ZebroGamqUtil::setXMLRPCProperties(Properties* prop) {
	if (xmlrpcProperties == NULL) {
		xmlrpcProperties = prop;
	}
}

Properties* ZebroGamqUtil::getXMLRPCProperties() {
	return xmlrpcProperties;
}

void ZebroGamqUtil::setConfigProperties(Properties* prop) {
	if (configProperties == NULL) {
		configProperties = prop;
	}
}

Properties* ZebroGamqUtil::getConfigProperties() {
	return configProperties;
}

void ZebroGamqUtil::println(const std::string message) {
	if (logger == NULL) {
		// Ignore this case that can appear when ending the application
		throw std::invalid_argument("Cannot print with a NULL logger");
	} else {
		logger->println(message);
	}
}

/**
* Method to convert number into string
*/
std::string ZebroGamqUtil::intToString(long number) {
	char buffer[50];
	sprintf(buffer, "%ld", number);
	return std::string(buffer);
}

/**
* Method to split string following delim
*/
std::vector<std::string> ZebroGamqUtil::splitString(std::string string, std::string delim) {
	std::vector<std::string> vector;
	char *str = new char[string.size()];
	sprintf(str, "%s", string.c_str());
	char * pch;
	pch = strtok (str, delim.c_str());
	while (pch != NULL) {
		if (pch != NULL) {
			vector.push_back(std::string(pch));
		}
		pch = strtok (NULL, delim.c_str());
	}
	return vector;
}

std::string ZebroGamqUtil::getContentKeyAt(std::string key, std::string regex, int at) {
    std::string content = "";
    if (key.length() == 0) {
        return "";
    }
    if (regex.length() == 0) {
        return "";
    }
    std::vector<std::string> seq = ZebroGamqUtil::splitString(key, regex);
    if (at < seq.size()) {
        content = seq.at(at);
    }
    return content;
}

/**
* Method to get current time in milliseconds
*/
long ZebroGamqUtil::currentTimeMillis() {
	timeval time;
	gettimeofday(&time, NULL);
	long millis = (time.tv_sec * 1000) + (time.tv_usec / 1000);
	return millis;
}

/**
* Convert string vector into string: {"one", "two", "three"} => {"one.two.three"}
*/
std::string ZebroGamqUtil::vectorToString(std::vector<std::string> vector) {
	std::string str("");
	unsigned int i;
	for(i=0; i<vector.size()-1; i++) {
		str.append(vector[i]);
		str.append(".");
	}
	str.append(vector[vector.size()-1]);
	return str;
}
