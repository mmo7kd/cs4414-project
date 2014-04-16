package com.example.groundstour;

import sofia.app.Screen;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TourMap extends Screen { // screen

	GoogleMap map;
	GoogleMapOptions options;
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.maptest);
//		setUpMap();
//	}
	
	public void initialize(Double latitude, Double longitude)
	{
//		setContentView(R.layout.tourmap);
		setUpMap(latitude,longitude);
		// set up a listener so the user location will change dynamically
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection for the menu
		switch (item.getItemId()) {
			case R.id.previous_screen:
				finish(false);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setUpMap(Double latitude, Double longitude) {
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
	        map.setMyLocationEnabled (true);
	        updateMap(latitude,longitude);
	    }
	}

	public void updateMap(Double latitude, Double longitude)
	{
		if (map != null)
		{
			CameraUpdate update = CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude) );
			map.moveCamera(update);
			CameraUpdate update2 = CameraUpdateFactory.zoomBy(14);
			map.moveCamera(update2);
			setMarker(latitude, longitude);
		}
		else
		{
			// HAVE A MESSAGE SAYING THAT THE MAP WAS NOT ACCESSIBLE
		}
	}
	
	public void setMarker(Double latitude, Double longitude)
	{
		MarkerOptions marker = new MarkerOptions();
		marker.position( new LatLng(latitude,longitude) );
		marker.title("You are here.");
		map.addMarker( marker );
	}
}
