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
import java.io.InputStream;
import java.util.Properties;

import zebrogamq.gamelogic.Log;
import zebrogamq.gamelogic.Util;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class GameLogicApplication extends Activity implements Log {
	
	final static 	String		INSTANCE_CREATOR_NAME 	= "PLAYER_1";
	final static 	String		INSTANCE_JOINER_NAME 	= "PLAYER_2";
	final static 	String		DEFAULT_PWD 			= "ufGf64";
	final static	String		CLASS_NAME				= GameLogicApplication.class.getSimpleName();
	
	private 		TextView 				connectionTextView;
	private 		TextView 				myTextView;
	private 		ScrollView 				scroller;
	private 		MessageDisplayHandler 	handler;
	private 		GameLogicTask			playerTask;
	
	String			playerName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// retrieve text view
		connectionTextView = (TextView) findViewById(R.id.connectionTextView);
		// retrieve scroll view
		scroller = (ScrollView) findViewById(R.id.scroller);
		// retrieve text view
		myTextView = (TextView) findViewById(R.id.myTextView);
		// instantiate handler
		handler = MessageDisplayHandler.getInstance(this);
		// initialize attributes
		Util.setLogger(this);
		// display indications to the player
		Util.println("Press the Menu button to create or to join a game instance.");
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// clear text view
		myTextView.setText("");
		// stop the threads
		if(playerTask != null){
			playerTask.exit();
		}
		// delete Logger for the next execution
		Util.removeLogger();
	}
	
	
	private boolean loadProperties(){
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
		menu.add(0, 1000, 0, "Create Instance");
		menu.add(0, 2000, 0, "Join Instance");
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1000:
			// create and join the instance
			startPlayerTask(true);
			break;

		case 2000:
			// just join the instance
			startPlayerTask(false);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/*
	 * If createInstance is set to true, player creates the instance and joins it.
	 * Otherwise, it just joins it.
	 */
	private void startPlayerTask(boolean createInstance){
		// retrieve and load properties
		boolean loadOK = loadProperties();
		if(loadOK){
			// set the name of the Player
			playerName = createInstance ? INSTANCE_CREATOR_NAME : INSTANCE_JOINER_NAME;
			// execute the player task
			playerTask = new GameLogicTask(GameLogicApplication.this);
			playerTask.execute();
		}else{
			println("Properties have not been load properly.");
		}
	}

	
	public void println(String msg) {
		// create message
		Message message = Message.obtain(handler,
				MessageDisplayHandler.PRINT_STRING_WHAT);
		// create and set data
		Bundle b = new Bundle();
		// first parameter is the key. It should be used to replace an existing
		// value in case of reusing of the same bundle.
		b.putByteArray(null, msg.getBytes());
		message.setData(b);
		handler.sendMessage(message);
		android.util.Log.i(CLASS_NAME, msg);
	}
	
	
	void printMessage(String message) {
		myTextView.append(message + "\n\n");
		scrollDown();
	}
	
	
	// scroll text view to the bottom
	private void scrollDown() {
        scroller.smoothScrollTo(0, myTextView.getBottom());  
	}
	
	
	void connected(){
		connectionTextView.setText("Connected");
		connectionTextView.setTextColor(Color.GREEN);
	}
	
	
	void reconnectionTry(String message){
		connectionTextView.setText(message);
		connectionTextView.setTextColor(Color.YELLOW);
	}
	
	
	void disconnected(){
		connectionTextView.setText("Disconnected");
		connectionTextView.setTextColor(Color.RED);
	}
}