package eu.totem.communication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import net.totem.gamelogic.ActionInvocationException;
import net.totem.gamelogic.Util;
import net.totem.gamelogic.player.PlayerActionInterface;
import net.totem.gamelogic.player.PlayerState;

public enum AmazingActionKind implements PlayerActionInterface {
	SEND_GPS_COORDINATES("sendGPSCoordinates") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.handleP2Data(body);
		}
	},
	PROXIMITY_VIOLATION("proximityViolation") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.proximityViolation(body);
		}
	},
	PROXIMITY_VIOLATION_CORRECTED("proximityViolationCorrected") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.proximityViolationCorrected(body);
		}
	},
	PLAYER_READY("playerReady") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.playerReady();
		}
	},
	LOCATION_IS_ACCURATE("locationIsAccurate") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.start(body);
		}
	},
	ITEM_REQUEST("itemRequest") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return null;
		}
	},
	ITEM_UPDATE("itemUpdate") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.itemUpdate(body);
		}
	},
	UPDATE_CROWN_CLAIMS("updateCrownClaims") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.updateCrownClaims(body);
		}
	},
	FINISH_GAME("finishGame") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.displayWinner(body);
		}
	},
	TELEPORT_ITEM("teleportItem") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.teleportItem(body);
		}
	},
	DRAW_ITEM("drawItem") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.drawItem(body);
		}
	},
	FREE_PASS("freePass") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.freePass(body);
		}
	},
	BREAK_MAZE("breakMaze") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.breakMaze(body);
		}
	},
	BINOCULARS("binoculars") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.binoculars(body);
		}
	},
	ROCKET("rocket") {
		public Object execute(PlayerState state, String[] header, String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.rocket(body);
		}
	},
	EXPAND_CROWNS("expandCrowns") {
		public Object execute(PlayerState state, String[] header, String body)
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
