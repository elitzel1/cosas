package com.clicky.liveshows.adapters;

import java.util.List;

import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Stand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterStand extends ArrayAdapter<Stand> {

	Context context;
	int resource;
	List<Stand> items;
	
	public AdapterStand(Context context, int resource, List<Stand> items) {
		super(context, resource, items);
		// TODO Auto-generated constructor stub
		
		this.context=context;
		this.resource=resource;
		this.items=items;
		
	}
	
	public View getView(int position,View convertView,ViewGroup parent){
		View view=convertView;
		ViewHolder holder;
		if(view==null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			
			holder= new ViewHolder();
			holder.txt = (TextView)view.findViewById(R.id.txtDrop);
			
			view.setTag(holder);
			
		}else 
			holder=(ViewHolder)view.getTag();
		
		
		Stand item = items.get(position);
		holder.txt.setText(item.getName());
		
		return view;
	}
	
	static class ViewHolder{
		TextView txt;
	}

}
