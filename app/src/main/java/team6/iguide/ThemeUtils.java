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

import android.app.Activity;

import android.content.Intent;

//ThemeUtils.onActivityCreateSetTheme(this);
//themeUtils.changeToTheme(this, themeUtils.RED);


public class ThemeUtils {
    // This class originally was going to be used to change the app theme, but hasn't been implemented yet.

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