package com.example.groundsGuide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class DirectionAlertFragment extends DialogFragment {

	String distance;
	String time;

	/** Called when the activity is first created. */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.distance = MainActivity.distance;
		this.time = MainActivity.time;


		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.directions_dialog) + "\n\r Distance: " + distance + "\n\r Time: " + time)
		.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		})
		.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
