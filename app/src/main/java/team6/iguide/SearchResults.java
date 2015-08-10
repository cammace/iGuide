package team6.iguide;

// http://developer.android.com/guide/topics/search/search-dialog.html

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

public class SearchResults extends Activity{

    private RequestQueue mRequestQueue;
    String myUrl;
    String boundBox = "&viewbox=-95.35668790340424,29.731896194504913,-95.31928449869156,29.709354854765827&bounded=1";
    public String jason;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.search);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    public void doMySearch(String query){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("nominatim.openstreetmap.org")
                .appendPath("search")
                .appendQueryParameter("q", query.replace(" ", "_"))
                .appendQueryParameter("format", "json");
        myUrl = builder.build().toString();
        myUrl = myUrl + boundBox;

        //System.out.println(myUrl);
        //Toast.makeText(getApplicationContext(), myUrl, Toast.LENGTH_SHORT).show();

        mRequestQueue = Volley.newRequestQueue(this);
        fetchJsonResponse();
        System.out.println(jason);

    }

    private void fetchJsonResponse() {

        JsonArrayRequest req = new JsonArrayRequest(myUrl, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                //System.out.println(response);

                Gson gson = new Gson();
                String nominatimData = response.toString();
                NominatimModel[] cam = gson.fromJson(nominatimData, NominatimModel[].class);

                jason = cam[0].getLat();



                //System.out.println(gson.toJson(cam));

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });
        mRequestQueue.add(req);
    }




}
