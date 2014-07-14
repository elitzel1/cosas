package com.clicky.liveshows;

import java.util.ArrayList;
import java.util.List;

import com.clicky.liveshows.adapters.AdapterStandProduct;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Stand;
import com.clicky.liveshows.utils.Taxes;

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
	AdapterStandProduct adapter;
	private OnNewAdicional listener;
	private OnNewCortesia listenerCortesia;

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
				menu.add(0, CONTEXTMENU_CHANGECOMISION,1,R.string.long_change);
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
	        	setStand(s);
	        }
	    }
	}
	
	public void setStand(Stand s){
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
							String talla = cursor.getString(6);
							String precio = cursor.getString(7);
							int cantidadTotal = cursor.getInt(4);
							//db.createImpuestoProducto(id, idCom);
							List<Comisiones> list_com=null;
							List<Taxes> list_tax=null;
							list_com = new ArrayList<Comisiones>();
							list_tax = new ArrayList<Taxes>();
							List<Integer> id_impuestos = new ArrayList<Integer>();
							Cursor cursorI=db.fetchProductImpuestoProd(idProd);
							if(cursorI.moveToNext()){
								do{
									id_impuestos.add(cursorI.getInt(1));
								}while(cursorI.moveToNext());
							}
							cursorI.close();

							for(int j=0;j<id_impuestos.size();j++){
								Cursor cursorPI = db.fetchImpuestos(id_impuestos.get(j));

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
											Comisiones comi = new Comisiones(nombreI, Integer.parseInt(porcentaje), iva, tipoPeso);
											comi.setId(idTaxes);
											list_com.add(comi);
										}else{
											Taxes tax = new Taxes(nombreI, Integer.parseInt(porcentaje));
											tax.setId(idTaxes);
											list_tax.add(tax);
										}
									}while(cursorPI.moveToNext());	
								}
							}
							p.setNombre(nombre);
							p.setTipo(tipo);
							p.setTalla(talla);
							p.setPrecio(precio);
							p.setCantidad(cantidadTotal);
							p.setComisiones(comisiones);
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

	public void setNewCantidad(int cantidad, int position){
		items.get(position).setCantidad(cantidad);
	}


	private void dialogProd(){
		if(s!=null){
			Intent i = new Intent(getActivity(),ActivityAgregarProductos.class);
			Bundle b = new Bundle();
			b.putString("nombre", s.getName());
			b.putInt("id",(int)s.getId());
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
					showDetails();
			return true;
		case CONTEXTMENU_CHANGECOMISION:
			return true;
		case CONTEXTMENU_ADDCORTESIA:
			listenerCortesia.onSetCortesia((Product)list.getAdapter().getItem(menuInfo.position), menuInfo.position, s);
			return true;
		}

		return false;
	}

	private void showDetails(){
		
	}
}
