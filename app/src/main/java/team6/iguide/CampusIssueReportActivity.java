package team6.iguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import team6.iguide.OverpassModel.OverpassModel;

public class CampusIssueReportActivity extends AppCompatActivity{

    private Toolbar toolbar;
    double issueLat;
    double issueLon;
    Spinner typeSpinner;
    MapView mv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_issue_report_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavUtils.navigateUpFromSameTask(CampusIssueReportActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        issueLat = getIntent().getExtras().getBundle("BUNDLE").getDouble("LOCATIONLAT");
        issueLon = getIntent().getExtras().getBundle("BUNDLE").getDouble("LOCATIONLON");


        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(issueLat, issueLon));
        mv.setZoom(20);
        mv.setMinZoomLevel(20);
        mv.setMaxZoomLevel(20);

        typeSpinner = (Spinner)findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_type, R.layout.spinner_title);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        typeSpinner.setAdapter(adapter);

        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putDouble("LOCATIONLAT", issueLat);
                bundle.putDouble("LOCATIONLON", issueLon);

                Intent intent = new Intent(CampusIssueReportActivity.this, UserInputLocationActivity.class);
                intent.putExtra("BUNDLE", bundle);
                startActivityForResult(intent, 1);

            }
        });










    }

    public void createIssue(String type, String title, String description, String room, Double issueLat, Double issueLon){

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

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

        System.out.println(Url);

        StringRequest req = new StringRequest(Request.Method.POST, Url,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.v("issue.Volley", "Volley Response: " + response);
                Toast.makeText(getApplicationContext(), "Issue created successfully", Toast.LENGTH_SHORT).show();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Error occured creating issue", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This method handles actions when a toolbar item is clicked on.
        int id = item.getItemId();

        // If our floor level icon is selected we inflate the menu and wait for user to select a specific floor.
        if (id == R.id.submit) {
            EditText issueTitle = (EditText)findViewById(R.id.issue_title);
            EditText issueDescription = (EditText)findViewById(R.id.issue_description);
            EditText issueRoom = (EditText)findViewById(R.id.issue_room);

            String title = issueTitle.getText().toString();
            String description = issueDescription.getText().toString();
            String room = issueRoom.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();

            if(type.equals("Choose One.."))
                Toast.makeText(getApplicationContext(), "You didn't select a type of issue", Toast.LENGTH_SHORT).show();
            else if(title.matches("")) Toast.makeText(getApplicationContext(), "You didn't enter a title", Toast.LENGTH_SHORT).show();
            else if(type.matches("Other") && description.matches("")) Toast.makeText(getApplicationContext(), "A other issue requires a description", Toast.LENGTH_SHORT).show();
            else if((type.equals("Room temperature too hot") || type.equals("Room temperature too cold")) && room.matches("")) Toast.makeText(getApplicationContext(), "A room number's required for the specific issue", Toast.LENGTH_SHORT).show();

            else {
                createIssue(type, title, description, room, issueLat, issueLon);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_campus_issue_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_OK){

            Bundle bundle = data.getParcelableExtra("BUNDLE");
            LatLng location = bundle.getParcelable("LOCATION");

            issueLat = location.getLatitude();
            issueLon = location.getLongitude();

            mv.getController().animateTo(new LatLng(issueLat, issueLon));
        }
    }


}
