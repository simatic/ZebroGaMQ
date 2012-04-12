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

package zebrogamq.integration.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import zebrogamq.gamelogic.ChannelsManager;
import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.JoinAction;
import zebrogamq.gamelogic.OptionalDelegationOfStandardActions;
import zebrogamq.gamelogic.PresenceAction;
import zebrogamq.gamelogic.Util;

public class GameLogicApplication {

	private static GameLogicState state = null;

	public static void main(final String[] argv) {
		Util.setLogger(new Logger());
		if (argv.length != 5 && argv.length != 6) {
			Util.println("Please, only five or six strings"
					+ " are accepted "
					+ "<login> <password> <role> <game name> <instance name> [<observation key>].");
			return;
		}
		boolean loadOK = loadProperties();
		if (loadOK) {
			// instantiate GameLogicState
			state = new GameLogicState();
			state.login = argv[0];
			state.password = argv[1];
			state.role = argv[2];
			state.gameName = argv[3];
			state.instanceName = argv[4];
			if (argv.length == 6) {
				state.observationKey = argv[5];
				if ((Util.getContentKeyAt(state.observationKey, "\\.", 0) == null)
						|| (Util.getContentKeyAt(state.observationKey, "\\.", 1) == null)
						|| (Util.getContentKeyAt(state.observationKey, "\\.", 2) == null)
						|| (Util.getContentKeyAt(state.observationKey, "\\.", 3) == null)) {
					Util.println("[" + state.role + "] Please, "
							+ "<observation key game play> "
							+ "must conform to the syntax "
							+ "<part1>.<part2>.<part3>.<part4>");
					return;
				}
			}
			// execute the XMLRPC call
			boolean loggedIn = executeXMLRPCLogin();
			if (loggedIn) {
				initChannelsManager();
				// launch the participant list thread (except for spectators
				// applications
				if (!state.role.equals(GameLogicState.SPECTATOR)) {
					startParticipantListThread();
				}
			} else {
				Util.println("Bad XML-RPC answer.");
			}
		} else {
			Util.println("Properties files have not been load.");
		}
	}

	private static boolean loadProperties() {
		boolean loadOK = false;
		Properties rabbitMQProperties = new Properties();
		String rabbitMQConfigFileName = System
				.getProperty("rabbitmq.config.file");
		InputStream in = Util.class.getClassLoader().getResourceAsStream(
				rabbitMQConfigFileName);
		if (in == null) {
			Util.println("RabbitMQ configuration file \""
					+ rabbitMQConfigFileName + "\" not found");
			throw new RuntimeException();
		}
		try {
			rabbitMQProperties.load(in);
			Util.setRabbitMQProperties(rabbitMQProperties);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Properties xmlrpcProperties = new Properties();
		String xmlrpcConfigFileName = System.getProperty("xmlrpc.config.file");
		in = Util.class.getClassLoader().getResourceAsStream(
				xmlrpcConfigFileName);
		if (in == null) {
			Util.println("XMLRPC configuration file \"" + xmlrpcConfigFileName
					+ "\" not found");
			throw new RuntimeException();
		}
		try {
			xmlrpcProperties.load(in);
			Util.setXMLRPCProperties(xmlrpcProperties);
			loadOK = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loadOK;
	}

	private static boolean executeXMLRPCLogin() {
		boolean res = false;
		if (state.role.equals(GameLogicState.GAME_MASTER)) {
			Util.println("[" + state.role + " " + state.login
					+ "] creating instance " + state.gameName + "/"
					+ state.instanceName);
			res = XMLRPCLogin.createAndJoinGameInstance(state.login,
					state.password, state.gameName, state.instanceName);
		} else if (state.observationKey != null) {
			Util.println("[" + state.role + " " + state.login
					+ "] joining instance " + state.gameName + "/"
					+ state.instanceName + " with observation key "
					+ state.observationKey);
			res = XMLRPCLogin.joinGameInstance(state.login, state.password,
					state.gameName, state.instanceName, state.observationKey);
		} else {
			Util.println("[" + state.role + " " + state.login
					+ "] joining instance " + state.gameName + "/"
					+ state.instanceName);
			res = XMLRPCLogin.joinGameInstance(state.login, state.password,
					state.gameName, state.instanceName);
		}
		return res;
	}

	/*
	 * This method successively:
	 * - instantiate the channelsManager of the state in order to 
	 *   start the consumption and to enable publication of messages.
	 * - publish a JOIN message to the GameLogicServer. By default,
	 *   on the reception of this message, there is only a message
	 *   displayed. If you want to define your own behavior on the 
	 *   reception of this message, please refer to the javadoc of 
	 *   the class OptionalDelegationOfStandardActions.
	 *   (http://simatic.github.com/ZebroGaMQ/doc/javadoc/index.html)
	 */
	private static void initChannelsManager() {
		try {
			// Instantiate the channelsManager
			state.channelsManager = ChannelsManager.getInstance(state,
					MyListOfGameLogicActions.ListOfActionsMaps);
			// Publish a Join message
			String content = state.login + "," + state.gameName + ","
					+ state.instanceName;
			state.channelsManager.publishToGameLogicServer(state,
					JoinAction.JOIN, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void startParticipantListThread() {
		new Thread() {
			public void run() {
				while (!state.hasConnectionExited()) {
					try {
						state.channelsManager.publishToGameLogicServer(state,
								PresenceAction.ASK_PARTICIPANTS_LIST, " ");
					} catch (IOException e) {
						e.printStackTrace();
					}
					// wait for a while
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						Util.println(" [Player " + state.login
								+ "] Thread sleep was interrupted");
					}
				}
			}
		}.start();
	}
}
