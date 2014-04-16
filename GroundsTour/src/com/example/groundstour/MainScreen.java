package com.example.groundstour;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sofia.app.Screen;
import sofia.app.ScreenLayout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
@ScreenLayout(scroll = true)
public class MainScreen extends Screen {
	
	ProgressBar progressBar;
	boolean checkVal = true;
	boolean pauseUpdates = false;
	int barMax = 300; // max value for the autoPilotSeekBar
	int toastDuration = 1000; // milliseconds for the toast to appear
	Toast factToast;
	SeekBar autoPilotSeekBar;
	Timer timer;
	TourModel tourModel;
	ImageView currentImage;
	TourLocation currentDestination;
	boolean autoPilot = false;
	boolean pauseAutoPilot = false;
	String pauseString = "Pause Autopilot";
	TextView destinationName;
	TextView currentFact;
	TextView userCoordinates;
	TextView speedAdjustText;
	TextView numOfFactsText;
	String speedAdjustStr = "Autopilot Speed: ";
	String coordinateStr = "User";
	Location userLocation;
	Menu menu;
	MenuItem autoPilotMenuItem;
	MenuItem coordinatesMenuItem;
	LocationManager locationManager;
	LocationListener locationListener;
	DecimalFormat speedFormat = new DecimalFormat("#.0");
	DecimalFormat distFormat = new DecimalFormat("#.00");
	DecimalFormat coordinateFormat = new DecimalFormat("#.0000000");
//	Double userLatitude;
//	Double userLongitude;
	Double previousLatitude = null;
	Double previousLongitude = null;
	// how far the autopilot moves each time
	Double autoPilotIncrement = .000005;
	// how often to update the autopilot values
	int timerFrequency = 250;
	//controls the transparency of the location image
	int imageVal = 0;
	float remainingDist;
	// when the user is within "proximity" distance of a location, tour moves to next location
	final int proximity = 2;

