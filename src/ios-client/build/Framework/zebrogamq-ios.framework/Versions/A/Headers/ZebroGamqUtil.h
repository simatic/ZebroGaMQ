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

#ifndef UTIL_H_
#define UTIL_H_

#include <stddef.h>
#include "Properties.h"
#include "Log.h"
#include "vector"

using namespace std;

class ZebroGamqUtil {
private:
	static Properties* rabbitMQProperties;
	static Properties* xmlrpcProperties;
	static Properties* configProperties;
	static Log* logger;

public:
	static void setRabbitMQProperties(Properties* prop);
	static Properties* getRabbitMQProperties();
	static void setXMLRPCProperties(Properties* prop);
	static Properties* getXMLRPCProperties();
	static void setConfigProperties(Properties* prop);
	static Properties* getConfigProperties();
	static void setLogger(Log* log);
	static void removeLogger();
	static void println(const std::string message);
	static std::string intToString(long number);
	static std::vector<std::string> splitString(std::string string, std::string delim);
    static std::string getContentKeyAt(std::string key, std::string regex, int at);
	static long currentTimeMillis();
	static std::string vectorToString(std::vector<std::string> vector);
};

#endif /* UTIL_H_ */
