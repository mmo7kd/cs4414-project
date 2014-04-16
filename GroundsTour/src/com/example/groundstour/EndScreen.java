package com.example.groundstour;

import sofia.app.Screen;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndScreen extends Screen {

	long seconds=0, minutes=0;
	TextView timeString;
	Button button;
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.endscreen);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.end_screen, menu);
		return true;
	}
	
	public void initialize(Long timeElapsed)
	{
		log(Long.toString(timeElapsed));
		setContentView(R.layout.endscreen);
		String tempStr = new String("");
		seconds = timeElapsed / 1000;
		minutes = seconds / 60;
		seconds = seconds - (minutes * 60);
		if ( minutes>0 )
		{
			tempStr += ((Long) minutes).toString();
			if ( minutes > 1 )
			{
				tempStr += " minutes and ";
			}
			else
			{
				tempStr += " minute and ";
			}
		}
		tempStr += ((Long) seconds).toString() + " seconds.";
		
		timeString = (TextView) findViewById(R.id.time_display);
		timeString.setText(tempStr);
		button = (Button) findViewById(R.id.redo_tour_button);
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish(true);
            }
        });
	}

}
