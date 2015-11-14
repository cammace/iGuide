package team6.iguide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

public class UserInputLocationActivity extends AppCompatActivity{

    private Toolbar toolbar;
    BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
    LatLng markerLatLng;
    MapView mv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_location_activity);

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

                markerLatLng = mv.getCenter();
                Bundle bundle = new Bundle();
                bundle.putParcelable("LOCATION", markerLatLng);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("BUNDLE", bundle);
                setResult(RESULT_OK, returnIntent);
                finish();

                // NavUtils.navigateUpFromSameTask(UserInputLocationActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        double centerLat = getIntent().getExtras().getBundle("BUNDLE").getDouble("LOCATIONLAT");
        double centerLon = getIntent().getExtras().getBundle("BUNDLE").getDouble("LOCATIONLON");

        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(centerLat, centerLon));
        mv.setZoom(20);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);
        mv.setMaxZoomLevel(21);



    }

    @Override
    public void onBackPressed(){
        markerLatLng = mv.getCenter();
        Bundle bundle = new Bundle();
        bundle.putParcelable("LOCATION", markerLatLng);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("BUNDLE", bundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
