package team6.iguide.OverpassModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class OverpassElement implements Serializable{

    @Expose
    private String type;
    @Expose
    private Long id;
    @Expose
    private OverpassCenter center;
    @Expose
    private List<Long> nodes = new ArrayList<Long>();
    @Expose
    private OverpassTags tags;
    @Expose
    private Double lat;
    @Expose
    private Double lon;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OverpassCenter getCenter() {
        return center;
    }

    public void setCenter(OverpassCenter center) {
        this.center = center;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public void setNodes(List<Long> nodes) {
        this.nodes = nodes;
    }

    public OverpassTags getTags() {
        return tags;
    }

    public void setTags(OverpassTags tags) {
        this.tags = tags;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

}