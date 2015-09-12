package team6.iguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomDetailItemAdapter  extends ArrayAdapter<DetailItem> {
    public CustomDetailItemAdapter(Context context, ArrayList<DetailItem> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DetailItem detailItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_default, parent, false);
        }
        // Lookup view for data population
        TextView tvKey = (TextView) convertView.findViewById(R.id.menuItemDescription);
        TextView tvValue = (TextView) convertView.findViewById(R.id.menuItemTitle);
        // Populate the data into the template view using the data object
        tvKey.setText(detailItem.key);
        tvValue.setText(detailItem.value);
        // Return the completed view to render on screen
        return convertView;
    }
}