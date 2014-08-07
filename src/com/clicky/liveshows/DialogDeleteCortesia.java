package com.clicky.liveshows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

public class DialogDeleteCortesia extends DialogFragment implements OnMultiChoiceClickListener {


	public static boolean[] arr;
	OnCortesiasSelected listener;
	private String[] comps;
	private int position;
	
	public interface OnCortesiasSelected{
		public void onDialogPositive(boolean[] arr,int pos);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnCortesiasSelected)activity;	
		}catch(ClassCastException e){}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
	
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		
		Bundle b =getArguments();
		comps = b.getStringArray("comps");
		position = b.getInt("pos");
		
		arr = new boolean[comps.length];
		
		dialog.setTitle(R.string.title_delete_comp)
		.setMultiChoiceItems(comps, arr, this).
		setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
				listener.onDialogPositive(arr,position);
				
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
			}
		});
		
		
		return dialog.create();
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		arr[which] = checked(arr[which],isChecked);
	}
	
	private boolean checked(boolean num, boolean isChecked){
		if(isChecked==true)
			num=true;
		else
			num=false;
		return num;
	}
	
	
	public boolean[] getOptionsChecked(){
		return arr;
	}
	
	public void onDestroy(){
		super.onDestroy();
		listener=null;
	}
}
