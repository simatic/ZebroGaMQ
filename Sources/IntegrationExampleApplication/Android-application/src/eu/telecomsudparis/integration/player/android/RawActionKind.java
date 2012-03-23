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

package eu.telecomsudparis.integration.player.android;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.totem.gamelogic.ActionInvocationException;
import net.totem.gamelogic.ConsumeChannel;
import net.totem.gamelogic.GameLogicActionInterface;
import net.totem.gamelogic.GameLogicState;
import net.totem.gamelogic.Util;

public enum RawActionKind implements
	GameLogicActionInterface {
	RAW_ACTION(ConsumeChannel.RAW_ACTION_NAME) {
		public Object execute(GameLogicState state, String[] header,
				String body) throws ActionInvocationException {
			//TODO Add your suitable behavior on the reception of raw messages.
			// header[0] = sender
			// header[1] = recipient
			// header[2] = action kind
			// header[3] = action name
			Util.println("RAW MESSAGE RECEIVED >> = "+body);
			return null;
		}
	};

	public final static int KIND_NUMBER = 10;
	public final static int LOWER_ACTION_NUMBER = 0;
	public final static int UPPER_ACTION_NUMBER = 1000;
	
	// Ignore the code below. Just make sure it is present in all your enums.
	// The copy and paste is due to a limitation of Java enums (no inheritance).

	private final static Map<String, RawActionKind> privateActionMap = new HashMap<String, RawActionKind>();
	public final static Map<String, RawActionKind> actionMap = Collections
			.unmodifiableMap(privateActionMap);

	private final String codeKind;
	public final String nameKind = ConsumeChannel.RAW_ACTION_KIND;
	private final String codeAction;
	private final String nameAction;

	static {
		for (RawActionKind gra : RawActionKind
				.values()) {
			privateActionMap.put(gra.toString(), gra);
		}
	}

	private RawActionKind(String nameAction) {
		this.codeKind = String.valueOf(KIND_NUMBER);
		this.codeAction = String.valueOf(LOWER_ACTION_NUMBER + ordinal());
		this.nameAction = nameAction;
	}

	public String getCodeKind() {
		return codeKind;
	}

	public String getNameKind() {
		return nameKind;
	}

	public String getCodeAction() {
		return codeAction;
	}

	public String getNameAction() {
		return nameAction;
	}

	public String toString() {
		return nameKind
				+ Util.getRabbitMQProperties().getProperty(
						"routingKeySeparator") + nameAction;
	}
}
