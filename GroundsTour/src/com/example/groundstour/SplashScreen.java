package com.example.groundstour;

import java.util.Timer;
import java.util.TimerTask;

import sofia.app.Screen;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashScreen extends Activity {
	
	Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		button = (Button) findViewById(R.id.startTourButton);
		button.setVisibility(Button.INVISIBLE);
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
				Intent start = new Intent().setClass(SplashScreen.this, MainScreen.class);
				startActivity(start);
            }
        });
		TimerTask task = new TimerTask()
		{
			@Override
			public void run(){
				SplashScreen.this.runOnUiThread(new Runnable() {
				    public void run() {
						button.setVisibility(Button.VISIBLE);
				    }
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 300);
	}
	
}
