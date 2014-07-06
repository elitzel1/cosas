package com.clicky.liveshows;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.clicky.liveshows.adapters.AdapterSpinnerAgencias;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.Agencia;
import com.clicky.liveshows.utils.Excel;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Gastos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class ActivityCierreDia extends Activity{
	
	private DBAdapter dbHelper;
	private Excel excel;
	
	private ArrayList<Agencia> agencias;
	private ArrayList<Gastos> gastos;
	private HashMap<Integer, String> artistas;
	List<Product> products;
	private int[]art;
	
	int idEvento;
	String evento,fecha,local;
	
	private LinearLayout layoutViaticos,layoutReportes;
	private EditText editCantidad;
	private Spinner spinnerGasto,spinVenue;
	private RadioGroup radioComprobante;

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
		gastos = new ArrayList<Gastos>();
		
		layoutViaticos = (LinearLayout)findViewById(R.id.listVisticos);
		layoutReportes = (LinearLayout)findViewById(R.id.layoutReportes);
		spinnerGasto = (Spinner)findViewById(R.id.spinnerGasto);
		editCantidad = (EditText)findViewById(R.id.editViatico);
		radioComprobante = (RadioGroup)findViewById(R.id.comprobante);
		
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
			}while(cEvento.moveToNext());
		}else{
		}
		cEvento.close();
		
		Cursor cFecha = dbHelper.fetchAllFechas();
		if(cFecha.moveToFirst()){
			DateFormat df= DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
			fecha = df.format(toDateDate(cFecha.getString(1)));
		}
		cFecha.close();

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
				int cortesias = cursorProd.getInt(7);
				String precio = cursorProd.getString(8);
				int idEvento = cursorProd.getInt(9);
				int idArtista = cursorProd.getInt(10);

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
				Product p = new Product(nombre, tipo, artistas.get(idArtista), precio, talla, cantidad, null, foto);
				p.setTotalCantidad(cantidadTotal);
				p.setCortesias(cortesias);
				p.setId(id);
				addProduct(p, a);
				Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
			}while(cursorProd.moveToNext());
			cursorProd.close();
			
		}
		
		setArtistas();
		
		spinVenue.setAdapter(new AdapterSpinnerAgencias(this,R.layout.item_spinner_drop, agencias));
		spinVenue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if(pos != 0){
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
					addView(concepto,cantidad,comprobante);
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
	
	public void enviarMail(View v){
		String mail = "";
		boolean acepta = false;
		if(v == (Button)findViewById(R.id.enviarInterno)){
			mail = ((EditText)findViewById(R.id.mailInterno)).getText().toString();
			acepta = validaCorreo(mail);
			if(acepta){
					
			}
		}else if(v == (Button)findViewById(R.id.enviarVenue)){
			mail = ((EditText)findViewById(R.id.mailVenue)).getText().toString();
			acepta = validaCorreo(mail);
			venueReport();
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
	
	private void addView(String concepto, String cantidad,String comprobante){
		LinearLayout linear = new LinearLayout(this);
		linear.setId(gastos.size());
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

		layoutViaticos.addView(linear);
		layoutViaticos.invalidate();
	}
	
	private void addReporte(String artista){
		LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView txt = new TextView(this);
		txt.setText(artista);
		txt.setTextColor(getResources().getColor(R.color.azul_textos));
		txt.setLayoutParams(params);
		layoutReportes.addView(txt);
		
		LinearLayout linear = new LinearLayout(this);
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,0.5f);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		Spinner spin = new Spinner(this);
		spin.setAdapter(new AdapterSpinnerAgencias(this,R.layout.item_spinner_drop, agencias));
		spin.setLayoutParams(params2);
		linear.addView(spin);
		
		final EditText contacto = new EditText(this);
		contacto.setTextColor(getResources().getColor(R.color.azul));
		contacto.setLayoutParams(params2);
		contacto.setHint(R.string.hint_contacto);
		linear.addView(contacto);
		
		final EditText mail = new EditText(this);
		mail.setTextColor(getResources().getColor(R.color.azul));
		mail.setLayoutParams(params2);
		mail.setHint(R.string.hint_mail);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alert_cierre);
		builder.setPositiveButton("Acept", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				borraTodo();
				
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
		});
		builder.create().show();
		
	}
	private void borraTodo(){
		dbHelper.open();
		dbHelper.deleteTodo();
		dbHelper.close();
		SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
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
	}
	
	private void venueReport(){
		WritableWorkbook wb = excel.createWorkbook("venue.xls");
		WritableSheet hoja1 = excel.createSheet(wb, "Venue", 0);
		try {
			
			//Formato del Reporte
			//excel.addImage(hoja1, R.drawable.ic_launcher);
			excel.writeCell(1, 5, "DATE", 1, hoja1);
			excel.writeCell(2, 5, fecha, 0, hoja1);
			excel.writeCell(1, 7, "EVENT", 1, hoja1);
			excel.writeCell(2, 7, evento, 0, hoja1);
			excel.writeCell(1, 9, "VENUE/\nPLACE", 1, hoja1);
			excel.writeCell(2, 9, local, 0, hoja1);
			excel.writeCell(1, 11, "AGENCY", 1, hoja1);
			excel.writeCell(1, 13, "CONTACT", 1, hoja1);
			excel.writeCell(0, 25, "PRICE SALES IN US", 2, hoja1);
			excel.writeCell(1, 25, "#", 2, hoja1);
			excel.writeCell(2, 25, "ITEM", 2, hoja1);
			excel.writeCell(3, 25, "STYLE", 2, hoja1);
			excel.writeCell(4, 25, "SIZE", 2, hoja1);
			excel.writeCell(5, 25, "INITIAL\nINVENTORY", 2, hoja1);
			excel.writeCell(6, 25, "ADDING 1", 2, hoja1);
			excel.writeCell(7, 25, "ADDING 2", 2, hoja1);
			excel.writeCell(8, 25, "ADDING 3", 2, hoja1);
			excel.writeCell(9, 25, "ADDING 4", 2, hoja1);
			excel.writeCell(10, 25, "ADDING 5", 2, hoja1);
			excel.writeCell(11, 25, "TOTAL\nINVENTORY", 2, hoja1);
			excel.writeCell(12, 25, "PRICE SALE", 2, hoja1);
			
			for(int i = 0; i < products.size(); i++){
				Product prod = products.get(i);
				excel.writeCell(1, (26+i), ""+(i+1), 4, hoja1);
				excel.writeCell(2, (26+i), prod.getTipo(), 3, hoja1);
				excel.writeCell(3, (26+i), prod.getNombre(), 3, hoja1);
				if(prod.getTalla().equals(""))
					excel.writeCell(4, (26+i), "N/A", 3, hoja1);
				else
					excel.writeCell(4, (26+i), prod.getTalla(), 3, hoja1);
				excel.writeCell(5, (26+i), ""+prod.getCantidad(), 4, hoja1);
				
				excel.writeCell(11, (26+i), ""+prod.getTotalCantidad(), 4, hoja1);
				excel.writeCell(12, (26+i), prod.getPrecio(), 5, hoja1);
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
