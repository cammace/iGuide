package team6.iguide;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LicenseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_activity);

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
                NavUtils.navigateUpFromSameTask(LicenseActivity.this);
                // overridePendingTransition(R.anim.pull_out, R.anim.hold);
            }
        });
    }
}