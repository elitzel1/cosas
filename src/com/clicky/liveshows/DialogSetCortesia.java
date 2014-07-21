package com.clicky.liveshows;


import com.clicky.liveshows.utils.Cortesias;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogSetCortesia extends DialogFragment {

	EditText editAdicional;
	OnCortesiaListener listener;
	int position;
	Spinner spinner;
	
	interface OnCortesiaListener{
		public void setCortesia(Cortesias cortesia,int position);
	}

	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnCortesiaListener)activity;	
		}catch(ClassCastException e){}
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();



		View view = inflater.inflate(R.layout.dialog_add_cortesia,null);
		editAdicional = (EditText)view.findViewById(R.id.editAdicional);
		TextView txtNombre = (TextView)view.findViewById(R.id.txtProdAd);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptarA);
		Button btnCancelar = (Button)view.findViewById(R.id.btnCancelarA);
		spinner = (Spinner)view.findViewById(R.id.spinnerTipo);
		
		Bundle b =getArguments();
		txtNombre.setText(b.getString("nombre"));
		TextView txtTitle = (TextView)view.findViewById(R.id.txtTitleDialog);
		txtTitle.setText(R.string.cortesias);
		position = b.getInt("position");
		editAdicional.setHint(""+0);
		
		

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String op1=prefs.getString("op1", "OTHER");
		String op2=prefs.getString("op2", "OTHER");
		String tiposCortesia[]=null;
		if(op1.contentEquals(op2)){
			tiposCortesia = new String[]{"DAMAGE","COMPS VENUE","COMPS OFFICE PRODUCTION",op1};
		}else{
			tiposCortesia = new String[]{"DAMAGE","COMPS VENUE","COMPS OFFICE PRODUCTION",op1,op2};
		}
		/**Spinner TALLA***/
		ArrayAdapter<String> tallasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,tiposCortesia);
		tallasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(tallasAdapter);
		
		dialog.setView(view);

		btnAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editAdicional.getEditableText()!=null){
					if(!editAdicional.getEditableText().toString().contentEquals("")){
						int amount = Integer.parseInt(editAdicional.getEditableText().toString());
						if(amount>0){
							listener.setCortesia(new Cortesias((String)spinner.getSelectedItem(),amount),position);
							dismiss();
						}else return;
					}else return;
				}else return;
				dismiss();	
			}
		});

		btnCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		return dialog.create();
	}

}
