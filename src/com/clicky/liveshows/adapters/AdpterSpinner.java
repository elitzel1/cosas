package com.clicky.liveshows.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Local;

public class AdpterSpinner extends ArrayAdapter<Local> {

	Context context;
	int resource;
	List<Local> items;
	public AdpterSpinner(Context context, int resource, List<Local> items) {
		super(context, resource, items);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.resource=resource;
		this.items=items;
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

		holderDrop.txtDrop.setText(""+
				items.get(position).getNombre());

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

		holder.txtTitle.setText(items.get(pos).getNombre());
		return view;
	}

	static class ViewHolder{
		TextView txtTitle;
	}

	static class ViewHolderDrop{
		TextView txtDrop;
	}


}
