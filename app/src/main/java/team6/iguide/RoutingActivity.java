package team6.iguide;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    private Toolbar toolbar;
    private GraphhopperModel route;
    PathOverlay line;
    MapView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routing_activity);

        toolbar = (Toolbar) findViewById(R.id.routing_toolbar);
        //setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        getWindow().setStatusBarColor(getResources().getColor(R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(RoutingActivity.this);
            }
        });

        begindirectionFAB();

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
        desMarker.setMarker(this.getResources().getDrawable(R.drawable.red_pin));
        mv.addMarker(desMarker);

        Marker startMarker = new Marker(null, null, new LatLng(route.getPaths().get(0).getPoints().getCoordinates().get(route.getPaths().get(0).getPoints().getCoordinates().size()-1).get(1),
                route.getPaths().get(0).getPoints().getCoordinates().get(route.getPaths().get(0).getPoints().getCoordinates().size()-1).get(0)));
        startMarker.setMarker(this.getResources().getDrawable(R.drawable.green_pin));
        mv.addMarker(startMarker);

    }

    public void drawRoute(){

        //line = new PathOverlay(mapContext.getResources().getColor(R.color.RoutePrimaryColor), 10);

        line = new PathOverlay(this.getResources().getColor(R.color.RoutePrimaryColor), 10);

        for(int a =0; a<route.getPaths().get(0).getPoints().getCoordinates().size(); a++ )
            line.addPoint(route.getPaths().get(0).getPoints().getCoordinates().get(a).get(1),
                    route.getPaths().get(0).getPoints().getCoordinates().get(a).get(0));


        mv.getOverlays().add(line);
        mv.invalidate();
    }

    private void begindirectionFAB(){
        // FAB for myLocationButton
        FloatingActionButton FAB;
        FAB = (FloatingActionButton) findViewById(R.id.begin_direction_fab);
        FAB.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.BeginDirectionFABColor)));
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("RoutingActivity", "begin Routing");
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

        return(sb.toString());
    }

    public String formatRouteDistance(double meters){
        double miles = meters * .00062137;
        DecimalFormat df = new DecimalFormat("#.##");
        miles = Double.valueOf(df.format(miles));

        StringBuilder sb = new StringBuilder();
        sb.append(miles);
        sb.append(" Miles");
        return(sb.toString());
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
