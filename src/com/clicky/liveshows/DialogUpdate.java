package com.clicky.liveshows;

import java.io.File;
import java.util.List;

import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogUpdate extends DialogFragment {

	private String []tipos;
	private int idImages[]; 
	private String[]comison = {"Elija comision","AGENCY","VENUE","PROMOTOR","OTHER"};
	private EditText editNombre;
	private EditText editCantidad;
	private EditText editPrecio;
	private Spinner spinnerTipos;
	private Spinner spinnerArtistas;
	private OnDialogUpdateListener listener;
	private ImageView img;
	private String path;
	private EditText editComisiones;
	private LinearLayout mLinearC;
	private int countC;
	private int countT;
	private int iCurrentSelection;
	private boolean enableSpinner = true; 
	private int idImagen;
	private LinearLayout mLinearT;
	private List<Taxes> list_taxes;
	private List<Comisiones> list_comisiones;
	private Spinner spinnerComisiones;
	private RadioGroup radioGroup;
	private RadioGroup radioGroup2;
	private boolean enableTipos = true;
	private EditText editTipo;
	Product product;
	View view;
	int position;

	interface OnDialogUpdateListener{
		public void articuloActualizado(Product p, int position);
		public void makeToastDialog(int msg);
	}

	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener = (OnDialogUpdateListener)activity;
		}catch(ClassCastException e){}
	}

	public void setProduct(Product product){
		this.product=product;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_update_prod,null);
		img = (ImageView)view.findViewById(R.id.imgProdD);
		editNombre = (EditText)view.findViewById(R.id.editNombreProd);
		editCantidad = (EditText)view.findViewById(R.id.editCantidadProd);
		editPrecio = (EditText)view.findViewById(R.id.editPrecio);
		spinnerTipos = (Spinner)view.findViewById(R.id.spinnerTipo);
		spinnerArtistas = (Spinner)view.findViewById(R.id.spinnerArtista);
		spinnerComisiones = (Spinner)view.findViewById(R.id.spinnerComisionesP);
		editTipo = (EditText)view.findViewById(R.id.editTipo);
		TextView talla = (TextView)view.findViewById(R.id.txtTallas);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptar);
		Button btnCancelar =(Button)view.findViewById(R.id.btnCancelar);
		ImageView menosC =(ImageView)view.findViewById(R.id.btnComLess);
		ImageView menosT =(ImageView)view.findViewById(R.id.btnTaxesLess);

		idImagen=0;
		path="";

		Bundle b =  getArguments();
		tipos = b.getStringArray("tipos");
		idImages = b.getIntArray("imagenes");
		position = b.getInt("position");




		/***Datos actuales del producto****/
		editNombre.setText(product.getNombre());
		editCantidad.setText(""+product.getTotalCantidad());
		editPrecio.setText(""+product.getPrecio());

		if(!product.getTalla().contentEquals("")){
			talla.setText(product.getTalla());
		}else{
			((LinearLayout)view.findViewById(R.id.tallas)).setVisibility(View.GONE);
		}

		/**Spinne TIPOS***/
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, tipos);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTipos.setAdapter(dataAdapter);

		iCurrentSelection = dataAdapter.getPosition(product.getTipo().toUpperCase());
		if(iCurrentSelection>=0){
			spinnerTipos.setSelection(iCurrentSelection);
		}else{
			editTipo.setText(product.getTipo().toUpperCase());
			enableTipos=false;
		}

		if(!product.getPath_imagen().contentEquals("")){
			setImage(product.getPath_imagen());
		}else{
			setImage(iCurrentSelection);
		}

		//iCurrentSelection = spinnerTipos.getSelectedItemPosition();

		spinnerTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (iCurrentSelection != position&&enableSpinner){
					setImage(position);
				}
				iCurrentSelection = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		//Otro tipo de ropa
		Button btnOtro =  (Button)view.findViewById(R.id.btnOtroTipo);
		btnOtro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(enableTipos==false){
					spinnerTipos.setVisibility(View.VISIBLE);
					view.findViewById(R.id.editTipo).setVisibility(View.GONE);
					enableTipos=true;
				}else{
					spinnerTipos.setVisibility(View.INVISIBLE);
					editTipo.setVisibility(View.VISIBLE);
					enableTipos=false;
				}


			}
		});


		/**Artistas**/
		ArrayAdapter<String> dataAdapterArtista = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,b.getStringArray("artistas"));
		dataAdapterArtista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerArtistas.setAdapter(dataAdapterArtista);
		int currentArtist = dataAdapterArtista.getPosition(product.getArtista());
		spinnerArtistas.setSelection(currentArtist);

		/***Comisiones***/
		ArrayAdapter<String> dataAdapterComision = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,comison);
		dataAdapterComision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerComisiones.setAdapter(dataAdapterComision);

		countC=0; //Contador Comisiones
		countT=0; //Contador Impuestos

		mLinearC = (LinearLayout)view.findViewById(R.id.listComisiones);  //Linear comisiones
		mLinearC.setOrientation(LinearLayout.VERTICAL);

		list_taxes = product.getTaxes();
		list_comisiones = product.getComisiones();

		/**Comisiones Cantidad, tipo e IVA****/
		editComisiones=(EditText)view.findViewById(R.id.editComisiones);
		radioGroup = (RadioGroup)view.findViewById(R.id.radioComisionT);
		radioGroup2 =(RadioGroup)view.findViewById(R.id.radioComisionP);
		ImageView btnComision = (ImageView)view.findViewById(R.id.btnComision);

		//Se agrega comision
		btnComision.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String com = (String)spinnerComisiones.getSelectedItem();
				if(!com.contentEquals("Elija comision")){
					if(editComisiones.getEditableText()!=null){
						if(!editComisiones.getEditableText().toString().equals("")){
							if(Integer.parseInt(editComisiones.getEditableText().toString())>0){
								int selected = radioGroup.getCheckedRadioButtonId();
								RadioButton r = (RadioButton)view.findViewById(selected);
								int selected2 = radioGroup2.getCheckedRadioButtonId();
								RadioButton r2 = (RadioButton)view.findViewById(selected2);
								list_comisiones.add(new Comisiones(com,Integer.parseInt(editComisiones.getEditableText().toString()),r2.getText().toString(),r.getText().toString()));
								countC++;
								addView(com,editComisiones.getEditableText().toString(),r.getText().toString(),r2.getText().toString(), countC, mLinearC);
								editComisiones.setText("");
								listener.makeToastDialog(R.string.d_com_agregada);
							}else{
								listener.makeToastDialog(R.string.d_com_nov);}
						}else{
							listener.makeToastDialog(R.string.d_com_err);}
					}else{
						listener.makeToastDialog(R.string.d_com_err);}
				}
			}
		});

		/**Layout impuestos***/
		mLinearT =(LinearLayout)view.findViewById(R.id.listImpuestos);
		mLinearT.setOrientation(LinearLayout.VERTICAL);
		ImageView btnImpuesto = (ImageView)view.findViewById(R.id.btnTaxes);

		/*Agregar impuesto**/
		btnImpuesto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String amount = ((EditText)view.findViewById(R.id.editImpuestoCantidad)).getEditableText().toString();
				if(!amount.contentEquals("")){
					if(Integer.parseInt(amount)>0){
						list_taxes.add(new Taxes("Tax",Integer.parseInt(amount)));
						countT++;
						addView("Tax", amount , countT, mLinearT);
						((EditText)view.findViewById(R.id.editImpuestoCantidad)).setText("");
						listener.makeToastDialog(R.string.d_tax_agregada);
					}else{
						listener.makeToastDialog(R.string.d_tax_nov);
					}
				}else{
					listener.makeToastDialog(R.string.d_tax_nov);
				}
			}
		});


		for(Taxes t:list_taxes){
			countT++;
			addView(t.getName(), ""+t.getAmount() , countT, mLinearT);
		}

		for(Comisiones c:list_comisiones){
			countC++;
			addView(c.getName(), ""+c.getCantidad(), c.getTipo(), c.getIva(), countC, mLinearC);
		}



		dialog.setView(view);



		/**Se elimina una comision**/
		menosC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(countC>0){
					final LinearLayout temp = (LinearLayout)mLinearC.findViewById(countC);
					temp.removeAllViews();
					mLinearC.removeView(temp);
					countC--;
					list_comisiones.remove(list_comisiones.size()-1);
				}				
			}
		});

		/**Se elimina impuesto**/
		menosT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(countT>0){
					final LinearLayout temp = (LinearLayout)mLinearT.findViewById(countT);
					temp.removeAllViews();
					mLinearT.removeView(temp);
					countT--;
					list_taxes.remove(list_taxes.size()-1);
				}
			}
		});

		btnAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String nombre=null;
				String cantidad=null;
				String precio = null;
				String tipo = null;

				if(enableTipos){
					tipo = spinnerTipos.getSelectedItem().toString();
				} else{
					if(editTipo.getEditableText()!=null){
						if(!editTipo.getEditableText().toString().contentEquals("")){
							tipo = editTipo.getEditableText().toString();
						}else {
							listener.makeToastDialog(R.string.sin_tipo);
							return;}
					}else {listener.makeToastDialog(R.string.sin_tipo);
					return;}
				}
				// TODO Auto-generated method stub
				if(editNombre.getEditableText()!=null){
					if(!editNombre.getEditableText().toString().contentEquals(""))
						if(editNombre.getEditableText().toString().contentEquals(product.getNombre())){
							nombre=product.getNombre();
						}else{
							nombre=editNombre.getEditableText().toString();
						}

					else{
						listener.makeToastDialog(R.string.sin_nombre_producto);
						return;
					}
				}
				else{
					return;}


				if(editPrecio.getEditableText()!=null){
					if(!editPrecio.getEditableText().toString().contentEquals("")){
						if(Double.parseDouble(editPrecio.getEditableText().toString())>0)
							if(editPrecio.getEditableText().toString().contentEquals(product.getPrecio())){
								precio = product.getPrecio();
							}else{
								precio = editPrecio.getEditableText().toString();
							}
						else{
							listener.makeToastDialog(R.string.sin_precio_valido);
							return;
						}
					}else{
						listener.makeToastDialog(R.string.sin_precio);
						return;
					}
				}else{
					listener.makeToastDialog(R.string.sin_precio);
					return;
				}

				if(list_comisiones.size()<=0){
					listener.makeToastDialog(R.string.sin_comisiones);
					return;
				}

				if(list_taxes.size()<=0){
					listener.makeToastDialog(R.string.sin_impuestos);
					return;
				}



				if(editCantidad.getEditableText()!=null){
					if(!editCantidad.getEditableText().toString().contentEquals("")){
						if(Integer.parseInt(editCantidad.getEditableText().toString())>0){
							if(editCantidad.getEditableText().toString().contentEquals(""+product.getTotalCantidad())){
								cantidad = ""+product.getTotalCantidad();
							}else{
								cantidad = editCantidad.getEditableText().toString();
							}
						}else{listener.makeToastDialog(R.string.sin_precio_valido);
						return;}
					}else{
						listener.makeToastDialog(R.string.sin_precio);
						return;
					}
				}else{
					listener.makeToastDialog(R.string.sin_precio);
					return;
				}

				Product p = new Product(nombre, tipo, spinnerArtistas.getSelectedItem().toString(), precio, "", Integer.parseInt(cantidad), list_comisiones, path);
				p.setTotalCantidad(Integer.parseInt(cantidad));
				p.setId(product.getId());
				p.setComisiones(list_comisiones);
				p.setTaxes(list_taxes);
				p.setId_imagen(idImagen);
				listener.articuloActualizado(p, position);
				dismiss();	
			}
		});

		btnCancelar.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});



		return dialog.create();

	}

	public void setImage(String path){
		File imgFile = new File(path);
		this.path=path;
		Log.i("ALBUMDIA", ""+path);
		if(imgFile.exists())
		{
			setPic(imgFile.getAbsolutePath());
			enableSpinner  = false;
			idImagen=0;
			Log.i("ALBUMDIA", ""+imgFile.getAbsolutePath());
		}
	}

	private void setPic(String mCurrentPhotoPath) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

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
	}

	private void setImage(int position){
		idImagen = idImages[position];
		img.setImageResource(idImagen);
	}

	private void addView(String text,String amout, int count, LinearLayout layout){
		LinearLayout linear = new LinearLayout(getActivity());
		linear.setId(count);
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

		layout.addView(linear,0);
		layout.invalidate();
	}

	private void addView(String text, String amount, String tipo,String taxes, int count, LinearLayout layout){
		LinearLayout linear = new LinearLayout(getActivity());
		linear.setId(count);
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
		layout.addView(linear,0);
		layout.invalidate();
	}
}
