package team6.iguide;

/***
 iGuide
 Copyright (C) 2015 Cameron Mace

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

public class GetBusStops {
    // This class is used to get the bus stops on OpenStreetMap.

    MapView mv;
    MainActivity mainActivity = new MainActivity();
    String boundingBox = "(29.709354854765827,-95.35668790340424,29.731896194504913,-95.31928449869156);";
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    List<Marker> campusLoopStops = new LinkedList<>();
    List<Marker> outerLoopStops = new LinkedList<>();
    List<Marker> erpExpress = new LinkedList<>();
    List<Marker> eastwoodErpLine = new LinkedList<>();

    public List getBusStops(Context context,  MapView mapView){

        mapContext = context;
        mv = mapView;

        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
                "node[\"highway\"~\"" + "bus_stop" + "\",i]" + boundingBox +
                "way[\"highway\"~\"" + "bus_stop" + "\",i]" + boundingBox +
                "relation[\"highway\"~\"" + "bus_stop" + "\",i]" + boundingBox +
                ");out%20center;>;out%20skel%20qt;";

        mRequestQueue = Volley.newRequestQueue(context);   // Create queue for volley
        fetchJsonResponse(URI);

        // Finish up by storing the list in a list and return it
        List<List> busStops = new ArrayList<>();
        busStops.add(campusLoopStops);

        return busStops;
    }



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
                Log.e("GetBusStops.Volley", "onErrorResponse ", error);
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
    }



    public void showSearchResults() {

        List<OverpassElement> parsedList = results.getElements();

        // Check if the search returns no results. This should never happen unless the overpass API
        // isn't working or OSM data has been deleted.
        if (parsedList.isEmpty()) {
            Log.e("getBusStops", "Parsed JSON list is empty");
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

    public void addMarker( List<OverpassElement> parsedList, LatLng latLng, int i) {
        // This method is used to determine what the marker should look like as well as how it is treated.
        Marker marker;
        String ref;

        String title = parsedList.get(i).getTags().getName();
        if(parsedList.get(i).getTags().getRouteRef() != null) ref = parsedList.get(i).getTags().getRouteRef();
        else ref = "blah";

        if(ref.contains("campus_loop")) ref = "campus_loop";

        // Create the new marker and set its infoWindow
        marker = new Marker(mv, title, ref, latLng);
        marker.setToolTip(new CustomInfoWindow(mapContext, mv, parsedList, i));

        switch(ref){
            case "campus_loop":
                Drawable atmIcon = ContextCompat.getDrawable(mapContext, R.drawable.green_bus_stop);
                if(marker.getTitle() == null) marker.setTitle("Campus Loop Stop");
                marker.setMarker(atmIcon); // set the marker to the correct icon
                campusLoopStops.add(marker); // add the marker to the correct zoom level list
                break;
            /*case "outer_loop":
                Drawable atmIcon = ContextCompat.getDrawable(mapContext, R.drawable.atm);
                if(marker.getTitle() == null) marker.setTitle("Campus Loop Stop");
                marker.setMarker(atmIcon); // set the marker to the correct icon
                campusLoopStops.add(marker); // add the marker to the correct zoom level list
                break;*/
        }

    }
}
