package team6.iguide;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import team6.iguide.AnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends AnimatedExpandableListAdapter {

    private Context mContext;
    private List<ExpandedMenuModel> mListDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
    AnimatedExpandableListView expandList;
    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader,HashMap<ExpandedMenuModel, List<String>> listChildData,AnimatedExpandableListView mView) {

        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList=mView;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        int childCount=0;

        //TODO fix this so I don't personally have to come in here and tell it what group dont have a child
        if(groupPosition != 0) {
            if(groupPosition != 3) {
                if (groupPosition != 4) {
                    if (groupPosition != 5) {

                        childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();

                    }
                }
            }
        }

        //if(mListDataChild.size() != 0) childCount=this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View   convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        //if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           // System.out.println(groupPosition);
            if(groupPosition == 4) {
            //    System.out.println(groupPosition);
                convertView = infalInflater.inflate(R.layout.list_header_with_divider, null);
            }
            else convertView = infalInflater.inflate(R.layout.list_header, null);
       // }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        // lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getIconName());
        headerIcon.setImageResource(headerTitle.getIconImg());

        MainActivity mainActivity = (MainActivity) mContext;
        if (mainActivity.getCurrentMapViewOverlay().matches("Places of Interest") ||
                mainActivity.getCurrentMapViewOverlay().matches("Campus Issues") ||
                mainActivity.getCurrentMapViewOverlay().matches("Transit") ||
                mainActivity.getCurrentMapViewOverlay().matches("Parking")){
            //System.out.println("header Name: " + headerTitle.getIconName());
            if (mainActivity.getCurrentMapViewOverlay().equals(headerTitle.getIconName()) ) {
                lblListHeader.setTextColor(mContext.getResources().getColor(R.color.PrimaryColor));
                headerIcon.setColorFilter(mContext.getResources().getColor(R.color.PrimaryColor));
            }
        else{
            lblListHeader.setTextColor(Color.parseColor("#212121"));
            headerIcon.setColorFilter(Color.parseColor("#737373"));
        }}
        else{
            lblListHeader.setTextColor(Color.parseColor("#212121"));
            headerIcon.setColorFilter(Color.parseColor("#737373"));
        }



        View ind = convertView.findViewById(R.id.explist_indicator);
        if( ind != null) {
            ImageView indicator = (ImageView)ind;
            if (getChildrenCount(groupPosition) == 0) {
                indicator.setVisibility(View.INVISIBLE);
            } else {
                indicator.setVisibility(View.VISIBLE);
                indicator.setImageResource(isExpanded ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp);
            }
        }
        return convertView;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition,  boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.submenu);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}