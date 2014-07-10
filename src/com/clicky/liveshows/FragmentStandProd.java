package com.clicky.liveshows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.clicky.liveshows.adapters.AdapterStandProduct;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Stand;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentStandProd extends Fragment {

	LinearLayout empty;
	TextView txtNombre;
	TextView txtEncargado;
	TextView txtComision;
	ListView list;
	DBAdapter db;
	Stand s;
	List<Product> items;
	ArrayList<TipoProduct> tipos;
	AdapterStandProduct adapter;
	private OnNewAdicional listener;
	private OnNewCortesia listenerCortesia;

	int idFecha;
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_CHANGECOMISION = 1;
	protected static final int CONTEXTMENU_DETALLEITEM =2;
	protected static final int CONTEXTMENU_ADDCORTESIA = 3;
	
	protected static final int PRODUCTOS = 0;

	public interface OnNewAdicional{
		public void onSetAdicional(Product product,int position,Stand stand);
	}

	public interface OnNewCortesia{
		public void onSetCortesia(Product product,int position,Stand stand);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(OnNewAdicional)activity;
			listenerCortesia=(OnNewCortesia)activity;
		}catch(ClassCastException e){}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DBAdapter(getActivity());
		items = new ArrayList<Product>(); 
		tipos = new ArrayList<TipoProduct>();
		setXML();
		adapter = new AdapterStandProduct(getActivity(), R.layout.item_producto_stand, items);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_stand_prod, container, false); 
		
		empty = (LinearLayout)v.findViewById(R.id.vacio);
		txtEncargado = (TextView)v.findViewById(R.id.txtEncargado);
		txtComision = (TextView)v.findViewById(R.id.txtComision);
		list=(ListView)v.findViewById(R.id.listStandProd);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				listener.onSetAdicional(items.get(position),position,s);
				// TODO Auto-generated method stub

			}
		});

		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.add(0, CONTEXTMENU_CHANGECOMISION,1,R.string.m_actualizar);
				menu.add(0, CONTEXTMENU_DELETEITEM,0,R.string.m_eliminar);
				menu.add(0, CONTEXTMENU_DETALLEITEM, 2, R.string.m_detalles);
				menu.add(0, CONTEXTMENU_ADDCORTESIA, 3, R.string.m_cortesia);
			//	menu.add(R.string.title_menu);
			}
		});

		Button btnAgregar = (Button)getView().findViewById(R.id.btnProdnuevo);
		btnAgregar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogProd();
			}
		});

		Button btnCierre = (Button)getView().findViewById(R.id.btnCierreStand);
		btnCierre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),ActivityCierreStand.class);
				Bundle b = new Bundle();
				b.putInt("id_stand",(int)s.getId());
				b.putInt("fecha", idFecha);
				i.putExtra("extra", b);
				startActivity(i);
				getActivity().overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == PRODUCTOS) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setStand(s,idFecha);
	        }
	    }
	}
	
	public void setStand(Stand s,int idFecha){
		this.idFecha = idFecha;
		this.s=s;
		empty.setVisibility(View.GONE);
		Comisiones com = s.getComision();
		txtEncargado.setText(s.getEncargado());
		txtComision.setText(""+s.getComision().getCantidad()+" "+com.getIva());//CORREGIR AQUI
		items.clear();
		adapter.notifyDataSetChanged();
		db.open();
		//int idCom = (int)db.createImpuesto(com.getName(), "comision", com.getCantidad(), com.getIva(), com.getTipo());
		//if(idCom == -1){

		//}else{
		//	com.setId(idCom);
			List<Comisiones> comisiones = new ArrayList<Comisiones>();
			comisiones.add(com);

			Cursor c  = db.fetchStandProduct(s.getId());
			if(c.moveToFirst()){
				do{
					Product p = new Product();  //Se obtiene la cantidad de prod en el stand, nombre,tipo, talla y precio
					int cantidad = c.getInt(1);
					int idProd = c.getInt(3);
					p.setCantidadStand(cantidad);
					p.setId(idProd);
					Cursor cursor = db.fetchProducto(idProd);
					if(cursor.moveToFirst()){
						do{
							//int id = cursor.getInt(0);
							String nombre = cursor.getString(1);
							String tipo = cursor.getString(2);
							String foto = cursor.getString(3);
							String talla = cursor.getString(6);
							String precio = cursor.getString(7);
							int cantidadTotal = cursor.getInt(4);
							//db.createImpuestoProducto(id, idCom);
							p.setNombre(nombre);
							p.setPath_imagen(foto);
							p.setTipo(tipo);
							p.setTalla(talla);
							p.setPrecio(precio);
							p.setCantidad(cantidadTotal);
							p.setComisiones(comisiones);
							p = verifyImage(p);
						}while(cursor.moveToNext());
					}
					items.add(p);
					Log.i("STAND_PROD", p.getNombre()+" "+p.getTipo()+" "+p.getTalla()+" "+cantidad);
				}while(c.moveToNext());
				adapter.notifyDataSetChanged();
			}else{
				items.clear();
				adapter.notifyDataSetChanged();
			}

			c.close();
		//}
		db.close();
	}

	private Product verifyImage(Product item){
		if(item.getPath_imagen().contentEquals("")){
			for(TipoProduct prod:tipos){
				if(prod.getNombre().contentEquals(item.getTipo())){
					item.setId_imagen(prod.getImage());
				}
			}
		}else{
			item.setId_imagen(0);
		}
		return item;
	}
	public void setNewCantidad(int cantidad, int position){
		items.get(position).setCantidad(cantidad);
	}


	private void dialogProd(){
		if(s!=null){
			Intent i = new Intent(getActivity(),ActivityAgregarProductos.class);
			Bundle b = new Bundle();
			b.putString("nombre", s.getName());
			b.putInt("id",(int)s.getId());
			b.putInt("fecha", idFecha);
			i.putExtra("stand",b);
			startActivityForResult(i, PRODUCTOS);
			getActivity().overridePendingTransition(R.anim.start_enter_anim, R.anim.start_exit_anim);
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


			db.open();
			if(db.deleteProductStand(s.getId(), p.getId(),p.getCantidad() + p.getCantidadStand())){
				items.remove(menuInfo.position);
				adapter.notifyDataSetChanged();
				Log.i("DB", " eliminado");
			}
			else
				Log.e("BD", "Error al eliminiar ");
			/* Remove it from the list.*/
			db.close();

			return true; /* true means: "we handled the event". */

		case CONTEXTMENU_DETALLEITEM:

			
		case CONTEXTMENU_CHANGECOMISION:
			return true;
		case CONTEXTMENU_ADDCORTESIA:
			listenerCortesia.onSetCortesia((Product)list.getAdapter().getItem(menuInfo.position), menuInfo.position, s);
			return true;
		}

		return false;
	}
	
	private void setXML(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = getActivity().getApplicationContext().getAssets().open("productos.xml");
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
						int idr =getResources().getIdentifier(parser.nextText(), "drawable", getActivity().getPackageName());
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
