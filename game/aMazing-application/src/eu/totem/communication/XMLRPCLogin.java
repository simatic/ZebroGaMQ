package eu.totem.communication;

import java.net.URI;
import java.util.Arrays;

import net.totem.gamelogic.Util;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

public class XMLRPCLogin {
	
	private final static URI GAME_SERVER_URI;
	private final static int MAX_RETRY;
	
	static{
		GAME_SERVER_URI = URI.create("http://"
				+ Util.getXMLRPCProperties()
				.getProperty("gameServerXMLRPCHost")
				+ ":"
				+ Util.getXMLRPCProperties()
				.getProperty("gameServerXMLRPCPort"));
		MAX_RETRY = Integer.valueOf(Util.getXMLRPCProperties().getProperty("maxRetry"));
	}
	
	
	public static boolean createAndJoinGameInstance(String login, String password, String gameName, String gameInstanceName) {
		boolean res = false;
		Object answer = null;
		answer = executeXmlRpcCall("createAndJoinGameInstance", login, password, gameName, gameInstanceName);
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer).equals(Boolean.TRUE)) {
			Util.println("XMLRPC call createAndJoinGameInstance has succeded for " +login + 
						" in "+ gameName + "/" + gameInstanceName);
			res = true;
		}
		return res;
	}
	
	
	public static boolean joinGameInstance(String login, String password, String gameName, String gameInstanceName) {
		boolean res = false;
		Object answer = null;
		answer = executeXmlRpcCall("joinGameInstance", login, password, gameName, gameInstanceName);
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer).equals(Boolean.TRUE)) {
			Util.println("XMLRPC call joinGameInstance has succeded for " +login + 
						" in "+ gameName + "/" + gameInstanceName);
			res = true;
		}
		return res;
	}
	
	
	public static boolean joinGameInstance(String login, String password, String gameName, String gameInstanceName, String observationKey) {
		boolean res = false;
		Object answer = null;
		answer = executeXmlRpcCall("joinGameInstance", login, password, gameName, gameInstanceName, observationKey);
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer).equals(Boolean.TRUE)) {
			Util.println("XMLRPC call joinGameInstance has succeded for " +login + 
						" in "+ gameName + "/" + gameInstanceName);
			res = true;
		}
		return res;
	}
	
	
	public static String[] listGameInstances(String gameName) {
		String[] res = null;
		Object answer = null;
		answer = executeXmlRpcCall("listGameInstances", gameName);
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Object[])) {
			Util.println("The procedure didn't return an Object array.");
			return res;
		} else {
			Util.println("XMLRPC call to listGameInstances "+ gameName +" has succeded.");
			Object[] objectArray = (Object[])answer;
			res = Arrays.asList(objectArray).toArray(new String[objectArray.length]);
		}
		return res;
	}
	
	
	public static boolean terminateGameInstance(String gameName, String gameInstanceName) {
		boolean res = false;
		Object answer = null;
		answer = executeXmlRpcCall("terminateGameInstance", gameName, gameInstanceName);
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer).equals(Boolean.TRUE)) {
			Util.println("XMLRPC call to terminateGameInstance "+ gameName + "/" + gameInstanceName+" has succeded.");
			res = true;
		}
		return res;
	}
	
	
	public static boolean terminate() {
		boolean res = false;
		Object answer = null;
		answer = executeXmlRpcCall("terminate");
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer).equals(Boolean.TRUE)) {
			Util.println("XMLRPC call to terminate has succeeded ");
			res = true;
		}
		return res;
	}
	
	
	
	private static Object executeXmlRpcCall(String method, String... params){
		Object answer = null;
		XMLRPCClient client = new XMLRPCClient(GAME_SERVER_URI);
		for (int i = 0; i < MAX_RETRY; i++) {
			try {
				answer = client.callEx(method, params);
				break;
			} catch (XMLRPCException e) {
				e.printStackTrace();
			}
			// sleep 1 second before retrying
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return answer;
	}
}
