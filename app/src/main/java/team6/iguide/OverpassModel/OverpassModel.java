package team6.iguide.OverpassModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class OverpassModel implements Serializable{

    @Expose
    private String version;
    @Expose
    private String generator;
    @Expose
    private OverpassOsm3s osm3s;
    @Expose
    private List<OverpassElement> elements = new ArrayList<>();


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public OverpassOsm3s getOsm3s() {
        return osm3s;
    }

    public void setOsm3s(OverpassOsm3s osm3s) {
        this.osm3s = osm3s;
    }

    public List<OverpassElement> getElements() {
        return elements;
    }

    public void setElements(List<OverpassElement> elements) {
        this.elements = elements;
    }

}