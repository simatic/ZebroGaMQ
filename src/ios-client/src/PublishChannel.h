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

 Developer(s): Michel SIMATIC, LE Van Hung
 */

#ifndef PUBLISHCHANNEL_H_
#define PUBLISHCHANNEL_H_

#include "RabbitMQGameInstanceChannel.h"
class ChannelsManager;
class GameLogicState;
class Action;

class PublishChannel: public RabbitMQGameInstanceChannel {
private:
	std::string routingKeyRootGameInstanceServer;
	std::string routingKeyRootAll;
public:
	PublishChannel(const ChannelsManager* channelsManager, const GameLogicState* state);

	void publish(const std::string consumer, const GameLogicState *state,
			const std::string action, const std::string message);

	void publish(const std::string consumer, const GameLogicState *state,
				const Action* action, const std::string message);

	void publishToAll(const GameLogicState *state,
				const Action* action, const string message);

	void publishToAll(const GameLogicState *state,
					const string action, const string message);

	void publishToGameLogicServer(const GameLogicState *state,
				const Action* action, const std::string message);

	void publishToGameLogicServer(const GameLogicState *state,
			const string action, const std::string message);

	virtual ~PublishChannel();
	void close();
};

#endif /* PUBLISHCHANNEL_H_ */
