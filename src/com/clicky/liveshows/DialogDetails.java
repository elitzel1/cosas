package com.clicky.liveshows;

import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DialogDetails extends DialogFragment {

	View view;
	TextView txtNombre;
	TextView txtTipo;
	TextView txtTalla;
	TextView txtPrecio;
	TextView txtCantidad;
	TextView txtArtista;
	TextView txtCortesias;
	TableRow rowTallas;
	ImageView img;
	Product product;
	Activity ac;

	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			ac=activity;
		}catch(ClassCastException e){}
	}
	
	public void setProduct(Product product){
		this.product=product;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_details,null);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptar);
		LinearLayout listAdicionales= (LinearLayout)view.findViewById(R.id.listAdicionales);
		LinearLayout listComisiones = (LinearLayout)view.findViewById(R.id.listComisiones);
		LinearLayout listTaxes = (LinearLayout)view.findViewById(R.id.listImpuestos);
		txtNombre = (TextView)view.findViewById(R.id.txtNombre);
		txtTipo = (TextView)view.findViewById(R.id.txtTipo);
		txtTalla = (TextView)view.findViewById(R.id.txtTalla);
		txtPrecio = (TextView)view.findViewById(R.id.txtPrecio);
		txtCantidad = (TextView)view.findViewById(R.id.txtCantidad);
		txtArtista = (TextView)view.findViewById(R.id.txtArtista);
		txtCortesias = (TextView)view.findViewById(R.id.txtCortesias);

		txtNombre.setText(product.getNombre());
		txtTipo.setText(product.getTipo());
		if(product.getTalla().contentEquals("")){
			((TableRow)view.findViewById(R.id.rowTalla)).setVisibility(View.GONE);
		}else{
			txtTalla.setText(product.getTalla());
		}
		txtPrecio.setText(product.getPrecio());
		txtCantidad.setText(""+product.getCantidad());
		txtArtista.setText(product.getArtista());
		txtCortesias.setText(""+product.getCortesias());
		
		if(product.getAdicionalSize()>0){
			for(Adicionales adicional:product.getAdicional()){
				addView(adicional.getNombre(), ""+adicional.getCantidad(), listAdicionales);
			}
		}
		
		if(product.getComisiones().size()>0){
			for(Comisiones com:product.getComisiones()){
				addView(com.getName(), ""+com.getCantidad(), listComisiones);
			}
		}
		
		if(product.getTaxes().size()>0){
			for(Taxes tax:product.getTaxes()){
				addView(tax.getName(), ""+tax.getAmount(), listTaxes);
			}
		}
		
		dialog.setView(view);

		btnAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();	
			}
		});


		return dialog.create();
	}
	
	private void addView(String text,String amout, LinearLayout layout){
		LinearLayout linear = new LinearLayout(getActivity());
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.5f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		TextView txt = new TextView(getActivity());
		txt.setText(text);
		txt.setTextColor(getResources().getColor(R.color.azul));
		txt.setLayoutParams(params2);
		linear.addView(txt);

		TextView txtA = new TextView(getActivity());
		txtA.setText(amout);
		txtA.setTextColor(getResources().getColor(R.color.azul));
		txtA.setLayoutParams(params2);
		linear.addView(txtA);

		layout.addView(linear);
		layout.invalidate();
	}

}
