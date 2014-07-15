package com.clicky.liveshows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.adapters.AdapterCloseStand;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.Excel;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCierreStand extends Activity implements OnItemClickListener,OnCortesiaListener {

	private DBAdapter dbHelper;
	private Excel excel;
	List<Product> products;
	AdapterCloseStand adapter;
	int id,idFecha;
	int[] comisiones;
	TextView txtTotal,txtComision;
	EditText editEfectivo,editBanamex,editBanorte,editSantander,editAmex,editOtro;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cierre_stand);
		// Show the Up button in the action bar.
		setupActionBar();
		Bundle b= getIntent().getBundleExtra("extra");
		id = b.getInt("id_stand");
		idFecha = b.getInt("fecha");
		
		txtTotal=(TextView)findViewById(R.id.txtTotal);
		txtComision = (TextView)findViewById(R.id.txtComisiones);
		editEfectivo = (EditText)findViewById(R.id.editEfec);
		editBanamex = (EditText)findViewById(R.id.editBanamex);
		editBanorte = (EditText)findViewById(R.id.editBanorte);
		editSantander = (EditText)findViewById(R.id.editSantander);
		editAmex = (EditText)findViewById(R.id.editAmex);
		editOtro = (EditText)findViewById(R.id.editOtro);
		
		products = new ArrayList<Product>();
		ListView list=(ListView)findViewById(R.id.listCierre);
		
		txtTotal.setText("$"+String.format("%.2f",0.0));
		txtComision.setText("$"+String.format("%.2f",0.0));
		
		excel = new Excel(this);
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
		
		Cursor c  = dbHelper.fetchStandProduct(id,idFecha);
		if(c.moveToFirst()){
			comisiones = new int[c.getCount()];
			int numCom = 0;
			do{
				Product p = new Product();  //Se obtiene la cantidad de prod en el stand, nombre,tipo, talla y precio
				int cantidad = c.getInt(1);
				int idProd = c.getInt(3);
				int idComision = c.getInt(4);
				p.setCantidadStand(cantidad);
				p.setId(c.getInt(0));
				List<Comisiones> listCom = new ArrayList<Comisiones>();
				List<Taxes> listTax = new ArrayList<Taxes>();
				List<Cortesias> listCor = new ArrayList<Cortesias>();
				Cursor cursorCr = dbHelper.fetchCortesias(c.getInt(0), id);
				if(cursorCr.moveToFirst()){
					int cantCor = 0;
					do{
						Cortesias cort = new Cortesias();
						cort.setTipo(cursorCr.getString(1));
						cort.setAmount(cursorCr.getInt(2));
						cantCor += cursorCr.getInt(2);
						listCor.add(cort);
					}while(cursorCr.moveToNext());
					comisiones[numCom] = cantCor;
					numCom++;
				}else{
					comisiones[numCom] = 0;
					numCom++;
				}
				Cursor cursorCom = dbHelper.fetchImpuestos(idComision);
				if(cursorCom.moveToFirst()){
					Comisiones com = new Comisiones();
					com.setName(cursorCom.getString(1));
					com.setCantidad(cursorCom.getInt(2));
					com.setTipo(cursorCom.getString(5));
					com.setIva(cursorCom.getString(4));
					listCom.add(com);
				}
				cursorCom = dbHelper.fetchProductImpuestoProd(idProd);
				if(cursorCom.moveToFirst()){
					do{
						Cursor cm = dbHelper.fetchImpuestos(cursorCom.getInt(1));
						if(cm.moveToFirst()){
							do{
								if(cm.getString(3).equals("taxes")){
									Taxes tax = new Taxes(cm.getString(1),cm.getInt(2));
									listTax.add(tax);
								}else{
									Comisiones com = new Comisiones();
									com.setName(cm.getString(1));
									com.setCantidad(cm.getInt(2));
									com.setTipo(cm.getString(5));
									com.setIva(cm.getString(4));
									listCom.add(com);
								}
							}while(cm.moveToNext());
						}
					}while(cursorCom.moveToNext());
				}
				
				Cursor cursor = dbHelper.fetchProducto(idProd);
				if(cursor.moveToFirst()){
					String nombre = cursor.getString(1);
					String artista = artistas.get(cursor.getInt(9));
					String tipo = cursor.getString(2);
					String talla = cursor.getString(6);
					String precio = cursor.getString(7);
					int cantidadTotal = cursor.getInt(5);
					
					p.setNombre(nombre);
					p.setArtista(artista);
					p.setTipo(tipo);
					p.setTalla(talla);
					p.setPrecio(precio);
					p.setCantidad(cantidadTotal);
					p.setTaxes(listTax);
					p.setComisiones(listCom);
					p.setCortesias(listCor);
				}
				products.add(p);
				
				Log.i("STAND_PROD", p.getNombre()+" "+p.getTipo()+" "+p.getTalla()+" "+cantidad);
			}while(c.moveToNext());
			//adapter.notifyDataSetChanged();
		}else{
			products.clear();
			//adapter.notifyDataSetChanged();
		}
		dbHelper.close();
		
		adapter = new AdapterCloseStand(this, R.layout.item_cierra_stand, products,comisiones);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		list.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				double total = 0;
				double comision = 0;
				for(Product p : products){
					if(p.getProdNo() >= 0){
						total += (p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio()));
						comision += setComision((p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio())),p.getComisiones(),p.getTaxes());
						txtTotal.setText("$"+String.format("%.2f",total));
						txtComision.setText("$"+String.format("%.2f",comision));
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
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			return true;
		case R.id.action_accept:
			validaCierre();
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
	public void setCortesia(Cortesias cortesia, int position) {
		// TODO Auto-generated method stub
		Product p = products.get(position);
		p.addCortesia(cortesia);
		Log.i("COR", "Set cortesia "+p.getNombre()+" "+cortesia);
		dbHelper.open();
		int total=p.getCantidad()-p.getCortesias().get(p.sizeCortesias()-1).getAmount();
		if((total)>0){

			if(dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getId(),id)>=0){
				int cantidad = total;
				p.setCantidad(cantidad);
				dbHelper.updateProducto(p.getId(), cantidad);
			}
		}else{

		}
		dbHelper.close();
	}

	private double setComision(double total,List<Comisiones> comisiones,List<Taxes> taxes){
		double comision = 0.0;
		Comisiones vendedor =comisiones.get(0);
		if(vendedor.getIva().equals("After Taxes")){
			double iva = 0.0;
			for(int i = 0; i < taxes.size(); i++){
				Taxes tax = taxes.get(i);
				double aux = total * ((tax.getAmount()) * 0.01);
				iva += aux;
			}
			for(int i = 1; i < comisiones.size(); i++){
				Comisiones com = comisiones.get(i);
				if(com.getTipo().equals("Before Taxes")){
					if(com.getIva().equals("%")){
						double aux = total * (com.getCantidad() * 0.01);
						iva += aux;
					}else{
						iva += com.getCantidad();
					}
				}
			}
			total -= iva;
		}
		if(vendedor.getTipo().equals("%")){
			comision = total * (vendedor.getCantidad() * 0.01);
		}else{
			comision = vendedor.getCantidad();
		}
		
		return comision;
	}
	private void cierreProds(){
		dbHelper.open();
		for(int i = 0; i < products.size(); i++){
			Product prod = products.get(i);
			long venta = dbHelper.createVentaProducto(id,prod.getId() , (prod.getCantidadStand() - prod.getProdNo()));
			if(venta == -1){
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		double efectivo = 0.0, banorte = 0.0, banamex = 0.0, santander = 0.0, amex = 0.0, other = 0.0;
		if(!editEfectivo.getText().toString().equals("")){
			efectivo = Double.parseDouble(editEfectivo.getText().toString());
		}
		if(!editBanamex.getText().toString().equals("")){
			banamex = Double.parseDouble(editBanamex.getText().toString());
		}
		if(!editBanorte.getText().toString().equals("")){
			banorte = Double.parseDouble(editBanorte.getText().toString());
		}
		if(!editSantander.getText().toString().equals("")){
			santander = Double.parseDouble(editSantander.getText().toString());
		}
		if(!editAmex.getText().toString().equals("")){
			amex = Double.parseDouble(editAmex.getText().toString());
		}
		if(!editOtro.getText().toString().equals("")){
			other = Double.parseDouble(editOtro.getText().toString());
		}
		dbHelper.updateStandCierre(id, efectivo, banamex, banorte, santander, amex , other);
		dbHelper.close();
	}
	
	private void validaCierre(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alert_cierre);
		builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cierreProds();
				finish();
				overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	/*
	private void getReport(){
		ArrayList<Double> totales = new ArrayList<Double>();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		WritableWorkbook wb = excel.createWorkbook("sales_report.xls");
		
		WritableSheet hoja1 = excel.createSheet(wb, "Sales", 0);
		
		try {
			
			hoja1.mergeCells(1, 3, 22, 3);
			//Formato del Reporte
			excel.addImage(0.0,0.0,hoja1);
			excel.writeCell(1, 3, "SALES REPORT(IN "+prefs.getString("moneda", "")+")", 10, hoja1);
			excel.writeCell(1, 5, "DATE", 1, hoja1);
			excel.writeCell(2, 5, fecha, 0, hoja1);
			excel.writeCell(1, 7, "EVENT", 1, hoja1);
			excel.writeCell(2, 7, evento, 0, hoja1);
			excel.writeCell(1, 9, "VENUE/\nPLACE", 1, hoja1);
			excel.writeCell(2, 9, local, 0, hoja1);
			excel.writeCell(1, 11, "VENDEDOR", 1, hoja1);
			excel.writeCell(2, 11, agency, 0, hoja1);
				
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
	*/
}
