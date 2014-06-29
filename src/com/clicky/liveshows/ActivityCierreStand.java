package com.clicky.liveshows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.adapters.AdapterCloseStand;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Product;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityCierreStand extends Activity implements OnItemClickListener,OnCortesiaListener {

	private DBAdapter dbHelper;
	List<Product> products;
	AdapterCloseStand adapter;
	int id;
	TextView txtTotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cierre_stand);
		// Show the Up button in the action bar.
		setupActionBar();
		Bundle b= getIntent().getBundleExtra("extra");
		id=b.getInt("id_stand");

		txtTotal=(TextView)findViewById(R.id.txtTotal);
		products = new ArrayList<Product>();
		ListView list=(ListView)findViewById(R.id.listCierre);
		
		adapter = new AdapterCloseStand(this, R.layout.item_cierra_stand, products);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		dbHelper = new DBAdapter(this);
		dbHelper.open();

		HashMap<Integer, String> artistas  = new HashMap<Integer, String>();
		Cursor cur = dbHelper.fetchAllArtistas();
		if(cur.moveToFirst()){
			do{
				int id =cur.getInt(0);
				String name = cur.getString(1);
				artistas.put(id, name);
				int idEvento = cur.getInt(2);
				Log.i("BD",""+id+" "+name+" "+idEvento);
			}while(cur.moveToNext());
		}
		cur.close();
		
		Cursor c  = dbHelper.fetchStandProduct(id);
		if(c.moveToFirst()){
			do{
				Product p = new Product();  //Se obtiene la cantidad de prod en el stand, nombre,tipo, talla y precio
				int cantidad = c.getInt(1);
				int idProd = c.getInt(3);
				p.setCantidadStand(cantidad);
				p.setId(idProd);
				Cursor cursor = dbHelper.fetchProducto(idProd);
				if(cursor.moveToFirst()){
					do{
						String nombre = cursor.getString(1);
						String tipo = cursor.getString(2);
						String talla = cursor.getString(6);
						int cortesias = cursor.getInt(7);
						String precio = cursor.getString(8);
						int cantidadTotal = cursor.getInt(4);
						p.setNombre(nombre);
						p.setCortesias(cortesias);
						p.setTipo(tipo);
						p.setTalla(talla);
						p.setPrecio(precio);
						p.setCantidad(cantidadTotal);
					}while(cursor.moveToNext());
				}
				products.add(p);
				
				Log.i("STAND_PROD", p.getNombre()+" "+p.getTipo()+" "+p.getTalla()+" "+cantidad);
			}while(c.moveToNext());
			adapter.notifyDataSetChanged();
		}else{
			products.clear();
			adapter.notifyDataSetChanged();
		}
		dbHelper.close();
		
		list.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				double total=0;
				for(Product p : products){
					if(p.getProdNo()>0){
						total = (p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio()));
						txtTotal.setText(""+total);
					}
				}
			}
		});
		
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
		getMenuInflater().inflate(R.menu.activity_cierre_stand, menu);
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
			return true;
		case R.id.action_accept:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onResume(){
		super.onResume();

	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		DialogSetCortesia dialogA = new DialogSetCortesia();
		Bundle params = new Bundle();
		params.putString("nombre", products.get(position).getNombre());
		params.putInt("position", position);
		dialogA.setArguments(params);
		dialogA.show(getFragmentManager(), "diagCor");
		
	}

	@Override
	public void setCortesia(String cortesia, int position) {
		// TODO Auto-generated method stub
		Product p = products.get(position);
		p.setCortesias(Integer.parseInt(cortesia));
		Log.i("COR", "Set cortesia "+p.getNombre()+" "+cortesia);
		dbHelper.open();
		dbHelper.close();
}
}
