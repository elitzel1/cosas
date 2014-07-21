package com.clicky.liveshows;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.adapters.AdapterCloseStand;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.PDF;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;
import com.itextpdf.text.Document;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	private PDF pdf;
	List<Product> products;
	AdapterCloseStand adapter;
	int id,idFecha;
	int[] comisiones;
	double totalVentas,comision;
	TextView txtTotal,txtComision;
	EditText editEfectivo,editBanamex,editBanorte,editSantander,editAmex,editOtro;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cierre_stand);
		// Show the Up button in the action bar.
		setupActionBar();
		pdf = new PDF(this);
		
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
				p.setCantidadStand(cantidad);
				p.setId(c.getInt(0));
				List<Comisiones> listCom = new ArrayList<Comisiones>();
				List<Taxes> listTax = new ArrayList<Taxes>();
				List<Cortesias> listCor = new ArrayList<Cortesias>();
				
				int comVendedorId = c.getInt(4);
				Cursor cursorCom = dbHelper.fetchImpuestos(comVendedorId);
				if(cursorCom.moveToFirst()){
					int idTaxes = cursorCom.getInt(0);
					String nombreI = cursorCom.getString(1);
					String porcentaje = cursorCom.getString(2);
					String iva = cursorCom.getString(4);
					String tipoPeso = cursorCom.getString(5);
					Comisiones comi = new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso);
					comi.setId(idTaxes);
					listCom.add(comi);
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
								}else if(cm.getString(3).equals("comision")){
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
					Cursor cursorCr = dbHelper.fetchCortesias(cursor.getLong(0), id);
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
				totalVentas = 0;
				comision = 0;
				
				DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
				formatter.applyPattern("$#,###.00");
				for(Product p : products){
					if(p.getProdNo() >= 0){
						totalVentas += (p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio()));
						comision += setComision((p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio())),p.getComisiones(),p.getTaxes(),p.getCantidadStand()-p.getProdNo());
						txtTotal.setText(formatter.format(totalVentas));
						txtComision.setText(formatter.format(comision));
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
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
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

	private double setComision(double total,List<Comisiones> comisiones,List<Taxes> taxes, int cantidad){
		double comision = 0.0;
		Comisiones vendedor =comisiones.get(0);
		if(vendedor.getTipo().equals("After taxes")){
			double iva = 0.0;
			for(int i = 0; i < taxes.size(); i++){
				Taxes tax = taxes.get(i);
				double aux = total * ((tax.getAmount()) * 0.01);
				iva += aux;
			}
			for(int i = 1; i < comisiones.size(); i++){
				Comisiones com = comisiones.get(i);
				if(com.getTipo().equals("Before taxes")){
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
		if(vendedor.getIva().equals("%")){
			comision = total * (vendedor.getCantidad() * 0.01);
		}else{
			comision = vendedor.getCantidad()*cantidad;
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
				getReport();
				String reporte = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MerchSys/sales_stand.pdf";
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_SUBJECT, "Sales Report");
				email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+reporte));
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
				cierreStand();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cierreStand();
			}
		});
		builder.create().show();
	}
	
	private void cierreStand(){
		cierreProds();
		finish();
		overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
	}
	
	private void getReport(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String[] headers = {"PRICE SALE IN US","#","ITEM","STYLE","SIZE","TOTAL\nINVENTORY","PRICE SALE","DAMAGE","COMPS\nVENUE",
				"COMPS\nOFFICE\nPRODUCTION",prefs.getString("op1", "OTHER"),prefs.getString("op2", "OTHER"),"FINAL\nINVENTORY",
				"SALES PIECES","GROSS TOTAL","% SALES","COMISSION","TOTAL\nCOMISSION"};
		String[] headerIngresos = {"EFECTIVO","TC BANAMEX","TC BANORTE","TC SANTANDER","TC AMEX","OTROS"};
		
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
		
		double[] ingresos = {efectivo,banamex,banorte,santander,amex,other};
		Document docPdf = pdf.createPDFHorizontal("sales_stand.pdf");
		
		pdf.addImage(25,540,docPdf);
		pdf.createHeadings(415, 535, 14, "SALES REPORT (IN "+prefs.getString("moneda", "")+")");
		pdf.createHeadings(docPdf.leftMargin(), 515, 8, "DATE");
		pdf.createHeadings(docPdf.leftMargin(), 505, 8, "EVENT");
		pdf.createHeadings(docPdf.leftMargin(), 495, 8, "VENUE");
		
		pdf.tableStandVentas(docPdf, products, headers, totalVentas, 480);
		int pos = 450 - (38*products.size());
		int x = 650;
		
		pdf.tableNum(docPdf, new String[]{"GROSS TOTAL","VENDOR COMMISION"}, new double[]{totalVentas,comision}, x, (pos));
		
		pdf.tableNum(docPdf, new String[]{"TOTAL A DEPOSITAR"}, new double[]{(totalVentas - comision)}, x, (pos-55));
		
		double depositado = pdf.tableIngresos(docPdf, "INGRESOS RECIBIDOS", "TOTAL DEPOSITADO", headerIngresos, ingresos, x, (pos - 105));
		
		pdf.tableNum(docPdf, new String[]{"DIF +/-"}, new double[]{depositado - (totalVentas - comision)}, x, (pos-225));
		
		docPdf.close();
	}
	
}
