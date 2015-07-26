package team6.iguide;

/**
 * This is the code executed when "Help & Feedback is selected within the navigation drawer. It
 * draws a dialog fragment with a custom layout. The layout includes the same toolbar found within
 * other activities and under it are the "Help & Feedback" options.
 *
 * References:
 * http://www.truiton.com/2015/04/android-action-bar-dialog-using-toolbar/
*/

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

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

        // TODO finish working on Help & Feedback dialog back button
        // Initiate toolbar within dialog
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        // Add title to toolbar
        toolbar.setTitle(R.string.help);
        // Add back button to dialog
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //onCancel(DialogInterface dialog);
            }
        });

        // TODO make "get help", "send feedback" and "map issues" work like buttons and add intents

        //Button help_button = (Button) view.findViewById(R.id.help);

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