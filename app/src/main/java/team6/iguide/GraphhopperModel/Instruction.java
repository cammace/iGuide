package team6.iguide.GraphhopperModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Instruction implements Serializable {

    @Expose
    private Double distance;
    @Expose
    private Long sign;
    @Expose
    private List<Long> interval = new ArrayList<Long>();
    @Expose
    private String text;
    @Expose
    private Long time;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getSign() {
        return sign;
    }

    public void setSign(Long sign) {
        this.sign = sign;
    }

    public List<Long> getInterval() {
        return interval;
    }

    public void setInterval(List<Long> interval) {
        this.interval = interval;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}