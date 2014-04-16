package com.example.groundstour;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.Bundle;

public class MapTest extends Activity { // screen

	GoogleMap map;
	GoogleMapOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptest);
		setUpMap();
		updateMap(1.0,1.0);
	}
	
	public void setUpMap() {
	    if (map == null)
	    {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        options = new GoogleMapOptions();
	        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
	        .compassEnabled(false)
	        .rotateGesturesEnabled(false)
	        .tiltGesturesEnabled(false);
	        map.setIndoorEnabled(true);
	    }
	}

	public void updateMap(Double latitude, Double longitude)
	{
		if (map != null)
		{
			CameraUpdate update = CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude) );
			map.moveCamera(update);
		}
	}
}
