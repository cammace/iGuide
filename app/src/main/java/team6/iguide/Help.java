package team6.iguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Help extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.help,container,false);
        return v;
    }

/*
    public void onCreate(Bundle savedInstanceState){


        Marker marker = new Marker( "test", "test", new LatLng(29.727, -95.342));
        marker.setMarker(getResources().getDrawable(R.drawable.test_marker));
        //marker.setToolTip(new CustomInfoWindow(mv));
        mv.addMarker(marker);
    }*/
}