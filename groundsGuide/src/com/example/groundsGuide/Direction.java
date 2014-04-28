package com.example.groundsGuide;

import java.util.HashMap;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

public class Direction {

	List<LatLng> coordinates; 
	
	String distance; 
	int meters; //distance in meters
	
	String duration; 
	int seconds; //duration in seconds
	
	String instructions; 
	
	
	public Direction(){
		this.coordinates = null; 
		this.distance = "";
		this.duration = "";
		this.instructions = ""; 
		this.meters = 0; 
		this.seconds = 0; 
	}
	
	public Direction(List<LatLng> coord, String dist, String dur, String inst, int secs, int met){
		this.coordinates = coord;
		this.distance = dist; 
		this.duration = dur; 
		this.instructions = inst; 
		this.seconds = secs; 
		this.meters = met; 
	}
	
	
	public Direction(Direction d){
		this.coordinates = d.coordinates; 
		this.distance = d.distance; 
		this.duration = d.duration; 
		this.instructions = d.instructions; 
		this.meters = d.meters;
		this.seconds = d.seconds; 
		
	}
}
