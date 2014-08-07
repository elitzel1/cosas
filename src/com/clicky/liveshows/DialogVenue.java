package com.clicky.liveshows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogVenue extends DialogFragment {
	
	EditText editNombre,editCapacidad;
	OnVenueListener listener;
	String nombre;
	int capacidad;
	interface OnVenueListener{
		public void changeVenue(String nombre,int capacidad);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnVenueListener)activity;	
		}catch(ClassCastException e){}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_venue,null);
		editNombre = (EditText)view.findViewById(R.id.editVenue);
		editCapacidad = (EditText)view.findViewById(R.id.editCapacidad);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptarA);
		Button btnCancelar = (Button)view.findViewById(R.id.btnCancelarA);
		
		Bundle b =getArguments();
		TextView txtTitle = (TextView)view.findViewById(R.id.txtTitleDialog);
		txtTitle.setText(R.string.action_venue);
		nombre = b.getString("nombre");
		capacidad = b.getInt("capacidad");
		
		editNombre.setText(nombre);
		editCapacidad.setText(""+capacidad);
		dialog.setView(view);
		
		btnAceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editNombre.getEditableText()!=null && editCapacidad != null){
					if(!editNombre.getEditableText().toString().contentEquals("") && !editCapacidad.getEditableText().toString().equals("")){
						listener.changeVenue(editNombre.getEditableText().toString(),Integer.valueOf(editCapacidad.getText().toString()));
						dismiss();
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
