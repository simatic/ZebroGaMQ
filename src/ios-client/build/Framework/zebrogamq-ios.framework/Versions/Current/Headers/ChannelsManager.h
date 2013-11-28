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

#ifndef CHANNELSMANAGER_H_
#define CHANNELSMANAGER_H_

#include <vector>
#include "GameLogicState.h"
#include "Action.h"
#include "PublishChannel.h"
#include "HeartbeatChannel.h"
#include "ConsumeChannel.h"

class ChannelsManager {
private:
	static const int 		CONNECTION_ESTABLISHMENT_TIMEOUT; // in ms
	ConsumeChannel 	*consumeChannel = nullptr;
	PublishChannel 	*publishChannel = nullptr;
	HeartbeatChannel 	*heartbeatChannel = nullptr;

	static void initGameLogicState(GameLogicState* state);

public:
	/**
	 * Instantiate the ChannelsManager, required for the consumption and the publication of messages.
	 * As soon as this method is called, the consumption of messages is automatically started.
	 *
	 * @param state the GameLogicState of the player
	 * @param loai	the list of Action
	 * @return		the ChannelsManager to use to publish messages.
	 */
	static ChannelsManager *getInstance(GameLogicState* state,
										const std::vector<Action *> loai);

	static ChannelsManager *getInstance(GameLogicState* state,
										const std::vector<Action *> loai,
										bool enableRawAction);

	ChannelsManager(GameLogicState *state,
						const std::vector<Action *> loai,
						bool enableRawAction);

	void exit();

	/**
	 * Publish a message to a specific recipient (designed by its login)
	 * and specify the action that should be triggered by the recipient on
	 * the reception of this message.
	 *
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message
	 *
	 */
	void publish(const std::string consumer, const GameLogicState* state,
						const Action* action, const std::string message);

	void publish(const std::string consumer, const GameLogicState* state,
						const string action, const std::string message);

	/**
	 * Publish a message to all the players and to the Game Logic server,
	 * and specify the action that should be triggered by recipients on
	 * the reception of this message.
	 *
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message
	 *
	 */
	void publishToAll(const GameLogicState* state,
					  const Action* action, const std::string message);

	void publishToAll(const GameLogicState* state,
					  const string action, const std::string message);

	/**
	 * Publish a message to the Game Logic server, and specify the action
	 * that it should triggered on the reception of this message.
	 *
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message
	 *
	 */
	void publishToGameLogicServer(const GameLogicState *state,
								  const Action* action, const std::string message);
	void publishToGameLogicServer(const GameLogicState *state,
								  const string action, const std::string message);

	const ConsumeChannel* getConsumeChannel() {
		return consumeChannel;
	}

	void setConsumeChannel(ConsumeChannel* consumeChannel) {
		this->consumeChannel = consumeChannel;
	}

	const HeartbeatChannel* getHeartbeatChannel() {
		return heartbeatChannel;
	}

	void setHeartbeatChannel(HeartbeatChannel* heartbeatChannel) {
		this->heartbeatChannel = heartbeatChannel;
	}

	const PublishChannel* getPublishChannel() {
		return publishChannel;
	}

	void setPublishChannel(PublishChannel* publishChannel) {
		this->publishChannel = publishChannel;
	}
};

#endif /* CHANNELSMANAGER_H_ */
