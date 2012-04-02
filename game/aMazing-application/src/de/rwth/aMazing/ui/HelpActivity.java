package de.rwth.aMazing.ui;
import de.rwth.aMazing.R;

import android.app.Activity;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private TextView[] tv = new TextView[3];
	private ImageView[] image = new ImageView[6];
//	private TextView tv, tv1, tv2, tv3, tv4, tv5;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);        
             
        tv[0] = (TextView) findViewById(R.id.previous);
        tv[1] = (TextView) findViewById(R.id.skip);
        tv[2] = (TextView) findViewById(R.id.next);
        
        tv[0].setOnTouchListener(new CustomTouchListener());
        tv[1].setOnTouchListener(new CustomTouchListener());
        tv[2].setOnTouchListener(new CustomTouchListener());
        
        tv[0].setOnClickListener(this);
        tv[1].setOnClickListener(this);
        tv[2].setOnClickListener(this);
        
        image[0] = (ImageView) findViewById(R.id.help1);
        image[1] = (ImageView) findViewById(R.id.help2);
        image[2] = (ImageView) findViewById(R.id.help3);
        image[3] = (ImageView) findViewById(R.id.help4);
        image[4] = (ImageView) findViewById(R.id.help5);
        image[5] = (ImageView) findViewById(R.id.help6);
        
        
        tv[0].setVisibility(View.INVISIBLE);
        image[0].setVisibility(View.VISIBLE);
        image[1].setVisibility(View.INVISIBLE);
        image[2].setVisibility(View.INVISIBLE);
        image[3].setVisibility(View.INVISIBLE);   
        image[4].setVisibility(View.INVISIBLE); 
        image[5].setVisibility(View.INVISIBLE); 
 //       getSettings();

 //       setText();        
    }
        
    private int i = 0;

	public void onClick(View v) {
//		SharedPreferences settings = getSharedPreferences(MenuActivity.PREFS_NAME, 0);
//	    SharedPreferences.Editor editor = settings.edit();
        tv[0].setVisibility(View.VISIBLE);

		switch(v.getId()){
			case R.id.previous:
                   
			    image[i-1].setVisibility(View.VISIBLE);
			    image[i].setVisibility(View.INVISIBLE);
			    i--;
		        if(i<5){tv[2].setVisibility(View.VISIBLE);}
                if(i==0){tv[0].setVisibility(View.INVISIBLE);}

				break;
			case R.id.next:

				image[i].setVisibility(View.INVISIBLE);
		        image[i+1].setVisibility(View.VISIBLE);
		        i++;
		        if(i==5){tv[2].setVisibility(View.INVISIBLE);}
				break;
			case R.id.skip:
				finish();
				break;
		}
//		editor.commit();
		
//		getSettings();
	}
}