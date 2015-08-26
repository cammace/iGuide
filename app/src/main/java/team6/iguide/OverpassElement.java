package team6.iguide;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class OverpassElement {

    @Expose
    private String type;
    @Expose
    private Integer id;
    @Expose
    private OverpassCenter center;
    @Expose
    private List<Integer> nodes = new ArrayList<Integer>();
    @Expose
    private OverpassTags tags;
    @Expose
    private Double lat;
    @Expose
    private Double lon;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The center
     */
    public OverpassCenter getCenter() {
        return center;
    }

    /**
     *
     * @param center
     * The center
     */
    public void setCenter(OverpassCenter center) {
        this.center = center;
    }

    /**
     *
     * @return
     * The nodes
     */
    public List<Integer> getNodes() {
        return nodes;
    }

    /**
     *
     * @param nodes
     * The nodes
     */
    public void setNodes(List<Integer> nodes) {
        this.nodes = nodes;
    }

    /**
     *
     * @return
     * The tags
     */
    public OverpassTags getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     * The tags
     */
    public void setTags(OverpassTags tags) {
        this.tags = tags;
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