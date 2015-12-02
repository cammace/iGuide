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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

public class Search {
    // When user searches for something, executeSearch is called.

    MapView mv;
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    String boundingBox = "(29.709354854765827,-95.35668790340424,29.731896194504913,-95.31928449869156);";
    ArrayList<LatLng> resultBBList = new ArrayList<>();
    View progressBar;
    MainActivity mainActivity = new MainActivity();
    int requestAttempt = 0;


    public void executeSearch(Context context, MapView mapView, String value, View PB){

        progressBar = PB;
        progressBar.setVisibility(View.VISIBLE);

        mv = mapView;
        mapContext = context;

        value = value.replace(" ", "%20");

        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:60];(" +
                "node[\"name\"~\"" + value + "\",i]" + boundingBox +
                "way[\"name\"~\"" + value + "\",i]" + boundingBox +
                "relation[\"name\"~\"" + value + "\",i]" + boundingBox +

                "node[\"ref\"~\"" + value + "\",i]" + boundingBox +
                "way[\"ref\"~\"" + value + "\",i]" + boundingBox +
                "relation[\"ref\"~\"" + value + "\",i]" + boundingBox +
                ");out%20center;>;out%20skel%20qt;";

        mRequestQueue = Volley.newRequestQueue(context);   // Create queue for volley
        fetchJsonResponse(URI);


    }

    private void fetchJsonResponse(final String URI) {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String overpassData = response.toString();

                results = gson.fromJson(overpassData, OverpassModel.class);
                showSearchResults();
                progressBar.setVisibility(View.INVISIBLE);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Search.Volley", "onErrorResponse ", error);
                if(requestAttempt < 3){
                    Snackbar.make(MainActivity.mapContainer, "Search taking longer then unusual", Snackbar.LENGTH_SHORT).show();
                    requestAttempt++;
                    fetchJsonResponse(URI);
                }
                else {
                    Snackbar.make(MainActivity.mapContainer,"Search Timed out, Check your internet", Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
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
        // This is the method used to show results on the map and move the camera. This methods
        // honestly really messy coding due to the fact that there's so many different possibilities
        // the JSON can give us, you've been warned!

        List<OverpassElement> q = results.getElements();

        // Clear the mapview of any markers
        mv.closeCurrentTooltip();
        mv.clear();
        mv.clearMarkerFocus();

        // Check if the search returns no results
        if (q.isEmpty()) {
            Toast.makeText(mapContext, "No Results", Toast.LENGTH_SHORT).show();
        } else {
            // This counts how many of the search results are valid results

            int pinCount = 0;
            for (int i = 0; i < q.size(); i++) {
                if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation") || q.get(i).getType().equals("node") && q.get(i).getTags() != null) {
                    pinCount++;
                }
            }

            if (pinCount == 1) {
                // Check if the search returned a building
                if (q.get(0).getTags().getBuilding() != null || q.get(0).getTags().getLanduse() != null || q.get(0).getTags().getLeisure() != null || q.get(0).getTags().getAmenity() != null) {
                    Marker marker;
                    if(q.get(0).getType().equals("way")) marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                            q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    else marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                           q.get(0).getLat(), q.get(0).getLon()));
                    marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, 0));
                    marker.setMarker(ContextCompat.getDrawable(mapContext, R.drawable.red_pin));
                    mv.addMarker(marker);
                    //mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    //mv.getController().setZoom(19);
                    if(q.get(0).getType().equals("way")) mv.getController().setZoomAnimated(18, new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()), true, true);
                    else mv.getController().setZoomAnimated(18, new LatLng(q.get(0).getLat(), q.get(0).getLon()), true, true);
                }
                // Check if the search returned a room
                else if (q.get(0).getTags().getIndoor() != null) {

                    //if(mainActivity.getCurrentFloor() != Integer.parseInt(q.get(0).getTags().getLevel())){

                        mainActivity.changeFloorLevel(mapContext, mv, Integer.parseInt(q.get(0).getTags().getLevel()));
                       // mainActivity.setCurrentFloor(Integer.parseInt(q.get(0).getTags().getLevel()));
                    //}

                    Marker marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                            q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, 0));
                    marker.setMarker(ContextCompat.getDrawable(mapContext, R.drawable.red_pin));
                    mv.addMarker(marker);
                    //mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    //mv.getController().setZoom(21);
                    mv.getController().setZoomAnimated(20, new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()), true, true);

                }
            } else {
                for (int i = 0; i < pinCount; i++) {
                    Marker marker;
                    if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")) {
                        marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                                q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                        marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, i));
                        marker.setMarker(ContextCompat.getDrawable(mapContext, R.drawable.red_pin));
                        mv.addMarker(marker);

                        resultBBList.add(new LatLng(q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                    }
                    else if(q.get(i).getType().equals("node") && q.get(i).getTags() != null){
                        marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                                q.get(i).getLat(), q.get(i).getLon()));
                        marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, i));
                        marker.setMarker(ContextCompat.getDrawable(mapContext, R.drawable.red_pin));
                        mv.addMarker(marker);

                        resultBBList.add(new LatLng(q.get(i).getLat(), q.get(i).getLon()));
                    }


                        //mv.getController().setZoom(17);
                }
                //BoundingBox resultBB = new BoundingBox(BoundingBox.fromLatLngs(resultBBList));
                //mv.zoomToBoundingBox(resultBB);

                mv.zoomToBoundingBox(findBoundingBoxForGivenLocations(resultBBList), true, true);

                //System.out.println(resultBB);

            }
        }

    }


    public BoundingBox findBoundingBoxForGivenLocations(ArrayList<LatLng> coordinates)
    {
        double west = 0.0;
        double east = 0.0;
        double north = 0.0;
        double south = 0.0;

        for (int lc = 0; lc < coordinates.size(); lc++)
        {
            LatLng loc = coordinates.get(lc);
            if (lc == 0)
            {
                north = loc.getLatitude();
                south = loc.getLatitude();
                west = loc.getLongitude();
                east = loc.getLongitude();
            }
            else
            {
                if (loc.getLatitude() > north)
                {
                    north = loc.getLatitude();
                }
                else if (loc.getLatitude() < south)
                {
                    south = loc.getLatitude();
                }
                if (loc.getLongitude() < west)
                {
                    west = loc.getLongitude();
                }
                else if (loc.getLongitude() > east)
                {
                    east = loc.getLongitude();
                }
            }
        }

        // OPTIONAL - Add some extra "padding" for better map display
        double padding = 0.005;
        north = north + padding;
        south = south - padding;
        west = west - padding;
        east = east + padding;

        return new BoundingBox(north, east, south, west);
    }

}
