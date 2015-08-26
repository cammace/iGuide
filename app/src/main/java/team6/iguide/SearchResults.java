package team6.iguide;

// http://developer.android.com/guide/topics/search/search-dialog.html

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

public class SearchResults extends Activity {

    private RequestQueue mRequestQueue;
    String myUrl;
    String boundingBox = "(29.717436030817996,-95.34679055213928,29.726082564728284,-95.33547163009644);";
    String URI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view is temporary just to show search output
        setContentView(R.layout.search_temp);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();

        // Get the search query
        String query = intent.getStringExtra(SearchManager.QUERY);

        //createURI(query);

        URI = "http://overpass-api.de/api/interpreter?" +
                "data=[out:json][timeout:25];(" +
                "node[\"name\"~\"" + query + "\"]" + boundingBox +
                "way[\"name\"~\"" + query + "\"]" + boundingBox +
                "relation[\"name\"~\"" + query + "\"]" + boundingBox +

                "node[\"ref\"~\"" + query + "\"]" + boundingBox +
                "way[\"ref\"~\"" + query + "\"]" + boundingBox +
                "relation[\"ref\"~\"" + query + "\"]" + boundingBox +
                ");" +
                "out%20center;" +
                ">;" +
                "out%20skel%20qt;";

        System.out.println(URI);



        mRequestQueue = Volley.newRequestQueue(this);   // Create queue for volley
        fetchJsonResponse();


    }
/*
    public void createURI(String query) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("nominatim.openstreetmap.org")
                .appendPath("search")
                .appendQueryParameter("q", query.replace(" ", "."))
                .appendQueryParameter("format", "json")
                .appendQueryParameter("viewbox", "-95.35668790340424,29.731896194504913,-95.31928449869156,29.709354854765827")
                .appendQueryParameter("bounded", "1");
        myUrl = builder.build().toString();
    }*/

    private void fetchJsonResponse() {

        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Gson gson = new Gson();
                String overpassData = response.toString();
                System.out.println(overpassData);
                OverpassModel cam = gson.fromJson(overpassData, OverpassModel.class);


                System.out.println(cam.getGenerator());



/*
                Intent resultIntent = new Intent();
                //resultIntent.putExtra("CAM", cam[0].getLat());
                Bundle bundle = new Bundle();


                bundle.putSerializable("CAM", cam);

                //bundle.putString("LAT", cam[0].getLat());
                //bundle.putString("LON", cam[0].getLon());
                resultIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();*/
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                System.out.println(error);

            }
        });
        mRequestQueue.add(req);
    }
}