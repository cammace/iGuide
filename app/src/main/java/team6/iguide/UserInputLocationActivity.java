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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

public class UserInputLocationActivity extends AppCompatActivity{

    BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
    LatLng markerLatLng;
    MapView mv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_location_activity);
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

                markerLatLng = mv.getCenter();
                Bundle bundle = new Bundle();
                bundle.putDouble("LOCATIONLAT", markerLatLng.getLatitude());
                bundle.putDouble("LOCATIONLON", markerLatLng.getLongitude());

                Intent returnIntent = new Intent();
                returnIntent.putExtras(bundle);
                setResult(RESULT_OK, returnIntent);
                finish();

                // NavUtils.navigateUpFromSameTask(UserInputLocationActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

        Bundle bundle = getIntent().getExtras();
        double centerLat = bundle.getDouble("LOCATIONLAT");
        double centerLon = bundle.getDouble("LOCATIONLON");

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
        bundle.putDouble("LOCATIONLAT", markerLatLng.getLatitude());
        bundle.putDouble("LOCATIONLON", markerLatLng.getLongitude());

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
