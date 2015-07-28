package team6.iguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HelpMenuAdapter extends ArrayAdapter<HelpMenuItems> {


    public HelpMenuAdapter(Context context, ArrayList<HelpMenuItems> helpmenuitems) {
        super(context, 0, helpmenuitems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HelpMenuItems helpmenuitems = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.help_menu_item, parent, false);
        }
        // Lookup view for data population
        ImageView menuImage = (ImageView) convertView.findViewById(R.id.menuIcon);
        TextView menuTitle = (TextView) convertView.findViewById(R.id.menuItemTitle);
        TextView menuDescription = (TextView) convertView.findViewById(R.id.menuItemDescription);

        // Populate the data into the template view using the data object
        menuImage.setImageResource(helpmenuitems.icon);
        menuTitle.setText(helpmenuitems.title);
        menuDescription.setText(helpmenuitems.description);
        // Return the completed view to render on screen
        return convertView;
    }
}