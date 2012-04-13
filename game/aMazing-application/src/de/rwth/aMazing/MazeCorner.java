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

package de.rwth.aMazing;

import com.google.android.maps.GeoPoint;

import android.location.Location;

/**
 * Class to describe the corners of the maze. Typically these are points
 * obtained from location updates. But they could also be created when location
 * updates were too far apart and extra points were inserted.
 * 
 * @author Alex
 * 
 */
public class MazeCorner {
	// These values are public to enhance the performance of the code. Since
	// they are needed every time we redraw the overlay we will be using them
	// often.
	public Location location;
	public GeoPoint geoPoint;

	// This boolean is set to true if there is a link to the previous corner in
	// the list of MazeCorners.
	public boolean connected;

	// This boolean specifies if this point is hidden, i.e. it is ignored
	// completely. These points are not deleted in order not to mess up the list
	// order. While it might not be the most efficient, it is by far easier than
	// searching through the whole list with ID's. TODO: consider a combination
	// between a hashmap and a list. The list is used for iteration and the
	// hashmap is used to query an object based on ID. If an object needs to be
	// removed we get get it from the hashmap with .get(ID) and the remove it
	// from there and use .remove(object) on the list. Since the list holds the
	// direct neighbors we can edit them in the right way.
	public boolean hidden;

	public int id;

	// This is true if the point was created by interpolation between two actual
	// gps points.
	public boolean generated;

	public MazeCorner(GeoPoint geoPoint, boolean connected, boolean generated,
			boolean hidden, int id) {
		this.geoPoint = geoPoint;
		this.connected = connected;
		this.generated = generated;
		this.id = id;
		this.hidden = hidden;
	}

	public MazeCorner(GeoPoint geoPoint, boolean connected, boolean generated) {
		this.geoPoint = geoPoint;
		this.connected = connected;
		this.generated = generated;
		this.id = PathStorage.getNewID();
		this.hidden = false;
	}

	/**
	 * Convert a MazeCorner to a String
	 * 
	 * @return String describing the MazeCorner in the following way:
	 *         Lat/Lng/Connected/Generated. Lat and Lng are represented in the
	 *         E6 format. Connected and Generated are either 1 or 0 representing
	 *         true of false.
	 */
	@Override
	public String toString() {
		return this.id + "/" + this.geoPoint.getLatitudeE6() + "/"
				+ this.geoPoint.getLongitudeE6() + "/"
				+ (this.connected ? 1 : 0) + "/" + (this.generated ? 1 : 0)
				+ "/" + (this.hidden ? 1 : 0);
	}

	/**
	 * Convert a String (obtained from toString) to a MazeCorner.
	 * 
	 * @param mc
	 *            String describing the MazeCorner in the following way:
	 *            id/Lat/Lng/Connected/Generated/Hidden. The id is the unique ID
	 *            for this corner. Lat and Lng are represented in the E6 format.
	 *            Connected, Generated and Hidden are either 1 or 0 representing
	 *            true of false.
	 * @return A corresponding MazeCorner Object.
	 */
	public static MazeCorner fromString(String mc, boolean e6) {
		String[] tokens = mc.split("[/]");
		if (e6) {
			return new MazeCorner(new GeoPoint(Integer.valueOf(tokens[1]),
					Integer.valueOf(tokens[2])), tokens[3].equals("1"),
					tokens[4].equals("1"), tokens[5].equals("1"),
					Integer.valueOf(tokens[0]));
		} else {
			return new MazeCorner(new GeoPoint(
					(int) (Float.valueOf(tokens[1]) * (float) (1E6)),
					(int) (Float.valueOf(tokens[2]) * (float) (1E6))),
					tokens[3].equals("1"), tokens[4].equals("1"),
					tokens[5].equals("1"), Integer.valueOf(tokens[0]));
		}

	}

}
