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

import java.util.Arrays;

import net.totem.gamelogic.Util;

public class SpectatorProtocol{

	public static Object terminate(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"terminate action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"terminate action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"terminate action with null body message");
		}
		Util.println("Terminate received.");
		state.connectionExit();
		return null;
	}

	public static Object joinMaster(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinMaster action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinMaster action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinMaster action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}

	public static Object joinMasterOK(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinMasterOK action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinMasterOK action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinMasterOK action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}

	public static Object joinSpectator(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinSpectator action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinSpectator action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinSpectator action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}

	public static Object joinSpectatorOK(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinSpectatorOK action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinSpectatorOK action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinSpectatorOK action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}

	public static Object joinPlayer(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinPlayer action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinPlayer action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinPlayer action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}

	public static Object joinPlayerOK(final SpectatorState state,
			final String[] header, final String body) {
		if (state == null) {
			throw new IllegalArgumentException(
					"joinPlayerOK action with null state");
		}
		if (header == null) {
			throw new IllegalArgumentException(
					"joinPlayerOK action with null header message");
		}
		if (body == null) {
			throw new IllegalArgumentException(
					"joinPlayerOK action with null body message");
		}
		Util.println(" [Spectator " + state.login + "] Received "
				+ Arrays.asList(header) + " / " + body);
		return null;
	}
}
