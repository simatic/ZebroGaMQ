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

package net.totem.integration.master.j2se;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.totem.gamelogic.ActionInvocationException;
import net.totem.gamelogic.Util;
import net.totem.gamelogic.gamemaster.GameMasterActionInterface;
import net.totem.gamelogic.gamemaster.GameMasterState;

public enum MySecondActionKind implements GameMasterActionInterface {
	MY_FOURTH_ACTION("myFourthAction") {
		public Object execute(final GameMasterState state,
				final String[] header, final String body)
				throws ActionInvocationException {
			return MyGameLogicProtocol.myFourthAction(state, header, body);
		}
	},
	MY_FIFTH_ACTION("myFitfhAction") {
		public Object execute(final GameMasterState state,
				final String[] header, final String body)
				throws ActionInvocationException {
			return null;
		}
	};
	public final static int KIND_NUMBER = 101;
	public final static int LOWER_ACTION_NUMBER = 0;
	public final static int UPPER_ACTION_NUMBER = 1000;

	// Ignore the code below. Just make sure it is present in all your enums.
	// The copy and paste is due to a limitation of Java enums (no inheritance).

	private final static Map<String, MySecondActionKind> privateActionMap = new HashMap<String, MySecondActionKind>();
	public final static Map<String, MySecondActionKind> actionMap = Collections
			.unmodifiableMap(privateActionMap);

	private final String codeKind;
	public final String nameKind = "mySecondActionKind";
	private final String codeAction;
	private final String nameAction;

	static {
		for (MySecondActionKind gra : MySecondActionKind.values()) {
			privateActionMap.put(gra.toString(), gra);
		}
	}

	private MySecondActionKind(final String nameAction) {
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
