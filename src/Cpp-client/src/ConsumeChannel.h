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

#ifndef CONSUMECHANNEL_H_
#define CONSUMECHANNEL_H_

#include "RabbitMQGameInstanceChannel.h"
#include "AMQPcpp.h"
#include "Action.h"
#include <iostream>
#include <vector>

class ConsumeChannel: public RabbitMQGameInstanceChannel {
private:
	bool rawActionsEnabled;
	std::vector<Action *> listsOfListOfActions;

	void startConsumeLoopThread(const GameLogicState* state);
	void consumeLoop(const GameLogicState* state);
	AMQPQueue* getQueingConsumer(const GameLogicState *state);

	void handleDelivery(AMQPMessage *&message, const GameLogicState* state);

	bool executeAction(const std::string nameKind,
			const std::string nameAction, const GameLogicState *state,
			const std::vector<std::string> header, const std::string body);

	bool executeRawAction(const GameLogicState *state,
			const std::vector<std::string> header, const std::string body);

	/*
	 * If state has not exited, this method:
	 * - print the exception message
	 * - close channel and connection
	 * - try to reconnect
	 * - return a new QueuingConsumer attached to the new channel.
	 *
	 * TODO QueueingConsumer, a particular JAVA RabbitMQ class, is used to get message. In amqpcpp, message is get through queue => so, let's change it by AMQPQueue
	 */
	AMQPQueue* newQueueingConsumer(const GameLogicState *state);

public:
	const static std::string RAW_ACTION_KIND;
	const static std::string RAW_ACTION_NAME;
	ConsumeChannel(const ChannelsManager* channelsManager,
				   const GameLogicState* state,
				   const std::vector<Action *> loai,
				   bool enableRawActions);
	void close();
	virtual ~ConsumeChannel();
};

#endif /* CONSUMECHANNEL_H_ */
