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

package net.totem.gamelogic.gamemaster;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MissedHeartbeatException;

import net.totem.gamelogic.Util;

public class ChannelsManager {
	
	private static final int 		CONNECTION_ESTABLISHMENT_TIMEOUT 	= 1000; // in ms
	
	private final ConnectionFactory	factory;
	private final ConsumeChannel 	consumeChannel;
	private final PublishChannel 	publishChannel;
	private final HeartbeatChannel 	heartbeatChannel;
	
	
	public static ChannelsManager getInstance(	final GameMasterState state,
												final List<Map<String, ? extends GameMasterActionInterface>> loai) 
												throws IOException{
		return new ChannelsManager(state, loai,false);
	}
	
	
	public static ChannelsManager getInstance(	final GameMasterState state,
			final List<Map<String, ? extends GameMasterActionInterface>> loai,
			boolean enableRawAction) 
			throws IOException{
return new ChannelsManager(state, loai,true);
}
	
	
	private ChannelsManager(final GameMasterState state,
							final List<Map<String, ? extends GameMasterActionInterface>> loai,
							boolean enableRawAction) 
							throws IOException {
		// initialize state
		initGameMasterState(state);
		// initialize connection factory
		factory = getConnectionFactory(state);
		// instantiate attributes
		heartbeatChannel = new HeartbeatChannel(this, state);
		consumeChannel = new ConsumeChannel(this, state, loai, enableRawAction);
		publishChannel = new PublishChannel(this, state);
	}
	
	
	private void initGameMasterState(GameMasterState state){
		if (state.gameName == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to create"
							+ " null game name");
		}
		if (state.gameInstanceName == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to create"
							+ " null game instance name");
		}
		state.virtualHost = Util.getRabbitMQProperties().getProperty(
				"virtualHostSeparator")
				+ state.gameName
				+ Util.getRabbitMQProperties().getProperty(
						"virtualHostSeparator") + state.gameInstanceName;
		state.exchangeName = Util.getRabbitMQProperties().getProperty(
				"gameLogicExchangeName");
		if (state.exchangeName == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to create"
							+ " null exchange name");
		}
		if (Util.getRabbitMQProperties().getProperty("gameLogicBrokerHost") == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to connect to"
							+ " null game logic broker host");
		}
		if (state.login == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to connect with"
							+ " null login");
		}
		if (state.password == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to connect with"
							+ " null password");
		}
	}
	
	
	private ConnectionFactory getConnectionFactory(final GameMasterState state){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Util.getRabbitMQProperties().getProperty(
				"gameLogicBrokerHost"));
		factory.setUsername(state.login);
		factory.setPassword(state.password);
		factory.setVirtualHost(state.virtualHost);
		factory.setPort(Integer.valueOf(Util
				.getRabbitMQProperties()
				.getProperty("gameLogicBrokerPort",
						String.valueOf(factory.getPort())))); 
		factory.setConnectionTimeout(CONNECTION_ESTABLISHMENT_TIMEOUT);
		// set the heartbeat value for the amqp connections
		// to avoid the keeping of obsolete consumers
		factory.setRequestedHeartbeat(Integer.valueOf(Util
				.getRabbitMQProperties()
				.getProperty("amqpConnectionHeartbeat",
						String.valueOf(factory.getRequestedHeartbeat()))));
		return factory;
	}
	
	/*
	 * Create a new TCP/IP connection to broker.
	 */
	Connection newConnection() throws IOException{
		synchronized (factory) {
			return factory.newConnection();
		}
	}
	
	
	void exit() {
		Util.println(" [Master] exiting: closing channels and connections");
		heartbeatChannel.close();
		consumeChannel.close();
		publishChannel.close();
	}
	
	/**
	 * Publish a message to a specific recipient (designed by its login)
	 * and specify the action that should be triggered by the recipient on 
	 * the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publish(final String consumer, final GameMasterState state,
						final GameMasterActionInterface action, final String message)
						throws IOException {
		publishChannel.publish(consumer, state, action, message);
	}
	
	
	/**
	 * Publish a message to a specific recipient (designed by its login)
	 * and specify the action that should be triggered by the recipient on 
	 * the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publish(final String consumer, final GameMasterState state,
						final String action, final String message)
						throws IOException {
		publishChannel.publish(consumer, state, action, message);
	}
	
	
	/**
	 * Publish a message to all the players and to the Game Logic server,
	 * and specify the action that should be triggered by recipients on 
	 * the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToAll(	final GameMasterState state,
								final GameMasterActionInterface action, final String message)
								throws IOException {
		publishChannel.publishToAll(state, action, message);
	}
	
	
	/**
	 * Publish a message to all the players and to the Game Logic server,
	 * and specify the action that should be triggered by recipients on 
	 * the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToAll(	final GameMasterState state,
								final String action, final String message)
								throws IOException {
		publishChannel.publishToAll(state, action, message);
	}
	
	
	/**
	 * Publish a message to the Game Logic server, and specify the action 
	 * that it should triggered on the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master 
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToGameLogicServer(	final GameMasterState state,
											final GameMasterActionInterface action, final String message)
											throws IOException {
		publishChannel.publishToGameLogicServer(state, action, message);
	}

	
	/**
	 * Publish a message to the Game Logic server, and specify the action 
	 * that it should triggered on the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameMasterState of the game master 
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToGameLogicServer(	final GameMasterState state,
											final String action, final String message)
											throws IOException {
		publishChannel.publishToGameLogicServer(state, action, message);
	}
}
