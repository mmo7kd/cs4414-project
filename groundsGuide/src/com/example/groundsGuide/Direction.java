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
	LatLng start; 
	
	String place; 
	
	
	public Direction(){
		this.coordinates = null; 
		this.distance = "";
		this.duration = "";
		this.instructions = ""; 
		this.meters = 0; 
		this.seconds = 0; 
		this.start = null;
		this.place = "";
	}
	
	public Direction(List<LatLng> coord, String dist, String dur, String inst, int secs, int met, String Place){
		this.coordinates = coord;
		this.distance = dist; 
		this.duration = dur; 
		this.instructions = inst; 
		this.seconds = secs; 
		this.meters = met; 
		this.start = coordinates.get(0); 
		this.place = Place; 
		this.rewriteInstructions(); 
	}
	
	
	public Direction(Direction d){
		this.coordinates = d.coordinates; 
		this.distance = d.distance; 
		this.duration = d.duration; 
		this.instructions = d.instructions; 
		this.meters = d.meters;
		this.seconds = d.seconds; 
		this.start = d.start; 
		this.place = d.place; 
	}
	
	public String toString(){
		
		return this.instructions + "\n\r length: " + this.distance + "\n\r time: " + this.duration; 
	}
	
	public void rewriteInstructions(){
		if(this.instructions.contains("Destination"))
			this.instructions = this.instructions.replaceFirst("Destination", ", Destination");
		if(this.instructions.contains("Take"))
			this.instructions = this.instructions.replaceFirst("Take", ", Take");
		
		if(this.instructions.contains("Turn")){
			if(this.instructions.equals("Turn right") || this.instructions.equals("Turn left")) {
				String addition = " at " + this.place; 
				this.instructions += addition;
			}
			else if (this.instructions.contains("right toward"))
				this.instructions = "Turn right near " + this.place;
			
			else if (this.instructions.contains("left toward"))
				this.instructions = "Turn left near " + this.place; 
		}
	}
	
	
}