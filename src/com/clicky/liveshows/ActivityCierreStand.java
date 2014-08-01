package com.clicky.liveshows;

import java.io.File;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCierreStand extends Activity implements OnCortesiaListener, TextWatcher {

	private DBAdapter dbHelper;
	private PDF pdf;
	List<Product> products;
	AdapterCloseStand adapter;
	int id,idFecha;
	int[] comisiones;
	double totalVentas,comision,depositado;
	String nombre,encargado;
	TextView txtTotal,txtComision,txtFalta;
	EditText editEfectivo,editBanamex,editBanorte,editSantander,editAmex,editOtro1,editOtro2,editOtro3;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cierre_stand);
		// Show the Up button in the action bar.
		pdf = new PDF(this);
		Bundle b= getIntent().getBundleExtra("extra");
		id = b.getInt("id_stand");
		idFecha = b.getInt("fecha");
		nombre = b.getString("nombre");
		encargado = b.getString("encargado");
		
		setupActionBar();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		txtTotal=(TextView)findViewById(R.id.txtTotal);
		txtComision = (TextView)findViewById(R.id.txtComisiones);
		txtFalta = (TextView)findViewById(R.id.txtFalta);
		editEfectivo = (EditText)findViewById(R.id.editEfec);
		editBanamex = (EditText)findViewById(R.id.editBanamex);
		editBanorte = (EditText)findViewById(R.id.editBanorte);
		editSantander = (EditText)findViewById(R.id.editSantander);
		editAmex = (EditText)findViewById(R.id.editAmex);
		editOtro1 = (EditText)findViewById(R.id.editOtro1);
		editOtro2 = (EditText)findViewById(R.id.editOtro2);
		editOtro3 = (EditText)findViewById(R.id.editOtro3);
		
		((TextView)findViewById(R.id.ingreso_1)).setText(prefs.getString("tipo1", "OTHER"));
		((TextView)findViewById(R.id.ingreso_2)).setText(prefs.getString("tipo2", "OTHER"));
		((TextView)findViewById(R.id.ingreso_3)).setText(prefs.getString("tipo3", "OTHER"));
		
		editEfectivo.addTextChangedListener(this);
		editBanamex.addTextChangedListener(this);
		editBanorte.addTextChangedListener(this);
		editSantander.addTextChangedListener(this);
		editAmex.addTextChangedListener(this);
		editOtro1.addTextChangedListener(this);
		editOtro2.addTextChangedListener(this);
		editOtro3.addTextChangedListener(this);
		
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
				p.setStandId(idProd);
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
		
		calculaTotales();
	}
	
	public void calculaTotales(){
		totalVentas = 0;
		comision = 0;
		
		for(Product p : products){
			if(p.getProdNo() >= 0){
				totalVentas += (p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio()));
				comision += setComision((p.getCantidadStand()-p.getProdNo())*(Double.parseDouble(p.getPrecio())),p.getComisiones(),p.getTaxes(),p.getCantidadStand()-p.getProdNo());
				setCantidades();
			}
		}
	}
	
	private void setCantidades(){
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		formatter.applyPattern("#,##0.00");
		
		txtTotal.setText("$"+formatter.format(totalVentas));
		txtComision.setText("$"+formatter.format(comision));
		txtFalta.setText("$"+formatter.format(totalVentas-comision-depositado));
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
		getActionBar().setTitle("Corte "+nombre);
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

	public void dialogCortesia(int position){
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
		Log.i("COR", "Set cortesia "+p.getNombre()+" "+cortesia);
		int total=p.getCantidadStand()-cortesia.getAmount();
		if((total) >= 0){
			dbHelper.open();
			if(dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getStandId(),id)>=0){
				int cantidad = total;
				p.addCortesia(cortesia);
				p.setCantidadStand(cantidad);
				comisiones[position] += cortesia.getAmount();
				dbHelper.updateStandProducto(p.getId(),id, cantidad);
				adapter.notifyDataSetChanged();
				calculaTotales();
			}
			dbHelper.close();
			Toast.makeText(this, R.string.p_anadido_cor, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, R.string.err_cort_noval, Toast.LENGTH_SHORT).show();
		}
	}

	private double setComision(double total,List<Comisiones> comisiones,List<Taxes> taxes, int cantidad){
		double comision = 0.0;
		Comisiones vendedor = comisiones.get(0);
		if(vendedor.getTipo().equals("After taxes")){
			double iva = 0.0;
			for(int i = 0; i < taxes.size(); i++){
				Taxes tax = taxes.get(i);
				double aux = 1 + (tax.getAmount()* 0.01);
				iva += truncate(total / aux);;
			}
			total = iva;
		}
		if(vendedor.getIva().equals("%")){
			comision = truncate(total * (vendedor.getCantidad() * 0.01));
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
			dbHelper.updateStandProducto(prod.getId(), id, prod.getProdNo());
			if(venta == -1){
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		double efectivo = 0.0, banorte = 0.0, banamex = 0.0, santander = 0.0, amex = 0.0, other1 = 0.0,other2 = 0.0,other3 = 0.0;
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
		if(!editOtro1.getText().toString().equals("")){
			other1 = Double.parseDouble(editOtro1.getText().toString());
		}
		if(!editOtro2.getText().toString().equals("")){
			other2 = Double.parseDouble(editOtro2.getText().toString());
		}
		if(!editOtro3.getText().toString().equals("")){
			other3 = Double.parseDouble(editOtro3.getText().toString());
		}
		dbHelper.updateStandCierre(id, efectivo, banamex, banorte, santander, amex , other1,other2,other3,comision);
		dbHelper.close();
	}
	
	private void validaCierre(){
		if((totalVentas-comision)==depositado){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_cierre);
			builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getReport();
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MerchSys/sales_stand.pdf");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
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
		}else{
			Toast.makeText(this, "The amount deposited is incorrect", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void cierreStand(){
		cierreProds();
		double efectivo = 0.0, banorte = 0.0, banamex = 0.0, santander = 0.0, amex = 0.0, other1 = 0.0,other2 = 0.0,other3 = 0.0;
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
		if(!editOtro1.getText().toString().equals("")){
			other1 = Double.parseDouble(editOtro1.getText().toString());
		}
		if(!editOtro2.getText().toString().equals("")){
			other2 = Double.parseDouble(editOtro2.getText().toString());
		}
		if(!editOtro3.getText().toString().equals("")){
			other3 = Double.parseDouble(editOtro3.getText().toString());
		}
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putDouble("efectivo", efectivo);
		b.putDouble("banamex", banamex);
		b.putDouble("banorte", banorte);
		b.putDouble("santander", santander);
		b.putDouble("amex", amex);
		b.putDouble("other1", other1);
		b.putDouble("other2", other2);
		b.putDouble("other3", other3);
		i.putExtras(b);
		setResult(RESULT_OK,i);
		finish();
		overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
	}
	
	private void getReport(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String[] headers = {"PRICE SALE IN US","#","ITEM","STYLE","SIZE","TOTAL\nINVENTORY","PRICE SALE","DAMAGE","COMPS\nVENUE",
				"COMPS\nOFFICE\nPRODUCTION",prefs.getString("op1", "OTHER"),prefs.getString("op2", "OTHER"),"FINAL\nINVENTORY",
				"SALES PIECES","GROSS TOTAL","% SALES","COMISSION","TOTAL\nCOMISSION"};
		String[] headerIngresos = {"EFECTIVO","TC BANAMEX","TC BANORTE","TC SANTANDER","TC AMEX",
				prefs.getString("tipo1", "OTHER"),prefs.getString("tipo2", "OTHER"),prefs.getString("tipo3", "OTHER")};
		
		double efectivo = 0.0, banorte = 0.0, banamex = 0.0, santander = 0.0, amex = 0.0, other1 = 0.0,other2=0.0,other3=0.0;
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
		if(!editOtro1.getText().toString().equals("")){
			other1 = Double.parseDouble(editOtro1.getText().toString());
		}
		if(!editOtro2.getText().toString().equals("")){
			other2 = Double.parseDouble(editOtro2.getText().toString());
		}
		if(!editOtro3.getText().toString().equals("")){
			other3 = Double.parseDouble(editOtro3.getText().toString());
		}
		
		double[] ingresos = {efectivo,banamex,banorte,santander,amex,other1,other2,other3};
		Document docPdf = pdf.createPDFHorizontal("sales_stand.pdf");
		
		int pag = 1;
		pdf.createHeadings(983, 15, 8, ""+pag);
		pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
		pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
		
		String texto = "SALES REPORT (IN "+prefs.getString("moneda", "")+")";
		pdf.createHeadings(504-((texto.length()/2)*9), 535, 14, texto);
		
		pdf.createHeadings(25, 500, 14, "Stand:   "+nombre);
		pdf.createHeadings(25, 485, 14, "Manager: "+encargado);
		
		float posInit = 470;
		double[] dob = pdf.tableStandVentas(docPdf, products, headers, totalVentas,0, 470);
		int mas = (int)dob[0];
		if(mas != 0){
			posInit = 500;
			int posAct = 0;
			do{
				posAct += (int)dob[1];
				docPdf.newPage();
				pag++;
				pdf.createHeadings(983, 15, 8, ""+pag);
				pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
				pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
				dob = pdf.tableStandVentas(docPdf, products.subList(posAct, products.size()), headers, totalVentas,posAct, 500);
				mas = (int)dob[0];
			}while(mas != 0);
		}
		
		float pos = (float) (posInit - dob[1] - 10);
		int x = 650;
		
		if(pos - 65 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(983, 15, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		pdf.tableNum(docPdf, new String[]{"GROSS TOTAL","VENDOR COMMISION"}, new double[]{totalVentas,comision}, x, (pos));
		
		pos -= 50;
		if(pos - 49 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(983, 15, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		pdf.tableNum(docPdf, new String[]{"TOTAL A DEPOSITAR"}, new double[]{(totalVentas - comision)}, x, (pos));
		
		pos -= 34;
		if(pos - 183 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(983, 15, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		double dep = pdf.tableIngresos(docPdf, "INGRESOS RECIBIDOS", "TOTAL DEPOSITADO", headerIngresos, ingresos, x, (pos));
		
		pos -= 168;
		if(pos - 41 < 0){
			docPdf.newPage();
			pag++;
			pdf.createHeadings(983, 15, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
			pos = 500;
		}
		pdf.tableNum(docPdf, new String[]{"DIF +/-"}, new double[]{dep - (totalVentas - comision)}, x, (pos));
		
		String line1 = "Gerente";
		String line2 = encargado;
		pdf.addLine(50, 90);
		pdf.createHeadings(150 - ((line1.length()/2)*9), 78, 14, line1);
		pdf.addLine(320, 90);
		pdf.createHeadings(420 - ((line1.length()/2)*9), 78, 14, line2);
		
		docPdf.close();
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		depositado = 0.0;
		if(!editEfectivo.getText().toString().equals("")){
			depositado += Double.parseDouble(editEfectivo.getText().toString());
		}
		if(!editBanamex.getText().toString().equals("")){
			depositado += Double.parseDouble(editBanamex.getText().toString());
		}
		if(!editBanorte.getText().toString().equals("")){
			depositado += Double.parseDouble(editBanorte.getText().toString());
		}
		if(!editSantander.getText().toString().equals("")){
			depositado += Double.parseDouble(editSantander.getText().toString());
		}
		if(!editAmex.getText().toString().equals("")){
			depositado += Double.parseDouble(editAmex.getText().toString());
		}
		if(!editOtro1.getText().toString().equals("")){
			depositado += Double.parseDouble(editOtro1.getText().toString());
		}
		if(!editOtro2.getText().toString().equals("")){
			depositado += Double.parseDouble(editOtro2.getText().toString());
		}
		if(!editOtro3.getText().toString().equals("")){
			depositado += Double.parseDouble(editOtro3.getText().toString());
		}
		setCantidades();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	private double truncate(double num){
		num *= 100;
		int aux = (int)num;
		double res = (double)aux / 100;
		return res;
	}
	
}
