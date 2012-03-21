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

package net.totem.integration.player.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Vector;
import net.totem.gamelogic.Util;
import net.totem.gamelogic.player.ChannelsManager;
import net.totem.gamelogic.player.JoinAction;
import net.totem.gamelogic.player.PlayerState;
import net.totem.gamelogic.player.PresenceAction;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class PlayerApplication {
	
	
	private static PlayerState state = null;

	public static void main(final String[] argv) throws java.io.IOException,
														SecurityException, 
														NoSuchMethodException {
		Util.setLogger(new Logger());
		if (argv.length != 4) {
			Util.println(" [Player] Please, only four strings"
					+ " are accepted "
					+ "<login> <password> <game name> <instance name>.");
			return;
		}
		boolean loadOK = loadProperties();
		if(loadOK){
			// instantiate PlayerState
			state = new PlayerState();
			state.login = argv[0];
			state.password = argv[1];
			state.gameName = argv[2];
			state.gameInstanceName = argv[3];
			// execute the XMLRPC call
			Object answer = executeXMLRPCCall();
			boolean res = computeXMLRPCAnswer(answer);
			if(res){
				// launch the participant list thread
				startParticipantListThread();
			} else{
				Util.println("Bad XML-RPC answer.");
			}
		}else{
			Util.println("Properties files have not been load.");
		}
	}
	
	
	private static boolean loadProperties(){
		boolean loadOK = false;
		Properties rabbitMQProperties = new Properties();
		String rabbitMQConfigFileName = System
				.getProperty("rabbitmq.config.file");
		InputStream in = Util.class.getClassLoader().getResourceAsStream(
				rabbitMQConfigFileName);
		if (in == null) {
			Util.println("RabbitMQ configuration file \""
					+ rabbitMQConfigFileName + "\" not found");
			System.exit(1);
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
			System.exit(1);
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
	
	
	private static Object executeXMLRPCCall() {
		Object answer = null;
		XmlRpcClient client;
		try {
			client = new XmlRpcClient("http://"
					+ Util.getXMLRPCProperties()
							.getProperty("gameServerXMLRPCHost")
					+ ":"
					+ Util.getXMLRPCProperties()
							.getProperty("gameServerXMLRPCPort"));
			
			Vector<String> params = new Vector<String>();
			params.add(state.login);
			params.add(state.password);
			params.add(state.gameName);
			params.add(state.gameInstanceName);
			// execute the XML-RPC call
			answer = client.execute("joinPlayerGameInstance", params);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	
	private static boolean computeXMLRPCAnswer(Object answer) {
		boolean res = false;
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer) == Boolean.TRUE) {
			Util.println("Game server created " + state.gameName + "/"
					+ state.gameInstanceName + " for Player " + state.login);
			try {
				// Instantiate the channelsManager
				state.channelsManager = ChannelsManager.getInstance(state, MyListOfGameLogicActions.ListOfActionsMaps);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				state.channelsManager.publishToGameLogicServer(
						state,
						JoinAction.JOIN_PLAYER,
						state.login
						+ Util.getRabbitMQProperties().getProperty(
						"bodySeparator")
						+ state.gameName
						+ Util.getRabbitMQProperties().getProperty(
						"bodySeparator")
						+ state.gameInstanceName);
				res = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		} else {
			Util.println("XMLRPC call has failed for player " +state.login + 
					" in "+ state.gameName + "/" + state.gameInstanceName);
			Util.println(" [Player " + state.login + "] Exiting ");
			return false;
		}
	}
	
	
	private static void startParticipantListThread(){
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
