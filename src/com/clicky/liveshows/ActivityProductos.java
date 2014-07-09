package com.clicky.liveshows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.clicky.liveshows.DialogAddProduct.OnDialogListener;
import com.clicky.liveshows.DialogSetCortesia.OnCortesiaListener;
import com.clicky.liveshows.adapters.AdapterProduct;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Adicionales;
import com.clicky.liveshows.utils.AlbumStorageDirFactory;
import com.clicky.liveshows.utils.BaseAlbumDirFactory;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Cortesias;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Taxes;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
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

public class ActivityProductos extends Activity implements OnDialogListener, OnItemClickListener,OnAdicionalListener,OnCortesiaListener {

	private DBAdapter dbHelper;
	private Cursor cursor;
	private String[] artists;
	private int[] id_artists;
	List<Product> products;
	AdapterProduct adapter;
	String nameEvento = null;
	ArrayList<TipoProduct> tipos;
	int idEvento;
	HashMap<Integer, String> artistas;
	DialogAddProduct dialog;
	protected static final int CAMERA_ACTIVITY = 100;
	ListView list;
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_UPDATEITEM = 1;
	protected static final int CONTEXTMENU_DETALLEITEM =2;
	protected static final int CONTEXTMENU_ADDCORTESIA = 3;

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
		
