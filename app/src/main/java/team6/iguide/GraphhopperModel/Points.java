package team6.iguide.GraphhopperModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Points implements Serializable {

    @Expose
    private List<List<Double>> coordinates = new ArrayList<List<Double>>();
    @Expose
    private String type;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
