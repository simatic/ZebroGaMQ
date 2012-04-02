package eu.totem.communication;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import net.totem.gamelogic.Util;
import net.totem.gamelogic.player.ChannelsManager;
import net.totem.gamelogic.player.JoinAction;
import net.totem.gamelogic.player.PlayerState;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

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

	private PlayerState state;
	private GameActivity activity;
	private ProgressDialog dialog;

	public PlayerTask(GameActivity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPostExecute(Integer result) {
		dialog.dismiss();
		switch (result) {
		case RESULT_OK:

			// start the number of retries thread
			startNumberOfRetriesThread();
            if(activity.playerName.equals(GameActivity.INSTANCE_CREATOR_NAME)){
    			Toast wait = Toast.makeText(activity, "Wait for another player to join",
    					Toast.LENGTH_LONG);
    			wait.show();
            }
			playerReady();
			break;
		case RESULT_ERROR:
			//Dialog.showMessage(activity, "Error on creation of game instance.");
			AlertDialog.Builder dialog = new AlertDialog.Builder(activity);	    
		    
		    dialog.setMessage("Error on creation of game instance. Either the instance name is in use or the server is down.");

		   dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
			dialog.setMessage("XML-RPC call...");
			break;
		case COMPUTING_ANSWER:
			dialog.setMessage("Computing XML-RPC answer...");
			break;
		}
	}

	@Override
	protected Integer doInBackground(Void... params) {
		publishProgress(COMPUTING_CALL);
		Object answer = executeXMLRPCCall();
		publishProgress(COMPUTING_ANSWER);
		boolean res = computeXMLRPCAnswer(answer);
		if (res) {
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

	private Object executeXMLRPCCall() {
		Object answer = null;
		URI uri = URI.create("http://"
				+ Util.getXMLRPCProperties()
						.getProperty("gameServerXMLRPCHost")
				+ ":"
				+ Util.getXMLRPCProperties()
						.getProperty("gameServerXMLRPCPort"));
		XMLRPCClient client = new XMLRPCClient(uri);
		state = new PlayerState();
		state.login = activity.playerName;
		state.password = GameActivity.DEFAULT_PWD;
		state.gameName = "aMazing";
		state.gameInstanceName = GameSession.instanceName;
		int maxRetry = Integer.valueOf(Util.getXMLRPCProperties().getProperty(
				"maxRetry"));
		// switch the request depending on the player name
		String request = state.login
				.equals(GameActivity.INSTANCE_CREATOR_NAME) ? "createGameInstance"
				: "joinPlayerGameInstance";
		for (int i = 0; i < maxRetry; i++) {
			if (!state.hasConnectionExited()) {
				try {
					answer = client.call(request, state.login, state.password,
							state.gameName, state.gameInstanceName);
					break;
				} catch (XMLRPCException e) {
					e.printStackTrace();
				}
				// sleep 1 second before retrying
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return answer;
	}

	private boolean computeXMLRPCAnswer(Object answer) {
		boolean res = false;
		if (answer == null) {
			Util.println("The server didn't return XML.");
			return res;
		} else if (!(answer instanceof Boolean)) {
			Util.println("The procedure didn't return a boolean.");
			return res;
		} else if (((Boolean) answer) == Boolean.TRUE) {
			Util.println("XMLRPC call has succeeded for player " + state.login
					+ " in " + state.gameName + "/" + state.gameInstanceName);
			try {
				// Instantiate the channelsManager
				state.channelsManager = ChannelsManager.getInstance(state,
						MyListOfGameLogicActions.ListOfActionsMaps);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				state.channelsManager.publishToGameLogicServer(
						state,
						JoinAction.JOIN_PLAYER,
						state.login
								+ Util.getRabbitMQProperties().getProperty(
										"bodySeparator")
								+ state.gameName
								+ Util.getRabbitMQProperties().getProperty(
										"bodySeparator")
								+ state.gameInstanceName);
				res = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		} else {
			Util.println("XMLRPC call has failed for player " + state.login
					+ " in " + state.gameName + "/" + state.gameInstanceName);
			Util.println(" [Player " + state.login + "] Exiting ");
			return false;
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
