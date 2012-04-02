package de.rwth.aMazing;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class GameItemOverlay extends ItemizedOverlay<Item> {

	private ArrayList<Item> mOverlays = new ArrayList<Item>();

	private MapView mv = null;

	private Handler handler;

	private Handler itemOverlayHandler;

	public GameItemOverlay(Drawable defaultMarker, Handler handler,
			MapView mapView) {
		super(boundCenter(defaultMarker));
		this.handler = handler;
		this.mv = mapView;
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
