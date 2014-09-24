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

public class DialogEvent extends DialogFragment {
	
	EditText editEvento;
	OnEventListener listener;
	String nombre;
	int capacidad;
	interface OnEventListener{
		public void changeEvent(String nombre);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnEventListener)activity;	
		}catch(ClassCastException e){}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_event,null);
		editEvento = (EditText)view.findViewById(R.id.editEvent);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptarA);
		Button btnCancelar = (Button)view.findViewById(R.id.btnCancelarA);
		
		Bundle b =getArguments();
		TextView txtTitle = (TextView)view.findViewById(R.id.txtTitleDialog);
		txtTitle.setText(R.string.action_event);
		nombre = b.getString("nombre");
		
		editEvento.setText(nombre);
		dialog.setView(view);
		
		btnAceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editEvento.getEditableText()!=null){
					if(!editEvento.getEditableText().toString().contentEquals("")){
						listener.changeEvent(editEvento.getEditableText().toString());
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
