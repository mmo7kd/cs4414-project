// some code taken from http://jigarlikes.wordpress.com/2013/04/26/driving-distance-and-travel-time-duration-between-two-locations-in-google-map-android-api-v2/

package com.example.groundsGuide;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.json.JSONObject;
 
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment; 
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
 
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
 
public class MainActivity extends FragmentActivity
{
    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;
    DatabaseHandler dbHandler; 
 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
 
        this.tvDistanceDuration = (TextView) this.findViewById(R.id.tv_distance_time);
        // Initializing
        this.markerPoints = new ArrayList<LatLng>();
 
        // Getting reference to SupportMapFragment of the activity_main
        //SupportMapFragment fm = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
 
        // Getting Map for the SupportMapFragment
       // this.map = fm.getMap();
        this.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        // Enable MyLocation Button in the Map
        this.map.setMyLocationEnabled(true);
 
        
        //initialize location to c-ville
        LatLngBounds cVille = new LatLngBounds( new LatLng(38.015648, -78.537912), new LatLng(38.047637, -78.480526));

      	this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(cVille.getCenter(), 15));
        
      	
      	//make database
        this.dbHandler = new DatabaseHandler(getApplicationContext());
        try {
			this.dbHandler.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        //try to get a point
        double[] latLonTest = new double[2];
        latLonTest = dbHandler.getClosestLocation(38.036342, -78.503037);
        
        // Setting onclick event listener for the map
        this.map.setOnMapClickListener(new OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
                // Already two locations
                if (MainActivity.this.markerPoints.size() > 1)
                {
                    MainActivity.this.markerPoints.clear();
                    MainActivity.this.map.clear();
                }
 
                // Adding new item to the ArrayList
                MainActivity.this.markerPoints.add(point);
 
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();
 
                // Setting the position of the marker
                options.position(point);
 
                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MainActivity.this.markerPoints.size() == 1)
                {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MainActivity.this.markerPoints.size() == 2)
                {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
 
                // Add new marker to the Google Map Android API V2
                MainActivity.this.map.addMarker(options);
 
                // Checks, whether start and end locations are captured
                if (MainActivity.this.markerPoints.size() >= 2)
                {
                    LatLng origin = MainActivity.this.markerPoints.get(0);
                    LatLng dest = MainActivity.this.markerPoints.get(1);
 
                    // Getting URL to the Google Directions API
                    String url = MainActivity.this.getDirectionsUrl(origin, dest);
 
                    DownloadTask downloadTask = new DownloadTask();
 
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });
    }
 
    private String getDirectionsUrl(LatLng origin, LatLng dest)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
 
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Walking mode
        String mode = "mode=walking";
        
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
 
        
        
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
 
        return url;
    }
 
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        } catch (Exception e)
        {
            Log.d("Exception while downloading url", e.toString());
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
 
            // For storing data from web service
            String data = "";
 
            try
            {
                // Fetching the data from web service
                data = MainActivity.this.downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
 
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<Direction>>
    {
 
        // Parsing the data in non-ui thread
        @Override
        protected List<Direction> doInBackground(String... jsonData)
        {
            JSONObject jObject;
            List<Direction> route = null;
 
            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                //routes = parser.parse(jObject);
                route = parser.parseDirections(jObject);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return route;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<Direction> result)
        {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if (result.size() < 1)
            {
                Toast.makeText(MainActivity.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
                // Fetching all the points in route
                for (int j = 0; j < result.size(); j++)
                {
                	Direction d = result.get(j);
                    if (j == 0)
                    { 
                    	distance = d.distance;
                    	duration = d.duration;
                        continue;
                    } 
                    for(LatLng coordinate : d.coordinates){
                    	points.add(coordinate);
                    }
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
           // }
 
            MainActivity.this.tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
 
            // Drawing polyline in the Google Map for the i-th route
            MainActivity.this.map.addPolyline(lineOptions);
        }
    }
 
    
    
}

//comments