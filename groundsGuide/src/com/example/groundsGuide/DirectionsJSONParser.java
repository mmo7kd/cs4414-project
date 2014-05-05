package com.example.groundsGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser
{
	public static List<Direction> path = new ArrayList<Direction>();


	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	
	
	public List<Direction> parseDirections(JSONObject jObject, DatabaseHandler dbHandler)
	{
		//List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		JSONObject jDistance = null;
		JSONObject jDuration = null;

		try
		{
			jRoutes = jObject.getJSONArray("routes");
			/** Traversing all routes */
			for (int i = 0; i < jRoutes.length(); i++)
			{
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				Direction summary = new Direction(); 
				
				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++)
				{
					/** Getting distance from the json data */
					jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
					summary.distance = jDistance.getString("text"); 

					/** Getting duration from the json data */
					jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
					summary.duration = jDuration.getString("text"); 

					/** Adding distance object to the path */
					path.add(summary);

					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for (int k = 0; k < jSteps.length(); k++)
					{
						Direction direction = new Direction(); 
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = this.decodePoly(polyline);
						direction.coordinates = list; 

						final JSONObject step = jSteps.getJSONObject(k);
						String instructions = step.getString("html_instructions").replaceAll("<(.*?)*>", "");
						direction.instructions = instructions; 
						System.out.println(instructions); 
						
						final JSONObject dist = step.getJSONObject("distance");
						direction.distance = dist.getString("text");
						direction.meters = dist.getInt("value");
						
						final JSONObject dur = step.getJSONObject("duration");	 
						direction.duration = dur.getString("text");
						direction.seconds = dur.getInt("value"); 
					
						direction.start = list.get(0); 
						//get closest place to direction
						direction.place = dbHandler.getLocationName(direction.start.latitude, direction.start.longitude);
						direction.rewriteInstructions(); 
						path.add(direction); 
					}
				}
				//routes.add(path);
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
		}

		return path;
	}

	/**
	 * Method to decode polyline points
	 * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private List<LatLng> decodePoly(String encoded)
	{

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len)
		{
			int b, shift = 0, result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}