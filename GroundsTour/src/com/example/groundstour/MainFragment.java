package com.example.groundstour;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class MainFragment extends FragmentActivity {

	ImageView currentImage;
	Fragment current;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptest);
		//currentImage = (ImageView) view.findViewById(R.id.test_image);
		fragmentManager  = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		current = fragmentManager.findFragmentById(R.id.map);
		fragmentTransaction.show(current);
		fragmentTransaction.commit();
	}

	//This method toggles the fragment shown or hidden when the button is clicked.
	public void swapFrag(View v){
		if( !current.isVisible() )
		{
			fragmentTransaction.show(current);
		}
		else if(current.isVisible())
		{
			fragmentTransaction.hide(current);
		}
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}