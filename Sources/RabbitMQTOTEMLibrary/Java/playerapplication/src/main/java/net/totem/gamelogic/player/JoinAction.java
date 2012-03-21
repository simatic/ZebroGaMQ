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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.totem.gamelogic.ActionInvocationException;
import net.totem.gamelogic.Util;

public enum JoinAction implements PlayerActionInterface {
	JOIN_MASTER("joinMaster") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinMaster(state, header, body);
		}
	},
	JOIN_MASTER_OK("joinMasterOK") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinMasterOK(state, header, body);
		}
	},
	JOIN_SPECTATOR_OK("joinSpectator") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinSpectator(state, header, body);
		}
	},
	JOIN_SPECTATOR("joinSpectatorOK") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinSpectatorOK(state, header, body);
		}
	},
	JOIN_PLAYER("joinPlayer") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinPlayer(state, header, body);
		}
	},
	JOIN_PLAYER_OK("joinPlayerOK") {
		public Object execute(final PlayerState state, final String[] header,
				final String body) throws ActionInvocationException {
			return PlayerProtocol.joinPlayerOK(state, header, body);
		}
	};

	private final static Map<String, JoinAction> privateActionMap = new HashMap<String, JoinAction>();
	public final static Map<String, JoinAction> actionMap = Collections
			.unmodifiableMap(privateActionMap);
	public final static int KIND_NUMBER = 10;
	public final static int LOWER_ACTION_NUMBER = 0;
	public final static int UPPER_ACTION_NUMBER = 1000;

	private final String codeKind;
	private final String nameKind = "join";
	private final String codeAction;
	private final String nameAction;

	static {
		for (JoinAction gra : JoinAction.values()) {
			privateActionMap.put(gra.toString(), gra);
		}
	}

	private JoinAction(final String nameAction) {
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
