package team6.iguide;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;

public class CustomInfoWindow extends InfoWindow {

    public LatLng destinationLat;
    private Context mContext;
    String title;
    String ref;
    String address;

    public CustomInfoWindow(Context context, final MapView mv, final List<OverpassElement> searchResults, final int listPosition) {
        super(R.layout.infowindow_custom, mv);
        this.mContext = context;
        getView().findViewById(R.id.mainpanal).setClipToOutline(true);
        mView.setElevation(24);

        Button routing = (Button)mView.findViewById(R.id.routing_button);
        routing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mView.getContext(), "begin routing", Toast.LENGTH_SHORT).show();

                Graphhopper graphhopper = new Graphhopper();
                graphhopper.executeRoute(searchResults.get(listPosition).getCenter().getLat(), searchResults.get(listPosition).getCenter().getLon(), mv.getUserLocation());

                close();
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
                    if(searchResults.get(listPosition).getTags().getAddrHousenumber() != null) {
                        address = searchResults.get(listPosition).getTags().getAddrHousenumber() + " " +
                                searchResults.get(listPosition).getTags().getAddrStreet() + " " +
                                searchResults.get(listPosition).getTags().getAddrCity() + " " +
                                searchResults.get(listPosition).getTags().getAddrPostcode() + " " +
                                searchResults.get(listPosition).getTags().getAddrCountry();
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
        destinationLat = overlayItem.getPoint();
        //System.out.println(destinationLat.toString());
        ((TextView) mView.findViewById(R.id.title)).setText(title);
        ((TextView) mView.findViewById(R.id.ref)).setText(ref);
    }
}
