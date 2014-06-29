package com.clicky.liveshows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Talla;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogAddProduct extends DialogFragment {
	private String []tipos;
	private int idImages[]; 
	private String[]tallas ={"SIN TALLA","UNITALLA","2-4","6-8","10-12",   
			"14-16","18-20","XS","S","M","L","XL","XXL",   
			"XXXL","XS JR","S JR","M JR","L JR","XL JR"};
	private String[]comison = {"Elija comision","artista","puesto","lugar","otro"};
	private EditText editNombre;
	private EditText editCantidad;
	private EditText editPrecio;
	private Spinner spinnerTipos;
	private Spinner spinnerArtistas;
	private CheckBox checkTalla;
	private OnDialogListener listener;
	private ImageView img;
	private String path;
	private boolean visibleComision = false;
	private boolean visibleTaxes=false;
	private EditText editComisiones;
	private EditText editTaxes;
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
	private LinearLayout mLinearTallas;
	private int countTallas;
	private boolean enableTallas = false;
	private RadioGroup radioGroup;
	private RadioGroup radioGroup2;
	private boolean enableTipos = true;
	private EditText editTipo;

	private Spinner spinnerTallas;
	private EditText editCantidadTallas;
	private List<Talla> list_tallas;
	private EditText editTalla;
	private boolean enableOtraTalla = true;
	private boolean visibleTallaMenos = false;
	
	View view;
	interface OnDialogListener{
		public void articuloNuevo(Product p, int idImg);
		public void makeToastDialog(int msg);
	}

	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener = (OnDialogListener)activity;
		}catch(ClassCastException e){}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.dialog_producto,null);
		img = (ImageView)view.findViewById(R.id.imgProdD);
		editNombre = (EditText)view.findViewById(R.id.editNombreProd);
		editCantidad = (EditText)view.findViewById(R.id.editCantidadProd);
		editPrecio = (EditText)view.findViewById(R.id.editPrecio);
		spinnerTipos = (Spinner)view.findViewById(R.id.spinnerTipo);
		spinnerArtistas = (Spinner)view.findViewById(R.id.spinnerArtista);
		spinnerComisiones = (Spinner)view.findViewById(R.id.spinnerComisionesP);
		spinnerTallas = (Spinner)view.findViewById(R.id.spinnerTallas);
		editCantidadTallas = (EditText)view.findViewById(R.id.editCantidadTallas);
		Button btnAceptar = (Button)view.findViewById(R.id.btnAceptar);
		Button btnCancelar =(Button)view.findViewById(R.id.btnCancelar);
		Button btnOtraTalla = (Button)view.findViewById(R.id.btnOtroTalla);
		ImageView masTallas = (ImageView)view.findViewById(R.id.btnMasTalla);
		ImageView menosTallas = (ImageView)view.findViewById(R.id.btnMenosTalla);
		ImageView menosC =(ImageView)view.findViewById(R.id.btnComLess);
		ImageView menosT =(ImageView)view.findViewById(R.id.btnTaxesLess);
		checkTalla = (CheckBox)view.findViewById(R.id.checkBox1);
		
		idImagen=0;
		path="";
		
		Bundle b =  getArguments();
		tipos = b.getStringArray("tipos");
		idImages = b.getIntArray("imagenes");
		
		/**Spinner TALLA***/
		ArrayAdapter<String> tallasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,tallas);
		tallasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTallas.setAdapter(tallasAdapter);
		
		/**Spinne TIPOS***/
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, tipos);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTipos.setAdapter(dataAdapter);
		iCurrentSelection = spinnerTipos.getSelectedItemPosition();
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
					editTipo = (EditText)view.findViewById(R.id.editTipo);
					editTipo.setVisibility(View.VISIBLE);
					enableTipos=false;
				}


			}
		});
		
		//Otra talla
		btnOtraTalla.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(enableOtraTalla==false){
					spinnerTallas.setVisibility(View.VISIBLE);
					view.findViewById(R.id.editTalla).setVisibility(View.GONE);
					enableOtraTalla=true;
				}else{
					spinnerTallas.setVisibility(View.INVISIBLE);
					editTalla = (EditText)view.findViewById(R.id.editTalla);
					editTalla.setVisibility(View.VISIBLE);
					enableTallas=false;
				}
				
			}
		});

		/**Artistas**/
		ArrayAdapter<String> dataAdapterArtista = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,b.getStringArray("artistas"));
		dataAdapterArtista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerArtistas.setAdapter(dataAdapterArtista);

		/***Comisiones***/
		ArrayAdapter<String> dataAdapterComision = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,comison);
		dataAdapterComision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerComisiones.setAdapter(dataAdapterComision);

		countC=0; //Contador Comisiones
		countT=0; //Contador Impuestos
		countTallas=0; //Contador Tallas
		
		mLinearC = (LinearLayout)view.findViewById(R.id.listComisiones);  //Linear comisiones
		mLinearC.setOrientation(LinearLayout.VERTICAL);
		
		list_taxes = new ArrayList<Taxes>();
		list_comisiones = new ArrayList<Comisiones>();
		
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
					if(editComisiones.getEditableText()!=null)
						if(!editComisiones.getEditableText().toString().equals("")){
							if(Integer.parseInt(editComisiones.getEditableText().toString())>0){
								int selected = radioGroup.getCheckedRadioButtonId();
								RadioButton r = (RadioButton)view.findViewById(selected);
								int selected2 = radioGroup2.getCheckedRadioButtonId();
								RadioButton r2 = (RadioButton)view.findViewById(selected2);
								list_comisiones.add(new Comisiones(com,Integer.parseInt(editComisiones.getEditableText().toString()),r2.getText().toString(),r.getText().toString()));
								countC++;
								addView(com,editComisiones.getEditableText().toString(),r.getText().toString(),r2.getText().toString(), countC, mLinearC);
							}
						}
					if(visibleComision==false){
						view.findViewById(R.id.btnComLess).setVisibility(View.VISIBLE);
						visibleComision=true;
					}
				}
			}
		});

		/**Layout impuestos***/
		mLinearT =(LinearLayout)view.findViewById(R.id.listImpuestos);
		mLinearT.setOrientation(LinearLayout.VERTICAL);
		ImageView btnImpuesto = (ImageView)view.findViewById(R.id.btnTaxes);
		editTaxes = (EditText)view.findViewById(R.id.editImpuestosNombre);
		
		/*Agregar impuesto**/
		btnImpuesto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editTaxes.getEditableText()!=null)
					if(!editTaxes.getEditableText().toString().equals("")){
						String amount = ((EditText)view.findViewById(R.id.editImpuestoCantidad)).getEditableText().toString();
						if(Integer.parseInt(amount)>0){
							list_taxes.add(new Taxes(editTaxes.getEditableText().toString(),Integer.parseInt(amount)));
							countT++;
							addView(editTaxes.getEditableText().toString(), amount , countT, mLinearT);
							if(visibleTaxes==false){
								view.findViewById(R.id.btnTaxesLess).setVisibility(View.VISIBLE);
								visibleTaxes=true;
							}
						}
					}
			}
		});

		mLinearTallas = (LinearLayout)view.findViewById(R.id.listTallas);
		mLinearTallas.setOrientation(LinearLayout.VERTICAL);
		/**Agregar cantidad por tallas**/
		checkTalla.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
			
				if(isChecked){
					LinearLayout linear = (LinearLayout)view.findViewById(R.id.linearTalla);
					linear.setVisibility(View.VISIBLE);
					editCantidad.setVisibility(View.INVISIBLE);
					mLinearTallas.setVisibility(View.VISIBLE);
					list_tallas = new ArrayList<Talla>();
//					for(int i =0;i<tallas.length;i++){
//						addViewTallas(tallas[i],i);
//					}
					enableTallas=true;
				}else
				{
					enableTallas=false;
					editCantidad.setVisibility(View.VISIBLE);
					((LinearLayout)view.findViewById(R.id.linearTalla)).setVisibility(View.GONE);
//					if(countTallas>0){
//						deletLayouts();
//					}
				}
			}
		}); 
		
		
		//Se agrega una talla
		masTallas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String com=null;
				if(enableOtraTalla){
					com = (String)spinnerTallas.getSelectedItem();
				}else{
					if(editTalla.getEditableText()!=null)
						if(!editTalla.getEditableText().toString().equals("")){
							com = editTalla.getEditableText().toString();
						}
						else{
							return;
						}
				}
				
				//if(!com.contentEquals()){
					if(editCantidadTallas.getEditableText()!=null)
						if(!editCantidadTallas.getEditableText().toString().equals("")){
							if(Integer.parseInt(editCantidadTallas.getEditableText().toString())>0){
								list_tallas.add(new Talla(com,editCantidadTallas.getEditableText().toString()));
								countTallas++;
								
								addView(com,editCantidadTallas.getEditableText().toString(), countTallas, mLinearTallas);
							}
			//			}
					if(visibleTallaMenos==false){
						view.findViewById(R.id.btnMenosTalla).setVisibility(View.VISIBLE);
						visibleTallaMenos=true;
					}
				}
			}
		});

	
		dialog.setView(view);

		/***Se elimina una talla***/
		menosTallas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(countTallas>0){
					final LinearLayout temp = (LinearLayout)mLinearTallas.findViewById(countTallas);
					temp.removeAllViews();
					mLinearTallas.removeView(temp);
					countTallas--;
					list_tallas.remove(list_tallas.size()-1);
				}
			}
		});
		
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
						nombre=editNombre.getEditableText().toString();
					else{
						listener.makeToastDialog(R.string.sin_nombre_producto);
						return;
					}
				}
				else{
					return;}

				if(!enableTallas){
					if(editCantidad.getEditableText()!=null){
						if(!editCantidad.getEditableText().toString().contentEquals(""))
							if(Integer.parseInt(editCantidad.getEditableText().toString())>0){
								cantidad=editCantidad.getEditableText().toString();
							}else{
								listener.makeToastDialog(R.string.sin_cantidad_valida);
								return;
							}
						else
						{
							listener.makeToastDialog(R.string.sin_cantidad);
							return;
						}
					}
					else {
						listener.makeToastDialog(R.string.sin_cantidad);
						return;}}
				
				if(editPrecio.getEditableText()!=null){
					if(!editPrecio.getEditableText().toString().contentEquals("")){
						if(Double.parseDouble(editPrecio.getEditableText().toString())>0)
							precio = editPrecio.getEditableText().toString();
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

				if(enableTallas){
					for(int i = 0;i<list_tallas.size();i++){
			/*			LinearLayout temp = (LinearLayout) mLinearTallas.findViewById(i);
						EditText edit = (EditText)temp.getChildAt(1);
						if(edit.getEditableText()!=null){
							if(!edit.getEditableText().toString().contentEquals("")){
								if(Integer.parseInt(edit.getEditableText().toString())>0){*/
									//Nombre, Tipo, cantidadTalla, artista, precio, impuestos, comisiones, path,idimagen, talla
									Product p = new Product(nombre, tipo, spinnerArtistas.getSelectedItem().toString(), precio, list_tallas.get(i).getTalla(),Integer.parseInt(list_tallas.get(i).getCantidad()),list_comisiones ,path);
							//		p.setTotalCantidad(Integer.parseInt(edit.getEditableText().toString()));
									p.setTotalCantidad(Integer.parseInt(list_tallas.get(i).getCantidad()));
									p.setComisiones(list_comisiones);
									p.setTaxes(list_taxes);
									listener.articuloNuevo(p, idImagen);
									//	listener.articuloNuevo(nombre, spinnerTipos.getSelectedItem().toString(), edit.getEditableText().toString(),spinnerArtistas.getSelectedItem().toString(),precio,list_taxes,taxes,path,idImagen,tallas[i]);
						/*		}
								else{
									return;
								}
							}
						}*/

					}

				}else{
					Product p = new Product(nombre, tipo, spinnerArtistas.getSelectedItem().toString(), precio, "", Integer.parseInt(cantidad), list_comisiones, path);
					p.setTotalCantidad(Integer.parseInt(cantidad));
					p.setComisiones(list_comisiones);
					p.setTaxes(list_taxes);
					listener.articuloNuevo(p, idImagen);
					//	listener.articuloNuevo(nombre, spinnerTipos.getSelectedItem().toString(), cantidad,spinnerArtistas.getSelectedItem().toString(),
					//		precio,list_taxes,list_comisiones,cantidad,path,idImagen,"");
				}
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
		if(imgFile.exists())
		{
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			img.setImageBitmap(myBitmap);
			enableSpinner  = false;
			idImagen=0;
		}
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

		layout.addView(linear);
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
		layout.addView(linear);
		layout.invalidate();
	}

/*
	private void addViewTallas(String talla,int i){
		countTallas=i;
		LinearLayout linear = new LinearLayout(getActivity());
		linear.setId(countTallas);
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		TextView txt = new TextView(getActivity());
		EditText edit = new EditText(getActivity());
		txt.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.8f));
		edit.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.2f));
		//		edit.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		edit.setId(i);
		txt.setText(talla);
		txt.setTextColor(getResources().getColor(R.color.azul));
		txt.setLayoutParams(params2);
		linear.addView(txt);
		linear.addView(edit);
		mLinearTallas.addView(linear);
		mLinearTallas.invalidate();
	}

	private void deletLayouts(){
		for(int j =0;j<=countTallas;j++){
			final LinearLayout temp = (LinearLayout) mLinearTallas.findViewById(j);
			temp.removeAllViews();
			mLinearTallas.removeView(temp);
		}
	}*/
}
