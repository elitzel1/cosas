package com.clicky.liveshows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.clicky.liveshows.adapters.AdapterListaAgregaProductos;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityAgregarProductos extends Activity {
	int id,idFecha;
	Comisiones comision;
	private DBAdapter dbHelper;
	List<Product> products;
	AdapterListaAgregaProductos adapter;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agregar_productos);
		// Show the Up button in the action bar.
		setupActionBar();
		Bundle b= getIntent().getBundleExtra("stand");
		TextView txt=(TextView)findViewById(R.id.txtStandD);
		txt.setText(b.getString("nombre"));
		id=b.getInt("id");
		idFecha = b.getInt("fecha");
		products = new ArrayList<Product>();
		ListView list = (ListView)findViewById(R.id.listAgregaP);
		adapter = new AdapterListaAgregaProductos(this, R.layout.item_agrega_prod_stand, products);
		list.setAdapter(adapter);

		ArrayList<Integer> idProductos = b.getIntegerArrayList("idsProductos");
		dbHelper = new DBAdapter(this);
		dbHelper.open();

		Cursor cursorStand = dbHelper.fetchStand(id);
		if(cursorStand.moveToFirst()){
			comision = new Comisiones();
			comision.setName("Stand"+cursorStand.getString(1));
			comision.setCantidad(cursorStand.getInt(3));
			comision.setIva(cursorStand.getString(4));
			comision.setTipo(cursorStand.getString(5));
		}
		HashMap<Integer, String> artistas  = new HashMap<Integer, String>();
		Cursor c = dbHelper.fetchAllArtistas();
		if(c.moveToFirst()){
			do{
				int id = c.getInt(0);
				String name = c.getString(1);
				artistas.put(id, name);
				int idEvento = c.getInt(2);
				Log.i("BD",""+id+" "+name+" "+idEvento);
			}while(c.moveToNext());
		}
		c.close();

		Cursor cursorProd = dbHelper.fetchAllProductos();
		if(cursorProd.moveToFirst()){
			do{
				int id = cursorProd.getInt(0);
				String nombre = cursorProd.getString(1);
				String tipo = cursorProd.getString(2);
				String foto = cursorProd.getString(3);
				int cantidad = cursorProd.getInt(4);
				int cantidadTotal = cursorProd.getInt(5);
				String talla = cursorProd.getString(6);
				int idArtista = cursorProd.getInt(9);

				boolean band = false;
				for(int i = 0; i<idProductos.size();i++){
					if(id==idProductos.get(i)){
						band=true;
					}
				}
				if(band==false)
					addProduct(nombre, tipo, foto, cantidad, cantidadTotal, talla, artistas.get(idArtista),id);
				Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+idArtista);
			}while(cursorProd.moveToNext());
			cursorProd.close();
		}
		dbHelper.close();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_agregar_productos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//	NavUtils.navigateUpFromSameTask(this);
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_accept:

			dbHelper.open();
			for(int i = 0;i<adapter.getCount();i++){
				int c = adapter.getItem(i).getCantidad()-adapter.getItem(i).getCantidadStand();
				if(0<=c && adapter.getItem(i).getCantidadStand() > 0){
					if(dbHelper.updateProducto(adapter.getItem(i).getId(), c)){
						long comId = dbHelper.createImpuesto(comision.getName(), "comision_stand", comision.getCantidad(), comision.getIva(), comision.getTipo());
						if(comId != -1)
							dbHelper.createStandProducto(id, adapter.getItem(i).getId(), idFecha, adapter.getItem(i).getCantidadStand(),(int)comId);	
						Log.i("ACEPTAR", "ID: "+id+" Cantidad Stand: "+adapter.getItem(i).getCantidadStand()+" Cantidad: "+adapter.getItem(i).getCantidad());
					}else{
					}
				}
			}
			dbHelper.close();
			setResult(RESULT_OK);
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void addProduct(String nombre, String tipo, String img, int cantidad, int cantidadTotal, String talla, String artista,int id){

		Product item = new Product();
		item.setNombre(nombre);
		item.setTipo(tipo);
		item.setId(id);
		if(img.contentEquals("")){
			if(tipo.contentEquals("taza")){
				item.setId_imagen(R.drawable.mug);
			}
			if(tipo.contentEquals("camisa")){
				item.setId_imagen(R.drawable.teeshirt);
			}
			if(tipo.contentEquals("sudadera")){

				item.setId_imagen(R.drawable.teeshirtlong);
			}
			if(tipo.contentEquals("disco")){

				item.setId_imagen(R.drawable.cd);
			}
			if(tipo.contentEquals("pluma")){

				item.setId_imagen(R.drawable.werelupe);
			}
		}else{
			item.setPath_imagen(img);
			item.setId_imagen(0);
		}

		item.setCantidad(cantidad);
		item.setTalla(talla);
		item.setArtista(artista);
		products.add(item);
		adapter.notifyDataSetChanged();
	}


}
