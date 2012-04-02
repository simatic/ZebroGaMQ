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
