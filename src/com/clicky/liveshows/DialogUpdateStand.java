package com.clicky.liveshows;

import com.clicky.liveshows.utils.Comisiones;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DialogUpdateStand extends DialogFragment{
	
	OnStandUpdate listener;
	EditText editNombre;
	EditText editEncargado;
	EditText editComision;
	RadioButton radioTipo;
	RadioGroup radioGroup;
	RadioGroup radioPorcentaje;
	RadioButton radioP;
	View view;
	long idStand;
	
	interface OnStandUpdate{
		public void updateStand(long idStand, String nombre,String encargado,Comisiones com);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnStandUpdate)activity;	
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
		RadioButton tipo = (RadioButton)view.findViewById(R.id.radio0);
		RadioButton tipo_peso = (RadioButton)view.findViewById(R.id.radio1);
		RadioButton after = (RadioButton)view.findViewById(R.id.radioNeto);
		RadioButton befor = (RadioButton)view.findViewById(R.id.radioBruto);
		
		Bundle b= getArguments();
		idStand = b.getLong("idStand");
		String nombre = b.getString("nombre");
		String encargado = b.getString("encargado");
		String txtTipo = b.getString("tipo");
		String txtIva = b.getString("iva");
		int comision = b.getInt("comision");
		
		editNombre.setText(nombre);
		editEncargado.setText(encargado);
		editComision.setText(""+comision);
		
		if(txtIva.equals("%")){
			tipo.setChecked(true);
		}else{
			tipo_peso.setChecked(true);
		}
		
		if(txtTipo.equals("After taxes")){
			after.setChecked(true);
		}else{
			befor.setChecked(true);
		}	
		
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
					}else{ 
						showToast(R.string.sin_stand);
						return;
					}
				}else { 
					showToast(R.string.sin_stand);
					return;}
				
				if(editEncargado.getEditableText()!=null){
					if(!editEncargado.getEditableText().toString().contentEquals("")){
						encargado=editEncargado.getEditableText().toString();
					}else{ 
						showToast(R.string.sin_encargado);
						return;
					}
				}else{ 
					showToast(R.string.sin_encargado);
					return;
				}
				
				int selectedId = radioGroup.getCheckedRadioButtonId();
				radioTipo = (RadioButton) view.findViewById(selectedId);
				
				int selectedP=radioPorcentaje.getCheckedRadioButtonId();
				radioP=(RadioButton)view.findViewById(selectedP);
				if(editComision.getEditableText()!=null){
					if(!editComision.getEditableText().toString().contentEquals("")){
						if(radioP.getText().toString().equals("%")){
							if(Double.parseDouble(editComision.getEditableText().toString()) > 100){
								showToast(R.string.err_com_noval);
								return;
							}else
								comision = editComision.getEditableText().toString();
						}else
							comision = editComision.getEditableText().toString();
					}else{ 
						showToast(R.string.sin_stand_comision);
						return;
					}
				}else{ 
					showToast(R.string.sin_stand_comision); 
					return;
				}
			
				Comisiones com = new Comisiones("Vendedor", Integer.parseInt(comision), radioP.getText().toString(), radioTipo.getText().toString());
			listener.updateStand(idStand,nombre, encargado, com);
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

	private void showToast(int texto){
		Toast.makeText(getActivity(), texto, Toast.LENGTH_SHORT).show();
	}

}
