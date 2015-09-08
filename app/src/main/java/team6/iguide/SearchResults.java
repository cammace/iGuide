package team6.iguide;

// http://developer.android.com/guide/topics/search/search-dialog.html

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import team6.iguide.OverpassModel.OverpassModel;

public class SearchResults extends Activity {

    private RequestQueue mRequestQueue;
    String boundingBox = "(29.709354854765827,-95.35668790340424,29.731896194504913,-95.31928449869156);";
    String URI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Searching");
        progress.setMessage("Wait while loading...");
        progress.show();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // Set content view is temporary just to show search output
        setContentView(R.layout.search_temp);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();

        // Get the search query
        String query = intent.getStringExtra(SearchManager.QUERY);

        //System.out.println();


        //createURI(query);
        query = query.replace(" ", "%20");
        URI = "http://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
                "node[\"name\"~\"" + query + "\",i]" + boundingBox +
                "way[\"name\"~\"" + query + "\",i]" + boundingBox +
                "relation[\"name\"~\"" + query + "\",i]" + boundingBox +

                "node[\"ref\"~\"" + query + "\",i]" + boundingBox +
                "way[\"ref\"~\"" + query + "\",i]" + boundingBox +
                "relation[\"ref\"~\"" + query + "\",i]" + boundingBox +

                "node[\"amenity\"~\"" + query + "\",i]" + boundingBox +
                "way[\"amenity\"~\"" + query + "\",i]" + boundingBox +
                "relation[\"amenity\"~\"" + query + "\",i]" + boundingBox +
                ");" +
                "out%20center;" +
                ">;" +
                "out%20skel%20qt;";

        //System.out.println(URI);



        mRequestQueue = Volley.newRequestQueue(this);   // Create queue for volley
        fetchJsonResponse();


    }

    // TODO add ability to try other 2 servers if first one can't be reached.
    private void fetchJsonResponse() {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String overpassData = response.toString();
                //System.out.println(overpassData);
                OverpassModel cam = gson.fromJson(overpassData, OverpassModel.class);

                //OverpassElement s = new OverpassElement();
                //String z = Double.toString(s.getLat());
                //String z = cam.getElements().toString();




                Intent resultIntent = new Intent();
                //resultIntent.putExtra("CAM", cam[0].getLat());
                Bundle bundle = new Bundle();


                bundle.putSerializable("CAM", cam);

                //bundle.putString("LAT", cam[0].getLat());
                //bundle.putString("LON", cam[0].getLon());
                resultIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
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