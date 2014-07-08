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
	private ArrayList<Gastos> sueldos;
	private HashMap<Integer, String> artistas;
	private HashMap<Integer, Product> mapProd;
	List<Product> products;
	private int[]art;
	
	int idEvento;
	int posVenue=0;
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
		mapProd  = new HashMap<Integer, Product>();
		gastos = new ArrayList<Gastos>();
		sueldos = new ArrayList<Gastos>();
		
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
				p.setProdNo(0);
				addProduct(p, a);
				Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
			}while(cursorProd.moveToNext());
			cursorProd.close();
			
		}
		
		setArtistas();
		
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
			getReport(0, "","");
			if(acepta){
					
			}
		}else if(v == (Button)findViewById(R.id.enviarVenue)){
			mail = ((EditText)findViewById(R.id.mailVenue)).getText().toString();
			acepta = validaCorreo(mail);
			getReport(1, agencias.get(posVenue).getNombre(), agencias.get(posVenue).getContacto());
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
		//mapProd.put(item.getId(), item);
	}
	
	/**
	 * 
	 * @param tipo 0 - LSG
	 * 			   1 - Venue
	 * 			   2 - Agency
	 * @param agency
	 * @param contact
	 */
	private void getReport(int tipo,String agency, String contact){
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
			if(tipo != 0){
				excel.writeCell(1, 11, "AGENCY", 1, hoja1);
				excel.writeCell(2, 11, agency, 0, hoja1);
				excel.writeCell(1, 13, "CONTACT", 1, hoja1);
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
			
			excel.writeCell(18, 15, "FINAL INVENTORY", 2, hoja1);
			excel.writeCell(19, 15, "SALES PIECES", 2, hoja1);
			excel.writeCell(20, 15, "GROSS TOTAL", 2, hoja1);
			excel.writeCell(21, 15, "% SALES", 2, hoja1);
			excel.writeCell(22, 15, "GROSS TOTAL\nUS$DLLS", 2, hoja1);
			
			for(int i = 0; i < products.size(); i++){
				Product prod = products.get(i);
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
				
				if(mapProd.get(prod.getId()) != null){
					excel.writeCell( 19, (16+i), ""+mapProd.get(prod.getId()).getProdNo(), 4, hoja1);
					prod.setProdNo(mapProd.get(prod.getId()).getProdNo());
				}
				
				excel.writeCell(20, (16+i), ""+(Double.parseDouble(prod.getPrecio()) * prod.getProdNo()), 5, hoja1);
				
			}
			
			excel.writeCell(18, 18+products.size(), "GROSS TOTAL", 6, hoja1);
			excel.writeCell(18, 19+products.size(), "TAX", 6, hoja1);
			excel.writeCell(18, 20+products.size(), "GROSS NET", 6, hoja1);
			
			if(tipo == 1){
				excel.writeCell(18, 22+products.size(), "VENUE FEE", 6, hoja1);
			}else if(tipo == 2){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
			}else if(tipo == 0){
				excel.writeCell(18, 22+products.size(), "ROYALTY FEE", 6, hoja1);
				excel.writeCell(18, 23+products.size(), "VENUE FEE", 6, hoja1);
				
				excel.writeCell(18, 24+products.size(), "GASTOS OPERATIVOS", 7, hoja1);
				excel.writeCell(20, 24+products.size(), "FACTURA", 7, hoja1);
				excel.writeCell(21, 24+products.size(), "NOTA", 7, hoja1);
				
				double totalGastos = 0;
				for(int i = 0; i< gastos.size(); i++){
					totalGastos += gastos.get(i).getCantidad();
					excel.writeCell(18, (25+products.size()+i), gastos.get(i).getConcepto(), 3, hoja1);
					excel.writeCell(19, (25+products.size()+i), ""+gastos.get(i).getCantidad(), 5, hoja1);
					if(gastos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (25+products.size()+i), "X", 8, hoja1);
					else if(gastos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (25+products.size()+i), "X", 8, hoja1);
				}
				excel.writeCell(18, 26+products.size()+gastos.size(), "SUBTOTAL", 7, hoja1);
				excel.writeCell(19, 26+products.size()+gastos.size(), ""+totalGastos, 5, hoja1);
				
				excel.writeCell(18, 28+products.size()+gastos.size(), "SUELDOS/BONOS/COMISIONES", 7, hoja1);
				excel.writeCell(20, 28+products.size()+gastos.size(), "FACTURA", 7, hoja1);
				excel.writeCell(21, 28+products.size()+gastos.size(), "NOTA", 7, hoja1);
				
				double totalSueldos = 0;
				for(int i = 0; i < sueldos.size(); i++){
					totalSueldos += sueldos.get(i).getCantidad();
					excel.writeCell(18, (29+products.size()+gastos.size()+i), sueldos.get(i).getConcepto(), 3, hoja1);
					excel.writeCell(19, (29+products.size()+gastos.size()+i), ""+sueldos.get(i).getCantidad(), 5, hoja1);
					if(sueldos.get(i).getComprobante().equals("Factura"))
						excel.writeCell(20, (29+products.size()+gastos.size()+i), "X", 8, hoja1);
					else if(sueldos.get(i).getComprobante().equals("Nota"))
						excel.writeCell(21, (29+products.size()+gastos.size()+i), "X", 8, hoja1);
				}
				excel.writeCell(18, 30+products.size()+gastos.size()+sueldos.size(), "SUBTOTAL", 7, hoja1);
				excel.writeCell(19, 30+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				excel.writeCell(18, 32+products.size()+gastos.size()+sueldos.size(), "TOTAL GASTOS OPERATIVOS", 0, hoja1);
				excel.writeCell(20, 32+products.size()+gastos.size()+sueldos.size(), ""+totalGastos, 5, hoja1);
				excel.writeCell(18, 33+products.size()+gastos.size()+sueldos.size(), "TOTAL SUELDOS/BONOS/COMISIONES", 0, hoja1);
				excel.writeCell(20, 33+products.size()+gastos.size()+sueldos.size(), ""+totalSueldos, 5, hoja1);
				
				excel.writeCell(18, 35+products.size()+gastos.size()+sueldos.size(), "TOTAL A DEPOSITAR", 0, hoja1);
				excel.writeCell(20, 35+products.size()+gastos.size()+sueldos.size(), ""+(totalSueldos+totalGastos), 5, hoja1);
				
				excel.writeCell(18, 37+products.size()+gastos.size()+sueldos.size(), "INGRESOS RECIBIDOS", 0, hoja1);
				
				excel.writeCell(18, 38+products.size()+gastos.size()+sueldos.size(), "EFECTIVO", 3, hoja1);
				excel.writeCell(18, 39+products.size()+gastos.size()+sueldos.size(), "TC BANAMEX", 3, hoja1);
				excel.writeCell(18, 40+products.size()+gastos.size()+sueldos.size(), "TC BANORTE", 3, hoja1);
				excel.writeCell(18, 41+products.size()+gastos.size()+sueldos.size(), "TC SANTANDER", 3, hoja1);
				excel.writeCell(18, 42+products.size()+gastos.size()+sueldos.size(), "TC AMEX", 3, hoja1);
				excel.writeCell(18, 43+products.size()+gastos.size()+sueldos.size(), "OTROS", 3, hoja1);
				
				excel.writeCell(18, 44+products.size()+gastos.size()+sueldos.size(), "TOTAL DEPOSITADO", 0, hoja1);
				
				excel.writeCell(18, 46+products.size()+gastos.size()+sueldos.size(), "DIF +/-", 0, hoja1);
				excel.writeCell(20, 46+products.size()+gastos.size()+sueldos.size(), ""+(0-(totalSueldos+totalGastos)), 5, hoja1);
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
