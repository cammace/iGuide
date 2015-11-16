package team6.iguide;

import android.app.Activity;

import android.content.Intent;

//ThemeUtils.onActivityCreateSetTheme(this);
//themeUtils.changeToTheme(this, themeUtils.RED);


public class ThemeUtils {

    private static int cTheme;
    public final static int BLUE = 0;
    public final static int RED = 1;

    public static void changeToTheme(Activity activity, int theme) {

        cTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity) {

        switch (cTheme) {
            default:
            case BLUE:
                activity.setTheme(R.style.AppTheme);
                break;
            case RED:
                activity.setTheme(R.style.RedTheme);
                break;
        }
    }
}