package team6.iguide.BusLocation;

        import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class BusLocation {

    @SerializedName("get_vehicles")
    @Expose
    private List<GetVehicle> getVehicles = new ArrayList<GetVehicle>();

    /**
     *
     * @return
     * The getVehicles
     */
    public List<GetVehicle> getGetVehicles() {
        return getVehicles;
    }

    /**
     *
     * @param getVehicles
     * The get_vehicles
     */
    public void setGetVehicles(List<GetVehicle> getVehicles) {
        this.getVehicles = getVehicles;
    }

}