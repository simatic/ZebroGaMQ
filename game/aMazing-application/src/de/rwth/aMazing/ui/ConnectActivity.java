/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for 
 * Mobile Devices" at Fraunhofer FIT (http://www.fit.fraunhofer.de).
 * Copyright (C) 2012  Alexander Hermans, Tianjiao Wang
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

import de.rwth.aMazing.GameSession;
import de.rwth.aMazing.GameActivity;
import de.rwth.aMazing.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ConnectActivity extends Activity{
    /** Called when the activity is first created. */
    private Context context;
	

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.connect_server_dialog);

        //set up button
      	 InputFilter[] filters = new InputFilter[1];
 		 filters[0] = new InputFilter() {
 			public CharSequence filter(CharSequence source, int start,
 					int end, Spanned dest, int dstart, int dend) {
 				for (int i = start; i < end; i++) {
 					if (!Character.isLetterOrDigit(source.charAt(i))) {
 						return "";
 					}
 				}
 				return null;
 			}
 		}; 
		final EditText instanceName = (EditText) findViewById(R.id.instanceEditText);
        instanceName.setFilters(filters);
        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new OnClickListener() {


		public void onClick(View v) {
       	
       
					if (!instanceName.getText().toString().equals("")) {
						// Not empty
						GameSession.instanceName = instanceName.getText()
								.toString();
						Intent k = new Intent(context, GameActivity.class);
						k.putExtra("create", true);
						startActivity(k);
					} else {
						Toast emptyField = Toast.makeText(context,
								"You need to specify an instance name!",
								Toast.LENGTH_LONG);
						emptyField.show();
					}
				}
			});


        
        Button join = (Button) findViewById(R.id.join);
        join.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
					if (!instanceName.getText().toString().equals("")) {
						// Not empty
						GameSession.instanceName = instanceName.getText()
								.toString();
						Intent k = new Intent(context, GameActivity.class);
						k.putExtra("create", false);
						startActivity(k);

					} else {
						Toast emptyField = Toast.makeText(context,
								"You need to specify an instance name!",
								Toast.LENGTH_LONG);
						emptyField.show();

					}
				}
			});		
       
        
		   
    }
      
	

	
}
