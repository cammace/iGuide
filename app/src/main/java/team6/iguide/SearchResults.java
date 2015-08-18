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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.Serializable;

public class SearchResults extends Activity {

    private RequestQueue mRequestQueue;
    String myUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view is temporary just to show search output
        setContentView(R.layout.search_temp);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();

        // Get the search query
        String query = intent.getStringExtra(SearchManager.QUERY);

        createURI(query);
        mRequestQueue = Volley.newRequestQueue(this);   // Create queue for volley
        fetchJsonResponse();


    }

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
    }

    private void fetchJsonResponse() {

        JsonArrayRequest req = new JsonArrayRequest(myUrl, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                String nominatimData = response.toString();
                NominatimModel[] cam = gson.fromJson(nominatimData, NominatimModel[].class);

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

            }
        });
        mRequestQueue.add(req);
    }
}