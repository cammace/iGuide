package team6.iguide;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import team6.iguide.GraphhopperModel.GraphhopperModel;
import team6.iguide.OverpassModel.OverpassElement;

public class CustomInfoWindow extends InfoWindow {


    private Context mContext;
    String title;
    String ref;
    String address;
    String country;
    Double desLat;
    Double desLon;
    BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);


    public CustomInfoWindow(Context context, final MapView mv, final List<OverpassElement> searchResults, final int listPosition) {
        super(R.layout.infowindow_custom, mv);
        this.mContext = context;

        if(Build.VERSION.SDK_INT >= 21) {
            getView().findViewById(R.id.mainpanal).setClipToOutline(true);
            mView.setElevation(24);
        }

        if(searchResults.get(listPosition).getType().equals("node")){
            desLat = searchResults.get(listPosition).getLat();
            desLon = searchResults.get(listPosition).getLon();
        }else{
            desLat = searchResults.get(listPosition).getCenter().getLat();
            desLon = searchResults.get(listPosition).getCenter().getLon();
        }

        ImageButton routing = (ImageButton)mView.findViewById(R.id.routing_button);
        routing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(mv.getUserLocation() == null){
                    Toast.makeText(mContext, "Can't find your location on map", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(scrollLimit.contains(mv.getUserLocation())){
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.displayRouting(mView.getContext(), mv, desLat, desLon);
                    close();
                } else Toast.makeText(mContext, mContext.getString(R.string.userLocationNotWithinBB), Toast.LENGTH_SHORT).show();

            }
        });


        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Demonstrate custom onTouch() control

                    //TextView textView = (TextView) v.findViewById(R.id.title);

                    //Pair<View, String> p3 = Pair.create((View) textView, "detailed_title");
                    //ActivityOptions options = ActivityOptions.
                    //        makeSceneTransitionAnimation(mView.getContext(), p3);

                    //System.out.println(searchResults.get(listPosition).getTags().getName());

                    if(searchResults.get(listPosition).getTags().getAddrCountry() != null &&
                            searchResults.get(listPosition).getTags().getAddrCountry().equals("US")) country = "United States";
                    else searchResults.get(listPosition).getTags().getAddrCountry();

                    if(searchResults.get(listPosition).getTags().getAddrHousenumber() != null) {
                        address = searchResults.get(listPosition).getTags().getAddrHousenumber() + " " +
                                searchResults.get(listPosition).getTags().getAddrStreet() + " " + "\n" +
                                searchResults.get(listPosition).getTags().getAddrCity() + " " +
                                searchResults.get(listPosition).getTags().getAddrPostcode() + " " + "\n" +
                                country;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("TITLE", title);
                    bundle.putString("REF", ref);
                    bundle.putString("PHONE", searchResults.get(listPosition).getTags().getPhone());
                    bundle.putString("WEBSITE", searchResults.get(listPosition).getTags().getWebsite());
                    bundle.putString("ADDRESS", address);
                    bundle.putString("FAX", searchResults.get(listPosition).getTags().getFax());
                    bundle.putString("WIKI", searchResults.get(listPosition).getTags().getWikipedia());
                    bundle.putString("HOURS", searchResults.get(listPosition).getTags().getOpeningHours());
                    bundle.putString("IMAGE", searchResults.get(listPosition).getTags().getImage());
                    bundle.putDouble("DESLAT", desLat);
                    bundle.putDouble("DESLON", desLon);

                    if(mv.getUserLocation() == null) {
                        Log.v("CustomInfoWindow", "Users location not within map bounds or can't be found");
                        bundle.putBoolean("FOUNDUSERLOCATION", false);
                    }
                    else {
                        bundle.putBoolean("FOUNDUSERLOCATION", true);
                        bundle.putDouble("CURRENTLAT", mv.getUserLocation().getLatitude());
                        bundle.putDouble("CURRENTLON", mv.getUserLocation().getLongitude());
                    }
                    Intent intent = new Intent(mView.getContext(), DetailedInfoActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent/*, options.toBundle()*/);

                    // Still close the InfoWindow though
                    close();
                }

                // Return true as we're done processing this event
                return true;
            }
        });
    }

    @Override
    public void onOpen(Marker overlayItem) {
        title = overlayItem.getTitle();
        ref = overlayItem.getDescription();
        ((TextView) mView.findViewById(R.id.title)).setText(title);
        ((TextView) mView.findViewById(R.id.ref)).setText(ref);
    }
}
