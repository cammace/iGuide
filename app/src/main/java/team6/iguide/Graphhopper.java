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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONObject;

import team6.iguide.GraphhopperModel.GraphhopperModel;

public class Graphhopper{
    // Graphhopper is used for our routing/navigation. Its soon to be removed in favor for Mapzen's routing

    private RequestQueue mRequestQueue;
    //MapView mv;
    GraphhopperModel routeInfo;
    //static PathOverlay line;
    Context mapContext;

    public void executeRoute(Context context, double lat, double lon, LatLng userLocation){

        mapContext = context;
        //mv = mapView;
/*
        if(line == null)
            line = new PathOverlay(mapContext.getResources().getColor(R.color.RoutePrimaryColor), 10);
        else line.clearPath();
*/





        // Convert all the latitudes and longitudes to strings
        String destinationLat = String.valueOf(lat);
        String destinationLon = String.valueOf(lon);
        String userLat = String.valueOf(userLocation.getLatitude());
        String userLon = String.valueOf(userLocation.getLongitude());

        // Create the Graphhopper API URI
        String graphhopperURI = buildURI(destinationLat, destinationLon, userLat, userLon);
        //System.out.println(graphhopperURI);

        mRequestQueue = Volley.newRequestQueue(context);
        fetchJsonResponse(graphhopperURI);


    }

    private String buildURI(String dLat, String dLon, String uLat, String uLon){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.graphhopper.com")
                .appendPath("api")
                .appendPath("1")
                .appendPath("route")
                .appendQueryParameter("point", dLat + "," + dLon)
                .appendQueryParameter("point", uLat + "," + uLon)
                .appendQueryParameter("vehicle", "foot")
                .appendQueryParameter("key", "2a3617f7-dd96-4a1b-b16a-412c3d74ee94")
                .appendQueryParameter("type", "json")
                .appendQueryParameter("points_encoded", "false");
        return builder.build().toString();
    }

    private void fetchJsonResponse(String URI) {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String graphhopperData = response.toString();

                routeInfo = gson.fromJson(graphhopperData, GraphhopperModel.class);
                Intent intent = new Intent(mapContext, RoutingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ROUTING_RESULTS", routeInfo);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mapContext.startActivity(intent);
                //drawRoute();

                //Bundle bundle = new Bundle();
                //bundle.putLong("TIME", routeInfo.getPaths().get(0).getTime());
                //System.out.println(mainActivity.blah);



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Graphopper", "onErrorResponse: ", error);

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

}

