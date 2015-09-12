package team6.iguide.GraphhopperModel;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Info {

    @Expose
    private Long took;
    @Expose
    private List<String> copyrights = new ArrayList<String>();

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(List<String> copyrights) {
        this.copyrights = copyrights;
    }

}