package com.clicky.liveshows;

import java.util.ArrayList;
import java.util.List;

import com.clicky.liveshows.adapters.AdapterStand;
import com.clicky.liveshows.utils.Stand;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentStands extends Fragment {
	
	private List<Stand> list;

	private ListView lstListado;
	AdapterStand a;
	private onStandSelected listener;
	private onFragmentCreate listenerC;

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


}
