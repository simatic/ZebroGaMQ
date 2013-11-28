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

#include "GameLogicState.h"
#include "ChannelsManager.h"

const std::string GameLogicState::GAME_MASTER 	= "Master";
const std::string GameLogicState::SPECTATOR 	= "Spectator";
const std::string GameLogicState::PLAYER 		= "Player";

/**
 * Create a GameLogicState.
 * By default, GameLogicState is associated to a player application.
 */
GameLogicState::GameLogicState() {
	this->role = PLAYER;
	this->numberOfRetries = 0;
	channelsManager = nullptr;
	connectionExited = false;
}

ChannelsManager* GameLogicState::getChannelsManager() const {
	return channelsManager;
}

/**
 * Tells whether GameLogicState connection has exited or not.
 * If channelsManager is not null, also tells if its channels have been closed.
 *
 * @return true if GameLogicState has exited.
 */
bool GameLogicState::hasConnectionExited() const {
	return connectionExited;
}


/**
 * Set the GameLogicState to the connectionExited state.
 * If channelsManager is not null, closes its channels.
 */
void GameLogicState::connectionExit() const {
	if(connectionExited == false){
		if (channelsManager != nullptr) {
			channelsManager->exit();
		}
	}
}

void GameLogicState::setConnectionExited(bool connectionExited) {
	this->connectionExited = connectionExited;
}

void GameLogicState::setNumberOfRetries(int numberOfRetries) const {
	// TODO Check it is OK or not if numberOfRetries is not reset
	//this->numberOfRetries = numberOfRetries;
}