    public void initialize(Boolean b)
    {    
    	currentFact.setText("<current fact>");
        //destinationName = new TextView(this); 
        //destinationName.setText( currentDestination.getLocationName() );      
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		super.onCreate(savedInstanceState);
        tourModel = new TourModel();
        tourModel.addObserver(this);
		setContentView(R.layout.mainscreen);		
		progressBar = (ProgressBar) findViewById(R.id.progress_indicator);
		destinationName = (TextView) findViewById(R.id.destination_name);
        userCoordinates = (TextView) findViewById(R.id.current_coordinates);
		numOfFactsText = (TextView) findViewById(R.id.num_of_facts);
        currentFact =  (TextView) findViewById(R.id.current_location_fact);
		currentImage = (ImageView) findViewById(R.id.location_image);
		currentImage.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				presentScreen(TourMap.class,userLocation.getLatitude(),userLocation.getLongitude());
			}
    	});

        // the timer is used to set intervals when updating the autopilot info
		timer = new Timer();
		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				MainScreen.this.runOnUiThread(new Runnable() {
				    public void run() {
				    	autoPilotMethod();
				    }
				});
			}
			
		}, 0, timerFrequency);
				
		currentDestination = tourModel.getCurrentDestination();
		setUpAutoPilotSeekBar();
		setUpLocationStuff();
        setCurrentDestination();
    	Context context = getApplicationContext();
    	CharSequence text = "A new fact appears!";
    	int duration = Toast.LENGTH_SHORT;
    	factToast = Toast.makeText(context, text, duration);
    	factToast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);	}
	
	// called from calculateRemainingDistance
	
	public void setProgressIndicator()
	{
		float modifier;
		int currentProgress;
		int r,g=0,b;
		progressBar.setMax(200);
		if ( remainingDist <= 200)
		{
			currentProgress = Math.round(remainingDist);
			modifier = currentProgress - 100;
		}
		else
		{
			currentProgress = 200;
			modifier = 100;
		}		
		if (modifier>0)
		{
			r = 0;
			b = (int) ( 155 + modifier );
		}
		else if (modifier < 0)
		{
			r = (int) (255 - Math.abs(modifier) );
			b = 0;
		}
		else
		{
			r = 0;
			b = 0;
		}
		modifier = (float) ( modifier * .01 );
		if ( -.25 < modifier && modifier < 0 )
		{
			modifier = (float) -.25;
		}
		if ( 0 < modifier && modifier < .1 )
		{
			modifier = (float) .1;
		}
		
		progressBar.setAlpha( Math.abs(modifier) );
		progressBar.setProgress(currentProgress);
		// Color.rgb(r,g,b);
		progressBar.getProgressDrawable().
			setColorFilter(Color.rgb(r,g,b), android.graphics.PorterDuff.Mode.MULTIPLY);
		Drawable drawable = progressBar.getProgressDrawable();
		drawable.setColorFilter(new LightingColorFilter(Color.rgb(r,g,b), Color.rgb(r,g,b)));
	}

	
	public void setUpAutoPilotSeekBar()
	{
		autoPilotSeekBar = (SeekBar) findViewById(R.id.seekBar1);
    	speedAdjustText = (TextView) findViewById(R.id.autopilot_speed_adjust);
		autoPilotSeekBar.setVisibility(SeekBar.INVISIBLE);
		speedAdjustText.setVisibility(TextView.INVISIBLE);
		autoPilotSeekBar.setMax(barMax); 
		autoPilotSeekBar.setProgress( barMax / 2);
		autoPilotSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {       
		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {}       
		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {}     
		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
		    {   
		    	String temp = ".";
		    	for (int i=0; i < (5 - (progress/100)); i++)
		    	{
		    		temp = temp + "0";
		    	}
		    	temp = temp + ((Integer) progress).toString();
		    	autoPilotIncrement = Double.valueOf(temp);
		    }       
		});   
	}
	
	public void setUpLocationStuff()
	{
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		setInitialUserLocationInformation();
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		        if ( Math.abs(location.distanceTo(userLocation)) > .5 )
		        {
		        	setUserLocationInformation(location);
		        }
		    }
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		menu.removeItem(R.id.pause_item);
//		menu.removeItem(R.id.edit_coordinates);
		getMenuInflater().inflate(R.menu.main_menu, menu);
        autoPilotMenuItem = menu.findItem(R.id.autopilot_menu_item);
        this.setAutoPilotText("OFF");
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menu.removeItem(R.id.pause_item);
//		menu.removeItem(R.id.edit_coordinates);
		if (autoPilot)
		{
			menu.add(0,R.id.pause_item, 0, pauseString);
//			menu.add(0,R.id.edit_coordinates, 0, "Input Coordinates");
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection for the menu
		switch (item.getItemId()) {
			case R.id.autopilot_menu_item:
	    		locationManager.removeUpdates(locationListener);
				this.setAutoPilot();
				return true;
	        case R.id.pause_item:
	        	if (pauseAutoPilot)
	        	{
	        		pauseTheAutoPilot(false);
	        	}
	        	else
	        	{
	        		pauseTheAutoPilot(true); 		
	        	}
	        	return true;
//	        case R.id.edit_coordinates:
//	        	return true;	
	        case R.id.view_all_locations:
	        	factToast.cancel();
	            presentScreen(LocationListScreen.class, currentDestination, this.getTourLocations(), this.getVisitedLocations() );
	            return true;
	        case R.id.restart_tour:
	        	this.setAutoPilot(false);
	        	tourModel.resetTourIndex(false);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void pauseTheAutoPilot(Boolean b)
	{
		pauseAutoPilot = b;
		if (b)
		{
			pauseString = "UN-pause Autopilot";
		}
		else
		{
			pauseString = "Pause Autopilot";
		}
	}
	
	public void setDestinationName()
	{
        destinationName.setText( currentDestination.getLocationName() );
	}
		
//    public void locationListScreenFinished(TourLocation newLocation)
//    {
//    	// this allows the user to select a new destination
//    	// from the LocationListScreen
//    	currentDestination = newLocation;
//    }

    public ArrayList<TourLocation> getTourLocations()
    {
    	return tourModel.getTourLocations();
    }

    public ArrayList<TourLocation> getVisitedLocations()
    {
    	return tourModel.getVisitedLocations();
    }
    
    public int getTourIndex()
    {
    	return tourModel.getTourIndex();
    }
    
    public TourLocation getCurrentDestination()
    {
    	return currentDestination;
    }
        
    public float calculateRemainingDistance()
    {   
    	remainingDist = currentDestination.getLocation().distanceTo(userLocation);
    	setProgressIndicator();
    	return remainingDist;
    }
    
    public boolean test(Location location)
    {
    	return true;
    }

    // this updates the user location based on a set increment that
    // the user can determine using a slider on the main screen (autoPilotIncrement)
    public void autoPilotMethod()
    {
    	// if the autopilot is paused or turned off, do not update the values 
    	if (autoPilot && !pauseAutoPilot)
    	{
    		Location newLocation = userLocation;
	    	Double lat = autoPilotHelper(currentDestination.getLatitude(), userLocation.getLatitude());
	    	Double lon = autoPilotHelper(currentDestination.getLongitude(), userLocation.getLongitude());
	    	float speed = tourModel.calculateCurrentSpeed(userLocation.getLatitude(),userLocation.getLongitude(),lat,lon,timerFrequency);
	    	speedAdjustText.setText( speedAdjustStr + speedFormat.format(speed) + "m/s" );
	    	newLocation.setLatitude( lat );
	    	newLocation.setLongitude( lon );
	    	this.setUserLocationInformation(newLocation);	    
    	}
    }
    
    // autoPilotMethod() calls this helper function to return the new latitude and longitude values
    public double autoPilotHelper(Double destVal, Double userVal)
    {
    	Double increment = Math.abs( Math.abs(destVal) - Math.abs(userVal) );
    	
    	// if the autopilot increment is greater than the distance remaining,
    	// then set the increment to that distance (otherwise, it's possible
    	// the autopilot will get caught in a loop of constantly overshooting the destination
    	
    	if (increment > autoPilotIncrement)
    	{
    		increment = autoPilotIncrement;
    	}
    	
    	if (destVal < userVal)
    	{
    		return userVal - increment;
    	}
    	else if (destVal > userVal) 
    	{
    		return userVal + increment;
    	}
    	else
    	{
    		return userVal;
    	}
    }
    
    // there are two setAutoPilot() methods (overloaded) --
    // if a parameter is given, the autopilot is set to that boolean
    // if no parameter is given, the autopilot is set to the opposite
    // of its current state
    public void setAutoPilot()
    {
    	updateAutoPilotValues(!autoPilot);
    }
    
    public void setAutoPilot(Boolean b)
    {
    	updateAutoPilotValues(b);
    }
    
    public void updateAutoPilotValues(Boolean b)
    {
    	userLocation.setLatitude(38.03572100); // FAILURE
    	userLocation.setLongitude(-78.49946020); // FAILURE
    	
    	autoPilot = b;
    	// whether or not the autopilot is now on or off, it should not be paused
    	pauseAutoPilot = false;
    	pauseString = "Pause Autopilot";
    	if (autoPilot)
    	{
    		locationManager.removeUpdates(locationListener);
    		this.setAutoPilotText("ON");
    		// these objects are made visible so the user can adjust the autopilot speed
    		speedAdjustText.setVisibility(TextView.VISIBLE);
    		autoPilotSeekBar.setVisibility(SeekBar.VISIBLE);
    		coordinateStr = "AUTOPILOT";
    		userCoordinates.setTextColor(Color.RED);
    		this.setCoordinateText();
    		// should the autopilot resume at the same point where it was previously turned off?
    		useOldAutopilotCoordinates();
    	}
    	else
    	{
    		// reset the user location values to the actual location of the user
    		this.resetUserLocation();
    		this.setAutoPilotText("OFF");
    		// hide the controls for the autopilot
    		speedAdjustText.setVisibility(TextView.INVISIBLE);
    		autoPilotSeekBar.setVisibility(SeekBar.INVISIBLE);
    		coordinateStr = "User";
    		userCoordinates.setTextColor(Color.BLACK);
    	}
    }
    
    public void resetOldAutopilotCoordinates()
    {
    	previousLatitude = null;
    	previousLongitude = null;
    }
    
    public void useOldAutopilotCoordinates()
    {
    	if (previousLatitude!=null && previousLongitude!=null)
    	{
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    builder.setTitle("Resume or start over?");
    	    builder.setMessage("Would you like the Autopilot to resume where it let off?");
    	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

    	        public void onClick(DialogInterface dialog, int which)
    	        {
        			userLocation.setLatitude(previousLatitude);
        			userLocation.setLongitude(previousLongitude);
    	            dialog.dismiss();
    	            resetOldAutopilotCoordinates(); 	        
            	}
    	    });
    	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

    	        @Override
    	        public void onClick(DialogInterface dialog, int which) {
    	        	resetOldAutopilotCoordinates();
            		dialog.dismiss();
    	        }
    	    });
    	    builder.create().show();
    	}
    }
    
    public void setAutoPilotText(String str)
    {
        autoPilotMenuItem.setTitle("Autopilot: " + str);
    }
    
    public void resetUserLocation()
    {
    	// store the current location in case the autopilot is resumed later
    	previousLatitude = userLocation.getLatitude();
    	previousLongitude = userLocation.getLongitude();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		setUserLocationInformation( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );
    }
    
    public void setInitialUserLocationInformation()
    {
    	// this is used from the onCreate() method
    	// unlike setUserLocationInformation() method, this does not call anything related
    	// to location facts, in case those fields have not yet been initialized
    	Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	userLocation = location;
//    	userLatitude = location.getLatitude();
//    	userLongitude = location.getLongitude();
    	this.calculateRemainingDistance();
    	this.setCoordinateText();
    }
    
    public void setUserLocationInformation(Location location)
    {
    	if (!pauseUpdates)
    	{
		userLocation = location;
		this.calculateRemainingDistance();
		if (remainingDist < proximity)
	    {
	    	// moveToNextDestination only returns true if there are more locations to visit
			checkVal = false;
			tourModel.moveToNextDestination();

	    }
		// else if (not just "if") because otherwise when a new location is loaded.
		// it will begin with fact #2 instead of #1
		// checkVal is probably not necessary, it was used in an attempt to fix an earlier bug
		// this is what tourMode.updateFacts() does:
		// if more facts are available, it moves to the next fact and returns true
		// otherwise, it simply returns false
		else if ( tourModel.updateFact(remainingDist) && checkVal)
		{
			newFactToast();
			setCurrentFact();
		}
		this.setCoordinateText();
//		this.adjustImage();
    	}
    }
    
    @SuppressLint("DefaultLocale")
    public void changeWasObserved(TourModel tourModel, boolean tourIsOver)
    {
    	// either the tour is over or the user reached the current destination
    	if ( tourIsOver )
    	{
    		this.setAutoPilot(false);
    		factToast.cancel();
    		presentScreen( EndScreen.class, tourModel.getTotalTimeElapsed() );
    		resetOldAutopilotCoordinates();
    		this.resetUserLocation();
    		tourModel.resetTourIndex(true); //FAILURE
    	}
    	else
    	{
			pauseTheAutoPilot(true);
			pauseUpdates = true;
			presentScreen(LocationInfoScreen.class,currentDestination,true,true);
//    		setCurrentDestination();
    	}
    	checkVal = true;
    }
    
    public void locationInfoScreenFinished(String s)
    {
    	log("Location screen finished with a STRING");
		setCurrentDestination();
		pauseUpdates = false;
		locationInfoScreenFinished(true);
    }
    
    public void locationInfoScreenFinished(Boolean b)
    {
    	log("Location screen finished with a BOOLEAN");
    	if (autoPilot)
    	{
    		pauseTheAutoPilot(false);
    		autoPilotSeekBar.setProgress( barMax / 2);
    	}
    }
    
    public void changeWasObserved(TourModel tourModel, String str)
    {
    	resetOldAutopilotCoordinates();
    	setCurrentDestination();
    }
    
    public void setCurrentDestination()
    {
		currentDestination = tourModel.getCurrentDestination();
        destinationName.setText( currentDestination.getLocationName() );
        tourModel.calculateFactIntervals( calculateRemainingDistance() );
    	tourModel.setStartTime();
        setCurrentFact();
    	updateImage();
    	// can't remember the purpose for having this code, so I didn't delete it entirely...
//        if (tourModel.tourIndex == 0)
//        {
//    		this.resetUserLocation();
//        }
    }
    
    // sets the image on the main screen to the one associated with the current image
    public void updateImage()
    {
    	String imageId = currentDestination.getImageId();
    	int pictureId = getResources().getIdentifier(imageId, "drawable", "com.example.groundstour");
    	currentImage.setImageResource(pictureId);
    }
    
	public void setCurrentFact()
	{
		// displays the number of facts and the current one displayed (ex: "Fact 1 of 3")
		numOfFactsText.setText("Fact " + (tourModel.factIndex + 1) + " of " + currentDestination.locationFacts.size() );
        // updates the current fact
		currentFact.setText( tourModel.getCurrentLocationFact() );
	}
    
	// this updates the text that displays the current latitude, longitude, and distance to the current location
    public void setCoordinateText()
    {
		userCoordinates.setText(
				coordinateStr + " Latitude:   " + coordinateFormat.format( userLocation.getLatitude() )
	    		+ "\n" + 
	    		coordinateStr + " Longitude:  " + coordinateFormat.format( userLocation.getLongitude() )
	    		+ "\n" +
	    		"Distance Remaining:\t" + distFormat.format(remainingDist) + " meters"
	    		);
    }

    // the image gets less and less transparent as the user approaches the current location
    public void adjustImage()
    {
//    	int fullTransparency = 20;
//    	float modifier;
//    	if ( remainingDist > fullTransparency)
//    		modifier = 1;
//    	if (remainingDist < proximity)
//    		modifier = 0;
//    	else
//    		modifier = remainingDist / (fullTransparency - proximity);
//    	imageVal = (int) (255 - (modifier * 255));
//    	currentImage.setImageAlpha(imageVal);
    }
    
    // this appears when the user reaches the current destination
    public void successToast()
    {
//    	Context context = getApplicationContext();
//    	CharSequence text = "Moving to the next location!";
//    	int duration = Toast.LENGTH_SHORT;
//    	Toast toast = Toast.makeText(context, text, duration);
//    	toast.setGravity(Gravity.CENTER|Gravity.LEFT, 0, 0);
//    	toast.show();
    }
    
    // displays a brief message when a new fact appears
    public void newFactToast()
    {
    	factToast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
        	   factToast.cancel(); 
           }
        }, toastDuration);
    }
    
}
