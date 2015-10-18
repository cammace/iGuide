package team6.iguide;

import android.content.Context;
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
import com.mapbox.mapboxsdk.api.ILatLng;
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

    MapView mv;
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    String boundingBox = "(29.709354854765827,-95.35668790340424,29.731896194504913,-95.31928449869156);";
    ArrayList<LatLng> resultBBList = new ArrayList<>();
    View progressBar;
    MainActivity mainActivity = new MainActivity();

    public void executeSearch(Context context, MapView mapView, String value, View PB){

        progressBar = PB;
        progressBar.setVisibility(View.VISIBLE);

        mv = mapView;
        mapContext = context;

        value = value.replace(" ", "%20");

        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
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

    private void fetchJsonResponse(String URI) {

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
                // TODO Auto-generated method stub
                Log.e("Search.Volley", "onErrorResponse ", error);
                Toast.makeText(mapContext, "Search Timed out, Try again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

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
                if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")) {
                    pinCount++;
                }
            }

            if (pinCount == 1) {
                // Check if the search returned a building
                if (q.get(0).getTags().getBuilding() != null || q.get(0).getTags().getLanduse() != null || q.get(0).getTags().getLeisure() != null) {
                    Marker marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                            q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, 0));
                    marker.setMarker(mapContext.getResources().getDrawable(R.drawable.red_pin));
                    mv.addMarker(marker);
                    //mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    //mv.getController().setZoom(19);
                    mv.getController().setZoomAnimated(19, new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()), true, true);

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
                    marker.setMarker(mapContext.getResources().getDrawable(R.drawable.red_pin));
                    mv.addMarker(marker);
                    //mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                    //mv.getController().setZoom(21);
                    mv.getController().setZoomAnimated(19, new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()), true, true);

                }
            } else {
                for (int i = 0; i < pinCount; i++) {
                    if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")) {
                        Marker marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                                q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                        marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, i));
                        marker.setMarker(mapContext.getResources().getDrawable(R.drawable.red_pin));
                        mv.addMarker(marker);

                        resultBBList.add(new LatLng(q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));

                        //mv.getController().setZoom(17);
                    }
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
