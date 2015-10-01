package team6.iguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.util.Map;

import team6.iguide.BusLocation.BusLocation;
import team6.iguide.GraphhopperModel.GraphhopperModel;

public class TransitOverlay{

    private RequestQueue mRequestQueue;
    Context mapContext;
    MapView mv;
/*
    public void getCampusBuses(Context context, MapView mapView){

        mapContext = context;
        mv = mapView;

        String URI = buildURI();

        mRequestQueue = Volley.newRequestQueue(context);
        fetchJsonResponse(URI);

    }

    private String buildURI(){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("uhpublic.etaspot.net")
                .appendPath("service.php")
                //.appendPath("1")
                //.appendPath("route")
                .appendQueryParameter("service", "get_vehicles")
                .appendQueryParameter("includeETAData", "1")
                .appendQueryParameter("orderedETAArray", "1")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("district", "1")
                .appendQueryParameter("debug", "true");
        return builder.build().toString();
    }

    private void fetchJsonResponse(String URI) {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String busData = response.toString();

                BusLocation busInfo = gson.fromJson(busData, BusLocation.class);

                mv.clear();
                for(int i=0; i<busInfo.getGetVehicles().size(); i++) {
                    if(busInfo.getGetVehicles().get(i).getInService() == 1) {
                        Marker marker = new Marker("blah", "blah", new LatLng(
                                busInfo.getGetVehicles().get(i).getLat(), busInfo.getGetVehicles().get(i).getLng()));
                        mv.addMarker(marker);
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                System.out.println(error);

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
*/
}
