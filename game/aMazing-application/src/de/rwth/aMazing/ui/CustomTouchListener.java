package de.rwth.aMazing.ui;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CustomTouchListener implements View.OnTouchListener {     
    public boolean onTouch(View view, MotionEvent motionEvent) {
    
	    switch(motionEvent.getAction()){            
            case MotionEvent.ACTION_DOWN:
             ((TextView) view).setTextColor(0xFFFFFF00); 
                break;          
            case MotionEvent.ACTION_CANCEL:             
            case MotionEvent.ACTION_UP:
            ((TextView) view).setTextColor(0xFFFFFFFF);
                break;
	    } 
     
        return false;   
    } 
}
