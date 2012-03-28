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

package net.totem.gamelogic;

import java.io.IOException;
import java.util.Date;

public class HeartbeatChannel extends PublishChannel{
	
	HeartbeatChannel(final ChannelsManager channelsManager, final GameLogicState state) throws IOException {
		super(channelsManager, state);
		final long heartbeatPeriod = loadHeartbeatPeriod(state);
		startHeartbeatThread(state, heartbeatPeriod);
	}
	
	
	private long loadHeartbeatPeriod(final GameLogicState state){
		long period = 0;
		try {
			period = new Long(Util.getRabbitMQProperties().getProperty(
					"heartbeatPeriod")) * 1000;
		} catch (NumberFormatException e) {
			Util.println(" ["+state.role+" "
					+ state.login
					+ "] heartbeatPeriod is not an integer ("
					+ Util.getRabbitMQProperties().getProperty(
					"heartbeatPeriod") + ")");
		}
		return period;
	}


	private void startHeartbeatThread(final GameLogicState state, final long heartbeatPeriod){
		if(heartbeatPeriod != 0){
			new Thread() {
				public void run() {
					while (!state.hasConnectionExited()) {
						try {
							publishToGameLogicServer(state,PresenceAction.HEARTBEAT,new Date().toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(heartbeatPeriod);
						} catch (InterruptedException e) {
							Util.println(" ["+state.role+" " + state.login
									+ "] Thread sleep was interrupted");
						}
					}
				}
			}.start();	
		}else{
			Util.println(" ["+state.role+" "
					+ state.login
					+ " cannot launch the heartbeat thread.");
		}
	}

}
