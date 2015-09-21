package team6.iguide.OverpassModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OverpassTags implements Serializable{

    @SerializedName("addr:city")
    @Expose
    private String addrCity;
    @SerializedName("addr:country")
    @Expose
    private String addrCountry;
    @SerializedName("addr:housenumber")
    @Expose
    private String addrHousenumber;
    @SerializedName("addr:postcode")
    @Expose
    private String addrPostcode;
    @SerializedName("addr:state")
    @Expose
    private String addrState;
    @SerializedName("addr:street")
    @Expose
    private String addrStreet;
    @SerializedName("addr:unit")
    @Expose
    private String addrUnit;
    @Expose
    private String indoor;
    @Expose
    private String level;
    @Expose
    private String name;
    @Expose
    private String ref;
    @Expose
    private String room;
    @Expose
    private String building;
    @Expose
    private String amenity;
    @Expose
    private String fax;
    @Expose
    private String phone;
    @Expose
    private String website;
    @Expose
    private String wikipedia;
    @SerializedName("opening_hours")
    @Expose
    private String openingHours;
    @Expose
    private String image;
    @Expose
    private String landuse;
    @Expose
    private String leisure;

    public String getIndoor() {
        return indoor;
    }

    public void setIndoor(String indoor) {
        this.indoor = indoor;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBuilding(){
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getAmenity(){
        return amenity;
    }

    public void setAmenity(String amenity){
        this.amenity = amenity;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getAddrCountry() {
        return addrCountry;
    }

    public void setAddrCountry(String addrCountry) {
        this.addrCountry = addrCountry;
    }

    public String getAddrHousenumber() {
        return addrHousenumber;
    }

    public void setAddrHousenumber(String addrHousenumber) {
        this.addrHousenumber = addrHousenumber;
    }

    public String getAddrPostcode() {
        return addrPostcode;
    }

    public void setAddrPostcode(String addrPostcode) {
        this.addrPostcode = addrPostcode;
    }

    public String getAddrState() {
        return addrState;
    }

    public void setAddrState(String addrState) {
        this.addrState = addrState;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public void setAddrStreet(String addrStreet) {
        this.addrStreet = addrStreet;
    }

    public String getAddrUnit() {
        return addrUnit;
    }

    public void setAddrUnit(String addrUnit) {
        this.addrUnit = addrUnit;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanduse(){
        return landuse;
    }

    public void setLanduse(String landuse){
        this.landuse = landuse;
    }

    public String getLeisure(){
        return leisure;
    }

    public void setLeisure(String leisure){
        this.leisure = leisure;
    }




}
