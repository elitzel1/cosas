package com.clicky.liveshows;

import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.DialogStand.OnStandNuevo;
import com.clicky.liveshows.DialogUpdateComision.OnChangeComision;
import com.clicky.liveshows.FragmentStandProd.OnNewAdicional;
import com.clicky.liveshows.FragmentStandProd.OnNewCortesia;
import com.clicky.liveshows.FragmentStands.onFragmentCreate;
import com.clicky.liveshows.FragmentStands.onStandSelected;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Stand;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class StandActivity extends Activity implements OnStandNuevo,OnChangeComision,onStandSelected,onFragmentCreate,OnNewCortesia,OnCortesiaListener,OnNewAdicional,com.clicky.liveshows.DialogAddAdcional.OnAdicionalListener{
	FragmentStands frag;
	private DBAdapter dbHelper;
	Product product;
	Stand stand;
	protected final int CIERRE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stand);
		// Show the Up button in the action bar.
		dbHelper = new DBAdapter(this);
		setupActionBar(getIntent().getStringExtra("evento"));
		frag= (FragmentStands)getFragmentManager().findFragmentById(R.id.headlines_fragment);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar(String name) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
		getActionBar().setTitle(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stand, menu);
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
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_new:
			newProduct();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void newProduct(){
		DialogStand dialog = new DialogStand();
		dialog.show(getFragmentManager(), "stands");
	}

	@Override
	public void setStand(String nombre, String encargado, Comisiones com) {
		dbHelper.open();
		long id= dbHelper.createStand(nombre, com.getCantidad(), com.getIva(), com.getTipo(), encargado);
		if(id==-1){
			
		}else{
			newStand(id, nombre, encargado, com);
		}
		dbHelper.close();
	}
	

	public void newStand(long id, String nombre, String encargado, Comisiones com){
		Stand stand = new Stand(id,nombre, encargado, com);
		frag.setStand(stand);
		//onStandSeleccionado(stand);
	}

	@Override
	public void onStandSeleccionado(Stand s) {
		// TODO Auto-generated method stub
		
		boolean hayDetalle = 
				(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

		if(hayDetalle) {
			((FragmentStandProd)getFragmentManager().
				findFragmentById(R.id.article_fragment)).setStand(s);
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == CIERRE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Intent  i = new Intent(this,MainActivity.class);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    		startActivity(i);
	        	finish();
	        }
	    }
	}
	
	public void borrarTodo(View v){
		Intent i = new Intent(StandActivity.this,ActivityCierreDia.class);
		startActivityForResult(i, CIERRE);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}

	@Override
	public void onGetData() {
		// TODO Auto-generated method stub
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllStand();
		if(cursor.moveToFirst()){
			do{
				long id = cursor.getLong(0);
				String nombre =  cursor.getString(1);
				String encargado = cursor.getString(2);
				int comision = cursor.getInt(3);
				String iva = cursor.getString(4);
				String tipo = cursor.getString(5);
				Comisiones com = new Comisiones("vendedor", comision, iva, tipo);
				newStand(id, nombre, encargado, com);
				if(cursor.isFirst()){
					Stand first = new Stand(id,nombre, encargado, com);
					onStandSeleccionado(first);
				}
		}while(cursor.moveToNext());
		}else{
		}
		cursor.close();
		dbHelper.close();
	}

	@Override
	public void onSetAdicional(Product p,int position, Stand s) {
		// TODO Auto-generated method stub
		DialogAddAdcional dialogA = new DialogAddAdcional();
		this.product=p;
		this.stand=s;
		Bundle params = new Bundle();
		params.putString("nombre",p.getNombre());
		params.putInt("position", position);
		dialogA.setArguments(params);
		dialogA.show(getFragmentManager(), "diagAd");
		
		
	}
	
	@Override
	public void onSetCortesia(Product p,int position, Stand s){
		DialogSetCortesia dialogA = new DialogSetCortesia();
		this.product=p;
		this.stand=s;
		Bundle params = new Bundle();
		params.putString("nombre",p.getNombre());
		params.putInt("position", position);
		dialogA.setArguments(params);
		dialogA.show(getFragmentManager(), "diagCor");
	}
	
	
	//CORREGIR CANTIDAD
	@Override
	public void setAdicional(String adicional, int position) {
		// TODO Auto-generated method stub

		Log.i("ERROR","En set adicional "+adicional+" "+position+" "+product.getCantidad());
		
		Product p = product;
			dbHelper.open();
			if((p.getCantidad()-Integer.parseInt(adicional))>0){
				if(dbHelper.updateProducto(p.getId(), p.getCantidad()-Integer.parseInt(adicional))){
					if(dbHelper.updateStandProducto(p.getId(), stand.getId(), p.getCantidadStand()+Integer.parseInt(adicional))){
							((FragmentStandProd)getFragmentManager().
								findFragmentById(R.id.article_fragment)).setNewCantidad(p.getCantidadStand()+Integer.parseInt(adicional), position);
							Log.i("ADICIONALES", p.getId()+" "+p.getArtista()+" "+p.getNombre());	
					}else{
						Log.i("ERROR","StandProduct"+ p.getId()+" "+p.getArtista()+" "+p.getNombre());
					}
				}else{
					Log.i("ERROR","UpdateProduct"+ p.getId()+" "+p.getArtista()+" "+p.getNombre());
				}
			}else{
				Log.i("ERROR","operacion"+ p.getId()+" "+p.getArtista()+" "+p.getNombre());
			}
			dbHelper.close();


	}
	


	private void makeToast(int resource){
		Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setCortesia(Cortesias cortesia, int position) {
		// TODO Auto-generated method stub
		Product p = product;
		p.addCortesia(cortesia);
		Log.i("COR", "Set cortesia "+p.getNombre()+" "+cortesia);
		dbHelper.open();
		int total=p.getCantidadStand()-p.getCortesias().get(p.sizeCortesias()-1).getAmount();
		if((total)>0){

			if(dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getId())>=0){
				int cantidad = total;
				p.setCantidadStand(cantidad);
				dbHelper.updateProducto(p.getId(), cantidad);
				dbHelper.updateStandProducto(p.getId(),stand.getId(), cantidad);
			}
		}else{

		}
		dbHelper.close();
	}

	@Override
	public void setNewComision(Comisiones com) {
		// TODO Auto-generated method stub
		dbHelper.open();
		
		if(dbHelper.updateComision(com.getId(), com.getCantidad(), com.getIva(), com.getTipo())){
			makeToast(R.string.update_exitoso);
		}else{
			makeToast(R.string.update_noexitoso);
		}
		
		dbHelper.close();
		
	}



}
