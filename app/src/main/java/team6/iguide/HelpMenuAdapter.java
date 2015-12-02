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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HelpMenuAdapter extends ArrayAdapter<HelpMenuItems> {
    // Adapter for the help dialog list

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