package team6.iguide;

/***
 iGuide
 Copyright (C) 2015 Cameron Mace

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {
    // This class is self explanatory...

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(AboutActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });

    }// End onCreate

    public static class aboutFragment extends PreferenceFragment {
        // This is the about fragment. It's were all the interesting things in this activity occur.

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about);

            String version = null;
            try {
                version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                // Couldn't get version package version number
                Log.w("AboutActivity", "Couldn't get version package version number");
            }

            //String versionSummary = (String)findPreference("Version").getSummary();
            findPreference("Version").setSummary(version);

            // Here is where we handle the rate app feature. if clicked, we open the app in the marketplace.
            findPreference("Rate App").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri marketUri = Uri.parse("market://details?id=" + preference.getContext().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
                    try {
                        preference.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException anfe) {
                        Toast.makeText(preference.getContext(), "Android Market is not installed", Toast.LENGTH_LONG).show();
                        Log.w("AboutActivity", "Android Market is not installed");
                    }
                    return false;
                }
            });

        }// End onCreate
    }// End aboutFragment
}// End AboutActivity