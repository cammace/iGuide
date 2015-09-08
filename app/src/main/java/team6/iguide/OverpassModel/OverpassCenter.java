package team6.iguide.OverpassModel;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class OverpassCenter implements Serializable{

    @Expose
    private Double lat;
    @Expose
    private Double lon;

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     *
     * @param lon
     * The lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

}