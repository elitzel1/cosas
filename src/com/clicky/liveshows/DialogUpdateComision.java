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
import android.widget.TextView;

public class DialogUpdateComision extends DialogFragment {
	OnChangeComision listener;
	TextView txtNombre;
	TextView txtStand;
	EditText editComision;
	RadioButton radioTipo;
	RadioGroup radioGroup;
	RadioGroup radioPorcentaje;
	RadioButton radioP;
	View view;
	Comisiones comision;
	
	interface OnChangeComision{
		public void setNewComision(Comisiones com);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnChangeComision)activity;	
		}catch(ClassCastException e){}
	}
	
	public void setComision(Comisiones comision){
		this.comision=comision;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_update_comision,null);
		editComision = (EditText)view.findViewById(R.id.editComisionD);
		txtNombre = (TextView)view.findViewById(R.id.txtNombreProducto);
		txtStand = (TextView)view.findViewById(R.id.txtNombreStand);
		Button btnAceptar = (Button)view.findViewById(R.id.btn_aceptar);
		Button btnCancelar = (Button)view.findViewById(R.id.btn_cancelar);
		radioGroup = (RadioGroup)view.findViewById(R.id.radioComision);
		radioPorcentaje = (RadioGroup)view.findViewById(R.id.radioPorcentaje);
		RadioButton tipo = (RadioButton)view.findViewById(R.id.radio0);
		RadioButton tipo_peso = (RadioButton)view.findViewById(R.id.radio1);
		RadioButton after = (RadioButton)view.findViewById(R.id.radioNeto);
		RadioButton befor = (RadioButton)view.findViewById(R.id.radioBruto);
		
		Bundle b= getArguments();
		String nombre =b.getString("nombre");
		String stand = b.getString("stand");
		
		editComision.setText(""+comision.getCantidad());
		txtNombre.setText(nombre);
		txtStand.setText(stand);
		
		if(comision.getTipo().contentEquals("%")){
			tipo.setChecked(true);
		}else{
			tipo_peso.setChecked(true);
		}
		
		if(comision.getIva().contentEquals("after taxes")){
			after.setChecked(true);
		}else{
			befor.setChecked(true);
		}		
		dialog.setView(view);
		
		btnAceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String comision;
			
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
			listener.setNewComision(com);
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
