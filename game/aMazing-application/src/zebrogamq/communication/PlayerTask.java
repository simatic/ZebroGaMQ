/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for
 * Mobile Devices" at Fraunhofer FIT.
 *
 * http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
 * http://www.totem-games.org/?q=aMazing
 *
 * Copyright (C) 2012 Alexander Hermans, Tianjiao Wang
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Developer(s): Alexander Hermans, Tianjiao Wang
 * ZebroGaMQ: Denis Conan, Gabriel Adgeg
 */

package zebrogamq.communication;

import java.io.IOException;
import java.util.ArrayList;

import zebrogamq.gamelogic.ChannelsManager;
import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.JoinAction;
import zebrogamq.gamelogic.Util;

import com.google.android.maps.GeoPoint;

import de.rwth.aMazing.GameActivity;
import de.rwth.aMazing.GameSession;
import de.rwth.aMazing.Item;
import de.rwth.aMazing.MazeCorner;
import de.rwth.aMazing.PathStorage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

public class PlayerTask extends AsyncTask<Void, Integer, Integer> {

	private static final int RESULT_OK = 0;
	private static final int RESULT_ERROR = 1;
	private static final int COMPUTING_CALL = 10;
	private static final int COMPUTING_ANSWER = 20;

	private GameLogicState state;
	private GameActivity activity;
	private ProgressDialog dialog;

	public PlayerTask(GameActivity activity) {
		this.activity = activity;
		state = new GameLogicState();
		state.login = activity.playerName;
		state.password = GameActivity.DEFAULT_PWD;
		state.gameName = "aMazing";
		state.instanceName = GameSession.instanceName;
	}

