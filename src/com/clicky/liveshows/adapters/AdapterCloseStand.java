package com.clicky.liveshows.adapters;

import java.util.List;

import com.clicky.liveshows.R;
import com.clicky.liveshows.utils.Product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class AdapterCloseStand extends ArrayAdapter<Product> {

	Context context;
	int resource;
	List<Product> items;

	
	public AdapterCloseStand(Context context, int resource,
			List<Product> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
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
			holder.txtProduct = (TextView)view.findViewById(R.id.txtNombreProducto);
			holder.txtSize = (TextView)view.findViewById(R.id.txtTallaRopa);
			holder.txtCantidad = (TextView)view.findViewById(R.id.txtCantidadProducto);
			holder.editCantidad = (EditText)view.findViewById(R.id.editNumeroProductos);
			view.setTag(holder);
		}else holder = (ViewHolder) view.getTag();

		final Product item = items.get(position);
		if(!item.getTalla().contentEquals("")){
			holder.txtSize.setText(item.getTalla());
		}

		holder.txtProduct.setText(item.getNombre());
		holder.txtArtista.setText(item.getArtista());
		holder.txtCantidad.setText(""+item.getCantidadStand());
		holder.editCantidad.setId(position);
		
		String oldText = ""+item.getProdNo();
		if(item.getCantidad()>0){
			holder.editCantidad.setText(oldText); 
		}

		String oldText2=""+item.getCortesias();
//		if(item.getCortesias()>0){
//		}
		
		holder.editCantidad.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					final int pos = v.getId();
					final EditText Caption = (EditText) v;
					if(Caption.getEditableText()!=null){
						if(!Caption.getEditableText().toString().contentEquals("")){
							if(Integer.parseInt(Caption.getEditableText().toString())>0&&Integer.parseInt(Caption.getEditableText().toString())<=item.getCantidadStand()){
								items.get(pos).setProdNo(Integer.parseInt(Caption.getEditableText().toString()));
							}

						}
					}
				}
			}
		});
		

		
		if(position==items.size()){
			if(!holder.editCantidad.getEditableText().toString().contentEquals("")){
				if(Integer.parseInt(holder.editCantidad.getEditableText().toString())>0&&Integer.parseInt(holder.editCantidad.getEditableText().toString())<=item.getCantidadStand()){
					items.get(position).setProdNo(Integer.parseInt(holder.editCantidad.getEditableText().toString()));

				}

			}
	
		}
		Log.i("PROD","position: "+position+" Cantidad: "+item.getProdNo());

		return view;
	}
	static class ViewHolder{
		TextView txtProduct;
		TextView txtSize;
		TextView txtArtista;
		TextView txtCantidad;
		EditText editCantidad;

	}

}
