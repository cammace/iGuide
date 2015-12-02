package team6.iguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

public class CampusIssueReportActivity extends AppCompatActivity{

    double issueLat;
    double issueLon;
    private Spinner typeSpinner;
    private MapView mv;
    private View view;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_issue_report_activity);
        view = findViewById(R.id.report_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavUtils.navigateUpFromSameTask(CampusIssueReportActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        issueLat = bundle.getDouble("LOCATIONLAT");
        issueLon = bundle.getDouble("LOCATIONLON");

        // We display a small portion of a map, therefore we initialize it here.
        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(issueLat, issueLon));
        mv.setZoom(20);
        mv.setMinZoomLevel(20);
        mv.setMaxZoomLevel(20);

        // We create the issue type (category) here
        typeSpinner = (Spinner)findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_type, R.layout.spinner_title);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        typeSpinner.setAdapter(adapter);

        // This is where we handle what happens when the user clicks the map. We start the
        // UserInputLocationActivity and pass it the current issueLat and issueLon
        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putDouble("LOCATIONLAT", issueLat);
                bundle.putDouble("LOCATIONLON", issueLon);

                Intent intent = new Intent(CampusIssueReportActivity.this, UserInputLocationActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);

            }
        });
    }// End onCreate

    public void createIssue(String type, String title, String description, String room, Double issueLat, Double issueLon){
        // Once all fields are checked and the user submits the issue, we need to post to the server.
        // We do this here.
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // We first build the uri
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("iguide.heliohost.org")
                .appendPath("create_marker.php")
                .appendQueryParameter("type", type)
                .appendQueryParameter("title", title)
                .appendQueryParameter("description", description)
                .appendQueryParameter("room", room)
                .appendQueryParameter("status", "Open")
                .appendQueryParameter("marker_lat", issueLat.toString())
                .appendQueryParameter("marker_lon", issueLon.toString());

        String Url = builder.build().toString();

        StringRequest req = new StringRequest(Request.Method.POST, Url,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.v("issue.Volley", "Volley Response: " + response);
                MainActivity.userReportedIssue = true;
                Snackbar.make(MainActivity.mapContainer, "Issue created successfully", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Issue created successfully", Toast.LENGTH_SHORT).show();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(MainActivity.mapContainer, "Error occurred creating issue", Snackbar.LENGTH_LONG).show();
                Log.e("issue.Volley", "onErrorResponse: ", error);

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
    }// End createIssue

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This method handles actions when a toolbar item is clicked on.
        int id = item.getItemId();

        // When the user submits the issue, we first get the values the user entered and then check
        // to make sure the required fields aren't empty. if all is well we create the issue.
        if (id == R.id.submit) {
            EditText issueTitle = (EditText)findViewById(R.id.issue_title);
            EditText issueDescription = (EditText)findViewById(R.id.issue_description);
            EditText issueRoom = (EditText)findViewById(R.id.issue_room);

            String title = issueTitle.getText().toString();
            String description = issueDescription.getText().toString();
            String room = issueRoom.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();

            if(type.equals("Choose One..")) Snackbar.make(view, "You didn't select a type of issue", Snackbar.LENGTH_LONG).show();
            else if(title.matches("")) Snackbar.make(view, "You didn't enter a title", Snackbar.LENGTH_LONG).show();
            else if(type.matches("Other") && description.matches("")) Snackbar.make(view, "A other issue requires a description", Snackbar.LENGTH_LONG).show();
            else if((type.equals("Room temperature too hot") || type.equals("Room temperature too cold")) && room.matches(""))  Snackbar.make(view, "A room number's required for the specific issue", Snackbar.LENGTH_LONG).show();

            else createIssue(type, title, description, room, issueLat, issueLon);
        }
        return true;
    }// End onOptionsItemSelected

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_campus_issue_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }// End onCreateOptionsMenu

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Whenever the user returns from UserInputLocationActivity, we need to update the new
        // location displayed on the mini map.

        if(resultCode == RESULT_OK){

            Bundle bundle = data.getExtras();
            issueLat = bundle.getDouble("LOCATIONLAT");
            issueLon = bundle.getDouble("LOCATIONLON");

            mv.getController().animateTo(new LatLng(issueLat, issueLon));
        }
    }// End onActivityResult
}// End CampusIssueReportActivity