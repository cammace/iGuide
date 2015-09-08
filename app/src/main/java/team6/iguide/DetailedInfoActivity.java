package team6.iguide;

// http://stackoverflow.com/questions/5776851/load-image-from-url
// Future plans: add method to check if current date is a holiday and if so display it to user within
// hour item.

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        populateDetailedList();

        //imageView = (ImageView)findViewById(R.id.place_image);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);



    }

    private void populateDetailedList() {
        ArrayList<DetailItem> detailItems = new ArrayList<DetailItem>();
        if(ref != null) detailItems.add(new DetailItem("Reference", ref));
        if(phone != null) detailItems.add(new DetailItem("Phone", phone));
        if(website != null) detailItems.add(new DetailItem("Website", website));
        if(address != null) detailItems.add(new DetailItem("Address", address));
        if(wiki != null) detailItems.add(new DetailItem("Wikipedia", wiki));
        if(fax != null) detailItems.add(new DetailItem("Fax", fax));
        if(hours != null) detailItems.add(new DetailItem("Hours", formatedHours));

        // Create the adapter to convert the array to views
        CustomDetailItemAdapter adapter = new CustomDetailItemAdapter(this, detailItems);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.details_list);
        listView.setAdapter(adapter);


    }

    private void formatHourString(){

        Calendar calendar = Calendar.getInstance();

        if(hours.contains("Su ") && calendar.get(Calendar.DAY_OF_WEEK) == 1) formatedHours = hours.substring(hours.indexOf("Su ")+2);
        if(hours.contains("Mo ") && calendar.get(Calendar.DAY_OF_WEEK) == 2) formatedHours = hours.substring(hours.indexOf("Mo ")+2);
        if(hours.contains("Tu ") && calendar.get(Calendar.DAY_OF_WEEK) == 3) formatedHours = hours.substring(hours.indexOf("Tu ")+2);
        if(hours.contains("We ") && calendar.get(Calendar.DAY_OF_WEEK) == 4) formatedHours = hours.substring(hours.indexOf("We ")+2);
        if(hours.contains("Th ") && calendar.get(Calendar.DAY_OF_WEEK) == 5) formatedHours = hours.substring(hours.indexOf("Th ")+2);
        if(hours.contains("Fr ") && calendar.get(Calendar.DAY_OF_WEEK) == 6) formatedHours = hours.substring(hours.indexOf("Fr ")+2);
        if(hours.contains("Sa ") && calendar.get(Calendar.DAY_OF_WEEK) == 7) formatedHours = hours.substring(hours.indexOf("Sa ")+2);

        if(hours.contains("Mo-Fr ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 6)) formatedHours = hours.substring(hours.indexOf("Mo-Fr ")+6, hours.indexOf(";"));
        if(hours.contains("Mo-Th ") && (calendar.get(Calendar.DAY_OF_WEEK) >=2) &&(calendar.get(Calendar.DAY_OF_WEEK) <= 5)) formatedHours = hours.substring(hours.indexOf("Mo-Th ")+6, hours.indexOf(";"));

        String[] hours = formatedHours.split("-");
        compareDates(hours[0], hours[1]);


        System.out.println(calendar.get(Calendar.HOUR));
        //calendar.get(Calendar.MINUTE));
        //System.out.println(isNowBetweenDateTime(start, end));

        String opening = convert24to12(hours[0]);
        String closing = convert24to12(hours[1]);
        if(opening.startsWith("0")) opening = opening.substring(1);
        if(closing.startsWith("0")) closing = closing.substring(1);

        formatedHours = opening + " - " + closing + ", " + OC;


        //System.out.println(calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE));


/*
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int pm = calendar.get(Calendar.AM_PM);
        Date date = parseDate(hour + ":" + minute + " " + pm);
        System.out.println(date);
*/


        System.out.println(formatedHours);










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
