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
#include "GameLogicApplication.h"
#include "ZebroGamqUtil.h"

using namespace std;

int main(int argc, char** argv) {
	if ((argc != 6) && (argc != 7)) {
		cout << "Please, only six strings are accepted : <login> <password> <role> <game name> <instance name> [<observation key>]." << endl;
		return 0;
	}

	bool loadOK = GameLogicApplication::loadProperties();
	if (loadOK) {
		// instantiate GameLogicState
		GameLogicApplication::state = new GameLogicState();
		GameLogicApplication::state->login = argv[1];
		GameLogicApplication::state->password = argv[2];
		GameLogicApplication::state->role = argv[3];
		GameLogicApplication::state->gameName = argv[4];
		GameLogicApplication::state->instanceName = argv[5];

        if (argc == 7) {
            GameLogicApplication::state->observationKey = argv[6];
            if ( (ZebroGamqUtil::getContentKeyAt(GameLogicApplication::state->observationKey, ".", 0) == "")
			  || (ZebroGamqUtil::getContentKeyAt(GameLogicApplication::state->observationKey, ".", 1) == "")
			  || (ZebroGamqUtil::getContentKeyAt(GameLogicApplication::state->observationKey, ".", 2) == "")
			  || (ZebroGamqUtil::getContentKeyAt(GameLogicApplication::state->observationKey, ".", 3) == "") ) {
                cout << "[" << GameLogicApplication::state->role << "] Please, "
							<< "<observation key game play> "
							<< "must conform to the syntax "
							<< "<part1>.<part2>.<part3>.<part4>" << endl;
                return 0;
            }
        }

		// execute the XMLRPC call
		bool loggedIn = GameLogicApplication::executeXMLRPCLogin();
		if (loggedIn) {
			GameLogicApplication::initChannelsManager();
            // launch the participant list thread (except for spectators
            // applications
            if ( GameLogicApplication::state->role.compare(GameLogicState::SPECTATOR) != 0 ) {
                GameLogicApplication::startParticipantListThread();
            }
		} else {
			cout << "Bad XML-RPC answer." << endl;
		}
	} else {
		cout << "Properties files have not been loaded." << endl;
	}
	sleep(1000000);
	return 0;
}
