package com.example.groundstour;

import sofia.app.Screen;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationInfoScreen extends Screen {

	TextView locationName;
	TextView screenTitle;
	TextView factsAboutLocation;
	ImageView image;
	String successStr = "Congratulations, you are at:";
	String nonSuccessStr = "Location Information:";
	Button resumeTourButton;
	Button returnButton;
	Boolean pause = false;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_info_screen, menu);
		return true;
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

	
	// if boolean success is true, this screen is being shown because the user just reached a location
	// if it is false, the screen is reached because the user accessed it from LocationListScreen
	// depending on the success boolean, the title of the screen and the buttons will change
	public void initialize(TourLocation location, boolean success, boolean pause)
	{
		this.pause = pause;
		initialize(location, success);
	}
	
	
	public void initialize(TourLocation location, boolean success)
	{
		setContentView(R.layout.locationinfoscreen);
		screenTitle = (TextView) findViewById(R.id.locationInfoScreenHeading);
		locationName = (TextView) findViewById(R.id.location_name);
		locationName.setText( location.getLocationName() );
		factsAboutLocation = (TextView) findViewById(R.id.location_fact_box);
		image = (ImageView) findViewById(R.id.locationImage);
    	String imageId = location.getImageId();
    	int pictureId = getResources().getIdentifier(imageId, "drawable", "com.example.groundstour");
    	image.setImageResource(pictureId);
		factsAboutLocation.setMovementMethod(new ScrollingMovementMethod());
		String factList = new String("");
		int i = 1;
		for ( String fact : location.getLocationFacts() )
		{
			factList += i + ". " + fact + "\n\n";
			i++;
		}
		factList = factList.substring(0,factList.length()-2);
		factsAboutLocation.setText(factList);
		
		returnButton = (Button) findViewById(R.id.return_to_list_screen);
		returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish(false);
            }
        });
		resumeTourButton = (Button) findViewById(R.id.resume_tour_button);
		resumeTourButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (pause)
                {
                	pause = false;
                	finish("resume");
                }
                else
                {
                	finish(true);
                }
            }
        });
		// the following code affects the appearance of the screen (based on how it was accessed)
		if (success)
		{
			screenTitle.setText(successStr);
			returnButton.setVisibility(Button.INVISIBLE);
		}
		else
		{
			screenTitle.setText(nonSuccessStr);
			returnButton.setVisibility(Button.VISIBLE);
		}
	}
	
	

	
}
