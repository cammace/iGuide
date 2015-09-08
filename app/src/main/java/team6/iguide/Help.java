package team6.iguide;

/**
 * This is the code executed when "Help & Feedback is selected within the navigation drawer. It
 * draws a dialog fragment with a custom layout. The layout includes the same toolbar found within
 * other activities and under it are the "Help & Feedback" options.
 * <p/>
 * References:
 * http://www.truiton.com/2015/04/android-action-bar-dialog-using-toolbar/
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 * http://stackoverflow.com/questions/11201022/how-to-correctly-dismiss-a-dialogfragment
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Help extends DialogFragment {


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public Help() {
        // Empty constructor required for DialogFragment
    }

    // ------------------------------------------------------------------------
    // onCreateView
    // ------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the main view
        View view = inflater.inflate(R.layout.help, container, false);

        // Initiate toolbar within dialog
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        // Add back button to dialog
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        // Add title to toolbar
        toolbar.setTitle(R.string.help);

        // When back button is pressed it calls dismiss() in DialogFragment and closes dialog
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Beginning of the menu creation. Basically the menu is a listview with an adapter that
        // load the option items in. More info can be found in HelpMenuAdapter.java and HelpMenuItems.java
        ArrayList<HelpMenuItems> menuItems = HelpMenuItems.getUsers();
        // Create the adapter to convert the array to views
        HelpMenuAdapter adapter = new HelpMenuAdapter(getActivity(), menuItems);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) view.findViewById(R.id.menu);
        listView.setAdapter(adapter);





        // This is the onClickListener for the four menu items.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "about", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        dismiss();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                        startActivity(browserIntent);
                        break;
                    case 2:
                        dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "report", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "feedback", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return view;
    }

    // ------------------------------------------------------------------------
    // onCreateDialog
    // ------------------------------------------------------------------------
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}