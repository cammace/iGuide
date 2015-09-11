package team6.iguide;

import android.net.Uri;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Graphhopper{

    public void executeRoute(double lat, double lon, LatLng userLocation){

        // Convert all the latitudes and longitudes to strings
        String destinationLat = String.valueOf(lat);
        String destinationLon = String.valueOf(lon);
        String userLat = String.valueOf(userLocation.getLatitude());
        String userLon = String.valueOf(userLocation.getLongitude());

        // Create the Graphhopper API URI
        String graphhopperURI = buildURI(destinationLat, destinationLon, userLat, userLon);
        System.out.println(graphhopperURI);
    }

    private String buildURI(String dLat, String dLon, String uLat, String uLon){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.graphhopper.com")
                .appendPath("api")
                .appendPath("1")
                .appendPath("route")
                .appendQueryParameter("point", dLat + "," + dLon)
                .appendQueryParameter("point", uLat + "," + uLon)
                .appendQueryParameter("vehicle", "foot")
                .appendQueryParameter("key", "2a3617f7-dd96-4a1b-b16a-412c3d74ee94")
                .appendQueryParameter("type", "json")
                .appendQueryParameter("points_encoded", "false");
        return builder.build().toString();
    }

}

