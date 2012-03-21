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

package net.totem.gamelogic.player;

import java.io.IOException;

import net.totem.gamelogic.Util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.PossibleAuthenticationFailureException;
import com.rabbitmq.client.ShutdownSignalException;

class RabbitMQGameInstanceChannel {

	private	final 	ChannelsManager		channelsManager;
	private 		Connection 			connection;
	protected 		Channel 			channel;	


	RabbitMQGameInstanceChannel(final ChannelsManager channelsManager, final PlayerState state) throws java.io.IOException {
		this.channelsManager = channelsManager;
		initCommunicationWithBroker(state);
	}

	
	protected boolean initCommunicationWithBroker(final PlayerState state){
		boolean communicationOK = false;
		Util.println(" [Player " + state.login
				+ "] initCommunicationWithBroker for "+getClass().getSimpleName()+".");
		for (int i = 0, max = Integer.valueOf(Util.getRabbitMQProperties()
				.getProperty("maxRetry")); i < max; i++) {
			if (!state.hasConnectionExited()) {
				try {
					connection = channelsManager.newConnection();
					channel = connection.createChannel();
					channel.exchangeDeclarePassive(state.exchangeName);
					// just useful for consume channel
					channel.queueDeclare(state.login, false, false, false, null);
					// just useful for consume channel
					channel.basicQos(1);
					communicationOK = true;
					synchronized (state) {
						// set back numberOfRetries
						state.numberOfRetries = 0;
					}
					break;
				} catch (PossibleAuthenticationFailureException e) {
					// This exception may appear with correct authentication parameters...
					Util.println(" [Player " + state.login
							+ "] Fail during authentication with broker for "+getClass().getSimpleName()+": "
							+ e.getMessage());
					e.printStackTrace();
					handleRetryNumber(i, max, state);
				} catch (IOException e) {
					Util.println(getClass().getSimpleName()+": Number of reconnection tries: "+i);
					e.printStackTrace();
					handleRetryNumber(i, max, state);
				}
			}
		}
		return communicationOK;
	}
	
	
	private void handleRetryNumber(int retryNumber, int maxRetryNumber, final PlayerState state){
		// update state's attribute
		updateNumberOfRetries(retryNumber, state);
		if (retryNumber == maxRetryNumber - 1){
			Util.println(" [Player " + state.login+ "]: "+getClass().getSimpleName()+" stop : too many retries.");
			state.connectionExit();
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void updateNumberOfRetries(int retryNumber, final PlayerState state){
		synchronized (state) {
			if(retryNumber > state.numberOfRetries){
				state.numberOfRetries = retryNumber;
			}
		}
	}


	void close() {
		try {
			channel.close();
			connection.close();
		} catch (ShutdownSignalException e){
			// ignore
		} catch (IOException e) {
			// ignore
		}
	}
}