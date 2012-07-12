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
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.lang.StringBuffer;

import zebrogamq.gamelogic.ChannelsManager;
import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.JoinAction;
import zebrogamq.gamelogic.OptionalDelegationOfStandardActions;
import zebrogamq.gamelogic.PresenceAction;
import zebrogamq.gamelogic.Util;

public class GameLogicApplication {

	private static MyGameLogicState state = null;
	private static MyOptionalDelegationOfStandardActions modosa = null;
	private static Random rand = null;

	public static void main(final String[] argv) {
		//Util.setLogger(new Logger());
		if (argv.length != 8) {
			Util.println("Please, only eight strings"
					+ " are accepted "
					+ "<login> <password> <role> <game name> <instance name> <nb msgs> <millisec between msg> <payload size>.");
			return;
		}
		boolean loadOK = loadProperties();
		if (loadOK) {
			// instantiate GameLogicState
			state = new MyGameLogicState();
			state.login = argv[0];
			state.password = argv[1];
			state.role = argv[2];
			state.gameName = argv[3];
			state.instanceName = argv[4];
			state.nbMsgs = Integer.parseInt(argv[5]);
			state.waitBetweenMsgs = Integer.parseInt(argv[6]);
			state.payloadSize = Integer.parseInt(argv[7]);
			
			rand = new Random(System.currentTimeMillis());
			// execute the XMLRPC call
			boolean loggedIn = executeXMLRPCLogin();
			if (loggedIn) {
				initChannelsManager();
				// launch the participant list thread (except for spectators
				// applications
				if (!state.role.equals(GameLogicState.SPECTATOR)) {
					startParticipantMsgThread();
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

	private static boolean executeXMLRPCTerminateGameInstance() {
		boolean res = false;
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
	 *   (http://simatic.github.com/ZebroGaMQ/doc/javadoc/zebrogamq/gamelogic/OptionalDelegationOfStandardActions.html)
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
		modosa = new MyOptionalDelegationOfStandardActions();
		OptionalDelegationOfStandardActions.setInstance(modosa);
	}

	private static String randomParticipant(){
		// Participants is structured as follows: {'participant_1': 'Thu Jul 12 10:45:59 CEST 2012', 'participant_2': 'Thu Jul 12 10:45:57 CEST 2012'}
		String participantsWithoutBrackets = modosa.participants.substring(1, modosa.participants.length()-1);
		String [] participantsArray = participantsWithoutBrackets.split(",");
		String participantWithDate = participantsArray[rand.nextInt(participantsArray.length)].trim();
		String [] part = participantWithDate.split("'");
		return part[1];
	}
	
	private static void startParticipantMsgThread() {
		new Thread() {
			public void run() {
				int sentMsgs = 0;
				StringBuffer sb = new StringBuffer(state.payloadSize);
				for (int i = 0; i < state.payloadSize; i++) {
					sb.append('A');					
				}
				long time = System.currentTimeMillis();
				String s;
				String consumer;
				while (!state.hasConnectionExited() && (sentMsgs < state.nbMsgs)) {
					time = System.currentTimeMillis();
					s = time + ",";
					try {
						switch (sentMsgs%4) {
						case 0:
							state.channelsManager.publishToGameLogicServer(state,
									PresenceAction.ASK_PARTICIPANTS_LIST, "");
							Stat.addSentPingServer(0);
							break;
						case 1:
							consumer = randomParticipant(); 
							s += consumer + ",";
							if (s.length() < state.payloadSize)
								s += sb.substring(s.length());
							state.channelsManager.publishToAll(state,
									PingActionKind.ALL_ACTION, s);
							Stat.addSentPingAll(s.length());
							break;
						case 2:
							consumer = randomParticipant(); 
							if (s.length() < state.payloadSize)
								s += sb.substring(s.length());
							state.channelsManager.publish(consumer, state,
									PingActionKind.PLAYER_ACTION, s);							
							Stat.addSentPingPlayer(s.length());
							break;
						default:
							if (s.length() < state.payloadSize)
								s += sb.substring(s.length());
							state.channelsManager.publishToGameLogicServer(state,
									PingActionKind.SERVER_ACTION, s);
							Stat.addSentPingServer(s.length());
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					sentMsgs += 1;
					// wait for a while
					try {
						Thread.sleep(state.waitBetweenMsgs);
					} catch (InterruptedException e) {
						Util.println(" [Player " + state.login
								+ "] Thread sleep was interrupted");
					}
				}
				// If we are the GAME_MASTER, we terminate the Game instance
				if (state.role.equals(GameLogicState.GAME_MASTER)) {
					Util.println("[" + state.role + " " + state.login
							+ "] terminating instance " + state.gameName + "/"
							+ state.instanceName);
					if (!XMLRPCLogin.terminateGameInstance(state.gameName, state.instanceName)){
						Util.println("***Problem with XMLRPCLogin.terminateGameInstance: Must exit!***");
						Stat.results();
						System.exit(1);				
					}
				}
				// We could do a System.exit(0) here, but it is more interesting to do it in terminate method of 
				// MyOptionalDelegationOfStandardActions. By doing so, all participants stop when the Game_MASTER
				// terminates
				//System.exit(0);				
			}
		}.start();
	}
}
