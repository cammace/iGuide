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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailedInfoActivity extends AppCompatActivity{
    // This class is the activity for when a user clicks the marker InfoWindow for a place.

    private String ref;
    private String phone;
    private String website;
    private String address;
    private String fax;
    private String wiki;
    private String hours;
    private String formattedHours;
    private ArrayList<DetailItemModel> detailItems;
    private String wikiExtract;
    private RecyclerView.Adapter adapter;
    private RequestQueue mRequestQueue;
    public static final String inputFormat = "HH:mm";
    private String OC;
    private Double desLat;
    private Double desLon;
    private Double currentLat;
    private Double currentLon;
    private Boolean foundUserLocation;
    static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10;
    private Intent phoneCall;
    boolean phoneCallPermission;
    private View coordinatorLayout;

    private SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info_activity);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(DetailedInfoActivity.this);
                //overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        routeFAB();

        // "Unpack" the bundle and assign all the information to their corresponding variables.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("TITLE");
        ref = bundle.getString("REF");
        phone = bundle.getString("PHONE");
        address = bundle.getString("ADDRESS");
        website = bundle.getString("WEBSITE");
        wiki = bundle.getString("WIKI");
        fax = bundle.getString("FAX");
        hours = bundle.getString("HOURS");
        String image = bundle.getString("IMAGE");
        desLat = bundle.getDouble("DESLAT");
        desLon = bundle.getDouble("DESLON");
        foundUserLocation = bundle.getBoolean("FOUNDUSERLOCATION");

        if(foundUserLocation) {
            currentLat = bundle.getDouble("CURRENTLAT");
            currentLon = bundle.getDouble("CURRENTLON");
        }

        // This is where I download the image and replace the default no_image_grey with it.
        // In addition, I also tint the color of the image so that the toolbar icons can still be seen.
        ImageView imageView = (ImageView)findViewById(R.id.building_image);
        imageView.setImageResource(R.drawable.no_image_grey);
        if(image != null){
            new DownloadImageTask((ImageView) findViewById(R.id.building_image)).execute(image);
            imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        // I tell the compiler that getSupportActionBar will never be null since I just set it
        // above. This gets rid of the annoying warnings the compiler kept throwing at me.
        assert getSupportActionBar() != null;

        // I then set the toolbar title to whatever the location title is.
        getSupportActionBar().setTitle(title);

        // format the hours if the place has them.
        if(hours != null) formatHourString();

        mRequestQueue = Volley.newRequestQueue(this);
        if(wiki != null) getWikiSnippet();

        populateDetailedList();
    }// End onCreate

    private void populateDetailedList() {
        // This method is where we create the list and add it to the recyclerview.
        detailItems = new ArrayList<>();
        if(ref != null) detailItems.add(new DetailItemModel("Reference", ref));
        if(hours != null) detailItems.add(new DetailItemModel("Hours", formattedHours));
        if(phone != null) detailItems.add(new DetailItemModel("Phone", phone));
        if(website != null) detailItems.add(new DetailItemModel("Website", website));
        if(address != null) detailItems.add(new DetailItemModel("Address", address));
        if(fax != null) detailItems.add(new DetailItemModel("Fax", fax));

        // Create the adapter to convert the array to views
        adapter = new RecyclerAdapter(this, detailItems);
        // Attach the adapter to a ListView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.details_list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        // We have to create a gesture detector inorder to understand what the users action is on the
        // recyclerview. Single tap is all we care about.
        final GestureDetector mGestureDetector = new GestureDetector(DetailedInfoActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // This is actually where we handle the users click on a recyclerview item.
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(),e.getY());
                if(child!=null && mGestureDetector.onTouchEvent(e)) {

                    switch(detailItems.get(rv.getChildAdapterPosition(child)).title){
                        case "Phone":
                            // Play the default item click sound.
                            coordinatorLayout.playSoundEffect(SoundEffectConstants.CLICK);

                            // since we are placing a call, we need to first make sure the user gave
                            // us permission. if so we place the call, else we ask for permission.
                            phoneCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detailItems.get(rv.getChildAdapterPosition(child)).description));
                            if(ContextCompat.checkSelfPermission(DetailedInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(DetailedInfoActivity.this, new String[] {Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                            if(phoneCallPermission) startActivity(phoneCall);
                            break;
                        case "Website":
                            coordinatorLayout.playSoundEffect(SoundEffectConstants.CLICK);
                            Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(detailItems.get(rv.getChildAdapterPosition(child)).description));
                            startActivity(openWebsite);
                            break;
                        case "Wikipedia":
                            coordinatorLayout.playSoundEffect(SoundEffectConstants.CLICK);
                            String[] wikiLinkParts = wiki.split(":");
                            String wikiURL = "https://" + wikiLinkParts[0] + ".wikipedia.org/wiki/" + wikiLinkParts[1].replace(" ", "_");
                            Intent openWiki = new Intent(Intent.ACTION_VIEW, Uri.parse(wikiURL));
                            startActivity(openWiki);
                            break;
                    }// End switch
                    return true;
                }
                return false;
            }// End onInterceptTouchEvent

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {/* We don't use this*/}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {/* We don't use this*/}
        });
    }// End populateDetailedList

    private void formatHourString(){
        // Honestly this is a mess of a method, It's just something I threw together for the hell of it
        // and only works half the time. It's suppose to format the hours variable to make it more
        // human readable but since OpenStreetMap hours has so many different formats, its hard to
        // accomplish what I wanted. Nevertheless, I kept it in the application.

        Calendar calendar = Calendar.getInstance();

        if(hours.contains("Su ") && calendar.get(Calendar.DAY_OF_WEEK) == 1) formattedHours = hours.substring(hours.indexOf("Su ")+2);
        else if(hours.contains("Mo ") && calendar.get(Calendar.DAY_OF_WEEK) == 2) formattedHours = hours.substring(hours.indexOf("Mo ")+2);
        else if(hours.contains("Tu ") && calendar.get(Calendar.DAY_OF_WEEK) == 3) formattedHours = hours.substring(hours.indexOf("Tu ")+2);
        else if(hours.contains("We ") && calendar.get(Calendar.DAY_OF_WEEK) == 4) formattedHours = hours.substring(hours.indexOf("We ")+2);
        else if(hours.contains("Th ") && calendar.get(Calendar.DAY_OF_WEEK) == 5) formattedHours = hours.substring(hours.indexOf("Th ")+2);
        else if(hours.contains("Fr ") && calendar.get(Calendar.DAY_OF_WEEK) == 6) formattedHours = hours.substring(hours.indexOf("Fr ")+2);
        else if(hours.contains("Sa ") && calendar.get(Calendar.DAY_OF_WEEK) == 7) formattedHours = hours.substring(hours.indexOf("Sa ")+2);

        else if(hours.contains("Mo-Fr ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 6) && hours.contains(";")) formattedHours = hours.substring(hours.indexOf("Mo-Fr ")+6, hours.indexOf(";"));
        else if(hours.contains("Mo-Th ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 5)) formattedHours = hours.substring(hours.indexOf("Mo-Th ")+6, hours.indexOf(";"));
        //else if(hours.contains("Tu-Sa ") && (calendar.get(Calendar.DAY_OF_WEEK) >=3) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 5)) formattedHours = hours.substring(hours.indexOf("Mo-Th ")+6, hours.indexOf(";"));)
        else {
            formattedHours = hours;
            return;
        }

        String[] hours = formattedHours.split("-");
        compareDates(hours[0], hours[1]);

        String opening = convert24to12(hours[0]);
        String closing = convert24to12(hours[1]);
        if(opening.startsWith("0")) opening = opening.substring(1);
        if(closing.startsWith("0")) closing = closing.substring(1);

        formattedHours = opening + " - " + closing + ", " + OC;

    }

    private void getWikiSnippet() {
        // This is where we use the OpenStreetMap wiki link that usually looks like this
        // "en:UniversityofHouston" and create a URI, call the API, and get the first part of the summary.

        String[] wikiLinkParts = wiki.split(":");

        String wikiLink = "https://" + wikiLinkParts[0] + ".wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&titles=" + wikiLinkParts[1].replace(" ", "%20");

        JsonObjectRequest req = new JsonObjectRequest(wikiLink, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                String wikiData = response.toString();

                wikiExtract = wikiData.substring(wikiData.indexOf("extract") +10);
                wikiExtract = wikiExtract.substring(0,wikiExtract.length()-5);
                detailItems.add(new DetailItemModel("Wikipedia", wikiExtract));
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DetailedInfoActivity", "onErrorResponse: ", error);
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
    }// End getWikiSnippet

    private void compareDates(String compareStringOne , String compareStringTwo){
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDate(hour + ":" + minute);
        Date dateCompareOne = parseDate(compareStringOne);
        Date dateCompareTwo = parseDate(compareStringTwo);

        if ( dateCompareOne.before( date ) && dateCompareTwo.after(date)) OC = "Open now";
        else OC = "Closed now";
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static String convert24to12(String time) {
        String convertedTime ="";
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.US);
            //DateFormat dateFormat = DateFormat.getTimeInstance();
            Date date = parseFormat.parse(time);
            convertedTime=displayFormat.format(date);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return convertedTime;
    }

    private void routeFAB(){
        // FAB for myLocationButton
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.routing_fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(foundUserLocation) {
                    Graphhopper graphhopper = new Graphhopper();
                    graphhopper.executeRoute(getApplicationContext(), desLat, desLon, new LatLng(currentLat, currentLon));
                }
                else Snackbar.make(findViewById(R.id.coordinatorLayout), "You have to be on campus in order to route", Snackbar.LENGTH_SHORT).show();

                /*
                if(mv.getUserLocation() == null){
                    Toast.makeText(mContext, "Can't find your location on map", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(scrollLimit.contains(mv.getUserLocation())){
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.displayRouting(mView.getContext(), mv, desLat, desLon);
                    close();
                } else Toast.makeText(mContext, mContext.getString(R.string.userLocationNotWithinBB), Toast.LENGTH_SHORT).show();

*/

            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                System.out.println("error");
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    phoneCallPermission = true;
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Snackbar.make(coordinatorLayout, "Allow app permission to make calls", Snackbar.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

}
