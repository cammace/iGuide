package team6.iguide;

// http://developer.android.com/guide/components/fragments.html
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class PlaceInfoFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.placeinfo_layout, container, false);

        TextView helloworld = (TextView)myInflatedView.findViewById(R.id.helloworld);
        Animation test = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        helloworld.setText("HelloWorld");
        myInflatedView.startAnimation(test);



        return myInflatedView;
    }
}