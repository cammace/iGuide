package team6.iguide;

import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

public class NoInfoMarker extends InfoWindow{

    public NoInfoMarker(MapView mv){
        super(R.layout.infowindow_custom, mv);
        //System.out.println("noinfo");
        close();
    }


}
