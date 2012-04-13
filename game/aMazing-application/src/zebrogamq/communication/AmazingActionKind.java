/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for 
 * Mobile Devices" at Fraunhofer FIT.
 * 
 * http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
 * http://www.totem-games.org/?q=aMazing
 * 
 * Copyright (C) 2012  Alexander Hermans, Tianjiao Wang
 * 
 * Contact: 
 * alexander.hermans0@gmail.com, tianjiao.wang@rwth-aachen.de,
 * richard.wetzel@fit.fraunhofer.de, lisa.blum@fit.fraunhofer.de, 
 * denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Developer(s): Alexander Hermans, Tianjiao Wang
 * ZebroGaMQ:  Denis Conan, Gabriel Adgeg 
 */

package zebrogamq.communication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import zebrogamq.gamelogic.ActionInvocationException;
import zebrogamq.gamelogic.GameLogicActionInterface;
import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.Util;

public enum AmazingActionKind implements GameLogicActionInterface {
	SEND_GPS_COORDINATES("sendGPSCoordinates") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.handleP2Data(body);
		}
	},
	PROXIMITY_VIOLATION("proximityViolation") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.proximityViolation(body);
		}
	},
	PROXIMITY_VIOLATION_CORRECTED("proximityViolationCorrected") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.proximityViolationCorrected(body);
		}
	},
	PLAYER_READY("playerReady") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.playerReady();
		}
	},
	LOCATION_IS_ACCURATE("locationIsAccurate") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.start(body);
		}
	},
	ITEM_REQUEST("itemRequest") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return null;
		}
	},
	ITEM_UPDATE("itemUpdate") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.itemUpdate(body);
		}
	},
	UPDATE_CROWN_CLAIMS("updateCrownClaims") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.updateCrownClaims(body);
		}
	},
	FINISH_GAME("finishGame") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.displayWinner(body);
		}
	},
	TELEPORT_ITEM("teleportItem") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.teleportItem(body);
		}
	},
	DRAW_ITEM("drawItem") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.drawItem(body);
		}
	},
	FREE_PASS("freePass") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.freePass(body);
		}
	},
	BREAK_MAZE("breakMaze") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.breakMaze(body);
		}
	},
	BINOCULARS("binoculars") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.binoculars(body);
		}
	},
	ROCKET("rocket") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.rocket(body);
		}
	},
	EXPAND_CROWNS("expandCrowns") {
		public Object execute(GameLogicState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.expandCrowns(body);
		}
	};




	
	public final static int KIND_NUMBER = 102;
	public final static int LOWER_ACTION_NUMBER = 0;
	public final static int UPPER_ACTION_NUMBER = 1000;

	// Ignore the code below. Just make sure it is present in all your enums.
	// The copy and paste is due to a limitation of Java enums (no inheritance).

	private final static Map<String, AmazingActionKind> privateActionMap = new HashMap<String, AmazingActionKind>();
	public final static Map<String, AmazingActionKind> actionMap = Collections
			.unmodifiableMap(privateActionMap);

	private final String codeKind;
	public final String nameKind = "amazingActionKind";
	private final String codeAction;
	private final String nameAction;

	static {
		for (AmazingActionKind gra : AmazingActionKind.values()) {
			privateActionMap.put(gra.toString(), gra);
		}
	}

	private AmazingActionKind(String nameAction) {
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
