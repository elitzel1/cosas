package com.clicky.liveshows;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.DialogStand.OnStandNuevo;
import com.clicky.liveshows.DialogUpdateComision.OnChangeComision;
import com.clicky.liveshows.DialogUpdateStand.OnStandUpdate;
import com.clicky.liveshows.FragmentStandProd.OnNewAdicional;
import com.clicky.liveshows.FragmentStandProd.OnNewCortesia;
import com.clicky.liveshows.FragmentStands.onFragmentCreate;
import com.clicky.liveshows.FragmentStands.onStandSelected;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.PDF;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Stand;
import com.clicky.liveshows.utils.Taxes;
import com.itextpdf.text.Document;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
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

@SuppressLint("UseSparseArrays")
public class StandActivity extends Activity implements OnStandNuevo,OnChangeComision,onStandSelected,onFragmentCreate,OnNewCortesia,OnCortesiaListener,OnNewAdicional,com.clicky.liveshows.DialogAddAdcional.OnAdicionalListener,OnStandUpdate{
	FragmentStands frag;
	private DBAdapter dbHelper;
	Product product;
	Stand stand;
	int idFecha;
	protected final int CIERRE = 0;
	protected final int STAND = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stand);
		// Show the Up button in the action bar.
		dbHelper = new DBAdapter(this);
		setupActionBar(getIntent().getStringExtra("evento"));
		idFecha = getIntent().getIntExtra("fecha",-1);
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
			case R.id.action_settings:
				openSettings();
				return true;
			case R.id.action_print:
				printProducts();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){
		NavUtils.navigateUpFromSameTask(this);
		overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
	}
	
	private void openSettings(){
		Intent i = new Intent(this,Settings.class);
		startActivity(i);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}
	
	private void newProduct(){
		DialogStand dialog = new DialogStand();
		dialog.show(getFragmentManager(), "stands");
	}
	private void printProducts(){
		if(stand != null){
			dbHelper.open();
			HashMap<Integer, String> artistas  = new HashMap<Integer, String>();
			Cursor cArtistas = dbHelper.fetchAllArtistas();
			if(cArtistas.moveToFirst()){
				do{
					int id = cArtistas.getInt(0);
					String name = cArtistas.getString(1);
					artistas.put(id, name);
				}while(cArtistas.moveToNext());
			}
			cArtistas.close();
			Cursor c  = dbHelper.fetchStandProduct(stand.getId(),idFecha);
			if(c.moveToFirst()){
				PDF pdf = new PDF(this);
				Document docPdf = pdf.createPDF("stand.pdf");
				List<Product> prodList = new ArrayList<Product>(); 
				List<Comisiones> comisiones = new ArrayList<Comisiones>();
				do{
					Product p = new Product();  //Se obtiene la cantidad de prod en el stand, nombre,tipo, talla y precio
					int cantidad = c.getInt(1);
					int idProd = c.getInt(3);
					int comVendedorId = c.getInt(4);
					p.setCantidadStand(cantidad);
					p.setId(idProd);
					Cursor cursorImp = dbHelper.fetchImpuestos(comVendedorId);
					if(cursorImp.moveToFirst()){
						int idTaxes = cursorImp.getInt(0);
						String nombreI = cursorImp.getString(1);
						String porcentaje = cursorImp.getString(2);
						String iva = cursorImp.getString(4);
						String tipoPeso = cursorImp.getString(5);
						Comisiones comi = new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso);
						comi.setId(idTaxes);
						comisiones.add(comi);
					}
					Cursor cursor = dbHelper.fetchProducto(idProd);
					if(cursor.moveToFirst()){
						String nombre = cursor.getString(1);
						int idArtista = cursor.getInt(9);
						String artista = artistas.get(idArtista);
						String tipo = cursor.getString(2);
						String foto = cursor.getString(3);
						String talla = cursor.getString(6);
						String precio = cursor.getString(7);
						int cantidadTotal = cursor.getInt(4);
							
						List<Taxes> list_tax = new ArrayList<Taxes>();
						List<Integer> id_impuestos = new ArrayList<Integer>();
						Cursor cursorI=dbHelper.fetchProductImpuestoProd(idProd);
						if(cursorI.moveToFirst()){
							do{
								id_impuestos.add(cursorI.getInt(1));
							}while(cursorI.moveToNext());
						}
						cursorI.close();

						for(int j = 0;j < id_impuestos.size();j++){
							Cursor cursorPI = dbHelper.fetchImpuestos(id_impuestos.get(j));
		
							if(cursorPI.moveToFirst()){
								do{
									//taxes
									//comision
									//colIdTaxes,colNombreT,colPorcentajeT,colTipoImpuesto,colIVA,colTipoPorPeso
									int idTaxes = cursorPI.getInt(0);
									String nombreI = cursorPI.getString(1);
									String porcentaje = cursorPI.getString(2);
									String tipoImpuesto = cursorPI.getString(3);
									if(tipoImpuesto.contentEquals("comision")){
										String iva = cursorPI.getString(4);
										String tipoPeso = cursorPI.getString(5);
										Comisiones comi = new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso);
										comi.setId(idTaxes);
										comisiones.add(comi);
									}else if(tipoImpuesto.contentEquals("taxes")){
										Taxes tax = new Taxes(nombreI, Integer.parseInt(porcentaje));
										tax.setId(idTaxes);
										list_tax.add(tax);
									}
								}while(cursorPI.moveToNext());	
							}
						}
						p.setNombre(nombre);
						p.setArtista(artista);
						p.setPath_imagen(foto);
						p.setTipo(tipo);
						p.setTalla(talla);
						p.setPrecio(precio);
						p.setCantidad(cantidadTotal);
						p.setComisiones(comisiones);
						p.setTaxes(list_tax);
					}
					prodList.add(p);
				}while(c.moveToNext());
				
				String[] headers = {"Amount","Product","Artist","Size","Price","Commission","Total\nCommission"};
				
				int pag = 1;
				pdf.createHeadings(580, 10, 8, ""+pag);
				pdf.addImage(docPdf, 25,770,"live_shows_logo.png");
				pdf.addImage(docPdf, 510,770,"merchsys_logo.png");
				String texto = "Stand: "+stand.getName();
				pdf.createHeadings((520-(texto.length() * 9)), 730, 24, texto);
				double[] dob = pdf.tableProducts(docPdf, prodList, headers,710);
				int posInit = 710;
				int mas = (int)dob[0];
				double totalVendedor = dob[1];
				
				if(mas != 0){
					posInit = 730;
					int posAct = 0;
					do{
						posAct += (int)dob[2];
						docPdf.newPage();
						pag++;
						pdf.createHeadings(580, 10, 8, ""+pag);
						pdf.addImage(docPdf, 25,770,"live_shows_logo.png");
						pdf.addImage(docPdf, 510,770,"merchsys_logo.png");
						dob = pdf.tableProducts(docPdf, prodList.subList(posAct, prodList.size()), headers, 730);
						totalVendedor += dob[1];
						mas = (int)dob[0];
					}while(mas != 0);
				}
				
				float pos = (float) (posInit - dob[2] - 10);
				if(pos - 41 < 0){
					docPdf.newPage();
					pag++;
					pdf.createHeadings(580, 10, 8, ""+pag);
					pdf.addImage(docPdf, 25,770,"live_shows_logo.png");
					pdf.addImage(docPdf, 510,770,"merchsys_logo.png");
					pos = 730;
				}
				pdf.tableNum(docPdf, new String[]{"TOTAL COMMISSION"}, new double[]{totalVendedor}, 150, pos);
				
				String line1 = "Gerente";
				String line2 = stand.getEncargado();
				pdf.addLine(50, 90);
				pdf.createHeadings(150 - ((line1.length()/2)*9), 78, 14, line1);
				pdf.addLine(320, 90);
				pdf.createHeadings(420 - ((line1.length()/2)*9), 78, 14, line2);
				docPdf.close();
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MerchSys/stand.pdf");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}else{
				Toast.makeText(this, R.string.no_products, Toast.LENGTH_SHORT).show();
			}
			c.close();
			dbHelper.close();
		}else{
			Toast.makeText(this, R.string.no_stand, Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void setStand(String nombre, String encargado, Comisiones com) {
		dbHelper.open();
		long id= dbHelper.createStand(nombre, com.getCantidad(), com.getTipo(), com.getIva(), encargado);
		if(id==-1){
			makeToast(R.string.d_com_err);
		}else{
			makeToast(R.string.s_ananido);
			newStand(id, nombre, encargado, com,0,0,0,0,0,0,0,0);
		}
		dbHelper.close();
	}
	

	public void newStand(long id, String nombre, String encargado, Comisiones com, double efectivo, double banamex, double banorte, double santander, double amex, double other1, double other2,double other3){
		Stand stand = new Stand(id,nombre, encargado, com, efectivo, banamex, banorte, santander, amex, other1,other2,other3);
		frag.setStand(stand);
		//onStandSeleccionado(stand);
	}

	@Override
	public void onStandSeleccionado(Stand s) {
		// TODO Auto-generated method stub
		this.stand = s;
		boolean hayDetalle = 
				(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

		if(hayDetalle) {
			((FragmentStandProd)getFragmentManager().
				findFragmentById(R.id.article_fragment)).setStand(s,idFecha);
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == CIERRE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Intent  i = new Intent(this,SplashActivity.class);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    		startActivity(i);
	        	finish();
	        }
	    }else if (requestCode == STAND){
	    	if (resultCode == Activity.RESULT_OK) {
	    		Bundle extras = data.getExtras();
	    		stand.setIngresos(extras.getDouble("efectivo"), extras.getDouble("banamex"), extras.getDouble("banorte"), 
	    				extras.getDouble("santander"), extras.getDouble("amex"), extras.getDouble("oher1"), 
	    				extras.getDouble("other2"), extras.getDouble("other3"));
	    		boolean hayDetalle = 
						(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

				if(hayDetalle) {
					((FragmentStandProd)getFragmentManager().
						findFragmentById(R.id.article_fragment)).setStand(stand,idFecha);
				}
	    	}
	    }
	}
	
	public void borrarTodo(View v){
		Intent i = new Intent(StandActivity.this,ActivityCierreDia.class);
		startActivityForResult(i, CIERRE);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}

	public void toCierre(View view){
		Intent i = new Intent(StandActivity.this,ActivityCierreStand.class);
		Bundle b = new Bundle();
		b.putInt("id_stand",(int)stand.getId());
		b.putInt("fecha", idFecha);
		b.putString("nombre", stand.getName());
		b.putString("encargado", stand.getEncargado());
		i.putExtra("extra", b);
		startActivityForResult(i,STAND);
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
				String iva = cursor.getString(5);
				String tipo = cursor.getString(4);
				double efectivo = cursor.getDouble(6);
				double banamex = cursor.getDouble(7);
				double banorte = cursor.getDouble(8);
				double santander = cursor.getDouble(9);
				double amex = cursor.getDouble(10);
				double other1 = cursor.getDouble(11);
				double other2 = cursor.getDouble(12);
				double other3 = cursor.getDouble(13);
				Comisiones com = new Comisiones("Vendedor", comision, iva, tipo);
				newStand(id, nombre, encargado, com,efectivo,banamex,banorte,santander,amex,other1,other2,other3);
				if(cursor.isFirst()){
					Stand first = new Stand(id,nombre, encargado, com,efectivo,banamex,banorte,santander,amex,other1,other2,other3);
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
		if((p.getCantidad()-Integer.parseInt(adicional)) >= 0){
			if(dbHelper.updateProducto(p.getId(), p.getCantidad()-Integer.parseInt(adicional))){
				if(dbHelper.updateStandProducto(p.getStandId(), stand.getId(), p.getCantidadStand()+Integer.parseInt(adicional))){
					((FragmentStandProd)getFragmentManager().
							findFragmentById(R.id.article_fragment)).setNewCantidad(p.getCantidadStand()+Integer.parseInt(adicional), position);
					boolean hayDetalle = 
							(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

					if(hayDetalle) {
						((FragmentStandProd)getFragmentManager().
							findFragmentById(R.id.article_fragment)).setStand(stand,idFecha);
					}
					makeToast(R.string.p_anadido_ad);
				}else{
					makeToast(R.string.d_com_err);
					Log.i("ERROR","StandProduct"+ p.getId()+" "+p.getArtista()+" "+p.getNombre());
				}
			}else{
				makeToast(R.string.d_com_err);
				Log.i("ERROR","UpdateProduct"+ p.getId()+" "+p.getArtista()+" "+p.getNombre());
			}
		}else{
			makeToast(R.string.err_cantidad);
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
		int total=p.getCantidadStand()-cortesia.getAmount();
		if((total) >= 0){
			dbHelper.open();
			if(dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getId(),(int)stand.getId())>=0){
				p.addCortesia(cortesia);
				int cantidad = total;
				p.setCantidadStand(cantidad);
				dbHelper.updateStandProducto(p.getStandId(),stand.getId(), cantidad);
				boolean hayDetalle = 
						(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

				if(hayDetalle) {
					((FragmentStandProd)getFragmentManager().
						findFragmentById(R.id.article_fragment)).setStand(stand,idFecha);
				}
				makeToast(R.string.p_anadido_cor);
			}
			dbHelper.close();
		}else{
			makeToast(R.string.err_cort_noval);
		}
	}
	
	@Override
	public void setNewComision(Comisiones com) {
		// TODO Auto-generated method stub
		dbHelper.open();
		
		if(dbHelper.updateComision(com.getId(), com.getCantidad(), com.getIva(), com.getTipo())){
			makeToast(R.string.update_exitoso);
			boolean hayDetalle = 
					(getFragmentManager().findFragmentById(R.id.article_fragment) != null);

			if(hayDetalle) {
				((FragmentStandProd)getFragmentManager().
					findFragmentById(R.id.article_fragment)).setStand(stand,idFecha);
			}
		}else{
			makeToast(R.string.update_noexitoso);
		}
		
		dbHelper.close();
		
	}

	@Override
	public void updateStand(long idStand, String nombre, String encargado, Comisiones com) {
		dbHelper.open();
		
		if(dbHelper.updateStand(idStand, nombre, encargado, com.getCantidad(), com.getTipo(), com.getIva())){
			makeToast(R.string.s_updated);
			frag.reloadData();
		}else{
			makeToast(R.string.update_noexitoso);
		}
		
		dbHelper.close();
		
	}

}