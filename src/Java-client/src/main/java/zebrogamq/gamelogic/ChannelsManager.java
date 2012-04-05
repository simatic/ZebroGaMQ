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

package zebrogamq.gamelogic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MissedHeartbeatException;

public class ChannelsManager {
	
	private static final int 		CONNECTION_ESTABLISHMENT_TIMEOUT 	= 1000; // in ms
	
	private final ConnectionFactory	factory;
	private final ConsumeChannel 	consumeChannel;
	private final PublishChannel 	publishChannel;
	private final HeartbeatChannel 	heartbeatChannel;
	
	/**
	 * Instantiate the ChannelsManager, required for the consumption and the publication of messages.
	 * As soon as this method is called, the consumption of messages is automatically started.
	 * 
	 * @param state the GameLogicState of the player
	 * @param loai	the list of GameLogicAction 
	 * @return		the ChannelsManager to use to publish messages.
	 * @throws IOException if an exception occurs during the connection to the RabbitMQ broker.
	 */
	
	public static ChannelsManager getInstance(	final GameLogicState state,
												final List<Map<String, ? extends GameLogicActionInterface>> loai) 
												throws IOException{
		return new ChannelsManager(state, loai,false);
	}
	
	
	/**
	 * Instantiate the ChannelsManager, required for the consumption and the publication of messages.
	 * As soon as this method is called, the consumption of messages is automatically started.
	 * If the action kind and the action name of the messages consumed are not registered in the list of GameLogicAction,
	 * setting the boolean enableRawAction to true allow to trigger a generic raw action, instead of raising an 
	 * ActionInvocationException.
	 * 
	 * @param state the GameLogicState of the player
	 * @param loai	the list of GameLogicAction 
	 * @param enableRawAction to enable the use of a generic raw action.
	 * @return		the ChannelsManager to use to publish messages.
	 * @throws IOException if an exception occurs during the connection to the RabbitMQ broker.
	 */
	public static ChannelsManager getInstance(	final GameLogicState state,
												final List<Map<String, ? extends GameLogicActionInterface>> loai,
												boolean enableRawAction) 
												throws IOException{
		return new ChannelsManager(state, loai,true);
	}
	
	
	private ChannelsManager(final GameLogicState state,
							final List<Map<String, ? extends GameLogicActionInterface>> loai,
							boolean enableRawAction) 
							throws IOException {
		// initialize state
		initGameLogicState(state);
		// initialize connection factory
		factory = getConnectionFactory(state);
		// instantiate attributes
		heartbeatChannel = new HeartbeatChannel(this, state);
		consumeChannel = new ConsumeChannel(this, state, loai, enableRawAction);
		publishChannel = new PublishChannel(this, state);
	}
	
	
	private void initGameLogicState(GameLogicState state){
		if (state.gameName == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to create"
							+ " null game name");
		}
		if (state.instanceName == null) {
			throw new IllegalStateException(
					"RabbitMQGameInstanceChannel tries to create"
							+ " null game instance name");
		}
		state.virtualHost = Util.getRabbitMQProperties().getProperty(
				"virtualHostSeparator")
				+ state.gameName
				+ Util.getRabbitMQProperties().getProperty(
						"virtualHostSeparator") + state.instanceName;
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
	
	
	private ConnectionFactory getConnectionFactory(final GameLogicState state){
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
		Util.println(" Exiting: closing channels and connections");
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
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publish(final String consumer, final GameLogicState state,
						final GameLogicActionInterface action, final String message)
						throws IOException {
		publishChannel.publish(consumer, state, action, message);
	}
	
	
	/**
	 * Publish a message to a specific recipient (designed by its login)
	 * and specify the action that should be triggered by the recipient on 
	 * the reception of this message.
	 * 
	 * @param 	consumer 	the login of the recipient
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publish(final String consumer, final GameLogicState state,
						final String action, final String message)
						throws IOException {
		publishChannel.publish(consumer, state, action, message);
	}
	
	
	/**
	 * Publish a message to all the players and to the Game Logic server,
	 * and specify the action that should be triggered by recipients on 
	 * the reception of this message.
	 * 
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToAll(	final GameLogicState state,
								final GameLogicActionInterface action, final String message)
								throws IOException {
		publishChannel.publishToAll(state, action, message);
	}
	
	
	/**
	 * Publish a message to all the players and to the Game Logic server,
	 * and specify the action that should be triggered by recipients on 
	 * the reception of this message.
	 * 
	 * @param 	state		the GameLogicState of the game logic
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToAll(	final GameLogicState state,
								final String action, final String message)
								throws IOException {
		publishChannel.publishToAll(state, action, message);
	}
	
	
	/**
	 * Publish a message to the Game Logic server, and specify the action 
	 * that it should triggered on the reception of this message.
	 * 
	 * @param 	state		the GameLogicState of the game logic 
	 * @param 	action		the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToGameLogicServer(	final GameLogicState state,
											final GameLogicActionInterface action, final String message)
											throws IOException {
		publishChannel.publishToGameLogicServer(state, action, message);
	}

	
	/**
	 * Publish a message to the Game Logic server, and specify the action 
	 * that it should triggered on the reception of this message.
	 * 
	 * @param 	state		the GameLogicState of the game logic 
	 * @param 	action		the name of the action to trigger on the recipient side
	 * @param 	message		the message 
	 * 
	 * @throws 	IOException if an exception occurs during the publish.
	 * @throws 	MissedHeartbeatException when the maximum numbers of retry has been done.
	 * 			The method PlayerState.exit() should be called on the handling of this exception.
	 */
	public void publishToGameLogicServer(	final GameLogicState state,
											final String action, final String message)
											throws IOException {
		publishChannel.publishToGameLogicServer(state, action, message);
	}
}
