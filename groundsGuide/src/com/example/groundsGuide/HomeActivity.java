package com.example.groundsGuide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
	
	Button searchButton;
	Button mapButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_home);
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setVisibility(Button.INVISIBLE);
		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
				Intent start = new Intent().setClass(HomeActivity.this, MainActivity.class);
				startActivity(start);
            }
        });
		mapButton = (Button) findViewById(R.id.mapButton);
		mapButton.setVisibility(Button.INVISIBLE);
		mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
				Intent start = new Intent().setClass(HomeActivity.this, MainActivity.class);
				startActivity(start);
            }
        });

		TimerTask task = new TimerTask()
		{
			@Override
			public void run(){
				HomeActivity.this.runOnUiThread(new Runnable() {
				    public void run() {
						searchButton.setVisibility(Button.VISIBLE);
						mapButton.setVisibility(Button.VISIBLE);
				    }
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 300);
	}
	
}
