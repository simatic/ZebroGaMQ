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

package net.totem.gamelogic.spectator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public enum ListOfActions {
	LIFECYCLE_ACTIONS(LifeCycleAction.actionMap),
	JOIN_ACTIONS(JoinAction.actionMap),
	PRESENCE_ACTIONS(PresenceAction.actionMap);

	private final Map<String, ? extends SpectatorActionInterface> actionMap;
	private final static List<Map<String, ? extends SpectatorActionInterface>> privateListOfActionsMaps = new Vector<Map<String, ? extends SpectatorActionInterface>>();
	public final static List<Map<String, ? extends SpectatorActionInterface>> ListOfActionsMaps = Collections
			.unmodifiableList(privateListOfActionsMaps);

	static {
		for (ListOfActions am : ListOfActions.values()) {
			privateListOfActionsMaps.add(am.actionMap);
		}
	}

	private ListOfActions(
			final Map<String, ? extends SpectatorActionInterface> map) {
		actionMap = map;
	}
}
