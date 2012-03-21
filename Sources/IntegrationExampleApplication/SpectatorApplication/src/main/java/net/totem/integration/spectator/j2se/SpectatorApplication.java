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

package net.totem.integration.spectator.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Vector;

import net.totem.gamelogic.Util;
import net.totem.gamelogic.spectator.ChannelsManager;
import net.totem.gamelogic.spectator.JoinAction;
import net.totem.gamelogic.spectator.SpectatorState;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class SpectatorApplication {private static SpectatorState state = null;

	public static void main(final String[] argv) throws java.io.IOException,
														SecurityException, 
														NoSuchMethodException {
		Util.setLogger(new Logger());
		if (argv.length != 5) {
			Util.println(" [Spectator] Please, only four strings"
					+ " are accepted "
					+ "<login> <password> <game name> <instance name>.");
			return;
		}
		boolean loadOK = loadProperties();
		if(loadOK){
			// instantiate SpectatorState
			state = new SpectatorState();
			state.login = argv[0];
			state.password = argv[1];
			state.gameName = argv[2];
			state.gameInstanceName = argv[3];
			state.observationKey = argv[4];
			if ((Util.getContentKeyAt(state.observationKey, "\\.", 0) == null)
					|| (Util.getContentKeyAt(state.observationKey, "\\.", 1) == null)
					|| (Util.getContentKeyAt(state.observationKey, "\\.", 2) == null)
					|| (Util.getContentKeyAt(state.observationKey, "\\.", 3) == null)) {
				Util.println(" [Specatator] Please, "
						+ "<observation key game play> "
						+ "must conform to the syntax "
						+ "<part1>.<part2>.<part3>.<part4>");
				return;
			}
			// execute the XMLRPC call
			Object answer = executeXMLRPCCall();
			boolean res = computeXMLRPCAnswer(answer);
			if(!res){
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
			params.add(state.observationKey);
			// execute the XML-RPC call
			answer = client.execute("joinSpectatorGameInstance", params);
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
					+ state.gameInstanceName + " for Spectator " + state.login);
			try {
				// Instantiate the channelsManager
				state.channelsManager = ChannelsManager.getInstance(state, MyListOfGameLogicActions.ListOfActionsMaps);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				state.channelsManager.publishToGameLogicServer(
						state,
						JoinAction.JOIN_SPECTATOR,
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
			Util.println("XMLRPC call has failed for Spectator " +state.login + 
					" in "+ state.gameName + "/" + state.gameInstanceName);
			Util.println(" [Spectator " + state.login + "] Exiting ");
			return false;
		}
	}
}
