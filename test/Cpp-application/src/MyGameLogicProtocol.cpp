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

#include "MyGameLogicProtocol.h"
#include <iostream>
#include <fstream>
#include "ZebroGamqUtil.h"
#include "GameLogicApplication.h"
#include "ChannelsManager.h"
#include <stdlib.h>

using namespace std;

void myFirstAction(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
    ofstream filestr;
    bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
    if ( isTempFileUsed ) {
        filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
        filestr << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFirstAction = " << body << endl;
        filestr.close();
    } else {
        cout << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFirstAction = " << body << endl;
    }
}

void mySecondAction(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
    ofstream filestr;
    bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
    if ( isTempFileUsed ) {
        filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
        filestr << " [" << gameLogicState.role << " " << gameLogicState.login << "] mySecondAction = " << body << endl;
        filestr.close();
    } else {
        cout << " [" << gameLogicState.role << " " << gameLogicState.login << "] mySecondAction = " << body << endl;
    }
}

void myThirdAction(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
    ofstream filestr;
    bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
    if ( isTempFileUsed ) {
        filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
        filestr << " [" << gameLogicState.role << " " << gameLogicState.login << "] myThirdAction = " << body << endl;
        filestr.close();
    } else {
        cout << " [" << gameLogicState.role << " " << gameLogicState.login << "] myThirdAction = " << body << endl;
    }
}

void myFourthAction(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
    ofstream filestr;
    bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
    if ( isTempFileUsed ) {
        filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
        filestr << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFourthAction = " << body << endl;
        filestr.close();
    } else {
        cout << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFourthAction = " << body << endl;
    }
}

void myFifthAction(GameLogicState const& gameLogicState, vector<string> const& header, string const& body) {
    ofstream filestr;
    bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
    if ( isTempFileUsed ) {
        filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
        filestr << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFifthAction = " << body << endl;
        filestr.close();
    } else {
        cout << " [" << gameLogicState.role << " " << gameLogicState.login << "] myFifthAction = " << body << endl;
    }
}

