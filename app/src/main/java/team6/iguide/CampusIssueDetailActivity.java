package team6.iguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CampusIssueDetailActivity extends AppCompatActivity {

    ArrayList<DetailItemRecycler> detailItems;
    CustomDetailItemAdapter adapter;
    String status;
    String title;
    String description;
    String type;
    String created;
    String updated;
    int pid;
    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
    public static final String inputFormat = "HH:mm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_campus_issue_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        title = bundle.getString("TITLE");
        status = bundle.getString("STATUS");
        description = bundle.getString("DESCRIPTION");
        type = bundle.getString("TYPE");
        created = bundle.getString("CREATEDAT");
        updated = bundle.getString("UPDATEDAT");
        pid = bundle.getInt("PID");


        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.PrimaryDarkColor));

        populateDetailedList();




















        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.BeginDirectionFABColor)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Build the alert dialog that displays when resolve issue is clicked
                AlertDialog.Builder resolveIssueConfirmation = new AlertDialog.Builder(CampusIssueDetailActivity.this);
                resolveIssueConfirmation.setTitle("Resolve Issue?");
                resolveIssueConfirmation.setMessage("This issue will be marked as resolved and disappear from the map permanently. Only resolve if you personally fixed the issue or know someone who did.");
                resolveIssueConfirmation.setPositiveButton("Resolve Issue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Issue Resolved", Toast.LENGTH_SHORT).show();

                                resolveIssue(pid);
                                finish();
                            }
                        });
                resolveIssueConfirmation.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do action when dialog is canceled, in our case, we don't have
                                // to do anything
                            }
                        });
                // Show the alert dialog
                resolveIssueConfirmation.show();
            }
        });
    }

    private void populateDetailedList() {

        detailItems = new ArrayList<>();

        if(status != null) detailItems.add(new DetailItemRecycler("Status", status));
        if(type != null) detailItems.add(new DetailItemRecycler("Category", type));
        if(!description.isEmpty()) detailItems.add(new DetailItemRecycler("Description", description));
        if(created != null) detailItems.add(new DetailItemRecycler("First created", formatTime(created)));
        if(!updated.equals("0000-00-00 00:00:00")) detailItems.add(new DetailItemRecycler("Last updated", updated));

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(CampusIssueDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new RecyclerAdapter(CampusIssueDetailActivity.this, detailItems);


        mRecyclerView.setAdapter(adapter);

    }

    public void resolveIssue(int pid){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String Url = "http://iguide.heliohost.org/delete_marker.php?pid=" + pid;

        StringRequest req = new StringRequest(Request.Method.POST, Url,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.v("issue.Volley", "Volley Response: " + response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error occurred resolving issue", Toast.LENGTH_SHORT).show();
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

    public static String formatTime(String datestr){
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, yyyy, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(datestr);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}

/*
Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
 */