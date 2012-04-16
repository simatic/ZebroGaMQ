/**
 ZebroGaMQ: Communication Middleware for Mobile Gaming
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

package zebrogamq.integration.android;

import java.io.IOException;

import zebrogamq.gamelogic.ChannelsManager;
import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.JoinAction;
import zebrogamq.gamelogic.PresenceAction;
import zebrogamq.gamelogic.Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;


class GameLogicTask extends AsyncTask<Void, Integer, Integer> {

	private static final int RESULT_OK			= 0;
	private static final int RESULT_ERROR		= 1;
	private static final int COMPUTING_CALL		= 10;
	private static final int COMPUTING_ANSWER	= 20;
	

	private GameLogicState			state;
	private GameLogicApplication 	activity;
	private ProgressDialog 			dialog;

	
	public GameLogicTask(GameLogicApplication activity){
		this.activity = activity;
		state = new GameLogicState();
		state.login = activity.playerName;
		state.password = GameLogicApplication.DEFAULT_PWD;
		state.gameName = "Tidy-City";
		state.instanceName = "Instance-1";
	}

	
	@Override
	protected void onPostExecute(Integer result) {
		dialog.dismiss();
		switch (result)
		{
		case RESULT_OK:
			break;
		case RESULT_ERROR:
			Dialog.showMessage(activity,"Error on creation of game instance.");
			break;
		}
	}

	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(activity, "Loading", "Joining game instance...",true,true,new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				cancel(true);
			}});
	}

	
	@Override
	protected void onCancelled() {
		Toast.makeText(activity, "Instance joining canceled", Toast.LENGTH_SHORT).show();
	}

	
	@Override
	protected void onProgressUpdate(Integer... values) {
		switch (values[0])
		{
		case COMPUTING_CALL:
			dialog.setMessage("Login to GameServer...");
			break;
		case COMPUTING_ANSWER:
			dialog.setMessage("Creating connection to GameLogicServer...");
			break;
		}
	}

	
	@Override
	protected Integer doInBackground(Void... params) {
		publishProgress(COMPUTING_CALL);
		boolean loggedIn = executeXMLRPCLogin();
		if(loggedIn){
			publishProgress(COMPUTING_ANSWER);
			initChannelsManager();
			// launch the participant list thread
			startParticipantListThread();
			// start the number of retries thread
			startNumberOfRetriesThread();
			return RESULT_OK;
		}else{
			return RESULT_ERROR;
		}
	}

	
	static class Dialog {		
		public static void showMessage(Context context,String msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg)
			.setCancelable(false)
			.setPositiveButton("Close",null);
			AlertDialog alert = builder.create();
			alert.show(); 
		}
	}
	
	
	private boolean executeXMLRPCLogin(){
		boolean res = false;
		if(state.login.equals(GameLogicApplication.INSTANCE_CREATOR_NAME)){
			res = XMLRPCLogin.createAndJoinGameInstance(state.login, state.password, state.gameName, state.instanceName);
		}else{
			res = XMLRPCLogin.joinGameInstance(state.login, state.password, state.gameName, state.instanceName);
		}
		return res;
	}
	
	/*
	 * This method successively:
	 * - instantiate the channelsManager of the state in order to 
	 *   start the consumption and to enable publication of messages.
	 * - publish a JOIN message to the GameLogicServer. By default,
	 *   on the reception of this message, there is only a message
	 *   displayed. If you want to define your own behavior on the 
	 *   reception of this message, please refer to the javadoc of 
	 *   the class OptionalDelegationOfStandardActions.
	 *   (http://simatic.github.com/ZebroGaMQ/doc/javadoc/zebrogamq/gamelogic/OptionalDelegationOfStandardActions.html)
	 */
	private void initChannelsManager(){
		try {
			// Instantiate the channelsManager
			state.channelsManager = ChannelsManager.getInstance(state, MyListOfGameLogicActions.ListOfActionsMaps);
			String content = state.login + "," + state.gameName + "," + state.instanceName;
			state.channelsManager.publishToGameLogicServer(state, JoinAction.JOIN, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void startParticipantListThread() {
		new Thread() {
			public void run() {
				while (!state.hasConnectionExited()) {
					try {
						state.channelsManager.publishToGameLogicServer(state, PresenceAction.ASK_PARTICIPANTS_LIST, " ");
					} catch (IOException e) {
						e.printStackTrace();
					}
					// wait for a while
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						Util.println(" [Player " + state.login
								+ "] Thread sleep was interrupted");
					}
				}
			}
		}.start();
	}
	
	
	// The use of runOnUiThread is here to avoid the handler mecanism
	// for the communication between this thread and UI thread.
	private void startNumberOfRetriesThread() {
		new Thread() {
			public void run() {
				while(!state.hasConnectionExited()) {
					synchronized (state) {
						if(state.numberOfRetries == 0){
							// display connection message
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.connected();	
								}
							});
						}else {
							// display disconnection message
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.reconnectionTry("Disconnected, number of retries = "+state.numberOfRetries);	
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
					@Override
					public void run() {
						activity.disconnected();	
					}
				});
			}
		}.start();
	}
	
	void exit(){
		state.connectionExit();
	}

}
