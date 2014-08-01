package com.clicky.liveshows;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import com.clicky.liveshows.utils.PDF;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Gastos;
import com.clicky.liveshows.utils.Taxes;
import com.itextpdf.text.Document;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
	private PDF pdf;
	
	private ArrayList<Agencia> agencias;
	private ArrayList<Gastos> gastos;
	private ArrayList<Gastos> sueldos;
	private ArrayList<Double> totales;
	private String[]txtGastos,txtSueldos;
	private HashMap<Integer, String> artistas;
	List<Product> products;
	private int[]art;
	
	double totalVenta = 0;
	int idEvento,idfecha,capacidad;
	int posVenue=0;
	String evento,fecha,local;
	double efectivo = 0,banamex =0,banorte=0,santander=0,amex=0,otro1=0,otro2=0,otro3=0,comisionVendedor=0;
	boolean more = false;
	
	private LinearLayout layoutViaticos,layoutSueldos,layoutReportes;
	private EditText editCantidad,editCantSueldos;
	private Spinner spinnerGasto,spinnerSueldos;
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
		pdf = new PDF(this);
		
		products = new ArrayList<Product>();
		artistas  = new HashMap<Integer, String>();
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
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		txtGastos = new String[]{"LONAS","GASOLINA PARA CARRETERA","ESTACIONAMIENTO","HOTELES","VUELOS","CAMIONES","PROPINAS",
				"BAÑOS PUBLICOS","TELEFONIA","COMIDAS","REPARACIONES","MUDANZAS","TRANSPORTACION","CASETAS","RENTA DE CARPAS",
				"RENTA DE TABLONES Y MANTELERIA","RENTA DE VALLAS","MULTAS","PERMISOS Y TRAMITES",
				prefs.getString("gasto1", "OTHER"),prefs.getString("gasto2", "OTHER"),prefs.getString("gasto3", "OTHER")};
		
		txtSueldos = new String[]{"SUPERVISORES","BONO GERENCIAL","SUELDO CHOFER","MONTAJE/DESMONTAJE",
				prefs.getString("sueldo1", "OTHER"),prefs.getString("sueldo2", "OTHER"),prefs.getString("sueldo3", "OTHER")};
		
		ArrayAdapter<String> gatosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,txtGastos);
		gatosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		ArrayAdapter<String> sueldosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,txtSueldos);
		sueldosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerGasto.setAdapter(gatosAdapter);
		spinnerSueldos.setAdapter(sueldosAdapter);
		
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

		((EditText)findViewById(R.id.agenciaVenue)).setText(local);
		setArtistas();
		
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
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
	}
	
	@Override
	public void onFinishDatePickerDialog(int year, int month, int day) {
		dbHelper.open();
		dbHelper.createFecha(""+day+"/"+month+"/"+year);
		dbHelper.close();
		borraDia();
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
		int tipo = -1;
		if(v == (Button)findViewById(R.id.enviarInterno)){
			mail = ((EditText)findViewById(R.id.mailInterno)).getText().toString();
			acepta = validaCorreo(mail);
			if(acepta){
				tipo = 0;
				getReport(0, "","","");
			}
		}else if(v == (Button)findViewById(R.id.enviarVenue)){
			String agencia = ((EditText)findViewById(R.id.agenciaVenue)).getText().toString();
			String contacto = ((EditText)findViewById(R.id.contactoVenue)).getText().toString();
			mail = ((EditText)findViewById(R.id.mailVenue)).getText().toString();
			acepta = validaCorreo(mail);
			if(acepta){
				tipo = 1;
				getPDFReport(1,agencia , contacto,"");
			}	
		}
		if(acepta){
			String reporte = "";
			if(tipo == 0)
				reporte = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MerchSys/sales_report.xls";
			else if(tipo == 1)
				reporte = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MerchSys/sales_report.pdf";
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});		  
			email.putExtra(Intent.EXTRA_SUBJECT, "Sales Report");
			email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+reporte));
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
			layoutViaticos.addView(linear,0);
			layoutViaticos.invalidate();
		}else if(tipo == 1){
			layoutSueldos.addView(linear,0);
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
		LinearLayout.LayoutParams params3 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.7f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		final Spinner spin = new Spinner(this);
		spin.setAdapter(new AdapterSpinnerAgencias(this,R.layout.item_spinner_drop, agencias));
		spin.setLayoutParams(params2);
		linear.addView(spin);
		
		final EditText agencia = new EditText(this);
		agencia.setTextColor(getResources().getColor(R.color.azul));
		agencia.setLayoutParams(params2);
		agencia.setHint(R.string.hint_agencia_venue);
		agencia.setSingleLine();
		linear.addView(agencia);
		
		final EditText contacto = new EditText(this);
		contacto.setTextColor(getResources().getColor(R.color.azul));
		contacto.setLayoutParams(params2);
		contacto.setHint(R.string.hint_contacto);
		contacto.setSingleLine();
		linear.addView(contacto);
		
		final EditText mail = new EditText(this);
		mail.setTextColor(getResources().getColor(R.color.azul));
		mail.setLayoutParams(params3);
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
					agencia.setText(agencias.get(pos).getNombre());
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
				getPDFReport(2, agencia.getText().toString(), contacto.getText().toString(),artista);
				if(validaCorreo(mail.getText().toString())){
					String reporte = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MerchSys/sales_report.pdf";
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[]{mail.getText().toString()});		  
					email.putExtra(Intent.EXTRA_SUBJECT, "Sales Report");
					email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+reporte));
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
		dbHelper.open();
		dbHelper.deleteFecha(idfecha);
		
		Cursor cursor1 = dbHelper.fetchAllFechas();
		List<Date> dates = new ArrayList<Date>();
		HashMap<Date, Integer> hashDate = new HashMap<Date, Integer>();
		if(cursor1.moveToFirst()){
			do{
				hashDate.put(toDateDate(cursor1.getString(1)), cursor1.getInt(0));
				dates.add(toDateDate(cursor1.getString(1)));
				Log.i("BD",""+cursor1.getString(1));
			}while(cursor1.moveToNext());
		}
		cursor1.close();
		Collections.sort(dates, new Comparator<Date>() {

			@Override
			public int compare(Date lhs, Date rhs) {
				return lhs.compareTo(rhs);
			}
		});
		
		int idNewFecha = hashDate.get(dates.get(0));
		
		Cursor cursorProd = dbHelper.fetchAllProductos();
		if(cursorProd.moveToFirst()){
			do{
				Product prod = new Product();
				prod.setId(cursorProd.getInt(0));
				
				int cant = cursorProd.getInt(4);
				int asignados = 0;
				
				Cursor cursorStandProd = dbHelper.fetchStandProductDetail(prod.getId(),idfecha);
				if(cursorStandProd.moveToFirst()){
					do{
						int standId = cursorStandProd.getInt(5);
						int idComision = cursorStandProd.getInt(4);
						int cantStand = cursorStandProd.getInt(1);
						asignados += cantStand;
						cant += cantStand;
						dbHelper.createStandProducto(standId, prod.getId(), idNewFecha, cantStand, idComision);
					}while(cursorStandProd.moveToNext());
				}
				dbHelper.updateProducto(prod.getId(), cant, (cant-asignados));
			}while(cursorProd.moveToNext());
		}
		Cursor cursorStand = dbHelper.fetchAllStand();
		if(cursorStand.moveToFirst()){
			do{
				dbHelper.updateAbrirStand(cursorStand.getLong(0));
			}while(cursorStand.moveToNext());
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
		totales.clear();
		dbHelper.open();
		totalVenta = 0;
		efectivo = banamex = banorte = santander = amex = otro1 = otro2 = otro3 = comisionVendedor = 0;
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
								}else if(tipoImpuesto.equals("taxes")){
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
					int ventas = 0;
					
					Cursor cursorStand = dbHelper.fetchStandProductDetail(id, idfecha);
					if(cursorStand.moveToFirst()){
						do{
							int standProdId = cursorStand.getInt(0);
							Cursor cursorVentas = dbHelper.fetchVentasProd(standProdId);
							if(cursorVentas.moveToFirst()){
								do{
									ventas += cursorVentas.getInt(3);
								}while(cursorVentas.moveToNext());
							}
						}while(cursorStand.moveToNext());
					}
					
					p.setProdNo(ventas);
					totalVenta += ventas * Double.parseDouble(p.getPrecio());
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
						otro1 += stands.getDouble(6);
						otro2 += stands.getDouble(7);
						otro3 += stands.getDouble(8);
						comisionVendedor += stands.getDouble(9);

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
								}else if(tipoImpuesto.equals("taxes")){
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
					int ventas = 0;
					
					Cursor cursorStand = dbHelper.fetchStandProductDetail(id, idfecha);
					if(cursorStand.moveToFirst()){
						do{
							int standProdId = cursorStand.getInt(0);
							Cursor cursorVentas = dbHelper.fetchVentasProd(standProdId);
							if(cursorVentas.moveToFirst()){
								do{
									ventas += cursorVentas.getInt(3);
								}while(cursorVentas.moveToNext());
							}
						}while(cursorStand.moveToNext());
					}
					
					p.setProdNo(ventas);
					totalVenta += ventas * Double.parseDouble(p.getPrecio());
					addProduct(p, a);
					
					Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
				}while(cursorProd.moveToNext());
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
		
		WritableWorkbook wb = excel.createWorkbook("sales_report.xls");
		
		WritableSheet hoja1 = excel.createSheet(wb, "Sales", 0);
		
		try {
			hoja1.mergeCells(6, 3, 11, 3);
			//Formato del Reporte
			excel.writeCell(6, 3, "SALES REPORT(IN "+prefs.getString("moneda", "")+")", 10, hoja1);
			excel.writeCell(1, 5, "DATE", 6, hoja1);
			excel.writeCell(2, 5, fecha, 0, hoja1);
			excel.writeCell(1, 7, "EVENT", 6, hoja1);
			excel.writeCell(2, 7, evento, 0, hoja1);
			excel.writeCell(1, 9, "VENUE/\nPLACE", 6, hoja1);
			excel.writeCell(2, 9, local, 0, hoja1);
			if(tipo != 0){
				excel.writeCell(1, 11, "AGENCY", 6, hoja1);
				excel.writeCell(2, 11, agency, 0, hoja1);
				excel.writeCell(1, 13, "CONTACT", 6, hoja1);
				excel.writeCell(2, 13, contact, 1, hoja1);
			}
			excel.writeCell(0, 15, "PRICE SALES IN US", 2, hoja1);
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
			excel.writeCell(22, 15, "GROSS TOTAL\nUS$DLLS", 2, hoja1);
			
			Float priceUs = Float.parseFloat(prefs.getString("divisa", "0"));
			double subTotal = 0, gross = 0, venueFee = 0, royaltyFee = 0,promotorFee = 0,  otherFee = 0;
			
			excel.writeCell(18, 13, "RATE EXCHANGE US$1 =", 2, hoja1);
			excel.writeCell(19, 13, ""+priceUs, 11, hoja1);
			excel.writeCell(20, 13, prefs.getString("moneda", ""), 12, hoja1);
			
			for(int i = 0; i < products.size(); i++){
				Product prod = products.get(i);
				
				excel.writeCell(0, (16+i), ""+(truncate(Float.parseFloat((prod.getPrecio()))/priceUs)), 11, hoja1);
				excel.writeCell(1, (16+i), ""+(i+1), 3, hoja1);
				excel.writeCell(2, (16+i), prod.getTipo(), 3, hoja1);
				excel.writeCell(3, (16+i), prod.getNombre(), 3, hoja1);
				if(prod.getTalla().equals(""))
					excel.writeCell(4, (16+i), "N/A", 3, hoja1);
				else
					excel.writeCell(4, (16+i), prod.getTalla(), 3, hoja1);
				
				int cantIn = prod.getTotalCantidad();
				for(int j = 0; j < prod.getAdicionalSize(); j++){
					cantIn -= prod.getAdicional().get(j).getCantidad();
					excel.writeCell( (6+j), (16+i), ""+prod.getAdicional().get(j).getCantidad(), 3, hoja1);
				}
				excel.writeCell( 5, (16+i), ""+cantIn, 3, hoja1);
				
				excel.writeCell(11, (16+i), ""+prod.getTotalCantidad(), 3, hoja1);
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
				
				excel.writeCell(13, (16+i), ""+cmd, 3, hoja1);
				excel.writeCell(14, (16+i), ""+cmv, 3, hoja1);
				excel.writeCell(15, (16+i), ""+cmo, 3, hoja1);
				excel.writeCell(16, (16+i), ""+cmo1, 3, hoja1);
				excel.writeCell(17, (16+i), ""+cmo2, 3, hoja1);
				
				int finalInventory = prod.getTotalCantidad()-(prod.getProdNo()+cmd+cmv+cmo+cmo1+cmo2);
				double total = Double.parseDouble(prod.getPrecio()) * prod.getProdNo();
				double conTax = total;
				totales.add(total);
				gross += total;
				excel.writeCell(18, (16+i), ""+finalInventory, 3, hoja1);
				excel.writeCell(19, (16+i), ""+prod.getProdNo(), 3, hoja1);
				excel.writeCell(20, (16+i), ""+total, 5, hoja1);
				excel.writeCell(22, (16+i), ""+truncate((total/priceUs)), 11, hoja1);
				
				for(Taxes tx : prod.getTaxes()){
					double aux = 1 + (tx.getAmount()* 0.01);
					subTotal += truncate(total / aux);
					conTax += truncate(total / aux);
				}
				for(Comisiones com : prod.getComisiones()){
					double cant = 0, aux = 0;
					if(com.getTipo().equals("After taxes")){
						cant = conTax;
					}else if(com.getTipo().equals("Before Taxes")){
						cant = total;
					}
					if(com.getIva().equals("%")){
						aux = truncate(cant * (com.getCantidad() * 0.01));  
					}else if(com.getIva().equals("$")){
						aux = com.getCantidad() * prod.getProdNo();
					}
					if(com.getName().equals("VENUE")){
						venueFee += aux;
					}else if(com.getName().equals("AGENCY")){
						royaltyFee += aux;
					}else if(com.getName().equals("PROMOTOR")){
						promotorFee += aux;
					}else if(com.getName().equals("OTHER")){
						otherFee += aux;
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
			excel.writeCell(19, 19+products.size(), ""+(gross-subTotal), 5, hoja1);
			excel.writeCell(19, 20+products.size(), ""+subTotal, 5, hoja1);
			
			excel.writeCell(22, 18+products.size(), ""+truncate((gross/priceUs)), 11, hoja1);
			excel.writeCell(22, 19+products.size(), ""+truncate(((gross-subTotal)/priceUs)), 11, hoja1);
			excel.writeCell(22, 20+products.size(), ""+truncate((subTotal/priceUs)), 11, hoja1);
			
			if(tipo == 1){
				excel.writeCell(18, 22+products.size(), "VENUE FEE", 6, hoja1);
				excel.writeCell(19, 22+products.size(), ""+venueFee, 5, hoja1);
				excel.writeCell(22, 22+products.size(), ""+truncate((venueFee/priceUs)), 11, hoja1);
			}else if(tipo == 2){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
				excel.writeCell(19, 22+products.size(), ""+royaltyFee, 5, hoja1);
				excel.writeCell(22, 22+products.size(), ""+truncate((royaltyFee/priceUs)), 11, hoja1);
			}else if(tipo == 0){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
				excel.writeCell(18, 23+products.size(), "VENUE FEE", 6, hoja1);
				excel.writeCell(18, 24+products.size(), "PROMOTOR", 6, hoja1);
				excel.writeCell(18, 25+products.size(), "OTHER", 6, hoja1);
				
				excel.writeCell(19, 22+products.size(), ""+royaltyFee, 5, hoja1);
				excel.writeCell(19, 23+products.size(), ""+venueFee, 5, hoja1);
				excel.writeCell(19, 24+products.size(), ""+promotorFee, 5, hoja1);
				excel.writeCell(19, 25+products.size(), ""+otherFee, 5, hoja1);
				
				excel.writeCell(22, 22+products.size(), ""+truncate((royaltyFee/priceUs)), 11, hoja1);
				excel.writeCell(22, 23+products.size(), ""+truncate((venueFee/priceUs)), 11, hoja1);
				excel.writeCell(22, 24+products.size(), ""+truncate((promotorFee/priceUs)), 11, hoja1);
				excel.writeCell(22, 25+products.size(), ""+truncate((otherFee/priceUs)), 11, hoja1);
				
				hoja1.mergeCells(18, 27+products.size(), 19, 27+products.size());
				excel.writeCell(18, 27+products.size(), "GASTOS OPERATIVOS", 6, hoja1);
				excel.writeCell(20, 27+products.size(), "FACTURA", 2, hoja1);
				excel.writeCell(21, 27+products.size(), "NOTA", 2, hoja1);
				
				double totalGastos = 0;
				for(int i = 0; i< gastos.size(); i++){
					totalGastos += gastos.get(i).getCantidad();
					excel.writeCell(18, (28+products.size()+i), gastos.get(i).getConcepto(), 4, hoja1);
					excel.writeCell(19, (28+products.size()+i), ""+gastos.get(i).getCantidad(), 5, hoja1);
					if(gastos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (28+products.size()+i), "X", 3, hoja1);
					else if(gastos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (28+products.size()+i), "X", 3, hoja1);
				}
				excel.writeCell(18, 28+products.size()+gastos.size(), "SUBTOTAL", 6, hoja1);
				excel.writeCell(19, 28+products.size()+gastos.size(), ""+totalGastos, 5, hoja1);
				
				hoja1.mergeCells(18, 31+products.size()+gastos.size(), 19, 31+products.size()+gastos.size());
				excel.writeCell(18, 31+products.size()+gastos.size(), "SUELDOS/BONOS/COMISIONES", 6, hoja1);
				excel.writeCell(20, 31+products.size()+gastos.size(), "FACTURA", 2, hoja1);
				excel.writeCell(21, 31+products.size()+gastos.size(), "NOTA", 2, hoja1);
				
				excel.writeCell(18, 32+products.size()+gastos.size(), "Comisión Vendedores", 4, hoja1);
				excel.writeCell(19, 32+products.size()+gastos.size(), ""+comisionVendedor, 5, hoja1);
				
				double totalSueldos = comisionVendedor;
				for(int i = 0; i < sueldos.size(); i++){
					totalSueldos += sueldos.get(i).getCantidad();
					excel.writeCell(18, (33+products.size()+gastos.size()+i), sueldos.get(i).getConcepto(), 4, hoja1);
					excel.writeCell(19, (33+products.size()+gastos.size()+i), ""+sueldos.get(i).getCantidad(), 5, hoja1);
					if(sueldos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (33+products.size()+gastos.size()+i), "X", 3, hoja1);
					else if(sueldos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (33+products.size()+gastos.size()+i), "X", 3, hoja1);
				}
				excel.writeCell(18, 33+products.size()+gastos.size()+sueldos.size(), "SUBTOTAL", 6, hoja1);
				excel.writeCell(19, 33+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				excel.writeCell(18, 35+products.size()+gastos.size()+sueldos.size(), "TOTAL GASTOS OPERATIVOS", 6, hoja1);
				excel.writeCell(19, 35+products.size()+gastos.size()+sueldos.size(), ""+totalGastos, 5, hoja1);
				excel.writeCell(18, 36+products.size()+gastos.size()+sueldos.size(), "TOTAL SUELDOS/BONOS/COMISIONES", 6, hoja1);
				excel.writeCell(19, 36+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				double depositar = totalSueldos+totalGastos;
				excel.writeCell(18, 38+products.size()+gastos.size()+sueldos.size(), "TOTAL A DEPOSITAR", 6, hoja1);
				excel.writeCell(19, 38+products.size()+gastos.size()+sueldos.size(), ""+depositar, 5, hoja1);
				
				hoja1.mergeCells(18, 40+products.size()+gastos.size()+sueldos.size(), 19, 40+products.size()+gastos.size()+sueldos.size());
				excel.writeCell(18, 40+products.size()+gastos.size()+sueldos.size(), "INGRESOS RECIBIDOS", 6, hoja1);
				
				excel.writeCell(18, 41+products.size()+gastos.size()+sueldos.size(), "EFECTIVO", 4, hoja1);
				excel.writeCell(18, 42+products.size()+gastos.size()+sueldos.size(), "TC BANAMEX", 4, hoja1);
				excel.writeCell(18, 43+products.size()+gastos.size()+sueldos.size(), "TC BANORTE", 4, hoja1);
				excel.writeCell(18, 44+products.size()+gastos.size()+sueldos.size(), "TC SANTANDER", 4, hoja1);
				excel.writeCell(18, 45+products.size()+gastos.size()+sueldos.size(), "TC AMEX", 4, hoja1);
				excel.writeCell(18, 46+products.size()+gastos.size()+sueldos.size(), prefs.getString("tipo1", "OTHER"), 4, hoja1);
				excel.writeCell(18, 47+products.size()+gastos.size()+sueldos.size(), prefs.getString("tipo2", "OTHER"), 4, hoja1);
				excel.writeCell(18, 48+products.size()+gastos.size()+sueldos.size(), prefs.getString("tipo3", "OTHER"), 4, hoja1);
				
				excel.writeCell(19, 41+products.size()+gastos.size()+sueldos.size(), ""+efectivo, 5, hoja1);
				excel.writeCell(19, 42+products.size()+gastos.size()+sueldos.size(), ""+banamex, 5, hoja1);
				excel.writeCell(19, 43+products.size()+gastos.size()+sueldos.size(), ""+banorte, 5, hoja1);
				excel.writeCell(19, 44+products.size()+gastos.size()+sueldos.size(), ""+santander, 5, hoja1);
				excel.writeCell(19, 45+products.size()+gastos.size()+sueldos.size(), ""+amex, 5, hoja1);
				excel.writeCell(19, 46+products.size()+gastos.size()+sueldos.size(), ""+otro1, 5, hoja1);
				excel.writeCell(19, 47+products.size()+gastos.size()+sueldos.size(), ""+otro2, 5, hoja1);
				excel.writeCell(19, 48+products.size()+gastos.size()+sueldos.size(), ""+otro3, 5, hoja1);
				
				double depositado = efectivo+banamex+banorte+santander+amex+otro1+otro2+otro3;
				excel.writeCell(18, 49+products.size()+gastos.size()+sueldos.size(), "TOTAL DEPOSITADO", 6, hoja1);
				excel.writeCell(19, 49+products.size()+gastos.size()+sueldos.size(), ""+depositado, 5, hoja1);
				
				excel.writeCell(18, 51+products.size()+gastos.size()+sueldos.size(), "DIF +/-", 6, hoja1);
				excel.writeCell(19, 51+products.size()+gastos.size()+sueldos.size(), ""+(depositado-depositar), 5, hoja1);
				
				excel.writeCell(15, 5, "ATTENDANCE", 6, hoja1);
				excel.writeCell(15, 7, "PERCAP", 6, hoja1);
				excel.writeCell(15, 9, "GROSS TOTAL", 6, hoja1);
				
				excel.writeCell(16, 5, ""+capacidad, 3, hoja1);
				excel.writeCell(16, 7, ""+truncate((gross/capacidad)), 5, hoja1);
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
	
	private void getPDFReport(int tipo,String agency, String contact,String artista){
		getProducts(artista);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String[] headers = {"PRICE SALE IN US","#","ITEM","STYLE","SIZE","TOTAL\nINVENTORY","PRICE SALE","DAMAGE","COMPS\nVENUE",
				"COMPS\nOFFICE\nPRODUCTION",prefs.getString("op1", "OTHER"),prefs.getString("op2", "OTHER"),"FINAL\nINVENTORY",
				"SALES PIECES","GROSS TOTAL","% SALES","GROSS TOTAL\nUS$DLLS"};
		Double priceUs = Double.parseDouble(prefs.getString("divisa", "0"));
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		df.applyPattern("$#,##0.00");
		df.setRoundingMode(RoundingMode.DOWN);
		
		Document docPdf = pdf.createPDFHorizontal("sales_report.pdf");
		
		int pag = 1;
		pdf.createHeadings(998, 10, 8, ""+pag);
		pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
		pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
		
		String texto = "SALES REPORT (IN "+prefs.getString("moneda", "")+")";
		pdf.createHeadings(504-((texto.length()/2)*9), 535, 14, texto);
		
		float aux1 = pdf.tableDatos(docPdf, new String[]{"DATE","EVENT","VENUE","AGENCY","CONTACT"}, 
				new String[]{fecha,evento,local,agency,contact}, docPdf.leftMargin(), 515);
		
		float aux2 = pdf.tableDatos(docPdf, new String[]{"ATTENDANCE","PERCAP","GROSS TOTAL","RATE EXCHANGE US$1 ="}, 
				new String[]{""+capacidad,df.format(totalVenta/capacidad),df.format(totalVenta),df.format(priceUs)+" "+prefs.getString("moneda", "")}, 620, 515);
		
		double subTotal = 0, venueFee = 0, royaltyFee = 0;
		for(Product prod : products){
			double total = Double.parseDouble(prod.getPrecio()) * prod.getProdNo();
			double conTax = 0;
			
			for(Taxes tax : prod.getTaxes()){
				double aux = 1 + (tax.getAmount()* 0.01);
				conTax += truncate(total / aux);
				subTotal += truncate(total / aux);
			}
			
			for(Comisiones com : prod.getComisiones()){
				double cant = 0, aux = 0;
				if(com.getTipo().equals("After taxes")){
					cant = conTax;
				}else if(com.getTipo().equals("Before Taxes")){
					cant = total;
				}
				if(com.getIva().equals("%")){
					aux = truncate(cant * (com.getCantidad() * 0.01));  
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
		
		float posInit = 0;
		if(aux1 > aux2)
			posInit = 505 - aux1;
		else 
			posInit = 505 - aux2;
		
		double[] dob = pdf.tableVentas(docPdf, products, headers, totalVenta,0, posInit);
		int mas = (int)dob[0];
		if(mas != 0){
			posInit = 500;
			int posAct = 0;
			do{
				posAct += (int)dob[1];
				docPdf.newPage();
				pag++;
				pdf.createHeadings(998, 10, 8, ""+pag);
				pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
				pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
				dob = pdf.tableVentas(docPdf, products.subList(posAct, products.size()), headers, totalVenta,posAct, 500);
				mas = (int)dob[0];
			}while(mas != 0);
		}
		
		float pos = (float) (posInit - dob[1] - 10);
		int x = 650;
		
		if(pos - 73 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(998, 10, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		pdf.tableNum(docPdf, new String[]{"GROSS TOTAL","TAX","GROSS NET"}, new double[]{totalVenta,(totalVenta-subTotal),subTotal}, x, (pos));
		
		pos -= 58;
		if(pos - 41 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(998, 10, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		
		String line1 = "Gerente";
		String line2 = "";
		
		if(tipo == 1){
			line2 = "Venue";
			pdf.tableNum(docPdf, new String[]{"VENUE FEE"}, new double[]{venueFee}, x, (pos));
		}else if(tipo == 2){
			line2 = "Agency";
			pdf.tableNum(docPdf, new String[]{"ROYALTY FEE"}, new double[]{royaltyFee}, x, (pos));
		}
		
		pdf.addLine(50, 90);
		pdf.createHeadings(150 - ((line1.length()/2)*9), 78, 14, line1);
		pdf.addLine(320, 90);
		pdf.createHeadings(420 - ((line1.length()/2)*9), 78, 14, line2);
		
		docPdf.close();
	}

	private double truncate(double num){
		num *= 100;
		int aux = (int)num;
		double res = (double)aux / 100;
		return res;
	}
}
