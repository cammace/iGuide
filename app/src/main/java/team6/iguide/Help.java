package team6.iguide;

/**
 * iGuide
 * Copyright (C) 2015 Cameron Mace
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Help extends DialogFragment {
    //This is the code executed when "Help & Feedback is selected within the navigation drawer. It
    //draws a dialog fragment with a custom layout. The layout includes the same toolbar found within
    // other activities and under it are the "Help & Feedback" options.

    public Help() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the main view
        View view = inflater.inflate(R.layout.help, container, false);

        // Initiate toolbar within dialog
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        // Add back button to dialog
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
                        Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case 1:
                        dismiss();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                        startActivity(browserIntent);
                        break;
                    case 2:
                        dismiss();
                        Snackbar.make(MainActivity.mapContainer, "Feature coming soon", Snackbar.LENGTH_LONG).show();
                        break;
                    case 3:
                        dismiss();
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",getResources().getString(R.string.feedbackEmailAddress), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback: ");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n" + Build.MANUFACTURER + " "
                                + Build.MODEL + "\n" + Build.VERSION.SDK_INT);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
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