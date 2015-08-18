package team6.iguide;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NominatimModel implements Serializable{

    @SerializedName("place_id")
    @Expose
    private String placeId;
    @Expose
    private String licence;
    @SerializedName("osm_type")
    @Expose
    private String osmType;
    @SerializedName("osm_id")
    @Expose
    private String osmId;
    @Expose
    private List<String> boundingbox = new ArrayList<String>();
    @Expose
    private String lat;
    @Expose
    private String lon;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("class")
    @Expose
    private String _class;
    @Expose
    private String type;
    @Expose
    private Double importance;
    @Expose
    private String icon;

    /**
     * @return The placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * @param placeId The place_id
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     * @return The licence
     */
    public String getLicence() {
        return licence;
    }

    /**
     * @param licence The licence
     */
    public void setLicence(String licence) {
        this.licence = licence;
    }

    /**
     * @return The osmType
     */
    public String getOsmType() {
        return osmType;
    }

    /**
     * @param osmType The osm_type
     */
    public void setOsmType(String osmType) {
        this.osmType = osmType;
    }

    /**
     * @return The osmId
     */
    public String getOsmId() {
        return osmId;
    }

    /**
     * @param osmId The osm_id
     */
    public void setOsmId(String osmId) {
        this.osmId = osmId;
    }

    /**
     * @return The boundingbox
     */
    public List<String> getBoundingbox() {
        return boundingbox;
    }

    /**
     * @param boundingbox The boundingbox
     */
    public void setBoundingbox(List<String> boundingbox) {
        this.boundingbox = boundingbox;
    }

    /**
     * @return The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * @param lat The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * @return The lon
     */
    public String getLon() {
        return lon;
    }

    /**
     * @param lon The lon
     */
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * @return The displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The display_name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return The _class
     */
    public String getClass_() {
        return _class;
    }

    /**
     * @param _class The class
     */
    public void setClass_(String _class) {
        this._class = _class;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The importance
     */
    public Double getImportance() {
        return importance;
    }

    /**
     * @param importance The importance
     */
    public void setImportance(Double importance) {
        this.importance = importance;
    }

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
/*
public class NominatimModel {
    private long place_id;
    private String licence;
    private double lat;
    private double lon;


    public NominatimModel(){
    }


    public NominatimModel(double lat, double lon){
        //long place_id, String licence, double lat,

        //this.place_id = place_id;
        //this.licence = licence;
        this.lat = lat;
        this.lon = lon;
    }

    public long getPlace_id(){
        return place_id;
    }

    public String licence(){
        return licence;
    }

    public double lat(){
        return lat;
    }

    public double lon(){
        return lon;
    }



    public String toString(){
        return "place_id=" + place_id + " licence" + licence + " lat" + lat + " lon" + lon;
    }



}
*/