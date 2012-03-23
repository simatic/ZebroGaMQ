/**
 TCM: TOTEM Communication Middleware
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

 Developer(s): Denis Conan, Gabriel Adgeg
 */

package net.totem.gamelogic;

public class GameLogicState {
	
	public static final String PLAYER 		= "Player";
	public static final String GAME_MASTER 	= "Master";
	public static final String SPECTATOR 	= "Spectator";
	
	
	public String 	login;
	public String 	password;
	public String	role;
	public String 	gameName;
	public String 	gameInstanceName;
	public String 	virtualHost;
	public String 	exchangeName;
	public String 	observationKey;
	public int 		numberOfRetries;
	
	/**
	 * Create a GameLogicState.
	 * By default, GameLogicState is associated to a player application.
	 */
	public GameLogicState(){
		this.role = PLAYER;
	}
	
	public 	ChannelsManager 	channelsManager;
	private volatile boolean	connectionExited = false;
	
	/**
	 * Tells whether GameLogicState connection has exited or not.
	 * If channelsManager is not null, also tells if its channels have been closed.
	 * 
	 * @return true if GameLogicState has exited.
	 */
	public boolean hasConnectionExited(){
		return connectionExited;
	}
	
	
	/**
	 * Set the GameLogicState to the connectionExited state.
	 * If channelsManager is not null, closes its channels.
	 */
	public void connectionExit(){
		if(connectionExited == false){
			this.connectionExited = true;
			if(channelsManager != null){
				channelsManager.exit();
			}	
		}
	}
}
