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

#include "Properties.h"
#include <stdio.h>
#include <fstream>
#include <string>
#include <iostream>

using namespace std;

bool Properties::load(std::string propertyFilename) {
	bool result = false;
	std::string line ;
	std::ifstream infile(propertyFilename.c_str()) ;
	if ( infile ) {
		result = true;
		while ( getline( infile , line ) ) {
			if (line.size() > 0) {
				int firstStrBg = 0;
				while ( (firstStrBg < line.size()) && ((line.at(firstStrBg) == ' ') || (line.at(firstStrBg) == '\t')) ) firstStrBg++;
				if (line.at(firstStrBg) == '#') continue;
				if (firstStrBg >= line.size() - 1) continue;
				int firstStrEnd = firstStrBg;
				while ( (firstStrEnd < line.size()) && ((char)(line.at(firstStrEnd) >= '!') && ((char)line.at(firstStrEnd) <= '~')) ) firstStrEnd++;
				if (firstStrEnd >= line.size() - 1) continue;

				int secondStrEnd = line.size() - 1;
				while ((line.at(secondStrEnd) == ' ') || (line.at(secondStrEnd) == '\t')) secondStrEnd--;
				if (secondStrEnd <= firstStrEnd) continue;
				
				int secondStrBg = firstStrEnd;
				while (((char)line.at(secondStrBg) == ' ') || ((char)line.at(secondStrBg) == '\t')) secondStrBg++;

				std::string propType = line.substr(firstStrBg, firstStrEnd - firstStrBg);
				std::string propValue = line.substr(secondStrBg, secondStrEnd - secondStrBg + 1);
				property prop;
				prop.propType = propType;
				prop.propValue = propValue;
				properties.push_back(prop);
			}
		}
	} else return false;
	infile.close();
	return result;
}

std::string Properties::getProperty(std::string propType) {
	std::string result("");
	if (properties.size() <= 0) {return "";}
	for(unsigned int i=0; i < properties.size(); i++) {
		property prop = properties.at(i);
		if (prop.propType.compare(propType) == 0) {
			result = prop.propValue;
			break;
		}
	}
	return result;
}
