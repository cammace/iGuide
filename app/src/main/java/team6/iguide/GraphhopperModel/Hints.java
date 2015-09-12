package team6.iguide.GraphhopperModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hints {

    @SerializedName("visited_nodes.average")
    @Expose
    private String visitedNodesAverage;
    @SerializedName("visited_nodes.sum")
    @Expose
    private String visitedNodesSum;

    public String getVisitedNodesAverage() {
        return visitedNodesAverage;
    }

    public void setVisitedNodesAverage(String visitedNodesAverage) {
        this.visitedNodesAverage = visitedNodesAverage;
    }

    public String getVisitedNodesSum() {
        return visitedNodesSum;
    }

    public void setVisitedNodesSum(String visitedNodesSum) {
        this.visitedNodesSum = visitedNodesSum;
    }

}