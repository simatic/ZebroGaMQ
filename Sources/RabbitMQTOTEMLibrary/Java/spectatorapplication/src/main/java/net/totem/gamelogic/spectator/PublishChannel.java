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

package net.totem.gamelogic.spectator;

import java.io.IOException;

import net.totem.gamelogic.Util;

import com.rabbitmq.client.ShutdownSignalException;

public class PublishChannel extends RabbitMQGameInstanceChannel {
	
	private String routingKeyRootGameInstanceServer = null;
	private String routingKeyRootAll = null;

	PublishChannel(final ChannelsManager channelsManager, final SpectatorState state) throws IOException {
		super(channelsManager, state);
		routingKeyRootGameInstanceServer = 	state.login
											+ Util.getRabbitMQProperties().getProperty("routingKeySeparator")
											+ Util.getRabbitMQProperties().getProperty("gameLogicUserName")
											+ Util.getRabbitMQProperties().getProperty("routingKeySeparator");
		routingKeyRootAll = state.login
							+ Util.getRabbitMQProperties().getProperty("routingKeySeparator")
							+ "all"
							+ Util.getRabbitMQProperties().getProperty("routingKeySeparator");
	}

	void publish(final String consumer, final SpectatorState state,
			final SpectatorActionInterface action, final String message)
			throws IOException{
		if (consumer == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " to null consumer");
		}
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = state.login
				+ Util.getRabbitMQProperties().getProperty(
						"routingKeySeparator")
				+ consumer
				+ Util.getRabbitMQProperties().getProperty(
						"routingKeySeparator") + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}
	
	
	void publish(final String consumer, final SpectatorState state,
				final String action, final String message)
			throws IOException{
		if (consumer == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " to null consumer");
		}
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = state.login
				+ Util.getRabbitMQProperties().getProperty(
						"routingKeySeparator")
				+ consumer
				+ Util.getRabbitMQProperties().getProperty(
						"routingKeySeparator") + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}

	void publishToAll(final SpectatorState state,
			final SpectatorActionInterface action, final String message)
			throws IOException {
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = routingKeyRootAll + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}
	
	
	void publishToAll(final SpectatorState state,
			final String action, final String message)
			throws IOException {
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = routingKeyRootAll + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}

	void publishToGameLogicServer(final SpectatorState state,
			final SpectatorActionInterface action, final String message)
			throws IOException {
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = routingKeyRootGameInstanceServer + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}
	
	void publishToGameLogicServer(final SpectatorState state,
			final String action, final String message)
			throws IOException {
		if (state == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null state");
		}
		if (action == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " with null action");
		}
		if (message == null) {
			throw new IllegalArgumentException("publish tries to publish"
					+ " a null message");
		}
		String routingKey = routingKeyRootGameInstanceServer + action;
		boolean communicationOK = true;
		while(communicationOK) {
			try {
				channel.basicPublish(state.exchangeName, routingKey, null,
						message.getBytes());
				Util.println(" [Spectator " + state.login + "] Sent " + message
						+ " with key = " + routingKey + " on vhost "
						+ state.virtualHost);
				return;
			} catch (ShutdownSignalException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			} catch (IOException e) {
				if (channel.isOpen()) {
					close();
				}
				communicationOK = initCommunicationWithBroker(state);
			}
		}
	}
}
