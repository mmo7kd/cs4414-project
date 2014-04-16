package com.example.groundstour;

import java.util.ArrayList;
import sofia.app.ListScreen;
import sofia.app.OptionsMenu;
import android.view.Menu;
import android.view.MenuItem;

@OptionsMenu
public class LocationListScreen extends ListScreen<TourLocation> {
	
	TourLocation currentDestination;
	ArrayList<TourLocation> allLocations;
	ArrayList<TourLocation> visitedLocations;
	String blankStr = "????";
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		setEmptyMessage("No locations to view.");
		getMenuInflater().inflate(R.menu.location_list_screen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection for the menu
		switch (item.getItemId()) {
	        case R.id.view_all_locations:
	            createList();
	        	return true;
	        case R.id.view_visited_locations:
	        	this.clear();
	        	this.addAll(visitedLocations);
	            return true;
	        case R.id.return_to_previous:
	        	finish(currentDestination);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void initialize(
			TourLocation currentDestination,
			ArrayList<TourLocation> tourLocations,
			ArrayList<TourLocation> visitedLocations)
	{
		this.currentDestination = currentDestination;
		this.allLocations = tourLocations;
		this.visitedLocations = visitedLocations;
		createList();
	}
	
    public void listViewItemClicked(TourLocation item)
    {
        if (item.getLocationName().equals(blankStr))
        {
        	showAlertDialog("Not so fast!", "You can only view information for locations you have already visited.");
        }
        else
        {
        	presentScreen(LocationInfoScreen.class,item,false);
        }
    }
	
	public void createList()
	{
		this.clear();
		for (TourLocation loc : allLocations)
		{
			if ( visitedLocations.contains(loc) )
			{
				this.add(loc);
			}
			else
			{
				TourLocation loc2 = new TourLocation(loc);
				loc2.setLocationName(blankStr);
				this.add(loc2);
			}
		}
	}
	
	// if b is true, goes back to the main screen
	public void locationInfoScreenFinished(Boolean b)
	{
		if (b)
		{
			finish(currentDestination);
		}
	}
	
	
	
}
