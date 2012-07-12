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

 Developer(s): Denis Conan, Gabriel Adgeg
 */

package zebrogamq.perf.j2se;

import java.io.IOException;

import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.Util;

public class MyGameLogicProtocol {

	/**
	 * Method called once the client receive a PingActionKind.ALL_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pingAllAction(final GameLogicState state, final String[] header,
			final String body) {
		String[] contents = body.split(",");
		Stat.addRecPingAll(body.length());
		Util.println(" ["+state.role+ " " + state.login + "] PingActionKind.ALL_ACTION time=" + contents[0] + " target=" + contents[1]);
		if (state.login.compareTo(contents[1]) == 0) {
			Util.println(" ["+state.role+ " " + state.login + "] I have to react");
			try {
				state.channelsManager.publish(header[0], state, PongActionKind.ALL_ACTION, contents[0]);
				Stat.addSentPongAll(contents[0].length());
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return null;
	}

	/**
	 * Method called once the client receive a PingActionKind.PLAYER_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pingPlayerAction(final GameLogicState state, final String[] header,
			final String body) {
		Util.println(" ["+state.role+ " " + state.login + "] PingActionKind.PLAYER_ACTION " + body);
		Stat.addRecPingPlayer(body.length());
		String[] contents = body.split(",");
		try {
			state.channelsManager.publish(header[0], state, PongActionKind.PLAYER_ACTION, contents[0]);
			Stat.addSentPongPlayer(contents[0].length());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Method called once the client receive a PingActionKind.SERVER_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pingServerAction(final GameLogicState state, final String[] header,
			final String body) {
		Util.println(" ["+state.role+ " " + state.login + "] PingActionKind.SERVER_ACTION " + body);
		Util.println(" ["+state.role+ " " + state.login + "] ***This should never happen : We exit!***");
		System.exit(1);
		return null;
	}

	/**
	 * Method called once the client receive a PongActionKind.ALL_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pongAllAction(final GameLogicState state, final String[] header,
			final String body) {
		String[] contents = body.split(",");
		Stat.addRecPongAll(body.length(), System.currentTimeMillis() - Long.parseLong(contents[0]));
		Util.println(" ["+state.role+ " " + state.login + "] PongActionKind.ALL_ACTION timer=" + contents[0]);
		return null;
	}
	
	/**
	 * Method called once the client receive a PongActionKind.PLAYER_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pongPlayerAction(final GameLogicState state, final String[] header,
			final String body) {
		String[] contents = body.split(",");
		Stat.addRecPongPlayer(body.length(), System.currentTimeMillis() - Long.parseLong(contents[0]));
		Util.println(" ["+state.role+ " " + state.login + "] PongActionKind.PLAYER_ACTION timer=" + contents[0]);
		return null;
	}
	
	/**
	 * Method called once the client receive a PongActionKind.SERVER_ACTION message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public static Object pongServerAction(final GameLogicState state, final String[] header,
			final String body) {
		String[] contents = body.split(",");
		Stat.addRecPongServer(body.length(), System.currentTimeMillis() - Long.parseLong(contents[0]));
		Util.println(" ["+state.role+ " " + state.login + "] PongActionKind.SERVER_ACTION timer=" + contents[0]);
		return null;
	}
}
