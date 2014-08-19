package com.clicky.liveshows;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity{
	
	private static int SPLASH_TIME_OUT = 3800;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final int bandEvento = prefs.getInt("evento", 0);
		
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MerchSys");
	    
	    //make them in case they're not there
	    dir.mkdir();
	    
		new Handler().postDelayed(new Runnable() {
			 
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
