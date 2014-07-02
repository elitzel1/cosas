package com.clicky.liveshows;

import com.clicky.liveshows.database.DBAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityCierreDia extends Activity{
	
	private DBAdapter dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cierre_dia);

		dbHelper = new DBAdapter(this);
		setupActionBar();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cierre_stand, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_accept:
			borraTodo();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
	}
	
	private void borraTodo(){
		dbHelper.open();
		dbHelper.deleteTodo();
		dbHelper.close();
		SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("evento", 0);
		edit.commit();
		setResult(RESULT_OK);
		finish();
	}

}
