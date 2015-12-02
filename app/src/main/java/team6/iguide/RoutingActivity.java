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

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import team6.iguide.GraphhopperModel.GraphhopperModel;

public class RoutingActivity extends AppCompatActivity {
    // Our routing activity which is displayed when the user request a route.

    private GraphhopperModel route;
    PathOverlay line;
    MapView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routing_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.routing_toolbar);
        //setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(RoutingActivity.this);
            }
        });

        beginDirectionFAB();

        Bundle bundle = getIntent().getExtras();
        Serializable serializable = bundle.getSerializable("ROUTING_RESULTS");
        route = ((GraphhopperModel) serializable);

        TextView timeTextView = (TextView)findViewById(R.id.route_time);
        timeTextView.setText(formatRouteTime(route.getPaths().get(0).getTime()));

        TextView distanceTextView = (TextView)findViewById(R.id.route_distance);
        distanceTextView.setText(formatRouteDistance(route.getPaths().get(0).getDistance()));

        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(29.7199489, -95.3422334));
        mv.setUserLocationEnabled(true);

        //for(int i=0; i<4; i++) System.out.println(route.getPaths().get(0).getBbox().get(i));

        double padding = 0.005;
        mv.zoomToBoundingBox(new BoundingBox(
                route.getPaths().get(0).getBbox().get(3) + padding, // East
                route.getPaths().get(0).getBbox().get(2) + padding, // North
                route.getPaths().get(0).getBbox().get(1) - padding, // West
                route.getPaths().get(0).getBbox().get(0) - padding), true, true); // South





        BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);

        drawRoute();

        Marker desMarker = new Marker(null, null, new LatLng(route.getPaths().get(0).getPoints().getCoordinates().get(0).get(1),
                route.getPaths().get(0).getPoints().getCoordinates().get(0).get(0)));
        desMarker.setMarker(ContextCompat.getDrawable(this, R.drawable.red_pin));
        mv.addMarker(desMarker);

        Marker startMarker = new Marker(null, null, new LatLng(route.getPaths().get(0).getPoints().getCoordinates().get(route.getPaths().get(0).getPoints().getCoordinates().size()-1).get(1),
                route.getPaths().get(0).getPoints().getCoordinates().get(route.getPaths().get(0).getPoints().getCoordinates().size()-1).get(0)));
        startMarker.setMarker(ContextCompat.getDrawable(this, R.drawable.green_pin));
        mv.addMarker(startMarker);

    }

    public void drawRoute(){

        //line = new PathOverlay(mapContext.getResources().getColor(R.color.RoutePrimaryColor), 10);

        line = new PathOverlay(ContextCompat.getColor(this, R.color.RoutePrimaryColor), 10);

        for(int a =0; a<route.getPaths().get(0).getPoints().getCoordinates().size(); a++ )
            line.addPoint(route.getPaths().get(0).getPoints().getCoordinates().get(a).get(1),
                    route.getPaths().get(0).getPoints().getCoordinates().get(a).get(0));


        mv.getOverlays().add(line);
        mv.invalidate();
    }

    private void beginDirectionFAB(){
        // FAB for myLocationButton
        FloatingActionButton FAB;
        FAB = (FloatingActionButton) findViewById(R.id.begin_direction_fab);
        FAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.BeginDirectionFABColor)));
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Turn-by-turn navigation coming soon", Toast.LENGTH_SHORT).show();
                //mv.goToUserLocation(true);

            }
        });
    }

    public static String formatRouteTime(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if(seconds >= 30) minutes = minutes + 1;

        StringBuilder sb = new StringBuilder(64);
        if(days != 0){
            sb.append(days);
            sb.append(" Days ");
        }
        if(hours != 0){
            sb.append(hours);
            sb.append(" Hours ");
        }
        if(minutes != 0){
            sb.append(minutes);
            sb.append(" Minutes ");
        }
        if(days == 0 && hours == 0 && minutes == 0) {
            sb.append(seconds);
            sb.append(" Seconds ");
        }
        return(sb.toString());
    }

    public String formatRouteDistance(double meters){
        double miles = meters * .00062137;
        DecimalFormat df = new DecimalFormat("#.##");
        miles = Double.valueOf(df.format(miles));

        return(miles + " Miles");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.setUserLocationEnabled(true);
        mv.getUserLocationOverlay().setDirectionArrowBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_arrow));
        mv.getUserLocationOverlay().setPersonBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_dot));
        //mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);
        //mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.NONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.setUserLocationEnabled(false);
    }

}
