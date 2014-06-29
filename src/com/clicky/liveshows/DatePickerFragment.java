package com.clicky.liveshows;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements OnDateSetListener {
	
	public interface DatePickerFragmentListener{ 
		void onFinishDatePickerDialog(int year, int month, int day); 
		}  
	
	@Override 
	public Dialog onCreateDialog(Bundle savedInstanceState) {  
		//Datos que seleccionara el usuario. 
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR); 
		int month = c.get(Calendar.MONTH); 
		int day = c.get(Calendar.DAY_OF_MONTH);   
		// Creamos la instancia del DatePicker para devolverla. 
		return new DatePickerDialog(getActivity(), this, year, month, day); 
		}   
	
	@Override
	public void onDateSet(DatePicker arg0, int year, int month, int day) { 
		// TODO Auto-generated method stub 
		DatePickerFragmentListener activity = (DatePickerFragmentListener) getActivity(); 
		activity.onFinishDatePickerDialog(year, month, day);  
		}
}
