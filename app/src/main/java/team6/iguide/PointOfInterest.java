package team6.iguide;

/**
 * iGuide
 * Copyright (C) 2015 Cameron Mace
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

public class PointOfInterest {

    MapView mv;
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    List<Marker> markerZoom18 = new LinkedList<>();
    List<Marker> markerZoom16 = new LinkedList<>();

    public List getPOI(Context context, MapView mapView){
        // This method is executed to get all the point of interest listed below and within the
        // bounding box. It's important to note that we only run this once when the main activity is
        // created and save the results in a list.



        mv = mapView;
        mapContext = context;

        String boundingBox = mapContext.getString(R.string.map_bounding_box);
        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
                "node[\"amenity\"~\"atm\",i]" + boundingBox +
                "node[\"amenity\"~\"bar\",i]" + boundingBox +
                "node[\"amenity\"~\"emergency_phone\",i]" + boundingBox +
                "node[\"amenity\"~\"fast_food\",i]" + boundingBox +
                "way[\"amenity\"~\"fast_food\",i]" + boundingBox +
                "node[\"amenity\"~\"library\",i]" + boundingBox +
                "way[\"amenity\"~\"library\",i]" + boundingBox +
                "node[\"amenity\"~\"restaurant\",i]" + boundingBox +
                "way[\"amenity\"~\"restaurant\",i]" + boundingBox +
                "node[\"shop\"~\"convenience\",i]" + boundingBox +
                "way[\"shop\"~\"convenience\",i]" + boundingBox +
                ");out%20center;>;out%20skel%20qt;";

        // Print the URI for debugging
        Log.i("PointOfInterest", URI);

        // Create queue for volley
        mRequestQueue = Volley.newRequestQueue(context);
        fetchJsonResponse(URI);

        // Finish up by storing the list in a list and return it
        List<List> poiMarkers = new ArrayList<>();
        poiMarkers.add(markerZoom16);
        poiMarkers.add(markerZoom18);

        return poiMarkers;
    }// End getPOI

    private void fetchJsonResponse(String URI) {
        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String overpassData = response.toString();

                results = gson.fromJson(overpassData, OverpassModel.class);
                showSearchResults();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PointOfInterest", error.toString());
            }
        });
        mRequestQueue.add(req);

        // This allows volley to retry request if for some reason it times out the first time
        // More info can be found in this question:
        // http://stackoverflow.com/questions/17094718/android-volley-timeout
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }// End fetchJsonResponse

    public void showSearchResults() {

        List<OverpassElement> parsedList = results.getElements();

        // Check if the search returns no results. This should never happen unless the overpass API
        // isn't working or OSM data has been deleted.
        if (parsedList.isEmpty()) {
            Log.e("PointOfInterest", "Parsed JSON list is empty");
        } else {
            // go through the results list and add the markers to zoom list 1 by 1
            for (int i = 0; i < parsedList.size(); i++) {
                // So with the Overpass results, we get nodes representing amenities such as fast
                // food, bars, etc. But Overpass also includes nodes that represent ways and
                // relations so we can draw them if wanted. However, we have no use for these nodes
                // here and they only cause trouble. Therefore, we have to check and ensure the
                // node we are adding to our POI list is representing a POI
                if (parsedList.get(i).getTags() != null) {
                    // Here we have to check if its a node which doesn't include a center point, or
                    // a way/relation which does include a center point
                    if (parsedList.get(i).getType().equals("node")) {
                        addMarker(parsedList, new LatLng(parsedList.get(i).getLat(), parsedList.get(i).getLon()), i);
                    } else if (parsedList.get(i).getType().equals("way")) {
                        addMarker(parsedList, new LatLng(parsedList.get(i).getCenter().getLat(), parsedList.get(i).getCenter().getLon()), i);
                    }
                }
            }
        }
    }// End showSearchResults

    public void addMarker( List<OverpassElement> parsedList, LatLng latLng, int i){
        // This method is used to determine what the marker should look like as well as how it is treated.

        Marker marker;
        String type;

        String title = parsedList.get(i).getTags().getName();
        String ref = parsedList.get(i).getTags().getAmenity();

        if(parsedList.get(i).getTags().getAmenity() != null) type = parsedList.get(i).getTags().getAmenity();
        else  type = parsedList.get(i).getTags().getShop();

        // Create the new marker and set its infoWindow
        marker = new Marker(mv, title, ref, latLng);
        marker.setToolTip(new CustomInfoWindow(mapContext, mv, parsedList, i));

        // This switch determines the amenity type
       switch(type){
           case "atm":
               Drawable atmIcon = ContextCompat.getDrawable(mapContext, R.drawable.atm);
               if(marker.getTitle() == null) marker.setTitle("ATM");
               marker.setMarker(atmIcon); // set the marker to the correct icon
               markerZoom18.add(marker); // add the marker to the correct zoom level list
               break;
           case "library":
              Drawable libraryIcon = ContextCompat.getDrawable(mapContext, R.drawable.library);
               marker.setMarker(libraryIcon);
               markerZoom16.add(marker);
               break;
           case "bar":
               Drawable barIcon = ContextCompat.getDrawable(mapContext, R.drawable.bar);
               marker.setMarker(barIcon);
               markerZoom18.add(marker);
               break;
           case "emergency_phone":
               Drawable emergencyPhoneIcon = ContextCompat.getDrawable(mapContext, R.drawable.emergency_phone);
               if(marker.getTitle() == null) marker.setTitle("Emergency Phone");
               marker.setDescription("");
               marker.setMarker(emergencyPhoneIcon);
               markerZoom18.add(marker);
               break;
           case "fast_food":
               Drawable fastFoodIcon = ContextCompat.getDrawable(mapContext, R.drawable.fast_food);
               marker.setMarker(fastFoodIcon);
               marker.setDescription("Fast Food");
               markerZoom18.add(marker);
               break;
           case "restaurant":
               Drawable restaurantIcon = ContextCompat.getDrawable(mapContext, R.drawable.restaurant);
               marker.setMarker(restaurantIcon);
               marker.setDescription("Restaurant");
               markerZoom18.add(marker);
               break;
           case "convenience":
               Drawable convenienceIcon = ContextCompat.getDrawable(mapContext, R.drawable.grocery);
               marker.setMarker(convenienceIcon);
               marker.setDescription("Convenience Store");
               markerZoom18.add(marker);
               break;
       }
    }// End addMarker
}// End PointOfInterest class
