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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Display the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Adds back button to toolbar and handle its onClick
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(Settings.this);
            }
        });
    }// End onCreate

    public static class MyPreferenceFragment extends PreferenceFragment {
        // This is the settings/preference fragment. It's were all the interesting things in this
        // activity occur.
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference clearHistory = findPreference(getString(R.string.clear_history));
            clearHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    // Build the alert dialog that displays when clear history is clicked
                    AlertDialog.Builder clearHistoryConfirmation = new AlertDialog.Builder(getActivity());
                    clearHistoryConfirmation.setTitle(getString(R.string.clear_history_alert_title));
                    clearHistoryConfirmation.setMessage(getString(R.string.clear_history_alert_message));
                    clearHistoryConfirmation.setPositiveButton(getString(R.string.clear_history_alert_positive),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // When the clear button (positive button) is pressed we clear the
                                    // SearchSuggestion database and display a toast alerting the user
                                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                                            SearchSuggestion.AUTHORITY, SearchSuggestion.MODE);
                                    suggestions.clearHistory();
                                    //if (getView() != null) Snackbar.make(getView().findViewById(R.id.settings_layout), getString(R.string.clear_history_alert__positive_toast_msg), Snackbar.LENGTH_LONG).show();
                                    Toast.makeText(getActivity(), getString(R.string.clear_history_alert__positive_toast_msg), Toast.LENGTH_SHORT).show();
                                }
                            });
                    clearHistoryConfirmation.setNegativeButton(getString(R.string.clear_history_alert_negative),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do action when dialog is canceled, in our case, we don't don't have
                                    // to do anything
                                }
                            });
                    // Show the alert dialog
                    clearHistoryConfirmation.show();
                    return false;
                }
            });
        }
    }// End MyPreferenceFragment
}// End Settings Activity