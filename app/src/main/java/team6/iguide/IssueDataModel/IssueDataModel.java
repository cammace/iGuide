package team6.iguide.IssueDataModel;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssueDataModel {

    @SerializedName("markers")
    @Expose
    private List<IssueMarker> markers = new ArrayList<IssueMarker>();

    public List<IssueMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<IssueMarker> markers) {
        this.markers = markers;
    }

}