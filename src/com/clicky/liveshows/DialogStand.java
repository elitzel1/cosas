package com.clicky.liveshows;


import com.clicky.liveshows.utils.Comisiones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class DialogStand extends DialogFragment {

	OnStandNuevo listener;
	EditText editNombre;
	EditText editEncargado;
	EditText editComision;
	RadioButton radioTipo;
	RadioGroup radioGroup;
	RadioGroup radioPorcentaje;
	RadioButton radioP;
	View view;
	interface OnStandNuevo{
		public void setStand(String nombre,String encargado,Comisiones com);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnStandNuevo)activity;	
		}catch(ClassCastException e){}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_new_stand,null);
		editNombre = (EditText)view.findViewById(R.id.editStand);
		editEncargado = (EditText)view.findViewById(R.id.editEncargado);
		editComision = (EditText)view.findViewById(R.id.editComisionD);
		Button btnAceptar = (Button)view.findViewById(R.id.btn_aceptar);
		Button btnCancelar = (Button)view.findViewById(R.id.btn_cancelar);
		radioGroup = (RadioGroup)view.findViewById(R.id.radioComision);
		radioPorcentaje = (RadioGroup)view.findViewById(R.id.radioPorcentaje);
		dialog.setView(view);
		
		btnAceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nombre;
				String encargado;
				String comision;
				if(editNombre.getEditableText()!=null){
					if(!editNombre.getEditableText().toString().contentEquals("")){
						nombre= editNombre.getEditableText().toString();
					}else { Log.e("DIA", "Falta nombre"); return;}
				}else { Log.e("DIA", "Falta nombre nulo"); return;}
				
				if(editEncargado.getEditableText()!=null){
					if(!editEncargado.getEditableText().toString().contentEquals("")){
						encargado=editEncargado.getEditableText().toString();
					}else  { Log.e("DIA", "Falta encargado"); return;}
				}else  { Log.e("DIA", "Falta encargado nulo"); return;}
				
				if(editComision.getEditableText()!=null){
					if(!editComision.getEditableText().toString().contentEquals("")){
						comision = editComision.getEditableText().toString();
					}else{ Log.e("DIA", "Falta comision"); return;}
				}else{ Log.e("DIA", "Falta comision nulo"); return;}
			
				int selectedId = radioGroup.getCheckedRadioButtonId();
				radioTipo = (RadioButton) view.findViewById(selectedId);
				
				int selectedP=radioPorcentaje.getCheckedRadioButtonId();
				radioP=(RadioButton)view.findViewById(selectedP);
				Comisiones com = new Comisiones("vendedor", Integer.parseInt(comision), radioTipo.getText().toString().toLowerCase(), radioP.getText().toString());
			listener.setStand(nombre, encargado, com);
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
