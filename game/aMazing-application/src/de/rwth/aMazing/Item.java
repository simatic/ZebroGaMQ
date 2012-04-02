package de.rwth.aMazing;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Item extends OverlayItem {
	public Item(GeoPoint location, String title, String snippet) {
		super(location, title, snippet);
		// TODO Auto-generated constructor stub
	}

	private int id;
	private GeoPoint location;
	public int type;
	public String extra;

	// For crowns
	public int owner = 0;
	public float crownClaimRadius; 	

	public static Item parseFromString(String itemData) {
		Log.d("game", "Adding item, item data: " + itemData);
		String[] item = itemData.split("[|]");
		GeoPoint gp = new GeoPoint(Integer.parseInt(item[1]),
				Integer.parseInt(item[2]));
		Item current = new Item(gp, "", "");
		current.location = gp;
		current.id = Integer.parseInt(item[0]);
		current.type = Integer.parseInt(item[3]);
		current.extra = item[4];
		current.crownClaimRadius = GameSession.crownClaimRadius;
		return current;
	}

	public void updateLocation(int lat, int lng) {
		this.location = new GeoPoint(lat, lng);
	}

	public int getID() {
		return this.id;
	}

	public static Item copy(Item old) {
		Item current = new Item(old.location, "", "");
		current.location = old.location;
		current.id = old.getID();
		current.type = old.type;
		current.extra = old.extra;
		current.setMarker(GameSession.mapIcons[old.type]);
		current.owner = old.owner;
		current.crownClaimRadius = old.crownClaimRadius;
		return current;
	}
}
