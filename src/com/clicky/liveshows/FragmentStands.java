package com.clicky.liveshows;

import java.util.ArrayList;
import java.util.List;

import com.clicky.liveshows.adapters.AdapterStand;
import com.clicky.liveshows.database.DBAdapter;
import com.clicky.liveshows.utils.Comisiones;
import com.clicky.liveshows.utils.Product;
import com.clicky.liveshows.utils.Stand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentStands extends Fragment {
	
	private List<Stand> list;
	private ListView lstListado;
	AdapterStand a;
	private onStandSelected listener;
	private onFragmentCreate listenerC;
	DBAdapter dbHelper;

	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_UPDATEITEM = 1;
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			listener=(onStandSelected)activity;	
			listenerC = (onFragmentCreate)activity;
		}catch(ClassCastException e){}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			                 ViewGroup container, 
			                 Bundle savedInstanceState) {
		
		list= new ArrayList<Stand>();
		return inflater.inflate(R.layout.fragment_stands, container, false);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

		dbHelper = new DBAdapter(getActivity());
		
		lstListado = (ListView)getView().findViewById(R.id.listStands);
		a = new AdapterStand(getActivity(), R.layout.item_spinner_drop, list);
		lstListado.setAdapter(a);
		lstListado.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
				if (listener!=null) {
					listener.onStandSeleccionado(
							(Stand)lstListado.getAdapter().getItem(pos));
					view.setSelected(true);
				}
			}
		});
		
		listenerC.onGetData();
		
		lstListado.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				//	menu.add(R.string.title_menu);
				menu.add(0, CONTEXTMENU_UPDATEITEM,1,R.string.m_actualizar);
				menu.add(0, CONTEXTMENU_DELETEITEM,0,R.string.m_eliminar);
			}
		});
	}

	public boolean onContextItemSelected(MenuItem aItem) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			deleteItem(menuInfo.position);
			return true; /* true means: "we handled the event". */

		case CONTEXTMENU_UPDATEITEM:
			updateItem(menuInfo.position);
			return true;
		}

		return false;
	}
	
	private void deleteItem(final int position){
		final Stand s = list.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getResources().getString(R.string.alert_delete) +" " + s.getName()+ "?");
		builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.open();
				
				ArrayList<Integer> idsStand = new ArrayList<Integer>();
				
				Cursor cursorProds = dbHelper.fetchStandProduct(s.getId());
				if(cursorProds.moveToFirst()){
					do{
						int idProdStand = cursorProds.getInt(0);
						idsStand.add(idProdStand);
					}while(cursorProds.moveToNext());
				}
				
				for(int i = 0; i < idsStand.size(); i++){
					dbHelper.deleteInfoProdStand(idsStand.get(i));
				}
				
				if(dbHelper.deleteStand(s.getId())){
					list.remove(position);
					a.notifyDataSetChanged();
					Log.i("DB", " eliminado");
					Toast.makeText(getActivity(),R.string.s_eliminado,Toast.LENGTH_SHORT).show();
				}
				else{
					Log.e("BD", "Error al eliminiar ");
					Toast.makeText(getActivity(),R.string.d_com_err,Toast.LENGTH_SHORT).show();
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
		DialogUpdateStand dialog = new DialogUpdateStand();
		Stand stand = list.get(position);
		Comisiones com = stand.getComision();
		Bundle b = new Bundle();
		b.putLong("idStand", stand.getId());
		b.putString("nombre", stand.getName());
		b.putString("encargado", stand.getEncargado());
		b.putString("tipo", com.getTipo());
		b.putString("iva", com.getIva());
		b.putInt("comision", com.getCantidad());
		dialog.setArguments(b);
		dialog.show(getFragmentManager(), "Update Stand");
	}
	
	public void setStand(Stand stand){
		list.add(stand);
		a.notifyDataSetChanged();
	}
	public interface onStandSelected {
		public void onStandSeleccionado(Stand s);
	}
	
	public interface onFragmentCreate{
		public void onGetData();
	}
	
	public void reloadData(){
		list.clear();
		listenerC.onGetData();
	}


}
