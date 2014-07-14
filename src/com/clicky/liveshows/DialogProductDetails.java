package com.clicky.liveshows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

public class DialogProductDetails extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_details_product,null);
		
		Bundle b = getArguments();
		String tipo = b.getString("tipo");
		String nombre= b.getString("nombre");
		String precio = b.getString("precio");
		String talla = b.getString("talla");
		int cantidad=b.getInt("cantidad");
		String artista = b.getString("artista");
		int foto = b.getInt("foto");
		
		TextView txtTipo = (TextView)view.findViewById(R.id.txtTipo);
		TextView txtNombre = (TextView)view.findViewById(R.id.txtNombre);
		TextView txtPrecio = (TextView)view.findViewById(R.id.txtPrecio);
		TextView txtTalla = (TextView)view.findViewById(R.id.txtTalla);
		TextView txtCantidad = (TextView)view.findViewById(R.id.txtCantidad);
		TextView txtArtista = (TextView)view.findViewById(R.id.txtArtista);
		ImageView img = (ImageView)view.findViewById(R.id.imgProdD);
		Button btn = (Button)view.findViewById(R.id.btnAceptar);
		
		if(talla.contentEquals("")){
			((TableRow)view.findViewById(R.id.rowTalla)).setVisibility(View.GONE);
		}else{
			txtTalla.setText(talla);
		}
		
		img.setImageResource(foto);
		txtTipo.setText(tipo);
		txtArtista.setText(artista);
		txtCantidad.setText(""+cantidad);
		txtNombre.setText(nombre);
		txtPrecio.setText(precio);
		
		dialog.setView(view);

		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
	return dialog.create();
	}
}
