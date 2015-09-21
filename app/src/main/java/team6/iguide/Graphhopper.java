package team6.iguide;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import team6.iguide.GraphhopperModel.GraphhopperModel;

public class Graphhopper{

    private RequestQueue mRequestQueue;
    MapView mv;
    GraphhopperModel routeInfo;
    //static PathOverlay line;
    Context mapContext;

    public void executeRoute(Context context, MapView mapView, double lat, double lon, LatLng userLocation){

        mapContext = context;
        mv = mapView;
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
                mapContext.startActivity(intent);
                //drawRoute();

                //Bundle bundle = new Bundle();
                //bundle.putLong("TIME", routeInfo.getPaths().get(0).getTime());
                //System.out.println(mainActivity.blah);



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

}

