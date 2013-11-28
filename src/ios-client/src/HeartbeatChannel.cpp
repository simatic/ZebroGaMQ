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

#include "HeartbeatChannel.h"
#include "PresenceAction.h"
#include "ZebroGamqUtil.h"
#include "Properties.h"
#include <thread>
#include <chrono>
#include <ctime>
#include <stdlib.h>

// TODO regarder si heartbeatThread ne pourrait pas être une méthode d'instance
void heartbeatThread(HeartbeatChannel *heartbeatChannel, const GameLogicState *state, const long heartbeatPeriod){
	while (!state->hasConnectionExited()) {
		time_t t = time(0);
		struct tm * now = localtime( & t );
		char buf[30];
		sprintf(buf, "%d-%d-%d", now->tm_mday, (now->tm_mon + 1), (now->tm_year + 1900));
		heartbeatChannel->publishToGameLogicServer(state, HEARTBEAT, std::string(buf));
		usleep(heartbeatPeriod*1000);
	}
}

HeartbeatChannel::HeartbeatChannel(const ChannelsManager *channelsManager, const GameLogicState *state) : PublishChannel(channelsManager, state) {
	long heartbeatPeriod = loadHeartbeatPeriod(state);
	if(heartbeatPeriod != 0){
		std::thread t(heartbeatThread, this, state, heartbeatPeriod);
		t.detach();
	}
}

long HeartbeatChannel::loadHeartbeatPeriod(const GameLogicState *state){
	std::string heartbeatPeriod = ZebroGamqUtil::getRabbitMQProperties()->getProperty("heartbeatPeriod");
	return atol(heartbeatPeriod.c_str()) * 1000;
}

void HeartbeatChannel::close(){}

HeartbeatChannel::~HeartbeatChannel() {
	// TODO Auto-generated destructor stub
}

