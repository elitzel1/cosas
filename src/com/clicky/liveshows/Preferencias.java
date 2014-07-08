package com.clicky.liveshows;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Preferencias extends Activity {
	EditText editDivisa;
	EditText editComision;
	EditText editOp1;
	EditText editOp2;
	float divisa;
	float comisiion;
	String op1;
	String op2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferencias);
		setupActionBar();

		editDivisa = (EditText)findViewById(R.id.editDivisas);
		editComision = (EditText)findViewById(R.id.editCredito);
		editOp1  = (EditText)findViewById(R.id.editOp1);
		editOp2 =(EditText)findViewById(R.id.editOp2);

		TextView txtDivisas = (TextView)findViewById(R.id.txtDivisa);
		SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
		String div=prefs.getString("moneda", "");
		divisa = prefs.getFloat("divisa", 0);
		comisiion = prefs.getFloat("comision_tarjeta", 0);
	op1=prefs.getString("op1", "OTHER");
		op2=prefs.getString("op2", "OTHER");

		editComision.setText(""+comisiion);
		editOp1.setText(op1);
		editOp2.setText(op2);

		if(div.contentEquals("Peso")){
			editDivisa.setEnabled(false);
		}else{
			if(div.contentEquals("Dolar")){
				editDivisa.setText(""+divisa);
				txtDivisas.setText("Dolar");
			}else{
				editDivisa.setText(""+divisa);
				txtDivisas.setText("Euro");
			}
		}
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preferencias, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//	NavUtils.navigateUpFromSameTask(this);
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_accept:
			saveData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveData(){



		if(editDivisa.getEditableText()!=null){
			if(!editDivisa.getEditableText().toString().contentEquals("")){
				if(Float.parseFloat(editDivisa.getEditableText().toString())>0){
					if(divisa!=Float.parseFloat(editDivisa.getEditableText().toString())){
						SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putFloat("divisa",Float.parseFloat(editDivisa.getEditableText().toString()));
						editor.commit();
						makeToast(R.string.p_divisa_g);
					}
				}else{
					makeToast(R.string.err_divisa_noval);
					return;
				}
			}else{
				makeToast(R.string.err_divisas);
				return;
			}
		}else{
			makeToast(R.string.err_divisas);
			return;
		}

		if(editComision.getEditableText()!=null){
			if(!editComision.getEditableText().toString().contentEquals("")){
				if(Float.parseFloat(editComision.getEditableText().toString())>0){
					if(comisiion!=Float.parseFloat(editComision.getEditableText().toString())){
						SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putFloat("comision_tarjeta",Float.parseFloat(editComision.getEditableText().toString()));
						editor.commit();
						makeToast(R.string.p_comision_g);
					}
				}else{
					makeToast(R.string.err_com_noval);
					return;
				}
			}else{
				makeToast(R.string.err_com);
				return;
			}
		}else{
			makeToast(R.string.err_com);
			return;
		}

		if(editOp1.getEditableText()!=null){
			if(!editOp1.getEditableText().toString().contentEquals("")){
					if(!editOp1.getEditableText().toString().contentEquals(op1)){
						SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("op1",editOp1.getEditableText().toString());
						editor.commit();
						makeToast(R.string.p_op_guar);
					}
				
			}else{
				makeToast(R.string.err_op_uno);
				return;
			}
		}else{
			makeToast(R.string.err_op_uno);
			return;
		}


		if(editOp2.getEditableText()!=null){
			if(!editOp2.getEditableText().toString().contentEquals("")){
					if(!editOp2.getEditableText().toString().contentEquals(op2)){
						SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("op2",editOp2.getEditableText().toString());
						editor.commit();
						makeToast(R.string.p_op_guar_d);
					}
				
			}else{
				makeToast(R.string.err_op_dos);
				return;
			}
		}else{
			makeToast(R.string.err_op_dos);
			return;
		}
		
		
		finish();
	}

	private void makeToast(int id){
		Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
	}

}
