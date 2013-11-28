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

#include "GameLogicProtocol.h"
#include "ZebroGamqUtil.h"

#include <iostream>
#include <fstream>

using namespace std;

void terminateZMQ(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (header.size()==0) {
		if ( isTempFileUsed ) {
			filestr << "terminate action with null header message" << endl;
		} else {
			cout << "terminate action with null header message" << endl;
		}
	}
	if (body.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "terminate action with null body message" << endl;
		} else {
			cout << "terminate action with null body message" << endl;
		}
	}
	
	if ( isTempFileUsed ) {
		filestr << "Terminate received." << endl;
		filestr.close();
	} else {
		cout << "Terminate received." << endl;
	}
	gameLogicState.connectionExit();
	// TODO Completer en s'inspirant de la methode terminate dans GameLogicProtocol.java
	// Completer en reprenant le mecanisme de OptionalDelegationOfStandardActions : ne pas faire (trop compliqué)
}

void join(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (header.size()==0) {
		if ( isTempFileUsed ) {
			filestr << "join action with null header message" << endl;
		} else {
			cout << "join action with null header message" << endl;
		}
	}
	if (body.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "join action with null body message" << endl;
		} else {
			cout << "join action with null body message" << endl;
		}
	}
	
	if ( isTempFileUsed ) {
		filestr << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
			 	<< ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
		filestr.close();
	} else {
		cout << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
			 << ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
	}
	// TODO Completer en s'inspirant de la methode terminate dans GameLogicProtocol.java
	// Completer en reprenant le mecanisme de OptionalDelegationOfStandardActions : ne pas faire (trop compliqué)
}

void joinOK(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (header.size()==0) {
		if ( isTempFileUsed ) {
			filestr << "joinOK action with null header message" << endl;
		} else {
			cout << "joinOK action with null header message" << endl;
		}
	}
	if (body.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "joinOK action with null body message" << endl;
		} else {
			cout << "joinOK action with null body message" << endl;
		}
	}
	
	if ( isTempFileUsed ) {
		filestr << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
				<< ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
		filestr.close();
	} else {
		cout << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
			 << ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
	}
	// TODO Completer en s'inspirant de la methode terminate dans GameLogicProtocol.java
	// Completer en reprenant le mecanisme de OptionalDelegationOfStandardActions : ne pas faire (trop compliqué)
}

void participantsList(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (header.size()==0) {
		if ( isTempFileUsed ) {
			filestr << "participantsList action with null header message" << endl;
		} else {
			cout << "participantsList action with null header message" << endl;
		}
	}
	if (body.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "participantsList action with null body message" << endl;
		} else {
			cout << "participantsList action with null body message" << endl;
		}
	}
	
	if ( isTempFileUsed ) {
		filestr << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
				<< ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
		filestr.close();
	} else {
		cout << " [" << gameLogicState.role + " " << gameLogicState.login << "] Received "
			 << ZebroGamqUtil::vectorToString(header) << " / " << body << endl;
	}
	// TODO Completer en s'inspirant de la methode terminate dans GameLogicProtocol.java
	// Completer en reprenant le mecanisme de OptionalDelegationOfStandardActions : ne pas faire (trop compliqué)
}

