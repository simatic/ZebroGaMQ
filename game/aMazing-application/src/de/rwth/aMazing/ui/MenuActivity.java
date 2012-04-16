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

package de.rwth.aMazing.ui;

import de.rwth.aMazing.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class MenuActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	public static final String PREFS_NAME = "sampleGameSettings";		
	public static Typeface tf;
	

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uimain);
        
        tf = Typeface.createFromAsset(getAssets(),"data/fonts/Home Bold.ttf");
        
        TextView tv = (TextView) findViewById(R.id.start);
        TextView tv1 = (TextView) findViewById(R.id.ins);
        TextView tv2 = (TextView) findViewById(R.id.settings);
        TextView tv3 = (TextView) findViewById(R.id.about);
        TextView tv4 = (TextView) findViewById(R.id.exit);
        
        tv.setTypeface(tf);
        tv1.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);
        tv4.setTypeface(tf);
        
        tv.setOnTouchListener(new CustomTouchListener());
        tv1.setOnTouchListener(new CustomTouchListener());
        tv2.setOnTouchListener(new CustomTouchListener());
        tv3.setOnTouchListener(new CustomTouchListener());
        tv4.setOnTouchListener(new CustomTouchListener());
        
        tv.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);    
        
		checkIfGPSIsAvailable();
        
		   
    }
      

	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.start:
				Intent k = new Intent(this, ConnectActivity.class);
				startActivity(k);
				break;
			case R.id.ins:
				Intent i = new Intent(this, HelpActivity.class);
				startActivity(i);
				break;
			case R.id.settings:
				Intent j = new Intent(this, SettingsActivity.class);
				startActivity(j);
				break;
			case R.id.about:
				makeDialog();
				break;	
			case R.id.exit:
				finish();
				break;
		}
		
	}
	
	private void makeDialog() {		
		
	    AlertDialog.Builder dialog = new AlertDialog.Builder(this);	    
	    
	    dialog.setMessage("This is for the Mixed Reality Game Design lab in Fraunhofer");

	    dialog.setPositiveButton("More", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface arg0, int arg1) {
	        	Toast.makeText(getBaseContext(), "By Alex and Tian", Toast.LENGTH_LONG).show();
	        }
	    });
	
	    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface arg0, int arg1) {}
	    });
	
	    dialog.show();
	}
	
	private void checkIfGPSIsAvailable() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showAlertMessageNoGps();
        }
        else{
        	Toast.makeText(this, "GPS is already on", Toast.LENGTH_SHORT).show();
        }
    }

     private void showAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on GPS.")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(
                                                final DialogInterface dialog,
                                                final int id) {
                                        Intent intent = new Intent(
                                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent, 0);
                                }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
      }
	
}
