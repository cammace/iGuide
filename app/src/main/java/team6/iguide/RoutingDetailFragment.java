package team6.iguide;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team6.iguide.GraphhopperModel.GraphhopperModel;

public class RoutingDetailFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.routing_detail_layout, container, false);

        //startRouteFAB();

        System.out.println("blahblahblahblahblah");

        //routingTime = getActivity().
        //String routingTime = String.valueOf(getArguments().getLong("TIME"));


        //TextView titleView = (TextView) getActivity().findViewById(R.id.route_time);
        //titleView.setText("yahyahyah");



        return view;

    }

/*
    private void startRouteFAB(){
        // FAB for myLocationButton
        FloatingActionButton FAB = (FloatingActionButton) getActivity().findViewById(R.id.start_routing_fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("begin routing");


            }
        });
    }
*/
}
