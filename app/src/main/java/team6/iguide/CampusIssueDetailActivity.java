package team6.iguide;

/***
 iGuide
 Copyright (C) 2015 Cameron Mace

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
    // This class is for the displaying detailed information about a single campus issue. it's
    // initiated when someone clicks the infowindow for one of the issues on the map.

    private String status;
    private String description;
    private String type;
    private String created;
    private String updated;
    private int pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_issue_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // We "unpack" the intent bundle and assign the values to their corresponding class variables.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("TITLE");
        status = bundle.getString("STATUS");
        description = bundle.getString("DESCRIPTION");
        type = bundle.getString("TYPE");
        created = bundle.getString("CREATEDAT");
        updated = bundle.getString("UPDATEDAT");
        pid = bundle.getInt("PID");

        // I tell the compiler that getSupportActionBar will never be null since I just set it
        // above. This gets rid of the annoying warnings the compiler kept throwing at me.
        assert getSupportActionBar() != null;

        // I then add the title and back button.
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Go ahead and create the FAB (Floating Action Button) and populate the recyclerview.
        resolveIssueFAB();
        populateDetailedList();
    }// End onCreate

    private void populateDetailedList() {
        // This method is where we add all the items to the recyclerview.

        // We create a list called detailItems and add the issue info to it.
        ArrayList<DetailItemModel> detailItems = new ArrayList<>();
        if(status != null) detailItems.add(new DetailItemModel("Status", status));
        if(type != null) detailItems.add(new DetailItemModel("Category", type));
        if(!description.isEmpty()) detailItems.add(new DetailItemModel("Description", description));
        if(created != null) detailItems.add(new DetailItemModel("First created", formatTime(created)));
        if(!updated.equals("0000-00-00 00:00:00")) detailItems.add(new DetailItemModel("Last updated", formatTime(updated)));

        // We then create the recyclerview and add the list.
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(CampusIssueDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new RecyclerAdapter(CampusIssueDetailActivity.this, detailItems);

        mRecyclerView.setAdapter(adapter);

    }// End populateDetailedList

    private void resolveIssue(int pid){
        // So because a user is able to resolve the issue, we need to update the database and this
        // is where we do that. The pid is how we identify what row to delete in database.
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

    private void resolveIssueFAB (){
        // We setup the FAB here and when its clicked we have a dialog that's displayed to confirm action.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.BeginDirectionFABColor)));
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
    }// End resolveIssueFAB

    private static String formatTime(String datestr){
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, yyyy, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);

        Date date;
        String str = null;
        try {
            date = inputFormat.parse(datestr);

            // Add 2 hours (7200000ms) to time since server timezone is cali time
            date.setTime(date.getTime() + 7200000);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }// End formatTime

}

