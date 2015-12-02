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

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

// http://www.movable-type.co.uk/scripts/latlong.html
// http://williams.best.vwh.net/avform.htm#Intermediate

public class TwoPointMath {
    // This class is used to define all the math equations used to get info between two points.
    // Right now all it's used for is using the bus routing

    public List pointsBetween(LatLng start, LatLng finish, double smoothness){

        List<LatLng> busPoints = new ArrayList<>();

        for(int i = 0; i<smoothness; i++) {

            // Check if start and finish LatLng are the same and if so just return the same position in list.
            if(start.getLatitude() == finish.getLatitude()){
                busPoints.add(start);
            }

            else {
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
        }
        return busPoints;
    }

}
