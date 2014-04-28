// some code taken from http://jigarlikes.wordpress.com/2013/04/26/driving-distance-and-travel-time-duration-between-two-locations-in-google-map-android-api-v2/

package com.example.groundsGuide;

/*
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
*/

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    ArrayList<LatLng> routePoints; 
    TextView tvDistanceDuration;
 
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
        
       
        
        
 
        
        LatLngBounds cVille = new LatLngBounds(
        		  new LatLng(38.015648, -78.537912), new LatLng(38.047637, -78.480526));

        		this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(cVille.getCenter(), 15));
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
 
    public void setDirections(ArrayList<LatLng> points){
    	ArrayList<String> pointNames = new ArrayList<String>();
    	for(LatLng coordinate : points){
    		String direction = coordinate.latitude + ", " + coordinate.longitude; 
    		System.out.println("turn at " + direction ); 
    		
    	}
    	
    
        //ListView directionsList =(ListView)findViewById(R.id.directions);
    	 // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.directions_list, pointNames);
          // Set The Adapter
        // directionsList.setAdapter(arrayAdapter); 
    	
         //this.setContentView(findViewById(R.id.directionslist)); 
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
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if (result.size() < 1)
            {
                Toast.makeText(MainActivity.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
 
                    if (j == 0)
                    { // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1)
                    { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            MainActivity.this.routePoints = points; 
            MainActivity.this.setDirections(points); 
            MainActivity.this.tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
 
            // Drawing polyline in the Google Map for the i-th route
            MainActivity.this.map.addPolyline(lineOptions);
        }
    }
    
   
 
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
    
}

//comments