package team6.iguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Makes status bar color same as PrimaryDarkColor
        if(Build.VERSION.SDK_INT >= 21)
        getWindow().setStatusBarColor(getResources().getColor(R.color.PrimaryDarkColor));

        // Adds back button to toolbar
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(AboutActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });
    }

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
            }

            //String versionSummary = (String)findPreference("Version").getSummary();
            findPreference("Version").setSummary(version);

/*
            Preference license = findPreference("License");
            license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent licenseActivity = new Intent(getActivity(), LicenseActivity.class);
                    startActivity(licenseActivity);
                    return false;
                }
            });
*/





        }
    }// End aboutFragment

}