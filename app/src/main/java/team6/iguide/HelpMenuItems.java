package team6.iguide;

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