	@Override
	protected void onPostExecute(Integer result) {
		dialog.dismiss();
		switch (result) {
		case RESULT_OK:

			// start the number of retries thread
			startNumberOfRetriesThread();
			if (activity.playerName.equals(GameActivity.INSTANCE_CREATOR_NAME)) {
				Toast wait = Toast.makeText(activity,
						"Wait for another player to join", Toast.LENGTH_LONG);
				wait.show();
			}
			playerReady();
			break;
		case RESULT_ERROR:
			// Dialog.showMessage(activity,
			// "Error on creation of game instance.");
			AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

			dialog.setMessage("Could not create or join this instance. \n It is either in use or there is a problem with the server.");

			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							activity.finish();
						}
					});

			dialog.show();

			break;
		}
	}

	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(activity, "Loading",
				"Joining game instance...", true, true, new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
						cancel(true);
					}
				});
	}

	@Override
	protected void onCancelled() {
		Toast.makeText(activity, "Instance joining canceled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		switch (values[0]) {
		case COMPUTING_CALL:
			dialog.setMessage("Trying to connect to the server...");
			break;
		case COMPUTING_ANSWER:
			dialog.setMessage("Starting instance...");
			break;
		}
	}

	@Override
	protected Integer doInBackground(Void... params) {
		publishProgress(COMPUTING_CALL);
		boolean loggedIn = executeXMLRPCLogin();
		if (loggedIn) {
			publishProgress(COMPUTING_ANSWER);
			initChannelsManager();
			return RESULT_OK;
		} else {
			return RESULT_ERROR;
		}
	}

	static class Dialog {
		public static void showMessage(Context context, String msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg).setCancelable(false)
					.setPositiveButton("Close", null);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private boolean executeXMLRPCLogin() {
		boolean res = false;
		if (state.login.equals(GameActivity.INSTANCE_CREATOR_NAME)) {
			res = XMLRPCLogin.createAndJoinGameInstance(state.login,
					state.password, state.gameName, state.instanceName);
		} else {
			res = XMLRPCLogin.joinGameInstance(state.login, state.password,
					state.gameName, state.instanceName);
		}
		return res;
	}

	private void initChannelsManager() {
		try {
			// Instantiate the channelsManager
			state.channelsManager = ChannelsManager.getInstance(state,
					MyListOfGameLogicActions.ListOfActionsMaps);
			String content = state.login + "," + state.gameName + ","
					+ state.instanceName;
			state.channelsManager.publishToGameLogicServer(state,
					JoinAction.JOIN, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// The use of runOnUiThread is here to avoid the handler mechanism
	// for the communication between this thread and UI thread.
	private void startNumberOfRetriesThread() {
		new Thread() {
			public void run() {
				while (!state.hasConnectionExited()) {
					synchronized (state) {
						if (state.numberOfRetries == 0) {
							// display connection message
							activity.runOnUiThread(new Runnable() {
								public void run() {
									activity.connected();
								}
							});
						} else {
							// display disconnection message
							activity.runOnUiThread(new Runnable() {
								public void run() {
									activity.reconnectionTry(state.numberOfRetries);
								}
							});
						}
					}
					// wait for a while
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Util.println(" [Player " + state.login
								+ "] Thread sleep was interrupted");
					}
				}
				// display disconnection message
				activity.runOnUiThread(new Runnable() {
					public void run() {
						activity.disconnected();
					}
				});
			}
		}.start();
	}

	void exit() {
		state.connectionExit();
	}

	public void playerReady() {
		PathStorage.reset();
		GameSession.reset();
		try {
			String sessionSetupData = "";
			if (state.login.equals(GameActivity.INSTANCE_CREATOR_NAME)) {
				sessionSetupData = GameSession.itemNumberSetting + "*"
						+ GameSession.crownNumberSetting + "*"
						+ GameSession.timeInMiliSetting + "*"
						+ GameSession.districtSizeInMetersSetting;
			}
			state.channelsManager.publishToGameLogicServer(state,
					AmazingActionKind.PLAYER_READY, sessionSetupData);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendStoredCorners(ArrayList<MazeCorner> tempStorage) {
		String pendingData = "";
		for (int i = 0; i < tempStorage.size() - 1; i++) {
			pendingData += tempStorage.get(i).toString() + "*";
		}
		pendingData += tempStorage.get(tempStorage.size() - 1).toString();
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.SEND_GPS_COORDINATES, pendingData);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void locationIsAccurate(Location location, float accuricy) {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.LOCATION_IS_ACCURATE,
						location.getLatitude() + "*" + location.getLongitude()
								+ "*" + accuricy);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void pickupItem(int id, GeoPoint geoPoint) {
		if (geoPoint != null) { // Otherwise we just ignore the call.
			String itemRequest = id + "*" + geoPoint.getLatitudeE6() + "*"
					+ geoPoint.getLongitudeE6();

			if (!state.hasConnectionExited()) {
				try {
					state.channelsManager.publishToGameLogicServer(state,
							AmazingActionKind.ITEM_REQUEST, itemRequest);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void finishGame() {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.FINISH_GAME, "");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void teleportItem(Item selectedItem) {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.TELEPORT_ITEM,
						"" + selectedItem.getID());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void drawItem(Item selectedItem, GeoPoint geoPoint) {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.DRAW_ITEM, "" + selectedItem.getID()
								+ "*" + geoPoint.getLatitudeE6() + "*"
								+ geoPoint.getLongitudeE6());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void freePass(GeoPoint geoPoint) {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.FREE_PASS, geoPoint.getLatitudeE6()
								+ "*" + geoPoint.getLongitudeE6());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void breakMaze(GeoPoint selectedPoint) {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(
						state,
						AmazingActionKind.BREAK_MAZE,
						selectedPoint.getLatitudeE6() + "*"
								+ selectedPoint.getLongitudeE6());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void binoculars() {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.BINOCULARS, "");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void rocket() {
		if (!state.hasConnectionExited()) {
			try {
				state.channelsManager.publishToGameLogicServer(state,
						AmazingActionKind.ROCKET, "");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}