package com.clicky.liveshows;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity{
	
	private static int SPLASH_TIME_OUT = 800;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final int bandEvento = prefs.getInt("evento", 0);
		
		new Handler().postDelayed(new Runnable() {
			 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
            	if(bandEvento == 1){
        			toActivityProducto();
        		}else if(bandEvento == 0)
        			toActivityEvento();
            }
        }, SPLASH_TIME_OUT);
		
	}
	
	private void toActivityProducto(){
		Intent i = new Intent(this, ActivityProductos.class);
		startActivity(i);
		finish();
	}
	private void toActivityEvento(){
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		finish();
	}

}
