package team6.iguide;

// http://developer.android.com/guide/components/fragments.html
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class PlaceInfoFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle latlon = getArguments();
        String latitude = latlon.getString("LAT");
        String longitude = latlon.getString("LON");



        // Build the Nominatim URI
        String myGeocodeURI;
        String GeocodeBoundBox = "&viewbox=-95.35668790340424,29.731896194504913,-95.31928449869156,29.709354854765827&bounded=1";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("nominatim.openstreetmap.org")
                .appendPath("reverse")
                .appendQueryParameter("lat", latitude)
                .appendQueryParameter("lon", longitude)
                .appendQueryParameter("format", "json");
        myGeocodeURI = builder.build().toString();
        // myGeocodeURI = myGeocodeURI + GeocodeBoundBox;

        System.out.println(myGeocodeURI);










        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.placeinfo_layout, container, false);

        //TextView helloworld = (TextView)myInflatedView.findViewById(R.id.title);
        Animation test = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        //helloworld.setText(myGeocodeURI);


        myInflatedView.startAnimation(test);



        return myInflatedView;
    }
}