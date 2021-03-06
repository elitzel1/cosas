package com.clicky.liveshows.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Product;

public class AdapterStandProduct extends ArrayAdapter<Product> {

	Context context;
	int resource;
	List<Product> items;
	
	public AdapterStandProduct(Context context, int resource,
			List<Product> items) {
		super(context, resource, items);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource=resource;
		this.items=items;
	}
	
	public View getView(int position,View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder;
		
		if(view==null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new ViewHolder();
			
			holder.txtNombre = (TextView)view.findViewById(R.id.txtNombreS);
			holder.txtTalla = (TextView)view.findViewById(R.id.txtTallaS);
			holder.txtPrecio = (TextView)view.findViewById(R.id.txtPrecioS);
			holder.txtCantidad =(TextView)view.findViewById(R.id.txtCantidadS);
			
			view.setTag(holder);
			
		}else
			holder = (ViewHolder)view.getTag();
		
		Product item = items.get(position);
		
		holder.txtNombre.setText(item.getNombre());
		
		if(!item.getTalla().contentEquals("")){
			holder.txtTalla.setVisibility(View.VISIBLE);
			holder.txtTalla.setText(item.getTalla());
		}else
			holder.txtTalla.setVisibility(View.GONE);
		
		holder.txtPrecio.setText(item.getPrecio());
		holder.txtCantidad.setText(""+item.getCantidadStand());
		
		return view;
	}
	
	static class ViewHolder{
		TextView txtNombre;
		TextView txtTalla;
		TextView txtPrecio;
		TextView txtCantidad;
		ImageView imgTipo;
	}


}
