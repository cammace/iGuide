package team6.iguide.BusLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetVehicle {

    @SerializedName("equipmentID")
    @Expose
    private String equipmentID;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("routeID")
    @Expose
    private Long routeID;
    @SerializedName("nextStopID")
    @Expose
    private Object nextStopID;
    @SerializedName("scheduleNumber")
    @Expose
    private String scheduleNumber;
    @SerializedName("inService")
    @Expose
    private Long inService;
    //@SerializedName("minutesToNextStops")
    //@Expose
   // private List<MinutesToNextStop> minutesToNextStops = new ArrayList<MinutesToNextStop>();
    @SerializedName("onSchedule")
    @Expose
    private Object onSchedule;
    @SerializedName("receiveTime")
    @Expose
    private Long receiveTime;

    /**
     *
     * @return
     * The equipmentID
     */
    public String getEquipmentID() {
        return equipmentID;
    }

    /**
     *
     * @param equipmentID
     * The equipmentID
     */
    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

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
     * The lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The routeID
     */
    public Long getRouteID() {
        return routeID;
    }

    /**
     *
     * @param routeID
     * The routeID
     */
    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    /**
     *
     * @return
     * The nextStopID
     */
    public Object getNextStopID() {
        return nextStopID;
    }

    /**
     *
     * @param nextStopID
     * The nextStopID
     */
    public void setNextStopID(Object nextStopID) {
        this.nextStopID = nextStopID;
    }

    /**
     *
     * @return
     * The scheduleNumber
     */
    public String getScheduleNumber() {
        return scheduleNumber;
    }

    /**
     *
     * @param scheduleNumber
     * The scheduleNumber
     */
    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    /**
     *
     * @return
     * The inService
     */
    public Long getInService() {
        return inService;
    }

    /**
     *
     * @param inService
     * The inService
     */
    public void setInService(Long inService) {
        this.inService = inService;
    }

    /*
    public List<MinutesToNextStop> getMinutesToNextStops() {
        return minutesToNextStops;
    }

    public void setMinutesToNextStops(List<MinutesToNextStop> minutesToNextStops) {
        this.minutesToNextStops = minutesToNextStops;
    }
*/
    /**
     *
     * @return
     * The onSchedule
     */
    public Object getOnSchedule() {
        return onSchedule;
    }

    /**
     *
     * @param onSchedule
     * The onSchedule
     */
    public void setOnSchedule(Object onSchedule) {
        this.onSchedule = onSchedule;
    }

    /**
     *
     * @return
     * The receiveTime
     */
    public Long getReceiveTime() {
        return receiveTime;
    }

    /**
     *
     * @param receiveTime
     * The receiveTime
     */
    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

}