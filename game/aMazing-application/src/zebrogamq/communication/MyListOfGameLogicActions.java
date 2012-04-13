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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import zebrogamq.gamelogic.GameLogicActionInterface;

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
