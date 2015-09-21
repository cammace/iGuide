package team6.iguide.GraphhopperModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Path implements Serializable {

    @Expose
    private List<Instruction> instructions = new ArrayList<Instruction>();
    @Expose
    private Double distance;
    @Expose
    private List<Double> bbox = new ArrayList<Double>();
    @Expose
    private Double weight;
    @Expose
    private Long time;
    @SerializedName("points_encoded")
    @Expose
    private Boolean pointsEncoded;
    @Expose
    private Points points;

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getPointsEncoded() {
        return pointsEncoded;
    }

    public void setPointsEncoded(Boolean pointsEncoded) {
        this.pointsEncoded = pointsEncoded;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

}