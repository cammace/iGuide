package team6.iguide;

import android.content.Context;
import android.widget.Toast;

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

import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

public class PointOfInterest {

    MapView mv;
    private RequestQueue mRequestQueue;
    OverpassModel results;
    Context mapContext;
    String boundingBox = "(29.709354854765827,-95.35668790340424,29.731896194504913,-95.31928449869156);";

    public void getPOI(Context context, MapView mapView){

        mv = mapView;
        mapContext = context;

        String URI = "https://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
                "node[\"amenity\"~\"atm\",i]" + boundingBox +
                "node[\"amenity\"~\"bar\",i]" + boundingBox +
                //"way[\"amenity\"~\"bar\",i]" + boundingBox +
                //"relation[\"amenity\"~\"bar\",i]" + boundingBox +
                ");out%20center;>;out%20skel%20qt;";
System.out.println(URI);
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

    public void showSearchResults() {

        List<OverpassElement> q = results.getElements();

        // Clear the mapview of any markers
        // mv.closeCurrentTooltip();
        // mv.clear();
        // mv.clearMarkerFocus();

        // Check if the search returns no results
        if (q.isEmpty()) {
            Toast.makeText(mapContext, "No Results", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < q.size(); i++) {
                if(q.get(i).getType().equals("node")) {
                    Marker marker = new Marker("ATM", "ATM", new LatLng(
                            q.get(i).getLat(), q.get(i).getLon()));
                    marker.setToolTip(new CustomInfoWindow(mapContext, mv, q, i));
                    if (q.get(i).getTags().getAmenity().equals("atm"))
                        marker.setMarker(mapContext.getResources().getDrawable(R.drawable.bank_18));
                    if (q.get(i).getTags().getAmenity().equals("bar"))
                        marker.setMarker(mapContext.getResources().getDrawable(R.drawable.bar_18));
                    mv.addMarker(marker);

                }
            }


        }
    }
}
