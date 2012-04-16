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

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class GameItemOverlay extends ItemizedOverlay<Item> {

	private ArrayList<Item> mOverlays = new ArrayList<Item>();

	private Handler handler;

	private Handler itemOverlayHandler;

	public GameItemOverlay(Drawable defaultMarker, Handler handler,
			MapView mapView) {
		super(boundCenter(defaultMarker));
		this.handler = handler;
		itemOverlayHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: // Proximity violation has occured
					doPopulate();
					break;
				}
			}
		};
		GameSession.itemOverlayHandler = this.itemOverlayHandler;
		doPopulate();
	}

	public void itemDataReady() {
		mOverlays = GameSession.getItems();
		doPopulate();
	}

	public void doPopulate() {
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	protected Item createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int i) {
		if (mOverlays.get(i).type == -1)
			return true; // makes sure the dirty fix does not interfere with the
							// normal game.

		if (GameSession.selectingForMagnet == true) {
			GameSession.selectedItem = mOverlays.get(i);
			return true;
		}

		if (GameSession.selectingForTeleport == true
				&& mOverlays.get(i).type != 0) {
			GameSession.selectedItem = mOverlays.get(i);
			return true;
		}

		Message msg = Message.obtain();
		msg.what = 3;
		msg.obj = (Item) (mOverlays.get(i));
		handler.sendMessage(msg);
		// GameSession.pickup(mOverlays.get(i).getID());
		// handler.sendEmptyMessage(4);
		// setLastFocusedIndex(-1);
		// populate(); //Set index to -1 and populate is needed in order to
		// remove the right item!
		return true;

	}

	public static Drawable boundCenter(Drawable d) {
		return ItemizedOverlay.boundCenter(d);
	}
}
