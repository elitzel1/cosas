package com.clicky.liveshows.adapters;

import com.clicky.liveshows.R;
import com.clicky.liveshows.adapters.AdpterSpinner.ViewHolder;
import com.clicky.liveshows.adapters.AdpterSpinner.ViewHolderDrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterSpinnerMoneda extends ArrayAdapter<String> {

	Context context;
	int resource;
	String[] objects;
	
	public AdapterSpinnerMoneda(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.resource=resource;
		this.objects= objects;
	}
	
	@Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) { 
		View view = cnvtView;
		ViewHolderDrop holderDrop;
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_spinner_drop, prnt, false);
			holderDrop= new ViewHolderDrop();

			holderDrop.txtDrop=(TextView)view.findViewById(R.id.txtDrop);
			view.setTag(holderDrop);
		}
		else
			holderDrop = (ViewHolderDrop)view.getTag();

		holderDrop.txtDrop.setText(""+objects[position]);

		return view;
	}

	@Override public View getView(int pos, View cnvtView, ViewGroup prnt) { 
		View view = cnvtView;
		ViewHolder holder;
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, prnt, false);
			holder = new ViewHolder();

			holder.txtTitle= (TextView)view.findViewById(R.id.txtDropTitle);
			view.setTag(holder);
		}
		else
			holder=(ViewHolder)view.getTag();

		holder.txtTitle.setText(objects[pos]);
		return view;
	}

	static class ViewHolder{
		TextView txtTitle;
	}

	static class ViewHolderDrop{
		TextView txtDrop;
	}


}
