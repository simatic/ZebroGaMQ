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
import java.util.Date;

import net.totem.gamelogic.Util;

public class HeartbeatChannel extends PublishChannel{
	
	HeartbeatChannel(final ChannelsManager channelsManager, final GameMasterState state) throws IOException {
		super(channelsManager, state);
		final long heartbeatPeriod = loadHeartbeatPeriod(state);
		startHeartbeatThread(state, heartbeatPeriod);
	}
	
	
	private long loadHeartbeatPeriod(final GameMasterState state){
		long period = 0;
		try {
			period = new Long(Util.getRabbitMQProperties().getProperty(
					"heartbeatPeriod")) * 1000;
		} catch (NumberFormatException e) {
			Util.println(" [Master "
					+ state.login
					+ "] heartbeatPeriod is not an integer ("
					+ Util.getRabbitMQProperties().getProperty(
					"heartbeatPeriod") + ")");
		}
		return period;
	}


	private void startHeartbeatThread(final GameMasterState state, final long heartbeatPeriod){
		if(heartbeatPeriod != 0){
			new Thread() {
				public void run() {
					int sequenceNumber = 1;
					while (!state.hasConnectionExited()) {
						try {
							publishToGameLogicServer(state,
									PresenceAction.HEARTBEAT,
									("Sequence Number "+sequenceNumber+"  "+new Date()).toString());
							sequenceNumber ++;
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(heartbeatPeriod);
						} catch (InterruptedException e) {
							Util.println(" [Master " + state.login
									+ "] Thread sleep was interrupted");
						}
					}
				}
			}.start();	
		}else{
			Util.println(" [Master "
					+ state.login
					+ " cannot launch the heartbeat thread.");
		}
	}

}
