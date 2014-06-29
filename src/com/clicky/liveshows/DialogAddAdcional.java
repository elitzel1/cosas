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

public class DialogAddAdcional extends DialogFragment {
	
	EditText editAdicional;
	OnAdicionalListener listener;
	int position;
	interface OnAdicionalListener{
		public void setAdicional(String adicional,int position);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnAdicionalListener)activity;	
		}catch(ClassCastException e){}
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_adicional,null);
		editAdicional = (EditText)view.findViewById(R.id.editAdicional);
		TextView txtNombre = (TextView)view.findViewById(R.id.txtProdAd);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptarA);
		Button btnCancelar = (Button)view.findViewById(R.id.btnCancelarA);
		
		Bundle b =getArguments();
		TextView txtTitle = (TextView)view.findViewById(R.id.txtTitleDialog);
		txtTitle.setText(R.string.adicionales);
		txtNombre.setText(b.getString("nombre"));
		position = b.getInt("position");
		dialog.setView(view);
		
		btnAceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editAdicional.getEditableText()!=null){
					if(!editAdicional.getEditableText().toString().contentEquals("")){
						listener.setAdicional(editAdicional.getEditableText().toString(),position);
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
