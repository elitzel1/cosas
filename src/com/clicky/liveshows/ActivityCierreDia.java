package com.clicky.liveshows;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.clicky.liveshows.DatePickerFragment.DatePickerFragmentListener;
import com.clicky.liveshows.adapters.AdapterSpinnerAgencias;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.Agencia;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.Excel;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Gastos;
import com.clicky.liveshows.utils.Taxes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCierreDia extends Activity implements DatePickerFragmentListener{
	
	private DBAdapter dbHelper;
	private Excel excel;
	
	private ArrayList<Agencia> agencias;
	private ArrayList<Gastos> gastos;
	private ArrayList<Gastos> sueldos;
	private ArrayList<Double> totales;
	private HashMap<Integer, String> artistas;
	private HashMap<Integer, Product> mapProd;
	List<Product> products;
	private int[]art;
	
	int idEvento,idfecha,capacidad;
	int posVenue=0;
	String evento,fecha,local;
	double efectivo = 0,banamex =0,banorte=0,santander=0,amex=0,otros=0;
	boolean more = false;
	
	private LinearLayout layoutViaticos,layoutSueldos,layoutReportes;
	private EditText editCantidad,editCantSueldos;
	private Spinner spinnerGasto,spinnerSueldos,spinVenue;
	private RadioGroup radioComprobante,radioComprobanteSueldos;

	private static String mailValidation = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+
			"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cierre_dia);

		dbHelper = new DBAdapter(this);
		excel = new Excel(this);
		
		products = new ArrayList<Product>();
		artistas  = new HashMap<Integer, String>();
		mapProd  = new HashMap<Integer, Product>();
		gastos = new ArrayList<Gastos>();
		sueldos = new ArrayList<Gastos>();
		totales = new ArrayList<Double>();
		
		layoutViaticos = (LinearLayout)findViewById(R.id.listGastos);
		layoutSueldos = (LinearLayout)findViewById(R.id.listSueldos);
		layoutReportes = (LinearLayout)findViewById(R.id.layoutReportes);
		spinnerGasto = (Spinner)findViewById(R.id.spinnerGasto);
		spinnerSueldos = (Spinner)findViewById(R.id.spinnerSueldo);
		editCantidad = (EditText)findViewById(R.id.editViatico);
		editCantSueldos = (EditText)findViewById(R.id.editSueldo);
		radioComprobante = (RadioGroup)findViewById(R.id.comprobante);
		radioComprobanteSueldos = (RadioGroup)findViewById(R.id.comprobanteSueldo);
		
		spinVenue = (Spinner)findViewById(R.id.spinVenue);
		
		setupActionBar();
		setXML();
		
		dbHelper.open();
		
		Cursor cArtistas = dbHelper.fetchAllArtistas();
		art = new int[cArtistas.getCount()];
		int i = 0;
		if(cArtistas.moveToFirst()){
			do{
				int id = cArtistas.getInt(0);
				String name = cArtistas.getString(1);
				art[i] = id;
				i++;
				artistas.put(id, name);
			}while(cArtistas.moveToNext());
		}
		cArtistas.close();
		
		Cursor cEvento = dbHelper.fetchAllEvento();
		if(cEvento.moveToFirst()){
			do{
				idEvento = cEvento.getInt(0);
				evento = cEvento.getString(1);
				local = cEvento.getString(2);
				capacidad = cEvento.getInt(3);
			}while(cEvento.moveToNext());
		}else{
		}
		cEvento.close();
		
		List<Date> dates = new ArrayList<Date>();
		HashMap<Date, Integer> hashDate = new HashMap<Date, Integer>();
		Cursor cFecha = dbHelper.fetchAllFechas();
		if(cFecha.getCount() > 1){
			more = true;
		}
		if(cFecha.moveToFirst()){
			do{
				hashDate.put(toDateDate(cFecha.getString(1)), cFecha.getInt(0));
				dates.add(toDateDate(cFecha.getString(1)));
			}while(cFecha.moveToNext());
		}
		cFecha.close();
		dbHelper.close();
		
		Collections.sort(dates, new Comparator<Date>() {

			@Override
			public int compare(Date lhs, Date rhs) {
				return lhs.compareTo(rhs);
			}
		});
		
		idfecha = hashDate.get(dates.get(0));
		DateFormat df= DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		fecha = df.format(dates.get(0));

		setArtistas();
		
		spinVenue.setAdapter(new AdapterSpinnerAgencias(this,R.layout.item_spinner_drop, agencias));
		spinVenue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if(pos != 0){
					posVenue = pos;
					((EditText)findViewById(R.id.contactoVenue)).setText(agencias.get(pos).getContacto());
					((EditText)findViewById(R.id.mailVenue)).setText(agencias.get(pos).getMail());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cierre_stand, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_accept:
			showAlert();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onFinishDatePickerDialog(int year, int month, int day) {
		dbHelper.open();
		dbHelper.createFecha(""+day+"/"+month+"/"+year);
		dbHelper.deleteFecha(idfecha);
		dbHelper.close();
		setResult(RESULT_OK);
		finish();
	}
	
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
	}
	
	public void addViatico(View v){
		String concepto = ((TextView)spinnerGasto.getSelectedView()).getText().toString();
		String cantidad = editCantidad.getText().toString();
		int tipoComprobante = radioComprobante.getCheckedRadioButtonId();
		if(!concepto.equals("")){
			if(!cantidad.contentEquals("")){
				double cant = Double.parseDouble(cantidad);
				if(cant > 0){
					String comprobante = ((RadioButton)findViewById(tipoComprobante)).getText().toString();
					gastos.add(new Gastos(concepto,cant,comprobante));
					addView(concepto,cantidad,comprobante,0);
					if(gastos.size() == 1){
						findViewById(R.id.btnEliminarComision).setVisibility(View.VISIBLE);
					}
					editCantidad.setText("");
				}else
					makeToast(R.string.sin_cantidad_valida);
			}else
				makeToast(R.string.sin_viatico);
		}else
			makeToast(R.string.sin_concepto);
	}
	
	public void removeViatico(View v){
		if(gastos.size() > 0){
			final LinearLayout temp = (LinearLayout)layoutViaticos.findViewById(gastos.size());
			temp.removeAllViews();
			layoutViaticos.removeView(temp);
			gastos.remove(gastos.size()-1);
			if(gastos.isEmpty())
				findViewById(R.id.btnEliminarComision).setVisibility(View.INVISIBLE);
		}
	}
	
	public void addSueldo(View v){
		String concepto = ((TextView)spinnerSueldos.getSelectedView()).getText().toString();
		String cantidad = editCantSueldos.getText().toString();
		int tipoComprobante = radioComprobanteSueldos.getCheckedRadioButtonId();
		if(!concepto.equals("")){
			if(!cantidad.contentEquals("")){
				double cant = Double.parseDouble(cantidad);
				if(cant > 0){
					String comprobante = ((RadioButton)findViewById(tipoComprobante)).getText().toString();
					sueldos.add(new Gastos(concepto,cant,comprobante));
					addView(concepto,cantidad,comprobante,1);
					if(sueldos.size() == 1){
						findViewById(R.id.btnEliminarComisionSueldo).setVisibility(View.VISIBLE);
					}
					editCantSueldos.setText("");
				}else
					makeToast(R.string.sin_cantidad_valida);
			}else
				makeToast(R.string.sin_viatico);
		}else
			makeToast(R.string.sin_concepto);
	}
	
	public void removeSueldo(View v){
		if(sueldos.size() > 0){
			final LinearLayout temp = (LinearLayout)layoutSueldos.findViewById(sueldos.size());
			temp.removeAllViews();
			layoutSueldos.removeView(temp);
			sueldos.remove(sueldos.size()-1);
			if(sueldos.isEmpty())
				findViewById(R.id.btnEliminarComisionSueldo).setVisibility(View.INVISIBLE);
		}
	}
	
	public void enviarMail(View v){
		String mail = "";
		boolean acepta = false;
		if(v == (Button)findViewById(R.id.enviarInterno)){
			mail = ((EditText)findViewById(R.id.mailInterno)).getText().toString();
			acepta = validaCorreo(mail);
			getReport(0, "","","");
			if(acepta){
					
			}
		}else if(v == (Button)findViewById(R.id.enviarVenue)){
			mail = ((EditText)findViewById(R.id.mailVenue)).getText().toString();
			acepta = validaCorreo(mail);
			getReport(1, agencias.get(posVenue).getNombre(), agencias.get(posVenue).getContacto(),"");
			if(acepta){
					
			}	
		}
		if(acepta){
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});		  
			email.putExtra(Intent.EXTRA_SUBJECT, "Sales Report");
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
		}
	}
	
	private void addView(String concepto, String cantidad,String comprobante,int tipo){
		LinearLayout linear = new LinearLayout(this);
		if(tipo == 0)
			linear.setId(gastos.size());
		else if(tipo == 1)
			linear.setId(sueldos.size());
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.5f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView txt = new TextView(this);
		txt.setText(concepto);
		txt.setTextColor(getResources().getColor(R.color.azul));
		txt.setLayoutParams(params2);
		linear.addView(txt);

		TextView txtA = new TextView(this);
		txtA.setText(cantidad);
		txtA.setTextColor(getResources().getColor(R.color.azul));
		txtA.setLayoutParams(params2);
		linear.addView(txtA);
		
		TextView txtB = new TextView(this);
		txtB.setText(comprobante);
		txtB.setTextColor(getResources().getColor(R.color.azul));
		txtB.setLayoutParams(params2);
		linear.addView(txtB);

		if(tipo == 0){
			layoutViaticos.addView(linear);
			layoutViaticos.invalidate();
		}else if(tipo == 1){
			layoutSueldos.addView(linear);
			layoutSueldos.invalidate();
		}
	}
	
	private void addReporte(final String artista){
		LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView txt = new TextView(this);
		txt.setText(artista);
		txt.setTextColor(getResources().getColor(R.color.azul_textos));
		txt.setLayoutParams(params);
		layoutReportes.addView(txt);
		
		LinearLayout linear = new LinearLayout(this);
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.5f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		final Spinner spin = new Spinner(this);
		spin.setAdapter(new AdapterSpinnerAgencias(this,R.layout.item_spinner_drop, agencias));
		spin.setLayoutParams(params2);
		linear.addView(spin);
		
		final EditText contacto = new EditText(this);
		contacto.setTextColor(getResources().getColor(R.color.azul));
		contacto.setLayoutParams(params2);
		contacto.setHint(R.string.hint_contacto);
		contacto.setSingleLine();
		linear.addView(contacto);
		
		final EditText mail = new EditText(this);
		mail.setTextColor(getResources().getColor(R.color.azul));
		mail.setLayoutParams(params2);
		mail.setHint(R.string.hint_mail);
		mail.setSingleLine();
		linear.addView(mail);
		
		Button btnMail = new Button(this);
		btnMail.setText(R.string.enviar_mail);
		btnMail.setTextColor(getResources().getColor(R.color.blanco));
		btnMail.setBackgroundResource(R.drawable.fondo_boton);
		
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if(pos != 0){
					contacto.setText(agencias.get(pos).getContacto());
					mail.setText(agencias.get(pos).getMail());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		btnMail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getReport(2, agencias.get(spin.getSelectedItemPosition()).getNombre(), agencias.get(spin.getSelectedItemPosition()).getContacto(),artista);
				if(validaCorreo(mail.getText().toString())){
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[]{mail.getText().toString()});		  
					email.putExtra(Intent.EXTRA_SUBJECT, "Sales Report");
					email.setType("message/rfc822");
					startActivity(Intent.createChooser(email, "Choose an Email client :"));
				}
			}
		});
		
		linear.addView(btnMail);

		layoutReportes.addView(linear);
		layoutReportes.invalidate();
	}
	
	private void makeToast(int text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private void showAlert(){
		if(more){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_next_date);
			builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					borraDia();
				}
			});
			builder.setNegativeButton(R.string.close_event, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					borraTodo();
				}
			});
			builder.create().show();
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_no_dates);
			builder.setPositiveButton(R.string.close_event, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					borraTodo();
				}
			});
			builder.setNegativeButton(R.string.add_date, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.cancel();
					DatePickerFragment fragment  = new DatePickerFragment();
					fragment.show(getFragmentManager(), "datePicker");
				}
			});
			builder.create().show();
		}
		
	}
	
	private void borraDia(){
		//TODO Teminar cierre mil 
		dbHelper.open();
		dbHelper.deleteFecha(idfecha);
		Cursor cursorProd = dbHelper.fetchAllProductos();
		if(cursorProd.moveToFirst()){
			do{
				Product prod = new Product();
				prod.setId(cursorProd.getInt(0));
				
				int cant = cursorProd.getInt(5);
				Cursor cursorStandProd = dbHelper.fetchStandProductDetail(prod.getId());
				if(cursorStandProd.moveToFirst()){
					do{
						int standId = cursorStandProd.getInt(5);
						int cantStand = cursorStandProd.getInt(1);
						Cursor ventas = dbHelper.fetchVentasProd(cursorStandProd.getInt(0));
						if(ventas.moveToFirst()){
							do{
								cant -= ventas.getInt(3);
								cantStand -= ventas.getInt(3);
							}while(ventas.moveToNext());
						}
						dbHelper.updateStandProducto(prod.getId(), standId, cantStand);
					}while(cursorStandProd.moveToNext());
				}
				dbHelper.updateProducto(prod.getId(), cant, cant);
			}while(cursorProd.moveToNext());
		}
		dbHelper.deleteDia();
		dbHelper.close();
		setResult(RESULT_OK);
		finish();
	}
	
	private void borraTodo(){
		dbHelper.open();
		dbHelper.deleteTodo();
		dbHelper.close();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("evento", 0);
		edit.commit();
		setResult(RESULT_OK);
		finish();
	}
	
	private void setArtistas(){
		for(int i = 0;i < artistas.size();i++){
			addReporte(artistas.get(art[i]));
		}
	}
	
	private void setXML(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = getApplicationContext().getAssets().open("agencias.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);

			parseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean validaCorreo(String mail){
		boolean acepta = true;
		if(mail.equals("")){
			makeToast(R.string.sin_correo);
			acepta = false;
		}else if(!mail.matches(mailValidation)){
			makeToast(R.string.no_correo);
			acepta = false;
		}
		return acepta;
	}
	
	private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{
		int eventType = parser.getEventType();
		Agencia currentLocal = null;

		while (eventType != XmlPullParser.END_DOCUMENT){
			String name = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				agencias = new ArrayList<Agencia>();
				currentLocal = new Agencia("","","");
				currentLocal.setNombre(getResources().getString(R.string.hint_agencia));
				agencias.add(currentLocal);
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.contentEquals("item")){
					currentLocal = new Agencia();
				}else if (currentLocal != null){
					if (name.contentEquals("agency")){
						currentLocal.setNombre(parser.nextText());
					}else if (name.contentEquals("contact")){
						currentLocal.setContacto(parser.nextText());
					}else if(name.contentEquals("mail")){
						currentLocal.setMail(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && currentLocal != null){
					agencias.add(currentLocal);
				} 
			}
			eventType = parser.next();
		}
	}
	
	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	private Date toDateDate(String myTimestamp){
		Log.i("FECHA", myTimestamp);
		SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
		try 
		{
			Date date = form.parse(myTimestamp);
			return date;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private void addProduct(Product p, List<Adicionales> adicionales){
		Product item = p;
		if(adicionales!=null){
			item.setAdicional(adicionales);
			Log.i("ADICIONALEs", ""+adicionales.size());
		}

		products.add(item);
		//mapProd.put(item.getId(), item);
	}
	
	public Integer getKeyFromValue(HashMap<Integer, String> hm, Object value) {
	    for (Object o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	    	  return (Integer)o;
	      }
	    }
	    return -1;
	  }
	
	private void getProducts(String artista){
		products.clear();
		mapProd.clear();
		totales.clear();
		dbHelper.open();
		if(artista.equals("")){
			Cursor cursorProd = dbHelper.fetchAllProductos();
			if(cursorProd.moveToFirst()){
				do{
					int id = cursorProd.getInt(0);
					String nombre = cursorProd.getString(1);
					String tipo = cursorProd.getString(2);
					String foto = cursorProd.getString(3);
					int cantidad = cursorProd.getInt(4);
					int cantidadTotal=cursorProd.getInt(5);
					String talla = cursorProd.getString(6);
					String precio = cursorProd.getString(7);
					int idEvento = cursorProd.getInt(8);
					int idArtista = cursorProd.getInt(9);


					List<Adicionales> a = null;
					Cursor cursorA = dbHelper.fetchAdicional(id);
					if(cursorA.moveToFirst()){
						a = new ArrayList<Adicionales>();
						do{
							int cantidadA = cursorA.getInt(1);
							String nomA = cursorA.getString(2);
							a.add(new Adicionales(nomA, cantidadA, id));
							Log.i("PAdicionales",nomA+" "+cantidadA);
						}while(cursorA.moveToNext());
					}
					cursorA.close();

					List<Comisiones> list_com=new ArrayList<Comisiones>();
					List<Taxes> list_tax=new ArrayList<Taxes>();
					List<Cortesias> cortesias = new ArrayList<Cortesias>();;

					Cursor cursorCortesias = dbHelper.fetchCortesias(id);
					if(cursorCortesias.moveToFirst()){
						cortesias= new ArrayList<Cortesias>();
						do{
							String tipoC = cursorCortesias.getString(1);
							int cantidadC =cursorCortesias.getInt(2);
							cortesias.add(new Cortesias(tipoC, cantidadC));
						}while(cursorCortesias.moveToNext());
					}


					List<Integer> id_impuestos = new ArrayList<Integer>();
					Cursor cursorI=dbHelper.fetchProductImpuestoProd(id);
					if(cursorI.moveToNext()){
						do{
							id_impuestos.add(cursorI.getInt(1));
						}while(cursorI.moveToNext());
					}
					cursorI.close();

					for(int j=0;j<id_impuestos.size();j++){
						Cursor cursorPI = dbHelper.fetchImpuestos(id_impuestos.get(j));

						if(cursorPI.moveToNext()){
							do{
								//taxes
								//comision
								//colIdTaxes,colNombreT,colPorcentajeT,colTipoImpuesto,colIVA,colTipoPorPeso
								String nombreI = cursorPI.getString(1);
								String porcentaje = cursorPI.getString(2);
								String tipoImpuesto = cursorPI.getString(3);
								if(tipoImpuesto.contentEquals("comision")){
									String iva = cursorPI.getString(4);
									String tipoPeso = cursorPI.getString(5);
									list_com.add(new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso));
								}else{

									list_tax.add(new Taxes(nombreI, Integer.parseInt(porcentaje)));
								}
							}while(cursorPI.moveToNext());	
						}
					}
					Product p = new Product(nombre, tipo, artistas.get(idArtista), precio, talla, cantidad, null, foto);
					p.setTotalCantidad(cantidadTotal);
					if(cortesias!=null){
						p.setCortesias(cortesias);
					}
					p.setId(id);
					p.setComisiones(list_com);
					p.setTaxes(list_tax);
					addProduct(p, a);
					
					Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
				}while(cursorProd.moveToNext());
				cursorProd.close();
				Cursor stands = dbHelper.fetchStandCierre();
				if(stands.moveToFirst()){
					do{
						efectivo += stands.getDouble(1);
						banamex += stands.getDouble(2);
						banorte += stands.getDouble(3);
						santander += stands.getDouble(4);
						amex += stands.getDouble(5);
						otros += stands.getDouble(6);
						
						Cursor ventas = dbHelper.fetchVentas(stands.getLong(0));
						if(ventas.moveToFirst()){
							do{
								int vendidos = ventas.getInt(3);
								Cursor ventaProd = dbHelper.fetchStandProductAll(ventas.getLong(0));
								if(ventaProd.moveToFirst()){
									Product prodAux;
									int proId = ventaProd.getInt(3);
									if(mapProd.get(proId) != null){
										prodAux = mapProd.get(proId);
										int suma = mapProd.get(proId).getProdNo() + vendidos;
										prodAux.setProdNo(suma);
									}else{
										prodAux = new Product();
										prodAux.setProdNo(vendidos);
										mapProd.put(proId, prodAux);
									}
								}
							}while(ventas.moveToNext());
						}
					}while(stands.moveToNext());
				}
			}
		}else{
			Cursor cursorProd = dbHelper.fetchProductosArtista(getKeyFromValue(artistas,artista));
			if(cursorProd.moveToFirst()){
				do{
					int id = cursorProd.getInt(0);
					String nombre = cursorProd.getString(1);
					String tipo = cursorProd.getString(2);
					String foto = cursorProd.getString(3);
					int cantidad = cursorProd.getInt(4);
					int cantidadTotal=cursorProd.getInt(5);
					String talla = cursorProd.getString(6);
					String precio = cursorProd.getString(7);
					int idEvento = cursorProd.getInt(8);
					int idArtista = cursorProd.getInt(9);


					List<Adicionales> a = null;
					Cursor cursorA = dbHelper.fetchAdicional(id);
					if(cursorA.moveToFirst()){
						a = new ArrayList<Adicionales>();
						do{
							int cantidadA = cursorA.getInt(1);
							String nomA = cursorA.getString(2);
							a.add(new Adicionales(nomA, cantidadA, id));
							Log.i("PAdicionales",nomA+" "+cantidadA);
						}while(cursorA.moveToNext());
					}
					cursorA.close();

					List<Comisiones> list_com=null;
					List<Taxes> list_tax=null;
					list_com = new ArrayList<Comisiones>();
					list_tax = new ArrayList<Taxes>();

					List<Cortesias> cortesias = null;
					Cursor cursorCortesias = dbHelper.fetchCortesias(id);
					if(cursorCortesias.moveToFirst()){
						cortesias= new ArrayList<Cortesias>();
						do{
							String tipoC = cursorCortesias.getString(1);
							int cantidadC =cursorCortesias.getInt(2);
							cortesias.add(new Cortesias(tipoC, cantidadC));
						}while(cursorCortesias.moveToNext());
					}


					List<Integer> id_impuestos = new ArrayList<Integer>();
					Cursor cursorI=dbHelper.fetchProductImpuestoProd(id);
					if(cursorI.moveToNext()){
						do{
							id_impuestos.add(cursorI.getInt(1));
						}while(cursorI.moveToNext());
					}
					cursorI.close();

					for(int j=0;j<id_impuestos.size();j++){
						Cursor cursorPI = dbHelper.fetchImpuestos(id_impuestos.get(j));

						if(cursorPI.moveToNext()){
							do{
								//taxes
								//comision
								//colIdTaxes,colNombreT,colPorcentajeT,colTipoImpuesto,colIVA,colTipoPorPeso
								String nombreI = cursorPI.getString(1);
								String porcentaje = cursorPI.getString(2);
								String tipoImpuesto = cursorPI.getString(3);
								if(tipoImpuesto.contentEquals("comision")){
									String iva = cursorPI.getString(4);
									String tipoPeso = cursorPI.getString(5);
									list_com.add(new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso));
								}else{

									list_tax.add(new Taxes(nombreI, Integer.parseInt(porcentaje)));
								}
							}while(cursorPI.moveToNext());	
						}
					}
					Product p = new Product(nombre, tipo, artistas.get(idArtista), precio, talla, cantidad, null, foto);
					p.setTotalCantidad(cantidadTotal);
					if(cortesias!=null){
						p.setCortesias(cortesias);
					}
					p.setId(id);
					p.setComisiones(list_com);
					p.setTaxes(list_tax);
					addProduct(p, a);
					
					Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
				}while(cursorProd.moveToNext());
				cursorProd.close();
				Cursor stands = dbHelper.fetchAllStand();
				if(stands.moveToFirst()){
					do{
						Cursor ventas = dbHelper.fetchVentas(stands.getLong(0));
						if(ventas.moveToFirst()){
							do{
								int vendidos = ventas.getInt(3);
								Cursor ventaProd = dbHelper.fetchStandProductAll(ventas.getLong(0));
								if(ventaProd.moveToFirst()){
									Product prodAux;
									int proId = ventaProd.getInt(3);
									if(mapProd.get(proId) != null){
										prodAux = mapProd.get(proId);
										int suma = mapProd.get(proId).getProdNo() + vendidos;
										prodAux.setProdNo(suma);
									}else{
										prodAux = new Product();
										prodAux.setProdNo(vendidos);
										mapProd.put(proId, prodAux);
									}
								}
							}while(ventas.moveToNext());
						}
					}while(stands.moveToNext());
				}
			}
		}
		dbHelper.close();
	}
	
	/**
	 * 
	 * @param tipo 0 - LSG
	 * 			   1 - Venue
	 * 			   2 - Agency
	 * @param agency
	 * @param contact
	 */
	private void getReport(int tipo,String agency, String contact,String artista){
		getProducts(artista);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		WritableWorkbook wb = excel.createWorkbook("venue.xls");
		WritableSheet hoja1 = excel.createSheet(wb, "Venue", 0);
		try {
			
			hoja1.mergeCells(1, 3, 22, 3);
			//Formato del Reporte
			//excel.addImage(hoja1, R.drawable.ic_launcher);
			excel.writeCell(1, 3, "SALES REPORT(IN "+prefs.getString("moneda", "")+")", 10, hoja1);
			excel.writeCell(1, 5, "DATE", 1, hoja1);
			excel.writeCell(2, 5, fecha, 0, hoja1);
			excel.writeCell(1, 7, "EVENT", 1, hoja1);
			excel.writeCell(2, 7, evento, 0, hoja1);
			excel.writeCell(1, 9, "VENUE/\nPLACE", 1, hoja1);
			excel.writeCell(2, 9, local, 0, hoja1);
			if(tipo != 0){
				excel.writeCell(1, 11, "AGENCY", 1, hoja1);
				excel.writeCell(2, 11, agency, 0, hoja1);
				excel.writeCell(1, 13, "CONTACT", 1, hoja1);
				excel.writeCell(2, 13, contact, 1, hoja1);
			}
			excel.writeCell(0, 15, "PRICE SALES IN US", 12, hoja1);
			excel.writeCell(1, 15, "#", 2, hoja1);
			excel.writeCell(2, 15, "ITEM", 2, hoja1);
			excel.writeCell(3, 15, "STYLE", 2, hoja1);
			excel.writeCell(4, 15, "SIZE", 2, hoja1);
			excel.writeCell(5, 15, "INITIAL\nINVENTORY", 2, hoja1);
			excel.writeCell(6, 15, "ADDING 1", 2, hoja1);
			excel.writeCell(7, 15, "ADDING 2", 2, hoja1);
			excel.writeCell(8, 15, "ADDING 3", 2, hoja1);
			excel.writeCell(9, 15, "ADDING 4", 2, hoja1);
			excel.writeCell(10, 15, "ADDING 5", 2, hoja1);
			excel.writeCell(11, 15, "TOTAL\nINVENTORY", 2, hoja1);
			excel.writeCell(12, 15, "PRICE SALE", 2, hoja1);
			excel.writeCell(13, 15, "DAMAGE", 2, hoja1);
			excel.writeCell(14, 15, "COMPS\nVENUE", 2, hoja1);
			excel.writeCell(15, 15, "COMPS\nOFFICE\nPRODUCTION", 2, hoja1);
			excel.writeCell(16, 15, prefs.getString("op1", "OTHER"), 2, hoja1);
			excel.writeCell(17, 15, prefs.getString("op2", "OTHER"), 2, hoja1);
			
			excel.writeCell(18, 15, "FINAL INVENTORY", 2, hoja1);
			excel.writeCell(19, 15, "SALES PIECES", 2, hoja1);
			excel.writeCell(20, 15, "GROSS TOTAL", 2, hoja1);
			excel.writeCell(21, 15, "% SALES", 2, hoja1);
			excel.writeCell(22, 15, "GROSS TOTAL\nUS$DLLS", 12, hoja1);
			
			Float priceUs = Float.parseFloat(prefs.getString("divisa", "0"));
			double tax = 0, gross = 0, venueFee = 0, royaltyFee = 0;
			
			excel.writeCell(18, 13, "RATE EXCHANGE US$1 =", 12, hoja1);
			excel.writeCell(19, 13, ""+priceUs, 11, hoja1);
			excel.writeCell(20, 13, prefs.getString("moneda", ""), 12, hoja1);
			
			for(int i = 0; i < products.size(); i++){
				Product prod = products.get(i);
				
				excel.writeCell(0, (16+i), ""+(Float.parseFloat((prod.getPrecio()))/priceUs), 11, hoja1);
				excel.writeCell(1, (16+i), ""+(i+1), 4, hoja1);
				excel.writeCell(2, (16+i), prod.getTipo(), 3, hoja1);
				excel.writeCell(3, (16+i), prod.getNombre(), 3, hoja1);
				if(prod.getTalla().equals(""))
					excel.writeCell(4, (16+i), "N/A", 3, hoja1);
				else
					excel.writeCell(4, (16+i), prod.getTalla(), 3, hoja1);
				
				int cantIn = prod.getTotalCantidad();
				for(int j = 0; j < prod.getAdicionalSize(); j++){
					cantIn -= prod.getAdicional().get(j).getCantidad();
					excel.writeCell( (6+j), (16+i), ""+prod.getAdicional().get(j).getCantidad(), 4, hoja1);
				}
				excel.writeCell( 5, (16+i), ""+cantIn, 4, hoja1);
				
				excel.writeCell(11, (16+i), ""+prod.getTotalCantidad(), 4, hoja1);
				excel.writeCell(12, (16+i), prod.getPrecio(), 5, hoja1);
				
				int cmv = 0, cmo = 0, cmd = 0, cmo1 = 0, cmo2 = 0;
				for(Cortesias cort : prod.getCortesias()){
					if(cort.getTipo().equals("DAMAGE")){
						cmd += cort.getAmount();
					}else if(cort.getTipo().equals("COMPS VENUE")){
						cmv += cort.getAmount();
					}else if(cort.getTipo().equals("COMPS OFFICE PRODUCTION")){
						cmo += cort.getAmount();
					}else if(cort.getTipo().equals(prefs.getString("op1", "OTHER"))){
						cmo1 += cort.getAmount();
					}else if(cort.getTipo().equals(prefs.getString("op2", "OTHER"))){
						cmo2 += cort.getAmount();
					}
				}
				
				excel.writeCell(13, (16+i), ""+cmd, 4, hoja1);
				excel.writeCell(14, (16+i), ""+cmv, 4, hoja1);
				excel.writeCell(15, (16+i), ""+cmo, 4, hoja1);
				excel.writeCell(16, (16+i), ""+cmo1, 4, hoja1);
				excel.writeCell(17, (16+i), ""+cmo2, 4, hoja1);
				
				if(mapProd.get(prod.getId()) != null){
					prod.setProdNo(mapProd.get(prod.getId()).getProdNo());
				}else{
					prod.setProdNo(0);
				}
				
				int finalInventory = prod.getTotalCantidad()-(prod.getProdNo()+cmd+cmv+cmo+cmo1+cmo2);
				double total = Double.parseDouble(prod.getPrecio()) * prod.getProdNo();
				double subTotal = total;
				totales.add(total);
				gross += total;
				excel.writeCell(18, (16+i), ""+finalInventory, 4, hoja1);
				excel.writeCell(19, (16+i), ""+prod.getProdNo(), 4, hoja1);
				excel.writeCell(20, (16+i), ""+total, 5, hoja1);
				excel.writeCell(22, (16+i), ""+(total/priceUs), 11, hoja1);
				
				for(Taxes tx : prod.getTaxes()){
					tax += total * (tx.getAmount()* 0.01);
					subTotal -= total * (tx.getAmount()* 0.01);
				}
				for(Comisiones com : prod.getComisiones()){
					double cant = 0, aux = 0;
					if(com.getTipo().equals("After taxes")){
						cant = subTotal;
					}else if(com.getTipo().equals("Before Taxes")){
						cant = total;
					}
					if(com.getIva().equals("%")){
						aux = cant * (com.getCantidad() * 0.01);  
					}else if(com.getIva().equals("$")){
						aux = com.getCantidad() * prod.getProdNo();
					}
					if(com.getName().equals("VENUE")){
						venueFee += aux;
					}else if(com.getName().equals("AGENCY")){
						royaltyFee += aux;
					}
				}
			}
			for(int i=0;i<totales.size();i++){
				excel.writeCell(21, (16+i), ""+(totales.get(i)/gross), 9, hoja1);
			}
			
			excel.writeCell(18, 18+products.size(), "GROSS TOTAL", 6, hoja1);
			excel.writeCell(18, 19+products.size(), "TAX", 6, hoja1);
			excel.writeCell(18, 20+products.size(), "GROSS NET", 6, hoja1);
			
			excel.writeCell(19, 18+products.size(), ""+gross, 5, hoja1);
			excel.writeCell(19, 19+products.size(), ""+tax, 5, hoja1);
			excel.writeCell(19, 20+products.size(), ""+(gross-tax), 5, hoja1);
			
			excel.writeCell(22, 18+products.size(), ""+(gross/priceUs), 11, hoja1);
			excel.writeCell(22, 19+products.size(), ""+(tax/priceUs), 11, hoja1);
			excel.writeCell(22, 20+products.size(), ""+((gross-tax)/priceUs), 11, hoja1);
			
			if(tipo == 1){
				excel.writeCell(18, 22+products.size(), "VENUE FEE", 6, hoja1);
				excel.writeCell(19, 22+products.size(), ""+venueFee, 5, hoja1);
				excel.writeCell(22, 22+products.size(), ""+(venueFee/priceUs), 11, hoja1);
			}else if(tipo == 2){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
				excel.writeCell(19, 22+products.size(), ""+royaltyFee, 5, hoja1);
				excel.writeCell(22, 22+products.size(), ""+(royaltyFee/priceUs), 11, hoja1);
			}else if(tipo == 0){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
				excel.writeCell(18, 23+products.size(), "VENUE FEE", 6, hoja1);
				
				excel.writeCell(19, 22+products.size(), ""+royaltyFee, 5, hoja1);
				excel.writeCell(19, 23+products.size(), ""+venueFee, 5, hoja1);
				
				excel.writeCell(22, 22+products.size(), ""+(royaltyFee/priceUs), 11, hoja1);
				excel.writeCell(22, 23+products.size(), ""+(venueFee/priceUs), 11, hoja1);
				
				excel.writeCell(18, 25+products.size(), "GASTOS OPERATIVOS", 7, hoja1);
				excel.writeCell(20, 25+products.size(), "FACTURA", 7, hoja1);
				excel.writeCell(21, 25+products.size(), "NOTA", 7, hoja1);
				
				double totalGastos = 0;
				for(int i = 0; i< gastos.size(); i++){
					totalGastos += gastos.get(i).getCantidad();
					excel.writeCell(18, (26+products.size()+i), gastos.get(i).getConcepto(), 3, hoja1);
					excel.writeCell(19, (26+products.size()+i), ""+gastos.get(i).getCantidad(), 5, hoja1);
					if(gastos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (26+products.size()+i), "X", 8, hoja1);
					else if(gastos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (26+products.size()+i), "X", 8, hoja1);
				}
				excel.writeCell(18, 27+products.size()+gastos.size(), "SUBTOTAL", 7, hoja1);
				excel.writeCell(19, 27+products.size()+gastos.size(), ""+totalGastos, 5, hoja1);
				
				excel.writeCell(18, 29+products.size()+gastos.size(), "SUELDOS/BONOS/COMISIONES", 7, hoja1);
				excel.writeCell(20, 29+products.size()+gastos.size(), "FACTURA", 7, hoja1);
				excel.writeCell(21, 29+products.size()+gastos.size(), "NOTA", 7, hoja1);
				
				double totalSueldos = 0;
				for(int i = 0; i < sueldos.size(); i++){
					totalSueldos += sueldos.get(i).getCantidad();
					excel.writeCell(18, (30+products.size()+gastos.size()+i), sueldos.get(i).getConcepto(), 3, hoja1);
					excel.writeCell(19, (30+products.size()+gastos.size()+i), ""+sueldos.get(i).getCantidad(), 5, hoja1);
					if(sueldos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (30+products.size()+gastos.size()+i), "X", 8, hoja1);
					else if(sueldos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (30+products.size()+gastos.size()+i), "X", 8, hoja1);
				}
				excel.writeCell(18, 31+products.size()+gastos.size()+sueldos.size(), "SUBTOTAL", 7, hoja1);
				excel.writeCell(19, 31+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				excel.writeCell(18, 33+products.size()+gastos.size()+sueldos.size(), "TOTAL GASTOS OPERATIVOS", 0, hoja1);
				excel.writeCell(20, 33+products.size()+gastos.size()+sueldos.size(), ""+totalGastos, 5, hoja1);
				excel.writeCell(18, 34+products.size()+gastos.size()+sueldos.size(), "TOTAL SUELDOS/BONOS/COMISIONES", 0, hoja1);
				excel.writeCell(20, 34+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				double depositar = totalSueldos+totalGastos;
				excel.writeCell(18, 36+products.size()+gastos.size()+sueldos.size(), "TOTAL A DEPOSITAR", 0, hoja1);
				excel.writeCell(20, 36+products.size()+gastos.size()+sueldos.size(), ""+depositar, 5, hoja1);
				
				excel.writeCell(18, 38+products.size()+gastos.size()+sueldos.size(), "INGRESOS RECIBIDOS", 0, hoja1);
				
				excel.writeCell(18, 39+products.size()+gastos.size()+sueldos.size(), "EFECTIVO", 3, hoja1);
				excel.writeCell(18, 40+products.size()+gastos.size()+sueldos.size(), "TC BANAMEX", 3, hoja1);
				excel.writeCell(18, 41+products.size()+gastos.size()+sueldos.size(), "TC BANORTE", 3, hoja1);
				excel.writeCell(18, 42+products.size()+gastos.size()+sueldos.size(), "TC SANTANDER", 3, hoja1);
				excel.writeCell(18, 43+products.size()+gastos.size()+sueldos.size(), "TC AMEX", 3, hoja1);
				excel.writeCell(18, 44+products.size()+gastos.size()+sueldos.size(), "OTROS", 3, hoja1);
				
				excel.writeCell(20, 39+products.size()+gastos.size()+sueldos.size(), ""+efectivo, 5, hoja1);
				excel.writeCell(20, 40+products.size()+gastos.size()+sueldos.size(), ""+banamex, 5, hoja1);
				excel.writeCell(20, 41+products.size()+gastos.size()+sueldos.size(), ""+banorte, 5, hoja1);
				excel.writeCell(20, 42+products.size()+gastos.size()+sueldos.size(), ""+santander, 5, hoja1);
				excel.writeCell(20, 43+products.size()+gastos.size()+sueldos.size(), ""+amex, 5, hoja1);
				excel.writeCell(20, 44+products.size()+gastos.size()+sueldos.size(), ""+otros, 5, hoja1);
				
				double depositado = efectivo+banamex+banorte+santander+amex+otros;
				excel.writeCell(18, 45+products.size()+gastos.size()+sueldos.size(), "TOTAL DEPOSITADO", 0, hoja1);
				excel.writeCell(20, 45+products.size()+gastos.size()+sueldos.size(), ""+depositado, 5, hoja1);
				
				excel.writeCell(18, 47+products.size()+gastos.size()+sueldos.size(), "DIF +/-", 0, hoja1);
				excel.writeCell(20, 47+products.size()+gastos.size()+sueldos.size(), ""+(depositado-depositar), 5, hoja1);
				
				excel.writeCell(15, 5, "ATTENDANCE", 6, hoja1);
				excel.writeCell(15, 7, "PERCAP", 6, hoja1);
				excel.writeCell(15, 9, "GROSS TOTAL", 6, hoja1);
				
				excel.writeCell(16, 5, ""+capacidad, 4, hoja1);
				excel.writeCell(16, 7, ""+(gross/capacidad), 5, hoja1);
				excel.writeCell(16, 9, ""+gross, 5, hoja1);
			}
			
			excel.sheetAutoFitColumns(hoja1);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		try {
			wb.write();
			wb.close();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
