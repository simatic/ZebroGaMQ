package de.rwth.aMazing;

import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.totem.gamelogic.Util;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import de.rwth.aMazing.ui.MenuActivity;
import eu.totem.communication.MyGameLogicProtocol;
import eu.totem.communication.PlayerTask;

import android.text.InputFilter;
import android.text.Spanned;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends MapActivity {

	public final static String INSTANCE_CREATOR_NAME = "PLAYER_1";
	final static String INSTANCE_JOINER_NAME = "PLAYER_2";
	public final static String DEFAULT_PWD = "ufGf64";
	private PlayerTask playerTask;
	public String playerName;

	// View stuff
	private MapView mapView;
	// private TextView tv;
	private View batteryView;
	private List<Overlay> mapOverlays;
	private GameItemOverlay itemOverlay;
	private MyLocationOverlay locOverlay;
	private View[] inventorySlots = new View[6];
	private View[] otherInventorySlots = new View[6];
	private View otherInventory;
	private OnClickListener[] itemClickListeners = new OnClickListener[6];
	private OnClickListener[] useClickListeners = new OnClickListener[6];
	private OnClickListener[] cancelClickListeners = new OnClickListener[6];
	private Button zoomPlusButton;
	private Button zoomMinusButton;
	private Button useButton;
	private Button cancelButton;
	private TextView gameTime;
	private TextView violationWarn;
	private View connectionFeedbackView;

	private Dialog dialog;

	// variables
	public final float RADIUS = 2.5f;
	Location previousLocation;
	GeoPoint geoPoint;
	GeoPoint previousGeoPoint;
	LocationManager locationManager;
	LocationListener locationListener;
	LocationListener locationListenerTemp;
	private MapController controller;
	long oldTime = 0;
	long newTime = 0;
	long difference = 0;
	float pool = 10;
	private boolean pathIsUnbroken = true;
	private boolean sufficientMazeLeft = true;
	private float distance = 0;
	private Context context = this;
	private LocationHandlerThread locationHandlerThread;

	// Countdown stuff
	private Handler CountdownHandler = new Handler();
	private long endTime = 0L;
	private int mazePoolTick = 0;

	private Runnable countDownTask = new Runnable() {
		public void run() {
			final long end = endTime;
			long millis = end - SystemClock.uptimeMillis();
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			if (seconds < 10) {
				gameTime.setText("" + minutes + ":0" + seconds);
			} else {
				gameTime.setText("" + minutes + ":" + seconds);
			}

			if (GameSession.showingOtherItems) {
				GameSession.binocularsTTL--;
				if (GameSession.binocularsTTL == 0) {
					otherInventory.setVisibility(View.INVISIBLE);
					GameSession.showingOtherItems = false;
				}
			}

			if (GameSession.rocketActive) {
				GameSession.rocketTTL--;
				pool = GameSession.mazePoolLimit * 5f;
				if (GameSession.rocketTTL == 0) {
					GameSession.rocketActive = false;
				}
			} else {
				mazePoolTick++;
				if (mazePoolTick == 5) {
					mazePoolTick = 0;

					pool = pool + GameSession.rechargeRate;
					if (pool > GameSession.mazePoolLimit)
						pool = GameSession.mazePoolLimit;
				}
			}
			// Update the battery view.
			float percentage = ((float) pool) / GameSession.mazePoolLimit;
			if (percentage <= 0.10) {
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[0]);
			} else if (percentage <= 0.25) {
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[1]);
			} else if (percentage <= 0.50) {
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[2]);
			} else if (percentage <= 0.80) {
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[3]);
			} else if (percentage <= 1) {
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[4]);
			} else {
				// We are using the rocket right now
				batteryView.setBackgroundDrawable(GameSession.batteryIcons[5]);
			}

			// tv.setText("Pool: " + pool);

			// Check if the time is over otherwise reschedule.
			if (seconds + minutes != 0) {
				CountdownHandler.postAtTime(this, end // Wonder why it works,
														// but it does ;)
						- (((minutes * 60) + seconds) * 1000));
			}
		}
	};

	// Handler for UI changing
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // Proximity violation has occurred

				AlertDialog.Builder vioDialog = new AlertDialog.Builder(context);

				vioDialog.setMessage("You are too close!");

				vioDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}
						});
				vioDialog.show();

				violationWarn.setVisibility(View.VISIBLE);

				SoundManager.playSound(4, 1);
				// vibration lasts 500 milliseconds
				((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);

				// Since the next point should not be connected we need to set
				// this to false.
				pathIsUnbroken = false;
				// tv.setBackgroundColor(Color.RED);
				locationManager.removeUpdates(locationListener);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 1f, locationListener);

				// Items cannot be used now. Update the inventory view
				updateInventory();

				break;
			case 1: // Proximity Violation has been corrected
				Toast proximityOke = Toast.makeText(context,
						"You returned to the last correct point",
						Toast.LENGTH_LONG);
				proximityOke.show();
				// vibration lasts 500 milliseconds
				((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
				violationWarn.setVisibility(View.INVISIBLE);

				// tv.setBackgroundColor(Color.BLACK);
				locationManager.removeUpdates(locationListener);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
						locationListener);

				updateInventory();
				break;
			case 2: // Game data has been parsed we can start recording data.
				// Register the listener with the Location Manager to receive
				// location updates
				locationManager.removeUpdates(locationListenerTemp);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
						locationListener);

				itemOverlay.itemDataReady();
				gameTime.setVisibility(View.VISIBLE);
				endTime = SystemClock.uptimeMillis()
						+ GameSession.gameDurationMS;
				CountdownHandler.post(countDownTask);
				Toast start = Toast.makeText(context,
						"GO! The game begins now!", Toast.LENGTH_LONG);
				start.show();
				SoundManager.playSound(8, 1);
				// vibration lasts 500 milliseconds
				((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
				break;
			case 3:
				if (((Item) msg.obj).type != 0) {
					playerTask.pickupItem(((Item) msg.obj).getID(),
							previousGeoPoint);
				} else {
					Toast itemTap = Toast.makeText(context,
							"Crowns can't be picked up", Toast.LENGTH_LONG);
					itemTap.show();
				}
				break;
			case 4:
				updateInventory(); // Might be unused
				break;
			case 5:
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
						locationListenerTemp);

				AlertDialog.Builder startDialog = new AlertDialog.Builder(
						context);

				startDialog
						.setMessage("Start moving away from the other player. The game will start when the GPS accuracy is sufficient.");

				startDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}
						});

				startDialog.show();

				// vibration lasts 500 milliseconds
				((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
				break;
			case 8:
				locationManager.removeUpdates(locationListener);
				locOverlay.disableMyLocation();
				// tv.setBackgroundColor(Color.BLUE);
				String[] scores = ((String) msg.obj).split("[*]");
				int p1 = Integer.parseInt(scores[0]);
				int p2 = Integer.parseInt(scores[1]);

				dialog = new Dialog(context);

				dialog.setContentView(R.layout.game_end_dialog);

				dialog.setTitle("Game result:");

				TextView score = (TextView) dialog.findViewById(R.id.score);
				TextView scoreText = (TextView) dialog
						.findViewById(R.id.scoreText);
				Button oki = (Button) dialog.findViewById(R.id.ok);

				gameTime.setVisibility(View.GONE);
				if (p1 > p2) {
					score.setText(p1 + ":" + p2);
					scoreText.setText("You WIN! :D");
					SoundManager.playSound(1, 1);

				} else if (p1 < p2) {
					score.setText(p1 + ":" + p2);
					scoreText.setText("You LOSE! :(");
					SoundManager.playSound(2, 1);
				} else {
					score.setText(p1 + ":" + p2);
					scoreText.setText("A draw!");
					SoundManager.playSound(3, 1);
				}
				oki.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						Toast ok = Toast.makeText(context,
								"Click Exit to go back to the start menu",
								Toast.LENGTH_LONG);
						ok.show();
						dialog.dismiss();
						Button exit = (Button) findViewById(R.id.gameEnd);
						exit.setVisibility(View.VISIBLE);
						exit.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								GameSession.gameStarted = false;
								Intent k = new Intent(context,
										MenuActivity.class);
								startActivity(k);
								finish();
							}
						});

					}
				});

				dialog.show();
				break;

			// Pickup related stuff
			// ------------------------------------------------------------------------------
			case 10:
				updateInventory();
				break;
			case -10:
				Toast pickupError1 = Toast.makeText(context,
						"Your inventory is full!", Toast.LENGTH_LONG);
				pickupError1.show();
				break;
			case -20:
				Toast pickupError2 = Toast.makeText(context,
						"You are too far away from the item.",
						Toast.LENGTH_LONG);
				pickupError2.show();
				break;
			case -30:
				Toast pickupError3 = Toast.makeText(context,
						"Go back to your last correct position first!",
						Toast.LENGTH_LONG);
				pickupError3.show();
				break;
			case -40:
				Toast pickupError4 = Toast.makeText(context,
						"Oops, the item was gone ...", Toast.LENGTH_LONG);
				pickupError4.show();
				itemOverlay.itemDataReady();
				break;

			// Item related stuff
			// ------------------------------------------------------------------------------
			case 101:
				hideUseCancel();
				otherInventory.setVisibility(View.VISIBLE);
				for (int i = 0; i < 6; i++) {
					otherInventorySlots[i].setBackgroundDrawable(GameSession
							.getDrawableByType(GameSession.otherItems[i]));
				}
				break;
			case 102:
				Toast errorBinoculars = Toast.makeText(context,
						"Error: Item not in inventory", Toast.LENGTH_LONG);
				errorBinoculars.show();
				hideUseCancel();
				break;
			case 201:
				hideUseCancel();
				break;
			case 202:
				Toast errorBreaker = Toast.makeText(context,
						"Error: Item not in inventory", Toast.LENGTH_LONG);
				errorBreaker.show();
				hideUseCancel();
				break;

			case 300:
				// just update the view.
				itemOverlay.doPopulate();
				break;
			case 301:
				hideUseCancel();
				itemOverlay.doPopulate();
				break;

			case 302:
				Toast errorMagnet = Toast.makeText(context,
						"Error: Item couldn't be moved", Toast.LENGTH_LONG);
				errorMagnet.show();
				hideUseCancel();
				break;

			case 401:
				Toast freePassSucces = Toast.makeText(context,
						"You used the free pass to get across the maze",
						Toast.LENGTH_LONG);
				freePassSucces.show();
				violationWarn.setVisibility(View.INVISIBLE);
				// tv.setBackgroundColor(Color.BLACK);
				locationManager.removeUpdates(locationListener);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
						locationListener);

				hideUseCancel();
				break;
			case 402:
				Toast errorFreePass1 = Toast.makeText(context,
						"Error: item not in inventory.", Toast.LENGTH_LONG);
				errorFreePass1.show();
				hideUseCancel();
				break;
			case 403:
				Toast errorFreePass2 = Toast.makeText(context,
						"You are not violating a maze currently.",
						Toast.LENGTH_LONG);
				errorFreePass2.show();
				hideUseCancel();
				break;
			case 404:
				Toast errorFreePass3 = Toast.makeText(context,
						"You are too close to a maze to use the item.",
						Toast.LENGTH_LONG);
				errorFreePass3.show();
				hideUseCancel();
				break;

			case 501:
				hideUseCancel();
				break;
			case 502:
				Toast errorRocket = Toast.makeText(context,
						"Error: Item not in inventory.", Toast.LENGTH_LONG);
				errorRocket.show();
				hideUseCancel();

				break;

			case 600:
				// just update the view.
				itemOverlay.doPopulate();
				break;
			case 601:
				hideUseCancel();
				itemOverlay.doPopulate();
				updateInventory();
				break;

			case 602:
				Toast errorTeleport = Toast.makeText(context,
						"Error: Item couldn't be moved", Toast.LENGTH_LONG);
				errorTeleport.show();
				hideUseCancel();
				break;

			}

		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Load the drawables for later drawing and initialize the static arrays
		// in the gameSession class.
		GameSession.Initialize(context);

		// Initialize all views.
		initViews();

		Thread soundloader = new Thread() {
			public void run() {
				// load everything here
				// initialize and load sounds
				SoundManager.getInstance();
				SoundManager.initSounds(context);
				SoundManager.loadSounds();

			}
		};

		soundloader.start();
		
		//Start a thread that will later receive the location updates.
		locationHandlerThread = new LocationHandlerThread();
		locationHandlerThread.start();
		
		// Set the handler in the gamelogic protocol.
		MyGameLogicProtocol.setHandler(handler);

		// Make sure the maps goes to my location directly
		locOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				controller.animateTo(locOverlay.getMyLocation());
			}
		});

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					Message msg = Message.obtain();
					msg.what = 1;
					msg.obj = location;
					locationHandlerThread.locationHandler.sendMessage(msg);
				
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Define a listener which serves for an initialization of an accurate
		// location.
		locationListenerTemp = new LocationListener() {
			public void onLocationChanged(Location location) {
				Message msg = Message.obtain();
				msg.what = 0;
				msg.obj = location;
				locationHandlerThread.locationHandler.sendMessage(msg);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// create: true (create instance), false (join instance)

		if (!GameSession.gameStarted) {

			startPlayerTask(getIntent().getBooleanExtra("create", true));

		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// Auto-generated method stub
		return false;
	}

	// This is done to make sure people don't accidentally press the back button
	// and close the app.
	// Pressing the home button cannot be blocked, but it seems that the
	// activity can survive that for a bit.
	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public void onStop() {
		super.onStop();
		locOverlay.disableMyLocation();
		locOverlay.disableCompass();
		locationManager.removeUpdates(locationListener);
		locationManager.removeUpdates(locationListenerTemp);

	}

	@Override
	public void onRestart() {
		super.onRestart();
		locOverlay.enableMyLocation();
		locOverlay.enableCompass();
		if (!GameSession.gameStarted) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
					locationListenerTemp);

		} else {
			if (GameSession.violationInAction) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 1f, locationListener);

			} else {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, (RADIUS - 0.5f),
						locationListener);

			}
		}
	}

	private boolean loadProperties() {
		Resources resources = getResources();
		InputStream rawResource = resources.openRawResource(R.raw.rabbitmq);
		Properties properties = new Properties();
		try {
			properties.load(rawResource);
			Util.setRabbitMQProperties(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		rawResource = resources.openRawResource(R.raw.xmlrpc);
		properties = new Properties();
		try {
			properties.load(rawResource);
			Util.setXMLRPCProperties(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 3000, 0, "change map mode");
		menu.add(0, 4000, 0, "conter on location");
		menu.getItem(1).setIcon(android.R.drawable.ic_menu_mylocation);
		menu.getItem(0).setIcon(android.R.drawable.ic_menu_mapmode);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (!Character.isLetterOrDigit(source.charAt(i))) {
						return "";
					}
				}
				return null;
			}
		};

		switch (item.getItemId()) {
		case 3000:
			mapView.setSatellite(!mapView.isSatellite());
			break;

		case 4000:
			GeoPoint myLastLocation = locOverlay.getMyLocation();
			if (myLastLocation != null) {
				controller.animateTo(myLastLocation);
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Waiting for a location...", Toast.LENGTH_LONG);
				toast.show();
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * If createInstance is set to true, player creates the instance and joins
	 * it. Otherwise, it just joins it.
	 */
	private void startPlayerTask(boolean createInstance) {
		// retrieve and load properties
		boolean loadOK = loadProperties();
		if (loadOK) {
			// set the name of the Player
			playerName = createInstance ? INSTANCE_CREATOR_NAME
					: INSTANCE_JOINER_NAME;
			// execute the player task
			playerTask = new PlayerTask(GameActivity.this);
			playerTask.execute();
		} else {
			Toast error = new Toast(this);
			error.setText("Properties have not been load properly.");
			error.show();
		}
	}

	// Adds new Maze Corners based on the location/geopoint.
	private void addToMaze(Location location) {
		// This is done to make sure we will not send mixed messages if
		// violationInAction changes its value during one call of this methode.
		boolean violation = GameSession.violationInAction;
		ArrayList<MazeCorner> tempStorage = new ArrayList<MazeCorner>();
		oldTime = newTime;
		newTime = System.currentTimeMillis();
		difference = (newTime - oldTime);
		// tv.setText("Pool: " + pool + " Time since last update: " + difference
		// + "ms");

		/*
		 * Between two ticks a certain distance can be elapsed when going with a
		 * normal speed.
		 * 
		 * Assume walking speed is 1.33333 m/s --> with problems and jitters we
		 * accept 2.5 m/s which gives a 5m radius. This way every time we get a
		 * new spot we should be inside the radius of the old spot and have a
		 * perfect maze.
		 * 
		 * between two ticks we need to check if yhe distance between the old
		 * and new position we create points every 2.5ms (this might look a bit
		 * weird close to the last point.) Add all points to the list.
		 * 
		 * However when one doesn't have enough maze power left we ignore all
		 * points in this period and all further points in the next ticks until
		 * we get a refill of the bar. Probabily these ticks should occure once
		 * every 10-30 seconds.
		 * 
		 * For every maze piece of maze we subtract its size from the maze bar.
		 * 
		 * 
		 * When running we get bigger distances --> more ticks, bar depletes
		 * faster. When walking we get smaller distances --> fewer ticks, bar
		 * depletes slower.
		 */

		double lat = location.getLatitude();
		double lng = location.getLongitude();

		geoPoint = new GeoPoint((int) (lat * 1000000), (int) (lng * 1000000));

		// calculate distance
		if (previousLocation != null) {
			distance = previousLocation.distanceTo(location);
			double latOld = previousLocation.getLatitude();
			double lngOld = previousLocation.getLongitude();

			// create intermediate points.
			// We would need to pay attention to the swap of -179
			// 180
			// 179 for the longitude, but lets just ignore that for
			// now!
			// Nobody plays there anyways ;)

			// Since we are talking very small distances linear
			// interpolation will be fine to calculate intermediate
			// points.

			float latTick = (float) ((Math.abs(latOld - lat) / distance) * RADIUS);
			float lngTick = (float) ((Math.abs(lngOld - lng) / distance) * RADIUS);
			lngTick = (lngOld < lng) ? lngTick : -lngTick;
			latTick = (latOld < lat) ? latTick : -latTick;
			int extraPoints = (int) (distance / RADIUS);
			sufficientMazeLeft = true;
			for (int i = 1; i <= extraPoints; i++) {
				GeoPoint genGeoPoint = new GeoPoint((int) ((latOld + i
						* latTick) * 1000000),
						(int) ((lngOld + i * lngTick) * 1000000));
				distance = distance - RADIUS;
				handlePoint(violation, RADIUS, genGeoPoint, tempStorage, true);
			}
		} else {
			// This is the first location change.
			controller.setCenter(geoPoint);
		}
		handlePoint(violation, distance, geoPoint, tempStorage, false);
		previousLocation = location;
		distance = 0;
		// Send all the data and add it to the storage
		if (playerTask != null)
			playerTask.sendStoredCorners(tempStorage);
		PathStorage.addAll(tempStorage, 1);
	}

	private void updateInventory() {
		for (int i = 0; i < 6; i++) {
			Item current = GameSession.getInventoryPlace(i);
			if (current != null) {
				inventorySlots[i].setBackgroundDrawable(GameSession
						.getDrawableByType(current.type));
				inventorySlots[i].getBackground().setAlpha(255);
				if (GameSession.violationInAction && current.type != 4) {
					// There is a violation, items cannot be used.
					inventorySlots[i].getBackground().setAlpha(55);
					inventorySlots[i].setOnClickListener(null);
				} else {
					// We can set the right onClickListeners.
					inventorySlots[i]
							.setOnClickListener(itemClickListeners[current.type - 1]);
				}
			} else {
				inventorySlots[i].setBackgroundDrawable(null);
				inventorySlots[i].setOnClickListener(null);
			}
		}
	}

	private void handlePoint(boolean violation, float distance, GeoPoint gp,
			ArrayList<MazeCorner> tempStorage, boolean generated) {
		if (violation) {
			// violation happened, we are just forwarding)
			// Do something similar as when maze is empty, just forward the
			// whole thing. Also set the last GeoPoint to this location as it
			// might be
			// used when the violation is corrected.
			previousGeoPoint = gp;
			MazeCorner mc = new MazeCorner(gp, pathIsUnbroken, generated, true,
					-1);
			tempStorage.add(mc);
			return;
		}
		if (distance < pool && sufficientMazeLeft) {
			if (pathIsUnbroken == false) {
				// We are drawing a point since the pool is full enough, however
				// the previous points are hidden. This means we need to add an
				// additional point to be able to draw a line to this point.
				MazeCorner mc = new MazeCorner(previousGeoPoint,
						pathIsUnbroken, false);
				tempStorage.add(mc);
			}

			pool = pool - distance;
			pathIsUnbroken = true;
			MazeCorner mc = new MazeCorner(gp, pathIsUnbroken, generated);
			tempStorage.add(mc);
		} else {
			if (sufficientMazeLeft == true) {
				// This is the first time we get here, we need to modify the
				// last entry. Generated needs to be false so that the last
				// point is actually drawn.
				if (tempStorage.size() >= 1) {
					tempStorage.get(tempStorage.size() - 1).generated = false;
				}
			}
			pathIsUnbroken = false;
			sufficientMazeLeft = false;
			MazeCorner mc = new MazeCorner(gp, pathIsUnbroken, generated, true,
					-1);
			tempStorage.add(mc);

		}
		previousGeoPoint = gp;
	}

	private void initViews() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(false);
		// tv = (TextView) findViewById(R.id.editText1);
		batteryView = (View) findViewById(R.id.batteryView);
		controller = mapView.getController();
		controller.setZoom(17);
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(new PathOverlay(RADIUS));
		locOverlay = new MyLocationOverlay(this, mapView);
		locOverlay.enableCompass();
		locOverlay.enableMyLocation();
		mapOverlays.add(locOverlay);
		Drawable d = this.getResources().getDrawable(R.drawable.ic_launcher);
		d.setBounds(0, 0, 5, 5);
		itemOverlay = new GameItemOverlay(d, handler, mapView);
		mapOverlays.add(itemOverlay);
		gameTime = (TextView) findViewById(R.id.GameCountdown);
		gameTime.setBackgroundColor(Color.GRAY);
		gameTime.setVisibility(View.INVISIBLE);
		violationWarn = (TextView) findViewById(R.id.violation);
		violationWarn.setVisibility(View.INVISIBLE);
		gameTime.setVisibility(View.INVISIBLE);
		connectionFeedbackView = (View) findViewById(R.id.connectionFeedback);
		zoomMinusButton = (Button) findViewById(R.id.zoomMinusButton);
		zoomPlusButton = (Button) findViewById(R.id.zoomPlusButton);
		useButton = (Button) findViewById(R.id.useButton);
		useButton.setVisibility(View.INVISIBLE);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setVisibility(View.INVISIBLE);
		zoomMinusButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				controller.zoomOut();
			}

		});
		zoomPlusButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				controller.zoomIn();
			}

		});
		inventorySlots[0] = (View) findViewById(R.id.button0);
		inventorySlots[1] = (View) findViewById(R.id.button1);
		inventorySlots[2] = (View) findViewById(R.id.button2);
		inventorySlots[3] = (View) findViewById(R.id.button3);
		inventorySlots[4] = (View) findViewById(R.id.button4);
		inventorySlots[5] = (View) findViewById(R.id.button5);

		otherInventory = (View) findViewById(R.id.otherItemLayout);
		otherInventorySlots[0] = (View) findViewById(R.id.Item0);
		otherInventorySlots[1] = (View) findViewById(R.id.Item1);
		otherInventorySlots[2] = (View) findViewById(R.id.Item2);
		otherInventorySlots[3] = (View) findViewById(R.id.Item3);
		otherInventorySlots[4] = (View) findViewById(R.id.Item4);
		otherInventorySlots[5] = (View) findViewById(R.id.Item5);

		itemClickListeners[0] = new OnClickListener() {
			// binoculars
			public void onClick(View v) {
				showUseCancel(useClickListeners[0], cancelClickListeners[0], v);

			}

		};
		useClickListeners[0] = new OnClickListener() {
			// binoculars
			public void onClick(View v) {
				playerTask.binoculars();
				// Remove the onClicklistener to avoid multiple uses
				// of an item.
				v.setOnClickListener(null);

			}

		};
		cancelClickListeners[0] = new OnClickListener() {
			// binoculars
			public void onClick(View v) {
				hideUseCancel();
			}

		};

		itemClickListeners[1] = new OnClickListener() {
			// breaker
			public void onClick(View v) {
				GameSession.selectingForBreaker = true;
				showUseCancel(useClickListeners[1], cancelClickListeners[1], v);
			}

		};
		useClickListeners[1] = new OnClickListener() {
			// breaker
			public void onClick(View v) {
				if (GameSession.selectedPoint != null) {
					// Send point to server wait and do nothing
					playerTask.breakMaze(GameSession.selectedPoint);
					// Remove the onClicklistener to avoid multiple uses
					// of an item.
					v.setOnClickListener(null);
				} else {
					Toast warning = Toast.makeText(context, "Select an Point",
							Toast.LENGTH_SHORT);
					warning.show();
				}

			}

		};
		cancelClickListeners[1] = new OnClickListener() {
			// breaker
			public void onClick(View v) {
				GameSession.selectedPoint = null;
				GameSession.selectingForBreaker = false;
				hideUseCancel();
			}

		};

		itemClickListeners[2] = new OnClickListener() {
			// magnet
			public void onClick(View v) {
				GameSession.selectingForMagnet = true;
				showUseCancel(useClickListeners[2], cancelClickListeners[2], v);

			}

		};
		useClickListeners[2] = new OnClickListener() {
			// magnet
			public void onClick(View v) {
				if (GameSession.selectedItem != null) {
					// Send item and current location to server wait and do
					// nothing
					playerTask.drawItem(GameSession.selectedItem, geoPoint);
					// remove the view if the result comes back positiv
					// or if the result is negative do nothing
					// However remove the onClicklistener to avoid multiple uses
					// of an item.
					v.setOnClickListener(null);
				} else {
					Toast warning = Toast.makeText(context,
							"Select an item first", Toast.LENGTH_SHORT);
					warning.show();
				}

			}

		};
		cancelClickListeners[2] = new OnClickListener() {
			// magnet
			public void onClick(View v) {
				GameSession.selectedItem = null;
				GameSession.selectingForMagnet = false;
				hideUseCancel();
			}

		};

		itemClickListeners[3] = new OnClickListener() {
			// pass
			public void onClick(View v) {
				if (GameSession.violationInAction) {
					// It is useful to use the item. We present the use/ cancel
					// button.
					showUseCancel(useClickListeners[3],
							cancelClickListeners[3], v);
				} else {
					// There is no used for the item. We just show a quick
					// message.
					Toast passNotNeeded = Toast
							.makeText(
									context,
									"You did not violate the other player's maze. Using a free pass makes no sense.",
									Toast.LENGTH_LONG);
					passNotNeeded.show();
				}
			}

		};
		useClickListeners[3] = new OnClickListener() {
			// pass
			public void onClick(View v) {
				// Make sure the maze is still violated.
				if (GameSession.violationInAction) {
					// It is useful to use the item. We send a message to the
					// server.
					playerTask.freePass(geoPoint);
					// Remove the onClicklistener to avoid multiple uses
					// of an item.
					v.setOnClickListener(null);
				} else {
					// There is no used for the item. The violation was already
					// corrected.
					Toast passNotNeeded = Toast.makeText(context,
							"You already corrected the proximity violation.",
							Toast.LENGTH_LONG);
					passNotNeeded.show();
					hideUseCancel();
				}

			}

		};
		cancelClickListeners[3] = new OnClickListener() {
			// pass
			public void onClick(View v) {
				hideUseCancel();
			}

		};

		itemClickListeners[4] = new OnClickListener() {
			// rocket
			public void onClick(View v) {
				showUseCancel(useClickListeners[4], cancelClickListeners[4], v);

			}

		};
		useClickListeners[4] = new OnClickListener() {
			// rocket
			public void onClick(View v) {
				playerTask.rocket();
				// Remove the onClicklistener to avoid multiple uses
				// of an item.
				v.setOnClickListener(null);

			}

		};
		cancelClickListeners[4] = new OnClickListener() {
			// rocket
			public void onClick(View v) {
				hideUseCancel();

			}

		};

		itemClickListeners[5] = new OnClickListener() {
			// teleportation
			public void onClick(View v) {
				GameSession.selectingForTeleport = true;
				showUseCancel(useClickListeners[5], cancelClickListeners[5], v);
			}

		};
		useClickListeners[5] = new OnClickListener() {
			// teleportation
			public void onClick(View v) {
				if (GameSession.selectedItem != null) {
					// Send item to server wait and do nothing
					playerTask.teleportItem(GameSession.selectedItem);
					// remove the view if the result comes back positiv
					// or if the result is negative do nothing
					// However remove the onClicklistener to avoid multiple uses
					// of an item.
					v.setOnClickListener(null);
				} else {
					Toast warning = Toast.makeText(context,
							"Select an item first", Toast.LENGTH_SHORT);
					warning.show();
				}

			}

		};
		cancelClickListeners[5] = new OnClickListener() {
			// teleportation
			public void onClick(View v) {
				GameSession.selectedItem = null;
				GameSession.selectingForTeleport = false;
				hideUseCancel();
			}

		};

	}

	private void showUseCancel(OnClickListener use, OnClickListener cancel,
			View clicked) {
		useButton.setVisibility(View.VISIBLE);
		cancelButton.setVisibility(View.VISIBLE);
		useButton.setOnClickListener(use);
		cancelButton.setOnClickListener(cancel);
		for (int i = 0; i < inventorySlots.length; i++) {
			if (!clicked.equals(inventorySlots[i])) {
				Drawable background = inventorySlots[i].getBackground();
				if (background != null)
					background.setAlpha(55);
			}
			inventorySlots[i].setOnClickListener(null);
		}
	}

	private void hideUseCancel() {
		useButton.setVisibility(View.INVISIBLE);
		cancelButton.setVisibility(View.INVISIBLE);
		updateInventory();
	}

	public void connected() {
		connectionFeedbackView.setBackgroundColor(Color.GREEN);
	}

	public void reconnectionTry(int numberOfRetries) {
		if (numberOfRetries < 3) {
			connectionFeedbackView.setBackgroundColor(Color.YELLOW);
		} else if (numberOfRetries < 5) {
			connectionFeedbackView.setBackgroundColor(Color.argb(255, 255, 127,
					0));
		} else {
			connectionFeedbackView.setBackgroundColor(Color.RED);
		}
	}

	public void disconnected() {
		connectionFeedbackView.setBackgroundColor(Color.GRAY);
	}

	class LocationHandlerThread extends Thread {
		public Handler locationHandler;

		public void run() {
			Looper.prepare();

			locationHandler = new Handler() {
				public void handleMessage(Message msg) {
					if(msg.what == 0){
						//This is a message received from the first location listener
						// Here we just check the accuracy.
						Location loc = (Location) msg.obj;
						if (loc.getAccuracy() < 25) {
							playerTask.locationIsAccurate(loc,
									loc.getAccuracy());
						}
						controller.animateTo(new GeoPoint(
								(int) (loc.getLatitude() * 1000000),
								(int) (loc.getLongitude() * 1000000)));
					}else{
						//This is a message received from the normal location listener
						// We need to add this location to the maze. 
						addToMaze((Location) msg.obj);
					}
					
					
				}
			};

			Looper.loop();
		}
	}

}