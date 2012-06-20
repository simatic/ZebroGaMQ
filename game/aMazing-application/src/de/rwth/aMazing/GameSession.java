package de.rwth.aMazing;

import de.rwth.aMazing.ui.SettingsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;


public class GameSession {
	public static String instanceName;

	public static float left;
	public static float right;
	public static float top;
	public static float bottom;

	public static Drawable[] mapIcons;
	public static Drawable[] batteryIcons;

	// Store in an array (for drawing) and an hashmap for ease of access.
	private static ArrayList<Item> items;
	private static HashMap<Integer, Item> itemsHashMap;

	public static ArrayList<Item> inventory;

	private static Context context;

	public static int itemNumberSetting = 25;
	public static int crownNumberSetting = 5;
	public static int timeInMiliSetting = 900000;
	public static int districtSizeInMetersSetting = 300;
	public static boolean soundSetting= true;

	public static float rechargeRate;
	public static long gameDurationMS; // 15*60*1000 i.e. 15 minutes
	public static float crownClaimRadius;
	public static float itemPickupRadius;
	public static float breakerRadius;
	public static float mazePoolLimit;
	public static boolean violationInAction = false;
	public static boolean gameStarted = false;

	public static GeoPoint lastCorrectLocation = null;
	public static Handler itemOverlayHandler;

	public static int usedItemID = -1;
	public static Item selectedItem = null;
	public static GeoPoint selectedPoint = null;
	public static boolean selectingForTeleport = false;
	public static boolean selectingForMagnet = false;
	public static boolean selectingForBreaker = false;
	public static boolean showingOtherItems = false;
	public static int[] otherItems = new int[6];
	public static int binocularsTTL = 0;
	public static boolean rocketActive = false;
	public static int rocketTTL = 0;
	public static int[] owner;
	public static boolean[] claimChange ;
	
	
	
	public static void parseGameSessionFromString(String gameData) {
		// gameData ->
		// "l,r,t,b,rechargerate, gameTime, crownRad, itemRad, poolLimit*nr of goals* nr of items* goal Data * item DATA "
		// goalData / itemData -> "item1,item2,item3,item4,..."
		// item -> "id|latE6|lngE6|typ|additional data handled by items"
		String[] data = gameData.split("[*]");
		String[] areaData = data[0].split("[,]");
		String[] goalData = data[3].split("[,]");
		String[] itemData = data[4].split("[,]");

		left = Float.parseFloat(areaData[0]);
		right = Float.parseFloat(areaData[1]);
		top = Float.parseFloat(areaData[2]);
		bottom = Float.parseFloat(areaData[3]);
		rechargeRate = Float.parseFloat(areaData[4]);
		gameDurationMS = Long.parseLong(areaData[5]);
		crownClaimRadius = Float.parseFloat(areaData[6]);
		itemPickupRadius = Float.parseFloat(areaData[7]);
		mazePoolLimit = Float.parseFloat(areaData[8]);
		breakerRadius = crownClaimRadius * 0.5f; // TODO change this.
		owner = new int[ Integer.parseInt(data[1])];
		claimChange = new boolean[Integer.parseInt(data[1])];
		for (int i = 0; i < Integer.parseInt(data[1]); i++) {
			owner[i]= 0;
			claimChange[i] = false;
			Item current = Item.parseFromString(goalData[i]);
			current.setMarker(mapIcons[current.type]);
			items.add(current);
			itemsHashMap.put(current.getID(), current);
		}
	

		for (int i = 0; i < Integer.parseInt(data[2]); i++) {
			Item current = Item.parseFromString(itemData[i]);
			current.setMarker(mapIcons[current.type]);
			items.add(current);
			itemsHashMap.put(current.getID(), current);
		}

		// Evil work around for map problem: Add an dummy item.
		Item empty = new Item(new GeoPoint(180, 0), "", "");
		empty.type = -1;
		empty.setMarker(mapIcons[7]);
		items.add(empty);
		
		gameStarted= true;
	}

	public static void Initialize(Context context) {
		items = new ArrayList<Item>();
		itemsHashMap = new HashMap<Integer, Item>();
		inventory = new ArrayList<Item>();
		GameSession.context = context;
		// 0: goal item
		// 1 -6 : Items

		mapIcons = new Drawable[8];
		mapIcons[0] = context.getResources().getDrawable(R.drawable.crowndraw);
		mapIcons[1] = context.getResources().getDrawable(R.drawable.binoculars);
		mapIcons[2] = context.getResources().getDrawable(R.drawable.breaker);
		mapIcons[3] = context.getResources().getDrawable(R.drawable.magnet);
		mapIcons[4] = context.getResources().getDrawable(R.drawable.pass);
		mapIcons[5] = context.getResources().getDrawable(R.drawable.rocket);
		mapIcons[6] = context.getResources().getDrawable(
				R.drawable.teleportation);
		mapIcons[7] = context.getResources().getDrawable(R.drawable.empty);
		mapIcons[0].setBounds(-30, -30, 30, 30);
		mapIcons[1].setBounds(-20, -20, 20, 20);
		mapIcons[2].setBounds(-20, -20, 20, 20);
		mapIcons[3].setBounds(-20, -20, 20, 20);
		mapIcons[4].setBounds(-20, -20, 20, 20);
		mapIcons[5].setBounds(-20, -20, 20, 20);
		mapIcons[6].setBounds(-20, -20, 20, 20);
		mapIcons[7].setBounds(0, 0, 0, 0);
		batteryIcons = new Drawable[8];
		batteryIcons[0] = context.getResources().getDrawable(
				R.drawable.battery_10);
		batteryIcons[1] = context.getResources().getDrawable(
				R.drawable.battery_25);
		batteryIcons[2] = context.getResources().getDrawable(
				R.drawable.battery_50);
		batteryIcons[3] = context.getResources().getDrawable(
				R.drawable.battery_80);
		batteryIcons[4] = context.getResources().getDrawable(
				R.drawable.battery_100);
		batteryIcons[5] = context.getResources().getDrawable(R.drawable.flash);

		
		

	}

