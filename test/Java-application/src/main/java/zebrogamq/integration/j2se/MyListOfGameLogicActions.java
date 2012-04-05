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

package zebrogamq.integration.j2se;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import zebrogamq.gamelogic.GameLogicActionInterface;

public enum MyListOfGameLogicActions {
	MY_FIRST_ACTION_KIND(MyFirstActionKind.actionMap),
	MY_SECOND_ACTION_KIND(MySecondActionKind.actionMap);
	
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

	private MyListOfGameLogicActions(final Map<String, ? extends GameLogicActionInterface> map) {
		actionMap = map;
	}
}
