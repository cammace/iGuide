package team6.iguide.GraphhopperModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class GraphhopperModel implements Serializable {

    @Expose
    private Hints hints;
    @Expose
    private List<Path> paths = new ArrayList<Path>();
    @Expose
    private Info info;

    public Hints getHints() {
        return hints;
    }

    public void setHints(Hints hints) {
        this.hints = hints;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

}