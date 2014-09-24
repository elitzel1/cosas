package com.clicky.liveshows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.clicky.liveshows.DialogAddAdcional.OnAdicionalListener;
import com.clicky.liveshows.DialogAddArtist.OnAddArtist;
import com.clicky.liveshows.DialogAddProduct.OnDialogListener;
import com.clicky.liveshows.DialogDeleteCortesia.OnCortesiasSelected;
import com.clicky.liveshows.DialogEvent.OnEventListener;
import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.DialogUpdate.OnDialogUpdateListener;
import com.clicky.liveshows.DialogVenue.OnVenueListener;
import com.clicky.liveshows.adapters.AdapterProduct;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.AlbumStorageDirFactory;
import com.clicky.liveshows.utils.BaseAlbumDirFactory;
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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProductos extends Activity implements OnDialogListener, OnItemClickListener,OnAdicionalListener,OnCortesiaListener,OnDialogUpdateListener,OnAddArtist,OnCortesiasSelected,OnVenueListener,OnEventListener {

	private DBAdapter dbHelper;
	private Cursor cursor;
	private String[] artists;
	private int[] id_artists;
	private PDF pdf;
	List<Product> products;
	AdapterProduct adapter;
	String nameEvento = null,venue,fechaEvento;
	ArrayList<TipoProduct> tipos;
	int idEvento,idFecha,capacidad;
	HashMap<Integer, String> artistas;
	DialogAddProduct dialog;
	DialogUpdate dialog_update;
	protected static final int CAMERA_ACTIVITY = 100;
	ListView list;
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_UPDATEITEM = 1;
	protected static final int CONTEXTMENU_DETALLEITEM =2;
	protected static final int CONTEXTMENU_ADDCORTESIA = 3;
	protected static final int CONTEXTMENU_DELETECORTESIA = 4;
	protected static final int CONTEXTMENU_DELETEADDING = 5;
	

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private String albumPath=null;
	private String mCurrentPhotoPath;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productos);
		setupActionBar();

		artistas  = new HashMap<Integer, String>();

		idEvento=0;
		products = new ArrayList<Product>();
		tipos = new ArrayList<TipoProduct>();
		setXML();
		
		pdf = new PDF(this);
		list = (ListView)findViewById(R.id.listArticulos);
		adapter = new AdapterProduct(this, R.layout.item_lista_producto, products);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				//	menu.add(R.string.title_menu);
				menu.add(0, CONTEXTMENU_UPDATEITEM,1,R.string.m_actualizar);
				menu.add(0, CONTEXTMENU_DELETEITEM,0,R.string.m_eliminar);
				menu.add(0, CONTEXTMENU_DETALLEITEM, 2, R.string.m_detalles);
				menu.add(0, CONTEXTMENU_ADDCORTESIA, 3, R.string.m_cortesia);
				menu.add(0, CONTEXTMENU_DELETECORTESIA, 4, R.string.m_delete_cortesia);
				menu.add(0, CONTEXTMENU_DELETEADDING, 5, R.string.m_delete_adding);
			}
		});

		dbHelper = new DBAdapter(this);
		dbHelper.open();
		cursor = dbHelper.fetchAllEvento();

		if(cursor.moveToFirst()){
			do{
				idEvento = cursor.getInt(0);
				nameEvento = cursor.getString(1);
				venue = cursor.getString(2);
				capacidad = cursor.getInt(3);
			}while(cursor.moveToNext());
		}else{
		}
		cursor.close();

		Cursor c = dbHelper.fetchAllArtistas();
		artists = new String[c.getCount()];
		id_artists = new int[c.getCount()];
		int i=0;
		if(c.moveToFirst()){
			do{
				int id = c.getInt(0);
				String name = c.getString(1);
				artistas.put(id, name);
				artists[i]=name;
				id_artists[i]=id;
				i++;
				int idEvento = c.getInt(2);
				Log.i("BD",""+id+" "+name+" "+idEvento);
			}while(c.moveToNext());
		}
		c.close();
		Cursor cursor1 = dbHelper.fetchAllFechas();
		List<Date> dates = new ArrayList<Date>();
		HashMap<Date, Integer> hashDate = new HashMap<Date, Integer>();
		if(cursor1.moveToFirst()){
			do{
				hashDate.put(toDateDate(cursor1.getString(1)), cursor1.getInt(0));
				dates.add(toDateDate(cursor1.getString(1)));
				Log.i("BD",""+cursor1.getString(1));
			}while(cursor1.moveToNext());
		}
		cursor1.close();

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
				String precio = cursorProd.getString(7);
				int idEvento = cursorProd.getInt(8);
				int idArtista = cursorProd.getInt(9);


				List<Adicionales> a = null;
				Cursor cursorA = dbHelper.fetchAdicional(id);
				if(cursorA.moveToFirst()){
					a = new ArrayList<Adicionales>();
					do{
						int adId = cursorA.getInt(0);
						int cantidadA = cursorA.getInt(1);
						String nomA = cursorA.getString(2);
						a.add(new Adicionales(adId,nomA, cantidadA, id));
						Log.i("PAdicionales",nomA+" "+cantidadA);
					}while(cursorA.moveToNext());
				}
				cursorA.close();

				List<Comisiones> list_com=null;
				List<Taxes> list_tax=null;
				list_com = new ArrayList<Comisiones>();
				list_tax = new ArrayList<Taxes>();

				List<Cortesias> cortesias = null;
				Cursor cursorCortesias = dbHelper.fetchCortesias(id);
				if(cursorCortesias.moveToFirst()){
					cortesias= new ArrayList<Cortesias>();
					do{
						int cortId = cursorCortesias.getInt(0);
						String tipoC = cursorCortesias.getString(1);
						int cantidadC =cursorCortesias.getInt(2);
						cortesias.add(new Cortesias(cortId,tipoC, cantidadC));
					}while(cursorCortesias.moveToNext());
				}
				cursorCortesias.close();

				List<Integer> id_impuestos = new ArrayList<Integer>();
				Cursor cursorI=dbHelper.fetchProductImpuestoProd(id);
				if(cursorI.moveToNext()){
					do{
						id_impuestos.add(cursorI.getInt(1));
					}while(cursorI.moveToNext());
				}
				cursorI.close();

				for(int j=0;j<id_impuestos.size();j++){
					Cursor cursorPI = dbHelper.fetchImpuestos(id_impuestos.get(j));

					if(cursorPI.moveToNext()){
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
								//list_com.add(new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso));
								Comisiones com = new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso);
								com.setId(idTaxes);
 								list_com.add(com);
							}else if(tipoImpuesto.contentEquals("taxes")){
								//list_tax.add(new Taxes(nombreI, Integer.parseInt(porcentaje)));
								Taxes tax = new Taxes(nombreI, Integer.parseInt(porcentaje));
								tax.setId(idTaxes);
 								list_tax.add(tax);
							}
						}while(cursorPI.moveToNext());	
					}
					cursorPI.close();
				}
				Product p = new Product(nombre, tipo, artistas.get(idArtista), precio, talla, cantidad, null, foto);
				p.setTotalCantidad(cantidadTotal);
				if(cortesias!=null){
					p.setCortesias(cortesias);
				}
				p.setId(id);
				p.setComisiones(list_com);
				p.setTaxes(list_tax);
				addProduct(p, a);
				Log.i("PRODUCTS",""+id+" "+nombre+" "+tipo+" "+talla+" "+cantidad+" "+cantidadTotal+" "+precio+" "+idEvento+" "+idArtista);
			}while(cursorProd.moveToNext());
		}
		cursorProd.close();

		dbHelper.close();

		Collections.sort(dates, new Comparator<Date>() {

			@Override
			public int compare(Date lhs, Date rhs) {
				return lhs.compareTo(rhs);
			}
		});

		TextView txtEvento = (TextView)findViewById(R.id.txtArtistaP);
		txtEvento.setText(nameEvento);
		TextView txtFecha = (TextView)findViewById(R.id.txtFechaP);
		
		idFecha = hashDate.get(dates.get(0));
		DateFormat df= DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		txtFecha.setText(df.format(dates.get(0)));
		fechaEvento = df.format(dates.get(0));
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String div = prefs.getString("moneda", "");
		float divisa = Float.parseFloat(prefs.getString("divisa", "0"));

		TextView txtDivisa = (TextView)findViewById(R.id.txtDivisas);

		txtDivisa.setText(div+" = "+divisa+" USD");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_productos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new:
			newProduct();
			return true;
		case R.id.action_settings:
			openSettings();
			return true;
		case R.id.action_print:
			printFile();
			return true;
		case R.id.action_artist:
			addArtist();
			return true;	
		case R.id.action_venue:
			dialogVenue();
			return true;
		case R.id.action_event:
			dialogEvent();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings(){
		Intent i = new Intent(this,Settings.class);
		startActivity(i);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}
	
	private void printFile(){
		if(!products.isEmpty()){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String[] headers = {"PRICE SALE IN US","#","ITEM","STYLE","SIZE","TOTAL\nINVENTORY","PRICE SALE","DAMAGE","COMPS\nVENUE",
					"COMPS\nOFFICE\nPRODUCTION",prefs.getString("op1", "OTHER"),prefs.getString("op2", "OTHER"),"FINAL\nINVENTORY",
					"SALES PIECES","GROSS TOTAL","% SALES","GROSS TOTAL\nUS$DLLS"};
			Double priceUs = Double.parseDouble(prefs.getString("divisa", "0"));
			DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
			df.applyPattern("$#,##0.00");
				
			double subTotal = 0, totalVenta = 0;
			for(Product prod : products){
				int totalCant = prod.getTotalCantidad() - prod.getCantidad();
				for(Cortesias cort : prod.getCortesias()){
					totalCant -= cort.getAmount();
				}
				double total = Double.parseDouble(prod.getPrecio()) * totalCant;
				totalVenta += total;
					
				for(Taxes tax : prod.getTaxes()){
					double aux = 1 + (tax.getAmount()* 0.01);
					subTotal += truncate(total / aux);
				}
					
			}
				
			Document docPdf = pdf.createPDFHorizontal("warehouse_report.pdf");
			
			int pag = 1;
			pdf.createHeadings(998, 10, 8, ""+pag);
			pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
			pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
				
			String texto = "SALES REPORT (IN "+prefs.getString("moneda", "")+")";
			pdf.createHeadings(504-((texto.length()/2)*9), 535, 14, texto);
				
			float aux1 = pdf.tableDatos(docPdf, new String[]{"DATE","EVENT","VENUE"}, 
					new String[]{fechaEvento,nameEvento,venue}, docPdf.leftMargin(), 515);
				
			float aux2 = pdf.tableDatos(docPdf, new String[]{"ATTENDANCE","PERCAP","GROSS TOTAL","RATE EXCHANGE US$1 ="}, 
					new String[]{""+capacidad,df.format(totalVenta/capacidad),df.format(totalVenta),df.format(priceUs)+" "+prefs.getString("moneda", "")}, 620, 515);
			
			float posInit = 0;
			if(aux1 > aux2)
				posInit = 505 - aux1;
			else 
				posInit = 505 - aux2;
				
			double[] dob = pdf.tableAlmacen(docPdf, products, headers, totalVenta,0, posInit);
			int mas = (int)dob[0];
			if(mas != 0){
				posInit = 500;
				int posAct = 0;
				do{
					posAct += (int)dob[1];
					docPdf.newPage();
					pag++;
					pdf.createHeadings(998, 10, 8, ""+pag);
					pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
					pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
					dob = pdf.tableAlmacen(docPdf, products.subList(posAct, products.size()), headers, totalVenta,posAct, 500);
					mas = (int)dob[0];
				}while(mas != 0);
			}
			
			float pos = (float) (posInit - dob[1] - 10);
			int x = 650;
			
			if(pos - 73 < 0){
				docPdf.newPage();
				pag++;
				pdf.createHeadings(998, 10, 8, ""+pag);
				pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
				pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
				pos = 500;
			}
			pdf.tableNum(docPdf, new String[]{"GROSS TOTAL","TAX","GROSS NET"}, new double[]{totalVenta,(totalVenta-subTotal),subTotal}, x, (pos));
			
			pos -= 58;
			if(pos - 60 < 0){
				docPdf.newPage();
				pag++;
				pdf.createHeadings(998, 10, 8, ""+pag);
				pdf.addImage(docPdf, 25,540,"live_shows_logo.png");
				pdf.addImage(docPdf, 923,540,"merchsys_logo.png");
				pos = 500;
			}
			
			docPdf.close();
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MerchSys/warehouse_report.pdf");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}else{
			Toast.makeText(this, R.string.no_products, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void addArtist(){
		DialogAddArtist dialog = new DialogAddArtist();
		dialog.show(getFragmentManager(), "Add Artist");
	}
	
	private void dialogVenue(){
		DialogVenue dialog = new DialogVenue();
		Bundle b = new Bundle();
		b.putString("nombre", venue);
		b.putInt("capacidad", capacidad);
		dialog.setArguments(b);
		dialog.show(getFragmentManager(), "Change Venue");
	}
	
	private void dialogEvent(){
		DialogEvent dialog = new DialogEvent();
		Bundle b = new Bundle();
		b.putString("nombre", nameEvento);
		dialog.setArguments(b);
		dialog.show(getFragmentManager(), "Change Event");
	}
	
	private void setupActionBar(){
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul)));
	}

	private void newProduct(){
		dialog = new DialogAddProduct();
		Bundle params = new Bundle();
		params.putStringArray("artistas", artists);
		params.putStringArray("tipos", toArray(tipos));
		params.putIntArray("imagenes", toArrayI(tipos));
		dialog.setArguments(params);
		dialog.show(getFragmentManager(), "diagProd");
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

	private String[] toArray(ArrayList<TipoProduct> aTipos){
		String[] array = new String[aTipos.size()];

		for(int i = 0 ;i<aTipos.size();i++){
			array[i] = aTipos.get(i).getNombre();
		}

		return array;
	}

	private int[] toArrayI(ArrayList<TipoProduct> aTipos){
		int[] array = new int[aTipos.size()];

		for(int i = 0 ;i<aTipos.size();i++){
			array[i] = aTipos.get(i).getImage();
		}

		return array;
	}

	@Override
	public void articuloNuevo(Product p, int idImag){
		int band = 0;
		for(int i = 0;i<artistas.size();i++){
			if(p.getArtista().contentEquals(artists[i]))
				band=i;
		}

		dbHelper.open();
		long idRow=dbHelper.createProducto(p.getNombre(), p.getTipo(), p.getPath_imagen(), p.getCantidad(),p.getCantidad(), p.getTalla(),0,p.getPrecio(), idEvento, id_artists[band]);
		if(idRow==-1){
			Log.e("BD", "ERROR");
		}else{//PROBAR ESTO
			for(Taxes tax : p.getTaxes()){
				long idRowT = dbHelper.createImpuesto(tax.getName(),"taxes",tax.getAmount());
				if(idRowT!=-1){
					if(dbHelper.createImpuestoProducto((int)idRow, (int)idRowT)==-1){

					}else{

					}
				}else{

				}
			}

			for(Comisiones com : p.getComisiones()){
				long idRowT = dbHelper.createImpuesto(com.getName(), "comision", com.getCantidad(), com.getIva(), com.getTipo());
				if(idRowT!=-1){
					if(dbHelper.createImpuestoProducto((int)idRow, (int)idRowT)==-1){

					}else{

					}
				}else{

				}
			}
			p.setId((int)idRow);
			addProduct(p,null);
			makeToast(R.string.p_anadido);
		}
		dbHelper.close();
	}


	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	/*Se obtiene la direcci��n del alb��m*/
	private File getAlbumDir() {
		File storageDir = null;

		/*Verificamos si el almacenamiento externo esta Montado*/
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			/*Obtenemos el nombre de la nueva Carpeta, donde se guardar��n las imagenes*/
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			/*Comprobamos que no sea nula la variable*/
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {	//Creamos la Carpeta
					Log.i("Directorio: ", ""+storageDir);
					if (! storageDir.exists()){ //Comprobamos que se crear�� realmente
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		albumPath = storageDir.getAbsolutePath();
		Log.i("AlbumPath", albumPath);
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_"; //Nombre de la imagen
		File albumF = getAlbumDir(); //Obtenemos el directorio donde se guardar��
		//		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF); //Guardamos temporalmente
		//la imagen indicando el sufijo, y el album, y el prefijo(Nombre de la imagen)
		File imageF = new File(Environment
				.getExternalStorageDirectory()+"/MerchSys",imageFileName+JPEG_FILE_SUFFIX); //Guardamos temporalmente

		return imageF;
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();	//Llamamos el metodo para crear temporalmente la imagen
		mCurrentPhotoPath = f.getAbsolutePath(); //Obtenemos la ruta

		return f;
	}


	public void takePicture(View view) throws IOException{

		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		Intent mIntent = null;
		if(isPackageExists("com.google.android.camera")){
			mIntent= new Intent();
			mIntent.setPackage("com.google.android.camera");
			mIntent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra("output", Uri.fromFile(setUpPhotoFile()));
		}else{
			mIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra("output", Uri.fromFile(setUpPhotoFile()));
			Log.i("in onMenuItemSelected",
					"Result code = "
							+ Environment.getExternalStorageDirectory());
		}
		startActivityForResult(mIntent, CAMERA_ACTIVITY);
	}

	public boolean isPackageExists(String targetPackage){
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = getPackageManager();        
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if(packageInfo.packageName.equals(targetPackage)) return true;
		}        
		return false;
	}



	private void addProduct(Product p, List<Adicionales> adicionales){
		/*
		if(findViewById(R.id.btnStand).getVisibility()==View.GONE){
			findViewById(R.id.btnStand).setVisibility(View.VISIBLE);
		}
		*/
		if(findViewById(R.id.btnStandB).getVisibility()==View.GONE){
			findViewById(R.id.btnStandB).setVisibility(View.VISIBLE);
		}
		Product item = p;
		item.setId_imagen(R.drawable.ic_launcher);
		if(item.getPath_imagen().contentEquals("")){
			for(TipoProduct prod:tipos){
				if(prod.getNombre().contentEquals(item.getTipo())){
					item.setId_imagen(prod.getImage());
					break;
				}
			}
		}else{
			item.setId_imagen(0);
		}

		if(adicionales!=null){
			item.setAdicional(adicionales);
			Log.i("ADICIONALEs", ""+adicionales.size());
		}

		products.add(item);
		adapter.notifyDataSetChanged();
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("in onActivityResult", "Result code = " + resultCode);
		if (resultCode == -1) {
			switch (requestCode) {
			case CAMERA_ACTIVITY:
				try{
				dialog.setImage(mCurrentPhotoPath);
				}catch (RuntimeException e) {
					dialog_update.setImage(mCurrentPhotoPath);
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		DialogAddAdcional dialogA = new DialogAddAdcional();

		Bundle params = new Bundle();
		params.putString("nombre", products.get(position).getNombre());
		params.putInt("position", position);
		dialogA.setArguments(params);
		dialogA.show(getFragmentManager(), "diagAd");
	}

	@Override
	public void setAdicional(String adicional,int position) {
		Product p = products.get(position);
		if(p.getAdicional().size()<5){
			String nombre ="Adicional"+(p.getAdicional().size()+1); 
			dbHelper.open();
			int adId = (int)dbHelper.createAdicional(nombre, Integer.parseInt(adicional),  p.getId());
			if(adId == -1){
				makeToast(R.string.error);
			}else{
				p.setAdicional(adId,nombre, Integer.parseInt(adicional), p.getId());
				dbHelper.updateProducto(p.getId(), p.getTotalCantidad()+Integer.parseInt(adicional),  p.getCantidad()+Integer.parseInt(adicional));
				p.setTotalCantidad(p.getTotalCantidad()+Integer.parseInt(adicional));
				p.setCantidad(p.getCantidad()+Integer.parseInt(adicional));
				Log.i("ADICIONALES", nombre+" "+adicional+" "+p.getId()+" "+p.getArtista()+" "+p.getNombre());
				makeToast(R.string.p_anadido_ad);
			}
			dbHelper.close();
			adapter.notifyDataSetChanged();

		}else
		{
			makeToast(R.string.t_adicionales);
		}
	}

	/******Menu*********/
	public boolean onContextItemSelected(MenuItem aItem) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			/* Get the selected item out of the Adapter by its position. */
			Log.i("AP", "POSITION "+menuInfo.position);

			Product p=(Product)list.getAdapter().getItem(menuInfo.position);
			if(p.getId() == -1){
				products.remove(menuInfo.position);
				adapter.notifyDataSetChanged();
			}else{
				deleteItem(menuInfo.position);
			}
			return true; /* true means: "we handled the event". */

		case CONTEXTMENU_UPDATEITEM:
			updateItem(menuInfo.position);
			return true;

		case CONTEXTMENU_DETALLEITEM:
			showDetails(menuInfo.position);
			return true;

		case CONTEXTMENU_ADDCORTESIA:
			DialogSetCortesia dialogA = new DialogSetCortesia();
			Bundle params = new Bundle();
			params.putString("nombre", products.get(menuInfo.position).getNombre());
			params.putInt("position", menuInfo.position);
			dialogA.setArguments(params);
			dialogA.show(getFragmentManager(), "diagCor");
			return true;
		case CONTEXTMENU_DELETECORTESIA:
			showDeleteCortesias(menuInfo.position);
			return true;
		case CONTEXTMENU_DELETEADDING:
			deleteAdding(menuInfo.position);
			return true;
		}

		return false;
	}
	
	private void deleteItem(final int position){
		final Product p = products.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.alert_delete) +" " + p.getNombre()+ "?");
		builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.open();
				
				ArrayList<Integer> idsStand = new ArrayList<Integer>();
				
				Cursor cursorProds = dbHelper.fetchProductoStand(p.getId());
				if(cursorProds.moveToFirst()){
					do{
						int idProdStand = cursorProds.getInt(0);
						idsStand.add(idProdStand);
					}while(cursorProds.moveToNext());
				}
				
				for(int i = 0; i < idsStand.size(); i++){
					dbHelper.deleteInfoProdStand(idsStand.get(i));
				}
				if(dbHelper.deleteProduct(p.getId())){
					products.remove(position);
					adapter.notifyDataSetChanged();
					Log.i("DB", " eliminado");
					makeToast(R.string.p_eliminado);
				}
				else{
					Log.e("BD", "Error al eliminiar ");
					makeToast(R.string.d_com_err);
				}
				/* Remove it from the list.*/
				dbHelper.close();
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

	private void updateItem(int position){
		Product p = products.get(position);
		dialog_update = new DialogUpdate();
		dialog_update.setProduct(p);
		Bundle params = new Bundle();
		params.putStringArray("artistas", artists);
		params.putStringArray("tipos", toArray(tipos));
		params.putIntArray("imagenes", toArrayI(tipos));
		params.putInt("position", position);
		dialog_update.setArguments(params);
		dialog_update.show(getFragmentManager(), "Actualizar");
	}
	
	private void showDetails(int position){
		Product p = products.get(position);
		DialogDetails dialog = new DialogDetails();
		dialog.setProduct(p);
		dialog.show(getFragmentManager(), "Detalles");
	}

	private void showDeleteCortesias(int position){
		Product product = products.get(position);
		if(product.getCortesias().size() > 0){
			String[] cortesias = new String[product.getCortesias().size()];
			
			for(int i = 0; i < product.getCortesias().size();i++){
				Cortesias cort = product.getCortesias().get(i);
				cortesias[i] = ""+cort.getAmount()+" "+cort.getTipo();
			}
			
			DialogDeleteCortesia dialogC = new DialogDeleteCortesia();
			Bundle b = new Bundle();
			b.putStringArray("comps", cortesias);
			b.putInt("pos", position);
			dialogC.setArguments(b);
			dialogC.show(getFragmentManager(), "diagCant");
		}else{
			makeToast(R.string.no_cortesias);
		}
	}
	private void deleteAdding(final int position){
		final Product p = products.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.alert_delete_adding));
		builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Adicionales ad = p.getAdicionalA(p.getAdicionalSize()-1);
				
				if(ad.getCantidad() <= p.getCantidad()){
					dbHelper.open();
					
					dbHelper.updateProducto(p.getId(), p.getTotalCantidad() - ad.getCantidad(), p.getCantidad() - ad.getCantidad());
					dbHelper.deleteAdicional(ad.getId());
					
					p.setTotalCantidad(p.getTotalCantidad() - ad.getCantidad());
					p.setCantidad(p.getCantidad() - ad.getCantidad());
					p.setAdicional(p.getAdicional().subList(0, p.getAdicionalSize()-1));
					dbHelper.close();
					adapter.notifyDataSetChanged();
					makeToast(R.string.a_eliminado);
				}else{
					makeToast(R.string.err_cantidad);
				}
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
	
	public Product getProduct(Product p){
		return p;
	}

	private void makeToast(int resource){
		Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
	}

	public void toStand(View v){
		Intent i = new Intent(this,StandActivity.class);
		i.putExtra("evento", nameEvento);
		i.putExtra("fecha", idFecha);
		startActivity(i);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}

	public void makeToastDialog(int msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setCortesia(Cortesias cortesia, int position) {
		Product p = products.get(position);
		Log.i("COR", "Set cortesia "+p.getNombre()+" "+cortesia);
		int total=p.getCantidad()-cortesia.getAmount();
		if(total >= 0){
			dbHelper.open();
			int cortId = (int)dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getId(),0);
			if(cortId >=0){
				int cantidad = total;
				cortesia.setId(cortId);
				p.addCortesia(cortesia);
				p.setCantidad(cantidad);
				dbHelper.updateProducto(p.getId(), cantidad);
				adapter.notifyDataSetChanged();
			}
			dbHelper.close();
			Toast.makeText(this, R.string.p_anadido_cor, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, R.string.err_cort_noval, Toast.LENGTH_SHORT).show();
		}
	}

	private void setXML(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = getApplicationContext().getAssets().open("productos.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);

			parseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		int eventType = parser.getEventType();
		TipoProduct currentLocal = null;


		while (eventType != XmlPullParser.END_DOCUMENT){
			String name = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.contentEquals("item")){
					currentLocal = new TipoProduct();
				} else if (currentLocal != null){
					if (name.contentEquals("title")){
						currentLocal.setNombre(parser.nextText());
					} else if (name.contentEquals("image")){
						int idr =getResources().getIdentifier(parser.nextText(), "drawable", getPackageName());
						if(idr==0){
							currentLocal.setImage(R.drawable.ic_launcher);
						}
						else{
							currentLocal.setImage(idr);
						}
					}  
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && currentLocal != null){
					tipos.add(currentLocal);
				} 
			}
			eventType = parser.next();
		}
	}


	public class TipoProduct{
		String name;
		int image;

		public TipoProduct(){}
		public TipoProduct(String name, int image){
			this.name=name;
			this.image=image;
		}
		public TipoProduct(String name){
			this.name=name;
		}
		public void setNombre(String nombre){
			this.name=nombre;
		}
		public void setImage(int imag){this.image=imag;}
		public String getNombre(){ return name;}
		public int getImage(){return image;}
	}


	@Override
	public void articuloActualizado(Product p, int position) {
		dbHelper.open();
		int band = 0;
		for(int i = 0;i<artistas.size();i++){
			if(p.getArtista().contentEquals(artists[i]))
				band=i;
		}
		products.get(position).setArtista(p.getArtista());
		products.get(position).setNombre(p.getNombre());
		products.get(position).setPrecio(p.getPrecio());
		products.get(position).setTotalCantidad(p.getTotalCantidad());
		products.get(position).setCantidad(p.getCantidad());
		products.get(position).setTipo(p.getTipo());
		if(!p.getPath_imagen().contentEquals(""))
			products.get(position).setPath_imagen(p.getPath_imagen());
		else
			products.get(position).setId_imagen(p.getId_imagen());
		
		Cursor cursorCom = dbHelper.fetchProductImpuestoProd(p.getId());
		if(cursorCom.moveToFirst()){
			do{
				Cursor c = dbHelper.fetchImpuestos(cursorCom.getLong(1));
				if(c.moveToFirst()){
					dbHelper.deleteComision(c.getLong(0));
				}
			}while(cursorCom.moveToNext());
		}
		dbHelper.deleteTaxesProd(p.getId());
		if(dbHelper.updateProducto((long)p.getId(), p.getNombre(), p.getTipo(), p.getPath_imagen(), p.getCantidad(),  p.getTotalCantidad(), p.getPrecio(), id_artists[band])){
			for(Taxes tax : p.getTaxes()){
				long idRowT = dbHelper.createImpuesto(tax.getName(),"taxes",tax.getAmount());
				if(idRowT!=-1){
					if(dbHelper.createImpuestoProducto(p.getId(), (int)idRowT)==-1){

					}else{

					}
				}else{

				}
			}

			for(Comisiones com : p.getComisiones()){
				long idRowT = dbHelper.createImpuesto(com.getName(), "comision", com.getCantidad(), com.getIva(), com.getTipo());
				if(idRowT!=-1){
					if(dbHelper.createImpuestoProducto(p.getId(), (int)idRowT)==-1){

					}else{

					}
				}else{

				}
			}
			adapter.notifyDataSetChanged();
			makeToast(R.string.update_exitoso);
		}else{
			makeToast(R.string.update_noexitoso);
		}

		dbHelper.close();

	}
	
	@Override
	public void setNewArtist(String artistName) {
		for(int i = 0 ; i < id_artists.length; i++){
			if(artistas.containsValue(artistName)){
				makeToast(R.string.e_artista);
				return;
			}
		}
		dbHelper.open();
		long newId = dbHelper.createArtista(artistName,idEvento);
		if(newId != -1){
			int[] resultInt = Arrays.copyOf(id_artists, id_artists.length +1);
			resultInt[id_artists.length] = (int) newId;
			id_artists = resultInt;
		    String[] resultString = Arrays.copyOf(artists, artists.length +1);
		    resultString[artists.length] = artistName;
		    artists = resultString;
		    artistas.put((int) newId, artistName);
			makeToast(R.string.a_anadido);
		}else{
			makeToast(R.string.error);
		}
	}
	
	private double truncate(double num){
		num *= 100;
		int aux = (int)num;
		double res = (double)aux / 100;
		return res;
	}

	@Override
	public void onDialogPositive(boolean[] arr,int pos) {
		Product p = products.get(pos);
		List<Cortesias> corts = p.getCortesias();
		List<Cortesias> sobrantes = new ArrayList<Cortesias>();
		dbHelper.open();
		for(int i = 0; i < arr.length ;i++){
			if(arr[i]){
				dbHelper.deleteCortesiasStand(corts.get(i).getId());
				dbHelper.updateProducto(p.getId(), p.getCantidad()+corts.get(i).getAmount());
				p.setCantidad(p.getCantidad()+corts.get(i).getAmount());
			}else{
				sobrantes.add(corts.get(i));
			}
		}
		p.setCortesias(sobrantes);
		makeToast(R.string.c_eliminado);
		dbHelper.close();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void changeVenue(String nombre, int cap) {
		if(capacidad > 0){
			dbHelper.open();
			if(dbHelper.updateEvento(idEvento, nombre, cap)){
				capacidad = cap;
				venue = nombre;
				makeToast(R.string.v_actualizado);
			}else{
				makeToast(R.string.error);
			}
			dbHelper.close();
		}else{
			makeToast(R.string.v_noval);
		}
		
	}

	@Override
	public void changeEvent(String nombre) {
		dbHelper.open();
		if(dbHelper.updateEvento(idEvento, nombre)){
			nameEvento = nombre;
			TextView txtEvento = (TextView)findViewById(R.id.txtArtistaP);
			txtEvento.setText(nameEvento);
			makeToast(R.string.e_actualizado);
		}else{
			makeToast(R.string.error);
		}
		dbHelper.close();
		
	}

}
