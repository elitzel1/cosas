package com.clicky.liveshows;

import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
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
		LinearLayout listCortesias =(LinearLayout)view.findViewById(R.id.listCortesias);
		LinearLayout listTaxes = (LinearLayout)view.findViewById(R.id.listImpuestos);
		txtNombre = (TextView)view.findViewById(R.id.txtNombre);
		txtTipo = (TextView)view.findViewById(R.id.txtTipo);
		txtTalla = (TextView)view.findViewById(R.id.txtTalla);
		txtPrecio = (TextView)view.findViewById(R.id.txtPrecio);
		txtCantidad = (TextView)view.findViewById(R.id.txtCantidad);
		txtArtista = (TextView)view.findViewById(R.id.txtArtista);
		txtCortesias = (TextView)view.findViewById(R.id.txtCortesias);
		img = (ImageView)view.findViewById(R.id.imgProdD);
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

		if(product.getId_imagen()==0){
			setPic(product.getPath_imagen());
		}else
			img.setImageResource(product.getId_imagen());

		if(product.sizeCortesias()>0){
			for(Cortesias cortesia:product.getCortesias()){
				addView(cortesia.getTipo(), ""+cortesia.getAmount(), listCortesias);
			}
		}else{
			txtCortesias.setVisibility(View.VISIBLE);
			txtCortesias.setText(R.string.d_no_cor);
		}

		if(product.getAdicionalSize()>0){
			for(Adicionales adicional:product.getAdicional()){
				addView(adicional.getNombre(), ""+adicional.getCantidad(), listAdicionales);
			}
		}else
		{
			TextView txtAd= (TextView)view.findViewById(R.id.txtNoAdicionales);
			txtAd.setVisibility(View.VISIBLE);
			txtAd.setText(R.string.d_no_adi);
		}

		if(product.getComisiones().size()>0){
			for(Comisiones com:product.getComisiones()){
				addView(com.getName(), ""+com.getCantidad(), com.getTipo(), com.getIva(), listComisiones);
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
	

	private void addView(String text, String amount, String tipo,String taxes, LinearLayout layout){
		LinearLayout linear = new LinearLayout(getActivity());
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.25f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		TextView txt = new TextView(getActivity());
		txt.setText(text);
		txt.setTextColor(getResources().getColor(R.color.azul));
		txt.setLayoutParams(params2);
		linear.addView(txt);
		TextView txtAmout = new TextView(getActivity());
		txtAmout.setText(amount);
		txtAmout.setTextColor(getResources().getColor(R.color.azul));
		txtAmout.setLayoutParams(params2);
		linear.addView(txtAmout);

		TextView txtTaxes = new TextView(getActivity());
		txtTaxes.setText(taxes);
		txtTaxes.setTextColor(getResources().getColor(R.color.azul));
		txtTaxes.setLayoutParams(params2);
		linear.addView(txtTaxes);

		TextView txtTipo = new TextView(getActivity());
		txtTipo.setText(tipo);
		txtTipo.setTextColor(getResources().getColor(R.color.azul));
		txtTipo.setLayoutParams(params2);
		linear.addView(txtTipo);
		layout.addView(linear);
		layout.invalidate();
	}

	private void setPic(String mCurrentPhotoPath)  {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		try{
			/* Get the size of the ImageView */
			int targetW = img.getWidth();
			int targetH = img.getHeight();

			/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true; //obtenemos el bitmap sin guardarlo en memoria
			BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions); //Convertimos el file en imagen.
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor; //Reduce la imagen si sacleFactor>1
			bmOptions.inPurgeable = true; //Elimina la foto si requiere memoria.

			/* Decode the JPEG file into a Bitmap */
			Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions); //Obtenemos la foto

			/* Associate the Bitmap to the ImageView */
			img.setImageBitmap(bitmap);
		}catch(OutOfMemoryError e){
			img.setImageResource(R.drawable.lupeb);
		}
	}

}
