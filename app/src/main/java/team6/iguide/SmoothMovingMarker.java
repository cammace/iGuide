package team6.iguide;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SmoothMovingMarker {

    public List pointsBetween(LatLng start, LatLng finish, double smoothness){

        List<LatLng> busPoints = new ArrayList<>();


        for(int i = 0; i<smoothness; i++) {
            double lat1 = start.getLatitude();
            double lon1 = start.getLongitude();
            double lat2 = finish.getLatitude();
            double lon2 = finish.getLongitude();
            double f = i * (1 / smoothness);

            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            lon1 = Math.toRadians(lon1);
            lon2 = Math.toRadians(lon2);

            double deltaLat = (lat2 - lat1);
            double deltaLon = (lon2 - lon1);

            double m = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
            double distance = 2 * Math.atan2(Math.sqrt(m), Math.sqrt(1 - m));

            double a = Math.sin((1 - f) * distance) / Math.sin(distance);
            double b = Math.sin(f * distance) / Math.sin(distance);
            double x = a * Math.cos(lat1) * Math.cos(lon1) + b * Math.cos(lat2) * Math.cos(lon2);
            double y = a * Math.cos(lat1) * Math.sin(lon1) + b * Math.cos(lat2) * Math.sin(lon2);
            double z = a * Math.sin(lat1) + b * Math.sin(lat2);
            double lat3 = Math.atan2(z, Math.sqrt((x * x) + (y * y)));
            double lon3 = Math.atan2(y, x);


            busPoints.add(new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3)));

        }
        return busPoints;
    }

}