	public static ArrayList<Item> getItems() {
		return items;
	}

	public static void add(Item newItem) {
		Item empty = items.remove(items.size() - 1);
		items.add(newItem);
		items.add(empty);
	}

	public static int numberOfDisplayedItems() {
		return items.size() - 1;
	}

	public static void pickup(int id) {
		Item item = itemsHashMap.get(id);
		if (inventory.size() < 6) {
			inventory.add(item);
			remove(id);
		}
	}

	public static void remove(int id) {
		items.remove(itemsHashMap.get(id));
		itemsHashMap.remove(id);
		itemOverlayHandler.sendEmptyMessage(0);
	}

	public static Item getInventoryPlace(int index) {
		if (index < inventory.size()) {
			return inventory.get(index);
		} else {
			return null;
		}

	}

	public static void removeInventoryItem(int itemIndex) {
		inventory.remove(itemIndex);
	}
    
	
	public static void updatCrownClaims(String crownInfo) {
		
		String[] crowns = crownInfo.split("[*]");
		String[] crown = null;
		for (int i = 0; i < crowns.length; i++) {
			crown = crowns[i].split("[,]");
			Item current = itemsHashMap.get(Integer.parseInt(crown[0]));
			current.owner = Integer.parseInt(crown[1]);
			if(current.owner==owner[i]){
				claimChange[i] = false;
			}
			else{
				claimChange[i] = true;
				owner[i] = current.owner;
			}

		}
    
	}

	public static void updateCrownClaimArea(String claimAreaInfo) {
		// id1, radius1 * id2, radius2 * ... * idn , radn
		String[] crowns = claimAreaInfo.split("[*]");
		String[] crown = null;
		for (int i = 0; i < crowns.length; i++) {
			crown = crowns[i].split("[,]");
			Item current = itemsHashMap.get(Integer.parseInt(crown[0]));
			current.crownClaimRadius = Float.parseFloat(crown[1]);
		}
	}

	// Not very nice, but since it seems that Items cannot get a new location we
	// need to replace them.
	public static void updateItemLocation(String itemInfo) {
		String[] data = itemInfo.split("[*]");
		Item toUpdate = itemsHashMap.get(Integer.parseInt(data[0]));
		toUpdate.updateLocation(Integer.parseInt(data[1]),
				Integer.parseInt(data[2]));
		Item replacement = Item.copy(toUpdate);
		itemsHashMap.put(toUpdate.getID(), replacement);
		items.set(items.indexOf(toUpdate), replacement);

	}

	public static Drawable getDrawableByType(int type) {
		Drawable icon;
		switch (type) {
		case -1:
			return null;
		case 1:
			icon = context.getResources().getDrawable(R.drawable.binoculars);
			icon.mutate();
			return icon;
		case 2:
			icon = context.getResources().getDrawable(R.drawable.breaker);
			icon.mutate();
			return icon;
		case 3:
			icon = context.getResources().getDrawable(R.drawable.magnet);
			icon.mutate();
			return icon;
		case 4:
			icon = context.getResources().getDrawable(R.drawable.pass);
			icon.mutate();
			return icon;
		case 5:
			icon = context.getResources().getDrawable(R.drawable.rocket);
			icon.mutate();
			return icon;
		case 6:
			icon = context.getResources().getDrawable(R.drawable.teleportation);
			icon.mutate();
			return icon;
		}
		return null;
	}

	public static void removeByType(int type) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).type == type) {
				inventory.remove(i);
				break;
			}
		}

	}

	public static void freePass(String body) {
		String[] data = body.split("[*]");
		lastCorrectLocation = new GeoPoint(Integer.parseInt(data[0]),
				Integer.parseInt(data[1]));
		violationInAction = false;
	}

	public static void binoculars(String body) {
		if (!body.equals("")) {
			// Check if the string is empty.
			String[] data = body.split("[*]");
			for (int i = 0; i < data.length; i++) {
				otherItems[i] = Integer.parseInt(data[i]);
			}
			for (int i = data.length; i < 6; i++) {
				otherItems[i] = -1;
			}
		} else {
			for (int i = 0; i < 6; i++) {
				otherItems[i] = -1;
			}
		}
		binocularsTTL = 20;
		showingOtherItems = true;

	}

	public static void rocket() {
		rocketTTL = 60;
		rocketActive = true;

	}

	public static void reset() {
		items.clear();
		itemsHashMap.clear();
		inventory.clear();
		violationInAction = false;
		lastCorrectLocation = null;
		usedItemID = -1;
		selectedItem = null;
		selectedPoint = null;
		selectingForTeleport = false;
		selectingForMagnet = false;
		selectingForBreaker = false;
		showingOtherItems = false;
		otherItems = new int[6];
		binocularsTTL = 0;
		rocketActive = false;
		rocketTTL = 0;
		gameStarted = false;

	}

}