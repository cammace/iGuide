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

import java.util.ArrayList;

public class HelpMenuItems {
    public int icon;
    public int title;
    public int description;

    public HelpMenuItems(int icon, int title, int description) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public static ArrayList<HelpMenuItems> getUsers() {
        ArrayList<HelpMenuItems> helpmenuitems = new ArrayList<>();
        helpmenuitems.add(new HelpMenuItems(R.drawable.ic_info_black_24dp, R.string.about_title, R.string.about_description));
        helpmenuitems.add(new HelpMenuItems(R.drawable.ic_help_black_24dp, R.string.get_help_title, R.string.get_help_description));
        helpmenuitems.add(new HelpMenuItems(R.drawable.ic_map_black_24dp, R.string.report_map_issue_title, R.string.report_map_issue_description));
        helpmenuitems.add(new HelpMenuItems(R.drawable.ic_feedback_black_24dp, R.string.send_app_feedback_title, R.string.send_app_feedback_description));
        return helpmenuitems;
    }
}