package com.clicky.liveshows.adapters;

import java.io.File;
import java.util.List;

import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.SDImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterProduct extends ArrayAdapter<Product> {

	Context context;
	int resource;
	List<Product> items;
	SDImageLoader sdim;
	public AdapterProduct(Context context, int resource, List<Product> items) {
		super(context, resource, items);

		this.context=context;
		this.resource=resource;
		this.items = items;
		 sdim =new SDImageLoader();
	}
	
	public View getView(int position,View convertView,ViewGroup parent ){
		View view = convertView;
		ViewHolder holder;
		
		if(view == null){
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			
			holder.txtNombre = (TextView)view.findViewById(R.id.txtNombreL);
			holder.txtTalla = (TextView)view.findViewById(R.id.txtTallaL);
			holder.txtPrecio = (TextView)view.findViewById(R.id.txtPrecioL);
			holder.txtCantidad = (TextView)view.findViewById(R.id.txtCantidadL);
			holder.txtAdicional = (TextView)view.findViewById(R.id.txtAdicionalL);
			holder.imgTipo =(ImageView)view.findViewById(R.id.imageView1);
			holder.txtArtista=(TextView)view.findViewById(R.id.txtArtista);
			holder.txtTotal=(TextView)view.findViewById(R.id.txtTotalL);
			view.setTag(holder);
			
			
		}else
			holder = (ViewHolder)view.getTag();
		
		Product product = items.get(position);
		holder.txtNombre.setText(product.getNombre());
		if(!product.getTalla().contentEquals("")){
		holder.txtTalla.setVisibility(View.VISIBLE);
		holder.txtTalla.setText(context.getResources().getString(R.string.talla)+": "+product.getTalla());
		}else
			holder.txtTalla.setVisibility(View.GONE);
			
		holder.txtPrecio.setText(product.getPrecio());
		holder.txtCantidad.setText(""+product.getCantidad());
		if(product.getAdicionalSize()>0){
		holder.txtAdicional.setText(""+product.getAdicionalA(0).getCantidad());
		}else
			holder.txtAdicional.setText("");
		holder.txtArtista.setText(product.getArtista());
		if(product.getId_imagen()==0){
			File imgFile = new File(product.getPath_imagen());

				if(imgFile.exists())
				{
				sdim.load(product.getPath_imagen(), holder.imgTipo);
		
				}
		}
		else
			holder.imgTipo.setImageResource(product.getId_imagen());
		
		holder.txtTotal.setText(""+product.getTotalCantidad());
		return view;
	}

	static class ViewHolder{
		TextView txtNombre;
		TextView txtTalla;
		TextView txtPrecio;
		TextView txtCantidad;
		TextView txtAdicional;
		TextView txtArtista;
		ImageView imgTipo;
		TextView txtTotal;
	}
	
	
	
	 

}
