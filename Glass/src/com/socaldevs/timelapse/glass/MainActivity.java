package com.socaldevs.timelapse.glass;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	/**
     	* Handle the tap event from the touchpad.
     	*/
    	@Override
    	public boolean onKeyDown(int keyCode, KeyEvent event) {
	        switch (keyCode) {
            	// On Tap, we want to open the menus
            	case KeyEvent.KEYCODE_DPAD_CENTER:
            	case KeyEvent.KEYCODE_ENTER:
	                openOptionsMenu();
                	return true;
            	default:
		        return super.onKeyDown(keyCode, event);
	        }
    	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
