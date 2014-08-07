package com.clicky.liveshows.adapters;

import java.util.List;

import com.clicky.liveshows.ActivityAgregarProductos;
import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterListaAgregaProductos extends ArrayAdapter<Product> {
	Context context;
	int resource;
	List<Product> items;

	public AdapterListaAgregaProductos(Context context, int resource,List<Product> objects) {
		super(context, resource, objects);
		this.context=context;
		this.resource=resource;
		this.items=objects;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder;
		if(view==null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);

			holder = new ViewHolder();

			holder.txtArtista = (TextView)view.findViewById(R.id.txtNombreArtista);
			holder.txtTipo = (TextView)view.findViewById(R.id.txtTipo);
			holder.txtProduct = (TextView)view.findViewById(R.id.txtNombreProducto);
			holder.txtSize = (TextView)view.findViewById(R.id.txtTallaRopa);
			holder.txtCantidad = (TextView)view.findViewById(R.id.txtCantidadProducto);
			holder.editCantidad = (TextView)view.findViewById(R.id.editNumeroProductos);
			view.setTag(holder);
		}else holder = (ViewHolder) view.getTag();

		final Product item = items.get(position);
		if(!item.getTalla().contentEquals("")){
			holder.txtSize.setVisibility(View.VISIBLE);
			holder.txtSize.setText(item.getTalla());
		}else{
			holder.txtSize.setVisibility(View.GONE);
		}

		holder.txtProduct.setText(item.getNombre());
		holder.txtTipo.setText(item.getTipo());
		holder.txtArtista.setText(item.getArtista());
		holder.txtCantidad.setText(""+(item.getCantidad()-item.getCantidadStand()));
		holder.editCantidad.setId(position);

		holder.editCantidad.setText(""+item.getCantidadStand()); 

		return view;
	}
	static class ViewHolder{
		TextView txtProduct;
		TextView txtTipo;
		TextView txtSize;
		TextView txtArtista;
		TextView txtCantidad;
		TextView editCantidad;
	}

}
