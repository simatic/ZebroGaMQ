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

#ifndef GAMELOGICSTATE_H_
#define GAMELOGICSTATE_H_
#include <string>

class ChannelsManager;

using namespace std;

class GameLogicState {
private:
	bool	connectionExited;
public:
	static const std::string GAME_MASTER;
	static const std::string SPECTATOR;
	static const std::string PLAYER;

	std::string	login = "";
	std::string	password = "";
	std::string role = "";
	std::string	gameName = "";
	std::string	instanceName = "";
	std::string	virtualHost = "";
	std::string	exchangeName = "";
	std::string	observationKey = "";
	int 		numberOfRetries = 0;
	ChannelsManager 	*channelsManager = nullptr;

	/**
	 * Create a GameLogicState.
	 * By default, GameLogicState is associated to a player application.
	 */
	GameLogicState();

	/**
	 * Tells whether GameLogicState connection has exited or not.
	 * If channelsManager is not null, also tells if its channels have been closed.
	 *
	 * @return true if GameLogicState has exited.
	 */
	bool hasConnectionExited() const;

	/**
	 * Set the GameLogicState to the connectionExited state.
	 * If channelsManager is not null, closes its channels.
	 */
	void connectionExit() const;

	void setConnectionExited(bool connectionExited);
	void setNumberOfRetries(int numberOfRetries) const;

	~GameLogicState();

	ChannelsManager* getChannelsManager() const;
};

#endif /* GAMELOGICSTATE_H_ */
