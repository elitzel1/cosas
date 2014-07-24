package com.clicky.liveshows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.clicky.liveshows.DatePickerFragment.DatePickerFragmentListener;
import com.clicky.liveshows.adapters.AdapterArtista;
import com.clicky.liveshows.adapters.AdapterSpinnerMoneda;
import com.clicky.liveshows.adapters.AdpterSpinner;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Evento;
import com.clicky.liveshows.utils.Local;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements DatePickerFragmentListener {

	private Spinner spinnerLocal;
	AdapterArtista adapterArtista;
	AdapterArtista adapterFecha;
	ArrayList<String> artistas;
	ArrayList<String> fechas;
	EditText editCapacidad;
	EditText editLugar;
	boolean enable = true;

	boolean visibleArtista=false;
	boolean visibleDate=false;
	private int countA;
	private int countD;
	LinearLayout.LayoutParams params;
	private LinearLayout mLinear;
	private LinearLayout mLinearDate;

	private String[] moneda = {
			"ARGENTINE PESOS","BOLIVARS","BOLIVIAN PESOS","CHILEAN PESOS","COLOMBIAN PESOS","COLONES","CORDOBAS","DOLLARS",
			"DOMINICAN PESOS","GUARANIES","LEMPIRAS","MEXICAN PESOS","QUETZALES","REALES","SOLES","URUGUAYAN PESOS"};
	private String[] divisa = {"8.14","6.91","6.91","548.89","1844","543.8","26.015","1","43.6","4326.18","20.99",
			"12.96","7.766","2.21","2.79","22.98"};
	private DBAdapter dbHelper;
	private Spinner spinnerTipoMoneda;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spinnerLocal = (Spinner) findViewById(R.id.spinnerLocacion);
		spinnerTipoMoneda = (Spinner)findViewById(R.id.spinnerMoneda);
		editCapacidad = (EditText)findViewById(R.id.editCapacidad);
		setupActionBar();
		setXML();

		dbHelper = new DBAdapter(this);

		countA=0;
		countD=0;
		artistas = new ArrayList<String>();
		mLinear = (LinearLayout)findViewById(R.id.linearLayoutArtista);
		//add LayoutParams
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mLinear.setOrientation(LinearLayout.VERTICAL);


		fechas = new ArrayList<String>();
		mLinearDate = (LinearLayout)findViewById(R.id.linearLayoutDate);
		EditText editFecha = (EditText)findViewById(R.id.editFecha);
		editFecha.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					DatePickerFragment fragment  = new DatePickerFragment();
					fragment.show(getFragmentManager(), "datePicker");
				}
				return false;
			}
		});

		spinnerTipoMoneda.setAdapter(new AdapterSpinnerMoneda(this, R.layout.item_spinner, moneda));
	}

	private void setXML(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = getApplicationContext().getAssets().open("locales.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);

			parseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void agregarArtista(View v){
		EditText editArtista = (EditText)findViewById(R.id.editArtista);
		if(editArtista.getEditableText()!=null){
			if(!editArtista.getEditableText().toString().contentEquals("")){
				artistas.add(editArtista.getEditableText().toString());
				countA++;
				addView(editArtista.getEditableText().toString(),countA,mLinear);
				makeToast(R.string.m_art_agregado);
				if(visibleArtista==false){
					findViewById(R.id.btnEliminarArtista).setVisibility(View.VISIBLE);
					visibleArtista=true;
				}
				editArtista.setText("");
			}
			else{
				makeToast(R.string.sin_artista);
			}
		}


	}

	private void addView(String text, int count, LinearLayout layout){
		LinearLayout linear = new LinearLayout(this);
		linear.setId(count);
		LinearLayout.LayoutParams params2 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView txt = new TextView(this);
		txt.setText(text);
		txt.setTextColor(getResources().getColor(R.color.azul));
		txt.setLayoutParams(params2);
		
		linear.addView(txt);
		layout.addView(linear,0);
		layout.invalidate();
	}

	public void eliminarArtista(View view){
		switch (view.getId()) {
		case R.id.btnEliminarArtista:
			if(countA>0){
				final LinearLayout temp = (LinearLayout) mLinear.findViewById(countA);
				temp.removeAllViews();
				mLinear.removeView(temp);
				artistas.remove(0);
				countA--;
			}else{
				findViewById(R.id.btnEliminarArtista).setVisibility(View.INVISIBLE);
				visibleArtista=false;
			}
			break;
		case R.id.btnEliminarFecha:
			if(countD>0){
				final LinearLayout temp = (LinearLayout) mLinearDate.findViewById(countD);
				temp.removeAllViews();
				mLinearDate.removeView(temp);
				fechas.remove(0);
				countD--;}
			else{
				findViewById(R.id.btnEliminarFecha).setVisibility(View.INVISIBLE);
				visibleDate=false;
			}
			break;
		default:
			break;
		}

	}
	private void makeToast(int id){
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	public void otroLugar(View v){

		if(enable==false){
			editLugar = (EditText)findViewById(R.id.editLocal);
			spinnerLocal.setVisibility(View.VISIBLE);
			findViewById(R.id.editLocal).setVisibility(View.GONE);
			enable=true;
		}else{
			spinnerLocal.setVisibility(View.GONE);
			editCapacidad.setText("");
			editLugar = (EditText)findViewById(R.id.editLocal);
			editLugar.setVisibility(View.VISIBLE);
			enable=false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayList<Local> local = null;
		int eventType = parser.getEventType();
		Local currentLocal = null;


		while (eventType != XmlPullParser.END_DOCUMENT){
			String name = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				local = new ArrayList<Local>();
				currentLocal = new Local();
				currentLocal.setNombre(getResources().getString(R.string.hint_local));
				currentLocal.setCapacidad(0);
				local.add(currentLocal);
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.contentEquals("item")){
					currentLocal = new Local();
				} else if (currentLocal != null){
					if (name.contentEquals("title")){
						currentLocal.setNombre(parser.nextText());
					} else if (name.contentEquals("capacidad")){
						currentLocal.setCapacidad(Integer.parseInt(parser.nextText()));
					}  
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && currentLocal != null){
					local.add(currentLocal);
				} 
			}
			eventType = parser.next();

		}
		printProducts(local);
	}

	private void printProducts(final ArrayList<Local> locals)
	{
		spinnerLocal.setAdapter(new AdpterSpinner(this,R.layout.item_spinner, locals));
		spinnerLocal.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int positon, long arg3) {
				editCapacidad.setText(""+locals.get(positon).getCapacidad());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void setupActionBar(){
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
	}


	@Override
	public void onFinishDatePickerDialog(int year, int month, int day) {
		fechas.add(day+"/"+month+"/"+year);
		countD++;
		addView(""+day+"/"+month+"/"+year, countD, mLinearDate);
		makeToast(R.string.m_date_agregado);
		if(visibleDate==false){
			findViewById(R.id.btnEliminarFecha).setVisibility(View.VISIBLE);
			visibleDate=true;
		}
	}

	public void guardarDatos(View v){
		boolean bandera[] = new boolean[5];

		for(int i=0;i<bandera.length;i++){
			bandera[i]=false;	
		}

		Evento evento = new Evento();

		evento.setNombre("LiveShows");

		if(artistas.size()<=0){
			makeToast(R.string.sin_artista);
			return;
		}else 
			evento.setArtista(artistas);


		if(enable==true){
			Local o =(Local)spinnerLocal.getSelectedItem();
			evento.setLocal(o.getNombre());
			if(Integer.parseInt(editCapacidad.getEditableText().toString())>0){
			evento.setCapacidad(editCapacidad.getEditableText().toString());
			}else{
				makeToast(R.string.sin_capacidad);
				return;
			}
		}else{
			if(editLugar.getEditableText()!=null){
				if(!editLugar.getEditableText().toString().contentEquals("")){
					evento.setLocal(editLugar.getEditableText().toString());
					if(editCapacidad.getEditableText()!=null){
						if(!editCapacidad.getEditableText().toString().contentEquals("")){
							if(Integer.parseInt(editCapacidad.getEditableText().toString())>0){
							evento.setCapacidad(editCapacidad.getEditableText().toString());
							}else{
								makeToast(R.string.sin_capacidad);
								return;
							}
						}else{
							makeToast(R.string.sin_capacidad);
							return;
						}
					}else{
						makeToast(R.string.sin_capacidad);
						return;
					}
				}else{
					makeToast(R.string.sin_lugar);
					return;
				}
			}else{ makeToast(R.string.sin_lugar);
			return;
			}
		}

		if(fechas.size()<=0){
			makeToast(R.string.sin_fecha);
			return;
		}
		else{
			evento.setFecha(fechas);
		}
		
		String moneda = (String)spinnerTipoMoneda.getSelectedItem();
		if(!moneda.contentEquals("Elija el tipo de moneda")){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("moneda", moneda);
			editor.putString("divisa", divisa[spinnerTipoMoneda.getSelectedItemPosition()]);
			editor.commit();
		}

		guardarBD(evento);
	}

	private void guardarBD(Evento evento){
		dbHelper.open();
		dbHelper.createEvento(evento.getNombre(), evento.getLocal(), evento.getCapacidad());
		for(int i = 0 ;i<artistas.size();i++){
			dbHelper.createArtista(artistas.get(i));
		}
		
		for(int i = 0;i<fechas.size();i++){
			dbHelper.createFecha(fechas.get(i));
		}
		dbHelper.close();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("evento", 1);
		editor.commit();
		toActivityProducto();
	}
	
	public void toActivityProducto(){
		Intent i = new Intent(this, ActivityProductos.class);
		startActivity(i);
		finish();
	}
}
