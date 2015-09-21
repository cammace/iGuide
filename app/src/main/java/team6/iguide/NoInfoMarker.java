package team6.iguide;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;

public class NoInfoMarker extends Marker{
    public String mtitle;
    public String getMtitle() {
        return mtitle;
    }
    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }
    public NoInfoMarker(String title, String description, LatLng latLng) {
        super(title, description, latLng);
    }
}
