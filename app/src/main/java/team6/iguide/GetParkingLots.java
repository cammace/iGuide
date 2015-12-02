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
import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

public class GetParkingLots {

    MapView mv;
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    int requestAttempt = 0;

    List<Marker> economyLots = new ArrayList<>();
    List<Marker> studentLots = new ArrayList<>();
    List<Marker> facultyLots = new ArrayList<>();
    List<Marker> visitorLots = new ArrayList<>();
    List<Marker> garageLots = new ArrayList<>();

    public List getParkingLots(Context context, MapView mapView){

        mv = mapView;
        mapContext = context;

        String boundingBox = mapContext.getString(R.string.map_bounding_box);
        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:60];(" +
                "way[\"name\"~\"Economy%20Lot\",i]" + boundingBox +
                "relation[\"name\"~\"Economy%20Lot\",i]" + boundingBox +

                "way[\"name\"~\"Student%20Parking\",i]" + boundingBox +
                "relation[\"ref\"~\"Student%20Parking\",i]" + boundingBox +

                "way[\"name\"~\"Faculty%20Parking\",i]" + boundingBox +
                "relation[\"ref\"~\"Faculty%20Parking\",i]" + boundingBox +

                "way[\"name\"~\"Visitor%20Parking\",i]" + boundingBox +
                "relation[\"ref\"~\"Visitor%20Parking\",i]" + boundingBox +

                "way[\"name\"~\"Garage\",i]" + boundingBox +
                "relation[\"ref\"~\"Garage\",i]" + boundingBox +
                ");out%20center;>;out%20skel%20qt;";

        mRequestQueue = Volley.newRequestQueue(context);   // Create queue for volley
        fetchJsonResponse(URI);

        List<List> parking = new ArrayList<>();
        parking.add(economyLots);
        parking.add(facultyLots);
        parking.add(garageLots);
        parking.add(studentLots);
        parking.add(visitorLots);

        return parking;
    }

    private void fetchJsonResponse(final String URI) {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String overpassData = response.toString();

                results = gson.fromJson(overpassData, OverpassModel.class);
                addToList();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GetParkingLots.Volley", "onErrorResponse ", error);
                if(requestAttempt < 3){
                    requestAttempt++;
                    fetchJsonResponse(URI);
                }
                else
                Snackbar.make(MainActivity.mapContainer, "Can't get Parking information", Snackbar.LENGTH_LONG).show();
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

    private void addToList(){

        List<OverpassElement> q = results.getElements();
        Marker marker;

        for (int i = 0; i < q.size(); i++) {

            if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")) {
                marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                        q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, i));
                marker.setMarker(ContextCompat.getDrawable(mapContext, R.drawable.parking_marker));

                String name = q.get(i).getTags().getName();
                if(name.contains("Economy Lot")) economyLots.add(marker);
                else if(name.contains("Student Parking")) studentLots.add(marker);
                else if(name.contains("Faculty Parking")) facultyLots.add(marker);
                else if(name.contains("Visitor Parking")) visitorLots.add(marker);
                else if(name.contains("Garage")) garageLots.add(marker);

            }
        }
    }
}
