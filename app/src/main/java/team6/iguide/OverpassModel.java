package team6.iguide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class OverpassModel implements Serializable {

    @Expose
    private Double version;
    @Expose
    private String generator;
    @Expose
    private OverpassOsm3s osm3s;
    @Expose
    private List<OverpassElement> elements = new ArrayList<OverpassElement>();

    /**
     *
     * @return
     * The version
     */
    public Double getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The generator
     */
    public String getGenerator() {
        return generator;
    }

    /**
     *
     * @param generator
     * The generator
     */
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    /**
     *
     * @return
     * The osm3s
     */
    public OverpassOsm3s getOsm3s() {
        return osm3s;
    }

    /**
     *
     * @param osm3s
     * The osm3s
     */
    public void setOsm3s(OverpassOsm3s osm3s) {
        this.osm3s = osm3s;
    }

    /**
     *
     * @return
     * The elements
     */
    public List<OverpassElement> getElements() {
        return elements;
    }

    /**
     *
     * @param elements
     * The elements
     */
    public void setElements(List<OverpassElement> elements) {
        this.elements = elements;
    }

}