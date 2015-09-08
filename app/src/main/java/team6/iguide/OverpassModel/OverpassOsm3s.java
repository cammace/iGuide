package team6.iguide.OverpassModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OverpassOsm3s implements Serializable{

    @SerializedName("timestamp_osm_base")
    @Expose
    private String timestampOsmBase;
    @Expose
    private String copyright;

    /**
     *
     * @return
     * The timestampOsmBase
     */
    public String getTimestampOsmBase() {
        return timestampOsmBase;
    }

    /**
     *
     * @param timestampOsmBase
     * The timestamp_osm_base
     */
    public void setTimestampOsmBase(String timestampOsmBase) {
        this.timestampOsmBase = timestampOsmBase;
    }

    /**
     *
     * @return
     * The copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     *
     * @param copyright
     * The copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

}