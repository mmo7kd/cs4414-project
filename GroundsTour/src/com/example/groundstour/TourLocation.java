package com.example.groundstour;

import java.util.ArrayList;

import android.location.Location;

import sofia.widget.ProvidesTitle;

public class TourLocation {

	String locationName;
	Location location;
	Double longitude;
	Double latitude;
	String imageRes;
	ArrayList<String> locationFacts;
	
	// creates a dummy TourLocation
	public TourLocation()
	{
		this.locationName = "No Destination";
		this.locationFacts = new ArrayList<String>();
	}
	
	public TourLocation(
			String locationName,
			Double latitude,
			Double longitude,
			String imageRes,
			ArrayList<String> LocationFacts)
	{
		this.locationName = locationName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationFacts = LocationFacts;
		this.imageRes = imageRes;
		location = new Location("default");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
	}
	
	public TourLocation(TourLocation loc)
	{
		this.locationName = loc.getLocationName();
		this.latitude = loc.getLatitude();
		this.longitude = loc.getLongitude();
		this.locationFacts = loc.getLocationFacts();
		this.imageRes = loc.getImageId();
		location = new Location("default");
		location.setLatitude(this.latitude);
		location.setLongitude(this.longitude);
	}
	
	public String getImageId()
	{
		return imageRes;
	}
	
	public Location getLocation()
	{
		return location;
	}

	public Double getLatitude()
	{
		return latitude;
	}

	public Double getLongitude()
	{
		return longitude;
	}
	
	public void addLocationFact(String newFact)
	{
		locationFacts.add(newFact);
	}
	
	public ArrayList<String> getLocationFacts() {
		return locationFacts;
	}

	public void setLocationFacts(ArrayList<String> LocationFacts) {
		this.locationFacts = LocationFacts;
	}

	@ProvidesTitle
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	public String getCurrentFact(int index)
	{
		return locationFacts.get(index);
	}
	
	@Override
	public String toString()
	{
		return "Name: " + getLocationName() + ", Lat: " + getLatitude() + ", Long: " + getLongitude() + ".";
	}
	
	// TourLocations are considered equal if their "locationName" fields are equal
	@Override
	public boolean equals(Object obj)
	{
		if ( !(obj instanceof TourLocation) )
		{
			return false;
		}
		else
		{
			String objName = ((TourLocation) obj).getLocationName();
			return this.getLocationName().equals(objName);
		}
	}
	
}