		list = (ListView)findViewById(R.id.listArticulos);
		adapter = new AdapterProduct(this, R.layout.item_lista_producto, products);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				//	menu.add(R.string.title_menu);
				menu.add(0, CONTEXTMENU_UPDATEITEM,1,R.string.m_actualizar);
				menu.add(0, CONTEXTMENU_DELETEITEM,0,R.string.m_eliminar);
				menu.add(0, CONTEXTMENU_DETALLEITEM, 2, R.string.m_detalles);
				menu.add(0, CONTEXTMENU_ADDCORTESIA, 3, R.string.m_cortesia);
			}
		});

		dbHelper = new DBAdapter(this);
		dbHelper.open();
		cursor = dbHelper.fetchAllEvento();
		
		if(cursor.moveToFirst()){
			do{
				idEvento = cursor.getInt(0);
				nameEvento = cursor.getString(1);
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
		if(cursor1.moveToFirst()){

			do{
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
						int cantidadA = cursorA.getInt(1);
						String nomA = cursorA.getString(2);
						a.add(new Adicionales(nomA, cantidadA, id));
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
						String tipoC = cursorCortesias.getString(1);
						int cantidadC =cursorCortesias.getInt(2);
						cortesias.add(new Cortesias(tipoC, cantidadC));
					}while(cursorCortesias.moveToNext());
				}


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
							String nombreI = cursorPI.getString(1);
							String porcentaje = cursorPI.getString(2);
							String tipoImpuesto = cursorPI.getString(3);
							if(tipoImpuesto.contentEquals("comision")){
								String iva = cursorPI.getString(4);
								String tipoPeso = cursorPI.getString(5);
								list_com.add(new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso));
							}else{

								list_tax.add(new Taxes(nombreI, Integer.parseInt(porcentaje)));
							}
						}while(cursorPI.moveToNext());	
					}
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
			cursorProd.close();
			
		}

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
		
		DateFormat df= DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		txtFecha.setText(df.format(dates.get(0)));
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String div = prefs.getString("moneda", "");
		float divisa = Float.parseFloat(prefs.getString("divisa", "0"));
		int comision = Integer.parseInt(prefs.getString("comision_tarjeta", "0"));

		TextView txtDivisa = (TextView)findViewById(R.id.txtDivisas);
		TextView txtTarCom = (TextView)findViewById(R.id.txtTarjetaCom);

		if(comision<=0){

			txtTarCom.setText(R.string.textcom);
		}else{
			txtTarCom.setText("Comisión Tarjeta: "+comision);
		}

		if(div.contentEquals("Peso")){
			txtDivisa.setText(div);
		}else{
			if(divisa <= 0){
				txtDivisa.setText(R.string.textdivisa);
			}else{
				txtDivisa.setText(div+" = "+divisa+" USD");
			}
		}

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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings(){
		Intent i = new Intent(this,Settings.class);
		startActivity(i);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
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
				.getExternalStorageDirectory(),imageFileName+JPEG_FILE_SUFFIX); //Guardamos temporalmente

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
		if(findViewById(R.id.btnStand).getVisibility()==View.GONE){
			findViewById(R.id.btnStand).setVisibility(View.VISIBLE);
		}
		Product item = p;
		item.setId_imagen(R.drawable.werelupe);
		if(item.getPath_imagen().contentEquals("")){
			for(TipoProduct prod:tipos){
				if(prod.getNombre().contentEquals(item.getTipo())){
					item.setId_imagen(prod.getImage());
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
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("in onActivityResult", "Result code = " + resultCode);
		if (resultCode == -1) {
			switch (requestCode) {
			case CAMERA_ACTIVITY:
				//do your stuff here, i am just calling the path of stored image
				//				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				//				String imageFileName = JPEG_FILE_PREFIX + timeStamp +JPEG_FILE_SUFFIX; //Nombre de la imagen
				//				//		Log.i("ALBUM", ""+getAlbumDir().getAbsolutePath());
				//				Log.i("ALBUM", ""+imageFileName);
				//				String filePath = Environment.getExternalStorageDirectory()+imageFileName;
				//				Log.i("ALBUM", ""+filePath);
				dialog.setImage(mCurrentPhotoPath);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		DialogAddAdcional dialogA = new DialogAddAdcional();

		Bundle params = new Bundle();
		params.putString("nombre", products.get(position).getNombre());
		params.putInt("position", position);
		dialogA.setArguments(params);
		dialogA.show(getFragmentManager(), "diagAd");
	}

	@Override
	public void setAdicional(String adicional,int position) {
		// TODO Auto-generated method stub
		Product p = products.get(position);
		if(p.getAdicional().size()<5){
			String nombre ="Adicional"+(p.getAdicional().size()+1); 
			p.setAdicional(nombre, Integer.parseInt(adicional), p.getId());
			dbHelper.open();
			if(dbHelper.createAdicional(nombre, Integer.parseInt(adicional),  p.getId())==-1){
				makeToast(R.string.error);
			}else{
				dbHelper.updateProducto(p.getId(), p.getTotalCantidad()+Integer.parseInt(adicional),  p.getCantidad()+Integer.parseInt(adicional));
				p.setTotalCantidad(p.getTotalCantidad()+Integer.parseInt(adicional));
				Log.i("ADICIONALES", nombre+" "+adicional+" "+p.getId()+" "+p.getArtista()+" "+p.getNombre());
			}
			dbHelper.close();

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
			if(p.getId()==-1){
				products.remove(menuInfo.position);
				adapter.notifyDataSetChanged();
			}else{
				dbHelper.open();
				products.remove(menuInfo.position);

				if(dbHelper.deleteProduct(p.getId())){
					adapter.notifyDataSetChanged();
					Log.i("DB", " eliminado");
				}
				else
					Log.e("BD", "Error al eliminiar ");
				/* Remove it from the list.*/
				dbHelper.close();
			}
			return true; /* true means: "we handled the event". */

		case CONTEXTMENU_UPDATEITEM:
			//	showDetails(menuInfo.position);
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

		}

		return false;
	}

	private void showDetails(int position){
		Product p = products.get(position);

		DialogDetails dialog = new DialogDetails();
		dialog.setProduct(p);
		dialog.show(getFragmentManager(), "Detalles");
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
		startActivity(i);
		overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
	}
	
	public void makeToastDialog(int msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

			if(dbHelper.createCortesia(cortesia.getTipo(), cortesia.getAmount(), p.getId(),0)>=0){
				int cantidad = total;
				p.setCantidad(cantidad);
				dbHelper.updateProducto(p.getId(), cantidad);
				adapter.notifyDataSetChanged();
			}
		}else{
			
		}
		dbHelper.close();
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
			// TODO Auto-generated catch block
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
							currentLocal.setImage(R.drawable.werelupe);
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

}
