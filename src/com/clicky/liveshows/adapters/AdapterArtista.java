package com.clicky.liveshows.adapters;

import java.util.ArrayList;

import com.clicky.liveshows.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdapterArtista extends ArrayAdapter<String>{
Context context;
int resource;
ArrayList<String> items;
	
	public AdapterArtista(Context context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.resource=resource;
		this.items=objects;
	}
	
	public View getView(final int position, View converView, ViewGroup parent){
		
		View view=converView;
		ViewHolder holder;
		
		if(view==null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			
			holder = new ViewHolder();
			
			holder.txtArtista = (TextView)view.findViewById(R.id.txtNombreAr);
			holder.btnEliminar = (Button)view.findViewById(R.id.btnEliminar);
			
			view.setTag(holder);
			
		}else
			holder=(ViewHolder)view.getTag();
		
		holder.txtArtista.setText(items.get(position));
		holder.btnEliminar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				items.remove(position);
				notifyDataSetChanged();
			}
		});
		
		return view;
	}
	
	static class ViewHolder{
		TextView txtArtista;
		Button btnEliminar;
	}



}
