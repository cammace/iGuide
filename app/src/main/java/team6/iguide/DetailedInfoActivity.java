package team6.iguide;

// http://stackoverflow.com/questions/5776851/load-image-from-url
// Future plans: add method to check if current date is a holiday and if so display it to user within
// hour item.

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import team6.iguide.OverpassModel.OverpassModel;

public class DetailedInfoActivity extends AppCompatActivity{

    private Toolbar toolbar;
    String title;
    String ref;
    String phone;
    String website;
    String address;
    String fax;
    String wiki;
    String hours;
    String formatedHours;
    String image;
    String wikiLink;
    ArrayList<DetailItem> detailItems;
    String wikiExtract;
    CustomDetailItemAdapter adapter;
    private RequestQueue mRequestQueue;
    public static final String inputFormat = "HH:mm";
    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;
    String OC;

    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        routeFAB();

        // Makes status bar color same as PrimaryDarkColor
        getWindow().setStatusBarColor(getResources().getColor(R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(DetailedInfoActivity.this);
                //overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        title = bundle.getString("TITLE");
        ref = bundle.getString("REF");
        phone = bundle.getString("PHONE");
        address = bundle.getString("ADDRESS");
        website = bundle.getString("WEBSITE");
        wiki = bundle.getString("WIKI");
        fax = bundle.getString("FAX");
        hours = bundle.getString("HOURS");
        image = bundle.getString("IMAGE");

        if(image != null) new DownloadImageTask((ImageView) findViewById(R.id.building_image)).execute(image);


        TextView titleView = (TextView) findViewById(R.id.detail_info_title);
        titleView.setText(title);

        if(hours != null) formatHourString();

        mRequestQueue = Volley.newRequestQueue(this);
        if(wiki != null) getWikiSnippet();

        populateDetailedList();
    }

    private void populateDetailedList() {
        detailItems = new ArrayList<DetailItem>();
        if(ref != null) detailItems.add(new DetailItem("Reference", ref));
        if(hours != null) detailItems.add(new DetailItem("Hours", formatedHours));
        if(phone != null) detailItems.add(new DetailItem("Phone", phone));
        if(website != null) detailItems.add(new DetailItem("Website", website));
        if(address != null) detailItems.add(new DetailItem("Address", address));
        if(fax != null) detailItems.add(new DetailItem("Fax", fax));

        // Create the adapter to convert the array to views
        adapter = new CustomDetailItemAdapter(this, detailItems);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.details_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(detailItems.get(position).key){
                    case "Phone":
                        Intent phoneCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detailItems.get(position).value));
                        startActivity(phoneCall);
                        break;
                    case "Website":
                        Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(detailItems.get(position).value));
                        startActivity(openWebsite);
                        break;
                    case "Wikipedia":
                        String[] wikiLinkParts = wiki.split(":");
                        String wikiURL = "https://" + wikiLinkParts[0] + ".wikipedia.org/wiki/" + wikiLinkParts[1].replace(" ", "_");
                        Intent openWiki = new Intent(Intent.ACTION_VIEW, Uri.parse(wikiURL));
                        startActivity(openWiki);
                        break;
                }
            }
        });
    }

    private void formatHourString(){

        Calendar calendar = Calendar.getInstance();

        if(hours.contains("Su ") && calendar.get(Calendar.DAY_OF_WEEK) == 1) formatedHours = hours.substring(hours.indexOf("Su ")+2);
        else if(hours.contains("Mo ") && calendar.get(Calendar.DAY_OF_WEEK) == 2) formatedHours = hours.substring(hours.indexOf("Mo ")+2);
        else if(hours.contains("Tu ") && calendar.get(Calendar.DAY_OF_WEEK) == 3) formatedHours = hours.substring(hours.indexOf("Tu ")+2);
        else if(hours.contains("We ") && calendar.get(Calendar.DAY_OF_WEEK) == 4) formatedHours = hours.substring(hours.indexOf("We ")+2);
        else if(hours.contains("Th ") && calendar.get(Calendar.DAY_OF_WEEK) == 5) formatedHours = hours.substring(hours.indexOf("Th ")+2);
        else if(hours.contains("Fr ") && calendar.get(Calendar.DAY_OF_WEEK) == 6) formatedHours = hours.substring(hours.indexOf("Fr ")+2);
        else if(hours.contains("Sa ") && calendar.get(Calendar.DAY_OF_WEEK) == 7) formatedHours = hours.substring(hours.indexOf("Sa ")+2);

        else if(hours.contains("Mo-Fr ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 6) && hours.contains(";")) formatedHours = hours.substring(hours.indexOf("Mo-Fr ")+6, hours.indexOf(";"));
        else if(hours.contains("Mo-Th ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 5)) formatedHours = hours.substring(hours.indexOf("Mo-Th ")+6, hours.indexOf(";"));
        else {
            formatedHours = hours;
            return;
        }

        String[] hours = formatedHours.split("-");
        compareDates(hours[0], hours[1]);

        String opening = convert24to12(hours[0]);
        String closing = convert24to12(hours[1]);
        if(opening.startsWith("0")) opening = opening.substring(1);
        if(closing.startsWith("0")) closing = closing.substring(1);

        formatedHours = opening + " - " + closing + ", " + OC;

    }

    private void getWikiSnippet(){

        String[] wikiLinkParts = wiki.split(":");

        wikiLink = "https://" + wikiLinkParts[0] + ".wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&titles=" + wikiLinkParts[1].replace(" ", "%20");
        //System.out.println(wikiLink);
        fetchJsonResponse();
    }

    private void fetchJsonResponse() {

        JsonObjectRequest req = new JsonObjectRequest(wikiLink, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Gson gson = new Gson();
                String wikiData = response.toString();
                //System.out.println(overpassData);
                //OverpassModel cam = gson.fromJson(overpassData, OverpassModel.class);

                //OverpassElement s = new OverpassElement();
                //String z = Double.toString(s.getLat());
                //String z = cam.getElements().toString();

                wikiExtract = wikiData.substring(wikiData.indexOf("extract") +10);
                wikiExtract = wikiExtract.substring(0,wikiExtract.length()-5);
                // TODO implement method to shorten wiki if very long
                //System.out.println(wikiExtract.trim().split(" ").length);
                detailItems.add(new DetailItem("Wikipedia", wikiExtract));
                adapter.notifyDataSetChanged();
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

    private void compareDates(String compareStringOne , String compareStringTwo){
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);
        dateCompareOne = parseDate(compareStringOne);
        dateCompareTwo = parseDate(compareStringTwo);

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
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
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

                System.out.println("begin routing");


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

}
