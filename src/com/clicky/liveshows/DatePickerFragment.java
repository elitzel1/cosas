package com.clicky.liveshows;

import java.util.Calendar;

import com.clicky.liveshows.DialogStand.OnStandNuevo;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment extends DialogFragment{
	
	OnDateSelected listener;
	View view;
	TextView titulo;
	DatePicker datePicker;
	
	public interface OnDateSelected{ 
		void onFinishDatePickerDialog(int year, int month, int day); 
	}  
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnDateSelected)activity;	
		}catch(ClassCastException e){}
	}
	
	@Override 
	public Dialog onCreateDialog(Bundle savedInstanceState) {  
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_date,null);
		Button btnAceptar = (Button)view.findViewById(R.id.btn_aceptar);
		Button btnCancelar = (Button)view.findViewById(R.id.btn_cancelar);
		titulo = (TextView)view.findViewById(R.id.txtTitleDialog);
		datePicker = (DatePicker)view.findViewById(R.id.datePicker1);
		dialog.setView(view);
		
		/*Datos que seleccionara el usuario. 
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR); 
		int month = c.get(Calendar.MONTH); 
		int day = c.get(Calendar.DAY_OF_MONTH);   
		// Creamos la instancia del DatePicker para devolverla. 
		return new DatePickerDialog(getActivity(), this, year, month, day); 
		*/
		
		titulo.setText("Select a date");
		
		btnAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.onFinishDatePickerDialog(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
				dismiss();
			}
			
		});
		
		btnCancelar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return dialog.create();
	}   

}
