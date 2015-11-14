package team6.iguide.IssueDataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssueMarker {

    @SerializedName("pid")
    @Expose
    private int pid;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("room")
    @Expose
    private String room;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("markerLat")
    @Expose
    private Double markerLat;
    @SerializedName("markerLon")
    @Expose
    private Double markerLon;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMarkerLat() {
        return markerLat;
    }

    public void setMarkerLat(Double markerLat) {
        this.markerLat = markerLat;
    }

    public Double getMarkerLon() {
        return markerLon;
    }

    public void setMarkerLon(Double markerLon) {
        this.markerLon = markerLon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}