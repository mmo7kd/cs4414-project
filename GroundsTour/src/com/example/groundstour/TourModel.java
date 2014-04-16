package com.example.groundstour;

import java.util.ArrayList;
import java.util.Arrays;

import android.location.Location;

public class TourModel extends sofia.util.Observable {
	
	int tourIndex;
	int factIndex;
	boolean tourIsOver = false;
	TourLocation currentDestination;
	ArrayList<Integer> factIntervals = new ArrayList<Integer>();
	//tourLocations is the entire list of locations
	ArrayList<TourLocation> tourLocations = new ArrayList<TourLocation>();
	//visitedLocations is the locations that have already been visited
	ArrayList<TourLocation> visitedLocations = new ArrayList<TourLocation>();

	long locationStartTime = 0;
	long lengthToAdd = 0;
	// used to store the length of time (in milliseconds) that the user spent on the tout
	long totalTimeElapsed = 0;
	
	public void loadVars()
	{	
		TourLocation r1 = new TourLocation(
				"The Rotunda",
				38.035721,
				-78.503347,
				"rotunda",
				new ArrayList<String>(Arrays.asList(
						"Thomas Jefferson designed this famous building.",
						"There was a fire here in 1895.",
						"The Rotunda is located at the top of the lawn."
						)));
		TourLocation r2 = new TourLocation(
				"McIntire Amphitheater",
				38.033515,
				-78.505825,
				"mcintire",
				new ArrayList<String>(Arrays.asList(
						"Construction was completed in 1921.",
						"It was designed by Fiske Kimball.",
						"McIntire Ampitheater hosts many great performances."
						)));
		TourLocation r3 = new TourLocation(
				"Newcomb Hall",
				38.035793,
				-78.506708,
				"newcomb",
				new ArrayList<String>(Arrays.asList(
						"This is home to the UVA Student Center.",
						"The dining hall here is one of the best.",
						"Newcomb Hall also has a post office."
						)));
		TourLocation r4 = new TourLocation(
				"Thornton Hall",
				38.032891,
				-78.510307,
				"thornton",
				new ArrayList<String>(Arrays.asList(
						"It has four wings.",
						"It is home to UVA Engineering.",
						"Thornton Hall is made of lots of bricks."
						)));
		TourLocation r5 = new TourLocation(
				"Scott Stadium Entrance Gate",
				38.032100,
				-78.514464,
				"stadium",
				new ArrayList<String>(Arrays.asList(
						"Many great players have passed through these gates.",
						"This is home to the best football team in Charlottesville.",
						"Scott Stadium has a capacity of 61,500 people."
						)));
		tourLocations.add(r1);
		tourLocations.add(r2);
		tourLocations.add(r3);
		tourLocations.add(r4);
		tourLocations.add(r5);
	}
	
	public TourModel()
	{	
		this.loadVars();  // THIS IS FOR TESTING ONLY
		tourIndex = 0;
		if ( tourLocations.size()>0 )
		{
			this.setCurrentDestination();
			notifyObservers(false);
		}
		else // end the tour if there are no locations available
		{
			tourIsOver = true;
			notifyObservers(true);
		}
	}
	
	public ArrayList<TourLocation> getVisitedLocations() {
		return visitedLocations;
	}
	
	public ArrayList<TourLocation> getTourLocations() {
		return tourLocations;
	}

	public void setTourLocations(ArrayList<TourLocation> tourLocations) {
		this.tourLocations = tourLocations;
	}

	public int getTourIndex() {
		return tourIndex;
	}

	public boolean getTourIsOver()
	{
		return tourIsOver;
	}
	
	public boolean updateFact(Float dist)
	{
		int numOfFacts = currentDestination.getLocationFacts().size();
		if ( (factIndex+1) < numOfFacts && dist < factIntervals.get(factIndex+1))
		{
			factIndex++;
			return true;
		}
		return false;
	}
	
	public void calculateFactIntervals(Float dist)
	{
		factIntervals.clear();
		int numOfFacts = currentDestination.getLocationFacts().size(); 
		Float interval = dist / numOfFacts;
		for (int i=0; i<numOfFacts; i++)
		{
			int newInterval = Math.round(dist - (i*interval));
			if (newInterval < 0)
			{
				newInterval = 0;
			}
			factIntervals.add(newInterval);
		}
	}

	public String getCurrentLocationFact()
	{
		int numOfFacts = currentDestination.getLocationFacts().size();
		if (factIndex < numOfFacts)
		{
			return currentDestination.getCurrentFact(factIndex);
		}
		else
		{
			return "Sorry, no more facts to share.";
		}
	}
	
	// the parameter is whether or not the tour is over
	public void resetTourIndex(Boolean b)
	{
		locationStartTime = 0;
		totalTimeElapsed = 0;
		tourIsOver = b;
		tourIndex = 0;
		setStartTime();
		visitedLocations.clear();
		setCurrentDestination();
		notifyObservers("reset");
	}
	
	public TourLocation getCurrentDestination()
	{
		return currentDestination;
	}

	public boolean moveToNextDestination()
	{
		updateTimeElapsed();
		// if there are more locations to visit (otherwise, the tour is over)
		if (tourIndex < tourLocations.size()-1)
		{
			visitedLocations.add(currentDestination);
			tourIndex++;
			this.setCurrentDestination();
			notifyObservers(false);
			return true;
		}
		else
		{
			notifyObservers(true);
//			resetTourIndex(true);
			return false;
		}
	}
	
	public void setCurrentDestination()
	{
		factIndex = 0;
		currentDestination = tourLocations.get(tourIndex);
	}
	
	
	public long getTotalTimeElapsed()
	{
		return totalTimeElapsed;
	}
	
	public void updateTimeElapsed()
	{
		lengthToAdd = System.currentTimeMillis() - locationStartTime;
		if ( locationStartTime!=0 )
		{
			totalTimeElapsed += lengthToAdd;
		}
		
	}
	
	public void setStartTime()
	{
		locationStartTime = System.currentTimeMillis();
	}
	
	public float calculateCurrentSpeed(Double oldLat, Double oldLong, Double newLat, Double newLong, int timerFrequency)
	{
		Location oldLoc = new Location("default");
		Location newLoc = new Location("default");
		oldLoc.setLatitude(oldLat);
		oldLoc.setLongitude(oldLong);
		newLoc.setLatitude(newLat);
		newLoc.setLongitude(newLong);
    	return ( oldLoc.distanceTo(newLoc) ) * (1000 / timerFrequency);
	}

	
}
