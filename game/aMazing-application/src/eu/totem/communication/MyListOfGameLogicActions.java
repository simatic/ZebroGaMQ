package eu.totem.communication;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.totem.gamelogic.GameLogicActionInterface;

public enum MyListOfGameLogicActions {
	AMAZING_ACTION_KIND(AmazingActionKind.actionMap);
	
	// Ignore the code below. Just make sure it is present in all your enums.
	// The copy and paste is due to a limitation of Java enums (no inheritance).

	private Map<String, ? extends GameLogicActionInterface> actionMap;
	private static List<Map<String, ? extends GameLogicActionInterface>> privateListOfActionsMaps = new Vector<Map<String, ? extends GameLogicActionInterface>>();
	
	public final static List<Map<String, ? extends GameLogicActionInterface>> ListOfActionsMaps = Collections.unmodifiableList(privateListOfActionsMaps);

	static {
		for (MyListOfGameLogicActions am : MyListOfGameLogicActions.values()) {
			privateListOfActionsMaps.add(am.actionMap);
		}
	}

	private MyListOfGameLogicActions(
			Map<String, ? extends GameLogicActionInterface> map) {
		actionMap = map;
	}
}
