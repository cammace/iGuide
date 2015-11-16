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

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.events.DelayedMapListener;
import com.mapbox.mapboxsdk.events.MapListener;
import com.mapbox.mapboxsdk.events.RotateEvent;
import com.mapbox.mapboxsdk.events.ScrollEvent;
import com.mapbox.mapboxsdk.events.ZoomEvent;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsOverlay;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBasic;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import team6.iguide.BusLocation.BusLocation;
import team6.iguide.IssueDataModel.IssueDataModel;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem searchItem;
    private SearchView searchView;
    private MapView mv;
    public  View progressBar;
    static int currentFloor;
    private RequestQueue mRequestQueue;
    List<LatLng> start = new ArrayList<>();
    List<LatLng> finish = new ArrayList<>();
    final TwoPointMath twoPointMath = new TwoPointMath();
    List<LatLng> busPlot = new ArrayList<>();
    List<List> buses = new ArrayList<>();
    boolean firstBusCheck = true;
    final Handler transitHandler = new Handler();
    int updateBusTimer = 0;
    MenuItem previousDrawerMenuItem;
    List<List> poiMarkers = new ArrayList<>();
    boolean poiShow = false;
    boolean busRouteShow = false;
    int currentBusRouteShowing;
    boolean campusIssueMarkersVisable = false;
    String currentMapViewOverlay = "";
    String currentChildMenuItem = "";





    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    AnimatedExpandableListView  expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;







    // Tiles
    private TilesOverlay campusLoopTiles;
    private TilesOverlay outerLoopTiles;
    private TilesOverlay eastwoodErpLineTiles;
    private TilesOverlay erpExpressTiles;
    static TilesOverlay floorLevel;

    BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userLocationFAB();

        progressBar = createProgressBar();
        progressBar.setVisibility(View.INVISIBLE);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setMaxWidth(2000);


        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);

        // Collapse the search menu when the user hits the back key
        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //log.trace("onFocusChange(): " + hasFocus);
                if (!hasFocus)
                    showSearch(false);
            }
        });


        // Creates TextView Cursor
        try {
            // This sets the cursor resource ID to 0 or @null
            // which will make it visible on white background.
            Field mCursorDrawableRes = TextView.class
                    .getDeclaredField("mCursorDrawableRes");

            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);

        } catch (Exception e) {
        }

        navigationDrawer();

        // Initialize MapView
        if(mv == null) setMap();

        // Get the POI and populate list so it's ready if user toogles POI in menu
        PointOfInterest pointOfInterest = new PointOfInterest();
        poiMarkers = pointOfInterest.getPOI(MainActivity.this, mv);
    }

    public String getCurrentMapViewOverlay(){
        return currentMapViewOverlay;
    }

    private void navigationDrawer(){

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        expandableList = (AnimatedExpandableListView) findViewById(R.id.navigationmenu);


        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(getString(R.string.poi));
        item1.setIconImg(R.drawable.ic_place_black_24dp);
        // Adding data header
        listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName(getString(R.string.parking));
        item2.setIconImg(R.drawable.ic_local_parking_black_24dp);
        listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName(getString(R.string.transit));
        item3.setIconImg(R.drawable.ic_directions_bus_black_24dp);
        listDataHeader.add(item3);

        ExpandedMenuModel item4 = new ExpandedMenuModel();
        item4.setIconName(getString(R.string.campusIssues));
        item4.setIconImg(R.drawable.ic_warning_black_24dp);
        listDataHeader.add(item4);

        ExpandedMenuModel item5 = new ExpandedMenuModel();
        item5.setIconName(getString(R.string.help));
        item5.setIconImg(R.drawable.ic_help_black_24dp);
        listDataHeader.add(item5);

        ExpandedMenuModel item6 = new ExpandedMenuModel();
        item6.setIconName(getString(R.string.settings));
        item6.setIconImg(R.drawable.ic_settings_black_24dp);
        listDataHeader.add(item6);


        // Adding child data
        List<String> heading1= new ArrayList<String>();
        heading1.add("Economy Lots");
        heading1.add("Faculty Lots");
        heading1.add("Garage Parking");
        heading1.add("Student Lots");
        heading1.add("Visitor Parking");

        List<String> heading2= new ArrayList<String>();
        heading2.add(getString(R.string.campus_loop));
        heading2.add(getString(R.string.outer_loop));
        heading2.add(getString(R.string.erp_express));
        heading2.add(getString(R.string.eastwood_erp_line));

        listDataChild.put(listDataHeader.get(1), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(2), heading2);



        expandableList.setOnGroupClickListener(new team6.iguide.AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                v.playSoundEffect(SoundEffectConstants.CLICK);
                currentChildMenuItem = "";

                //System.out.println(parent.getExpandableListAdapter().getGroup(groupPosition).toString());

                switch (listDataHeader.get(groupPosition).getIconName()) {
                    case "Places of Interest":
                        drawerLayout.closeDrawers();
                        mv.clearMarkerFocus();
                        mv.clear();
                        campusIssueMarkersVisable = false;
                        if(busRouteShow) stopBusRoute(currentBusRouteShowing);
                        if (!poiShow) {
                            poiShow = true;
                            currentMapViewOverlay = "Places of Interest";
                            if (mv.getZoomLevel() >= 18) {
                                mv.addMarkers(poiMarkers.get(1));
                            }
                            if (mv.getZoomLevel() >= 16) {
                                mv.addMarkers(poiMarkers.get(0));
                            }
                        } else {
                            poiShow = false;
                            currentMapViewOverlay = " ";
                            mv.removeMarkers(poiMarkers.get(1));
                            mv.removeMarkers(poiMarkers.get(0));
                            mv.clearMarkerFocus();
                        }
                        mv.invalidate();
                        break;

                    case "Parking":
                        if (expandableList.isGroupExpanded(groupPosition)) {
                            expandableList.collapseGroupWithAnimation(groupPosition);
                        } else {
                            expandableList.expandGroupWithAnimation(groupPosition);
                        }
                        break;

                    case "Transit":
                        if (expandableList.isGroupExpanded(groupPosition)) {
                            expandableList.collapseGroupWithAnimation(groupPosition);
                        } else {
                            expandableList.expandGroupWithAnimation(groupPosition);
                        }
                        break;

                    case "Campus Issues":
                        drawerLayout.closeDrawers();
                        mv.clearMarkerFocus();
                        mv.clear();
                        mv.invalidate();
                        if(busRouteShow) stopBusRoute(currentBusRouteShowing);
                        poiShow = false;
                        if (!campusIssueMarkersVisable) {
                            currentMapViewOverlay = "Campus Issues";
                            displayCampusIssues();
                        } else {
                            currentMapViewOverlay = " ";
                            campusIssueMarkersVisable = false;
                            mv.clearMarkerFocus();
                            mv.clear();
                        }
                        mv.invalidate();
                        break;

                    case "Help & Feedback":
                        drawerLayout.closeDrawers();
                        DialogFragment newFragmentHelp = new Help();
                        newFragmentHelp.show(getSupportFragmentManager(), "Help & Feedback");
                        break;

                    case "Settings":
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        break;
                }
                // Notify the adapter so it can redraw all items and change color of item selected
                mMenuAdapter.notifyDataSetChanged();
                return true;
            }
        });

        expandableList.setOnChildClickListener(new team6.iguide.AnimatedExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(busRouteShow) stopBusRoute(currentBusRouteShowing);
                mv.clearMarkerFocus();
                mv.clear();
                campusIssueMarkersVisable = false;
                poiShow = false;

                switch (parent.getExpandableListAdapter().getChild(groupPosition, childPosition).toString()) {

                    case "Campus Loop":
                        drawerLayout.closeDrawers();
                        if(!currentChildMenuItem.equals("Campus Loop")){
                            currentChildMenuItem = "Campus Loop";
                            currentMapViewOverlay = "Transit";
                            playBusRoute(1);
                        }
                        else{
                            currentMapViewOverlay = " ";
                            currentChildMenuItem = " ";
                        }
                        break;
                    case "Outer Loop":
                        drawerLayout.closeDrawers();
                        if(!currentChildMenuItem.equals("Outer Loop")){
                            currentChildMenuItem = "Outer Loop";
                            currentMapViewOverlay = "Transit";
                            playBusRoute(3);
                        }
                        else {
                            currentMapViewOverlay = " ";
                            currentChildMenuItem = " ";
                        }
                        break;
                    case "ERP Express":
                        drawerLayout.closeDrawers();
                        if(!currentChildMenuItem.equals("ERP Express")){
                            currentChildMenuItem = "ERP Express";
                            currentMapViewOverlay = "Transit";
                            playBusRoute(4);
                        }
                        else {
                            currentMapViewOverlay = " ";
                            currentChildMenuItem = " ";
                        }
                        break;
                    case "Eastwood ERP Line":
                        drawerLayout.closeDrawers();
                        if(!currentChildMenuItem.equals("Eastwood ERP Line")){
                            currentChildMenuItem = "Eastwood ERP Line";
                            currentMapViewOverlay = "Transit";
                            playBusRoute(2);
                        }
                        else {
                            currentMapViewOverlay = " ";
                            currentChildMenuItem = " ";
                        }
                        break;

                    case "Economy Lots":
                        drawerLayout.closeDrawers();
                        currentMapViewOverlay = "Parking";
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Coming soon", Snackbar.LENGTH_SHORT).show();
                        break;
                    case "Faculty Lots":
                        drawerLayout.closeDrawers();
                        currentMapViewOverlay = "Parking";
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Coming soon", Snackbar.LENGTH_SHORT).show();
                        break;
                    case "Garage Parking":
                        drawerLayout.closeDrawers();
                        currentMapViewOverlay = "Parking";
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Coming soon", Snackbar.LENGTH_SHORT).show();
                        break;
                    case "Student Lots":
                        drawerLayout.closeDrawers();
                        currentMapViewOverlay = "Parking";
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Coming soon", Snackbar.LENGTH_SHORT).show();
                        break;
                    case "Visitor Parking":
                        drawerLayout.closeDrawers();
                        currentMapViewOverlay = "Parking";
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Coming soon", Snackbar.LENGTH_SHORT).show();
                        break;

                }

                // Notify the adapter so it can redraw all items and change color of item selected
                mMenuAdapter.notifyDataSetChanged();

                return true;
            }

        });














/*

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                poiShow = false;
                campusIssueMarkersVisable = false;
                if (busRouteShow) stopBusRoute(currentBusRouteShowing);


                //Closing drawer on item click
                drawerLayout.closeDrawers();
                Intent intent;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.parking:
                        Toast.makeText(getApplicationContext(), "Parking feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.campus_issues:
                        mv.clearMarkerFocus();
                        mv.clear();
                        mv.invalidate();
                        if (menuItem.isChecked() && !campusIssueMarkersVisable)
                            displayCampusIssues();
                        else {
                            campusIssueMarkersVisable = false;
                            mv.clearMarkerFocus();
                            mv.clear();
                        }
                        mv.invalidate();
                        break;
                    case R.id.campus_loop:
                        // If bus route isn't showing on mapView then display it, else remove it.
                        if (menuItem.isChecked()) playBusRoute(1);
                        //else stopBusRoute(1);
                        break;
                    case R.id.outer_loop:
                        if (menuItem.isChecked()) playBusRoute(3);
                        //else stopBusRoute(3);
                        break;
                    case R.id.eastwood_erp_line:
                        if (menuItem.isChecked()) playBusRoute(2);
                        //else stopBusRoute(2);
                        break;
                    case R.id.erp_express:
                        if (menuItem.isChecked()) playBusRoute(4);
                        //else stopBusRoute(4);
                        break;
                    case R.id.poi:
                        mv.clearMarkerFocus();
                        mv.clear();
                        if (menuItem.isChecked() && !poiShow) {
                            poiShow = true;
                            if (mv.getZoomLevel() >= 18) {
                                mv.addMarkers(poiMarkers.get(1));
                            }
                            if (mv.getZoomLevel() >= 16) {
                                mv.addMarkers(poiMarkers.get(0));
                            }
                        } else {
                            poiShow = false;
                            mv.removeMarkers(poiMarkers.get(1));
                            mv.removeMarkers(poiMarkers.get(0));
                            mv.clearMarkerFocus();
                        }
                        mv.invalidate();
                        break;
                    case R.id.settings:
                        menuItem.setChecked(false);
                        intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.help:
                        menuItem.setChecked(false);
                        DialogFragment newFragmentHelp = new Help();
                        newFragmentHelp.show(getSupportFragmentManager(), "Help & Feedback");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        break;
                }

                // Setting drawerMenuItem allows us to see what items currently selected in the
                // navigation drawer globally
                previousDrawerMenuItem = menuItem;
                return true;
            }
        });*/
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed(){
        // We had to override the onBackPressed within the mainActivity because previously it would
        // destroy the activity and when the user returned to the app, the entire activity would be
        // recreated. We now create an intent ourselves that takes the user to the home screen
        // (like it should) and puts the mainActivity in the foreground.
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }


    private void userLocationFAB(){
        // This method sets up the floating action button (FAB) and handles the on click which will
        // goto the users current location.
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the FAB is clicked, we first check that we can find the user location and if
                // they are within the map scrolling limit. If so we move the mapView to the user
                // location. Otherwise, we display a message

                if (mv.getUserLocation() != null) {
                    if (scrollLimit.contains(mv.getUserLocation())) mv.goToUserLocation(true);
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.userLocationNotWithinBB), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Your current location cannot be found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void stopBusRoute(int busRoute){
        // Stop handler from moving and updating bus marker
        transitHandler.removeCallbacksAndMessages(null);
        // Clear all bus markers
        // TODO fix so that it only clears bus markers instead of all
        mv.clear();

        // Change boolean to false so globally we know we are no long showing a bus route
        busRouteShow = false;

        // Change the current bus showing int to 0 (null)
        currentBusRouteShowing = 0;

        // Since we are removing whatever current route is showing, we also want to uncheck the item
        // in the navigation drawer
//        previousDrawerMenuItem.setChecked(false);

        // Finally, remove bus route tile overlay from mapView
        switch(busRoute){
            case 1:
                mv.getOverlays().remove(campusLoopTiles);
                break;
            case 2:
                mv.getOverlays().remove(eastwoodErpLineTiles);
                break;
            case 3:
                mv.getOverlays().remove(outerLoopTiles);
                break;
            case 4:
                mv.getOverlays().remove(erpExpressTiles);
                break;
        }
        mv.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchItem = menu.add(android.R.string.search_go);

        searchItem.setIcon(R.drawable.ic_search_white_24dp);

        MenuItemCompat.setActionView(searchItem, searchView);

        MenuItemCompat.setShowAsAction(searchItem,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS
                        | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);

        showSearch(false);
    }

    protected void showSearch(boolean visible) {
        // This affects whether or not the search EditText view is shown or not for the search.
        // when the activity is recreated the searchItem is null and caused the app to crash
        // occasionally in the past. To fix this, we simply wrapped all code in an if statement.
        if(searchItem != null) {
            if (visible) MenuItemCompat.expandActionView(searchItem);
            else MenuItemCompat.collapseActionView(searchItem);
        }



    }

    public void displayCampusIssues(){

        Toast.makeText(getApplicationContext(), "Long press to add an issue at that location", Toast.LENGTH_SHORT).show();
        campusIssueMarkersVisable = true;

        // Create volley queue
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String URI = "http://iguide.heliohost.org/get_all_markers.php";

        // we begin by fetching the json data using volley
        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Parse the JSON response
                Gson gson = new Gson();
                String issueData = response.toString();
                IssueDataModel issueDataModel = gson.fromJson(issueData, IssueDataModel.class);

                //System.out.println(issueDataModel.getMarkers().get(0).getPid());
                for(int i=0; i<issueDataModel.getMarkers().size(); i++){

                    Marker issue = new Marker(issueDataModel.getMarkers().get(i).getTitle(),
                            issueDataModel.getMarkers().get(i).getDescription() ,
                            new LatLng(issueDataModel.getMarkers().get(i).getMarkerLat(), issueDataModel.getMarkers().get(i).getMarkerLon()));

                    issue.setToolTip(new CampusIssueInfoWindow(getApplicationContext(),MainActivity.this, mv, issueDataModel.getMarkers(), i));

                    switch (issueDataModel.getMarkers().get(i).getType()) {
                        case "Sidewalk light out":
                            issue.setMarker(getResources().getDrawable(R.drawable.light));
                            break;
                        case "IT Department":
                            issue.setMarker(getResources().getDrawable(R.drawable.computer));
                            break;
                        case "Room temperature":
                            issue.setMarker(getResources().getDrawable(R.drawable.temperature));
                            break;
                        default:
                            issue.setMarker(getResources().getDrawable(R.drawable.other));
                            break;
                    }
                    mv.addMarker(issue);

                }

            }
        }, new Response.ErrorListener() {
            // This is in case an error occurs when fetching the json.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("issueData", "Error occured when trying to get markers from database: " + error);
            }
        });
        mRequestQueue.add(req);

        // This allows volley to retry request if for some reason it times out the first time
        // More info can be found in this question:
        // http://stackoverflow.com/questions/17094718/android-volley-timeout
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));










    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This method handles actions when a toolbar item is clicked on.
        int id = item.getItemId();

        // If our floor level icon is selected we inflate the menu and wait for user to select a specific floor.
        if (id == R.id.floor) {

        // Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.floor));
        // Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.floor_level, popup.getMenu());

        // This is where we register a clicked menu item and perform action of that click.
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getTitle().equals("Floor 1")) {
                    if (currentFloor == 0) Toast.makeText(getApplicationContext(), "Already showing Floor 1", Toast.LENGTH_SHORT).show();
                    else{
                        changeFloorLevel(getApplicationContext(), mv, 0);
                    }
                }
                if(item.getTitle().equals("Floor 2")) {
                    if (currentFloor == 1) Toast.makeText(getApplicationContext(), "Already showing Floor 2", Toast.LENGTH_SHORT).show();
                    else{
                        changeFloorLevel(getApplicationContext(), mv, 1);
                    }
                }
                if(item.getTitle().equals("Floor 3")) {
                    if (currentFloor == 2) Toast.makeText(getApplicationContext(), "Already showing Floor 3", Toast.LENGTH_SHORT).show();
                    else{
                        changeFloorLevel(getApplicationContext(), mv, 2);
                    }
                }


                return true;
            }
        });

            popup.show(); //showing popup menu*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMap() {
        // Method is executed only once when the activity is created. We begin by actually creating
        // the mapView and setting its rules, such as max zoom, beginning center and zoom, etc.
        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(29.7199489, -95.3422334));
        mv.setZoom(17);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);
        mv.setMaxZoomLevel(21);
        //mv.setClusteringEnabled(true, null, 18);

        // We create an event listener so that we can perform actions each time the user scrolls,
        // zooms, or rotates the map.
        mv.addListener(new DelayedMapListener(new MapListener() {
            @Override
            public void onScroll(ScrollEvent event) {
                // Nothing needs to be done when the user scrolls.
            }

            @Override
            public void onZoom(ZoomEvent event) {
                // This listener is important only when POI is selected. The reason being is that at
                // certain zoom levels different POI are displayed. Therefore, we only ever do anything
                // in this method when poiShow == true.
                if (poiShow) {
                    // We close the current infoWindow and clear the map of all markers.
                    mv.closeCurrentTooltip();
                    mv.clear();

                    // We then redraw all the markers depending on the zoom level. This is definitly
                    // not the best way of doing this.
                    if (mv.getZoomLevel() >= 18 && poiShow) {
                        mv.addMarkers(poiMarkers.get(1));
                    }
                    if (mv.getZoomLevel() >= 16 && poiShow) {
                        mv.addMarkers(poiMarkers.get(0));
                    }
                }
            }

            @Override
            public void onRotate(RotateEvent event) {
                // We do nothing when the map is rotated.
            }
        }));

        // Following the map event listener above, we create an event overlay that can be though of
        // as a transparent overlay that sits atop the mapView and calls the methods below when the
        // user single/long taps the map. Its important to note that the ILatLng pressLatLon allows
        // us to get the location where the user tapped. for instance, pressLatLon.getLatitude, will
        // give us the latitude where the user pressed.
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapUpHelper(ILatLng pressLatLon) {
                // we close the infoWindow if the user single taps.
                mv.closeCurrentTooltip();
                return true;
            }

            @Override
            public boolean longPressHelper(ILatLng pressLatLon) {

                if(campusIssueMarkersVisable) {


                    Bundle bundle = new Bundle();
                    bundle.putDouble("LOCATIONLAT", pressLatLon.getLatitude());
                    bundle.putDouble("LOCATIONLON", pressLatLon.getLongitude());

                    Intent intent = new Intent(MainActivity.this, CampusIssueReportActivity.class);
                    intent.putExtra("BUNDLE", bundle);
                    startActivity(intent);
                }
                return true;
            }
        };

        // Now we need to actually add the overlay to the mapView overlay list so that it can be used.
        MapEventsOverlay gestureOverlay = new MapEventsOverlay(this, mReceive);
        mv.getOverlays().add(gestureOverlay);

        // The last thing we do setting up the main mapView is that we go ahead and display the
        // "Floor 1" level.
        //System.out.println();
        changeFloorLevel(getApplicationContext(), mv, 0);

        // Once the main map is setup, we go ahead and assign tileLayers for all the bus routes
        MapboxTileLayer campusLoopOverlay = new MapboxTileLayer("cammace.n2jn0loh");
        MapTileLayerBase test = new MapTileLayerBasic(getApplicationContext(), campusLoopOverlay, mv);
        campusLoopTiles = new TilesOverlay(test);
        campusLoopTiles.setDrawLoadingTile(false);
        campusLoopTiles.setLoadingBackgroundColor(Color.TRANSPARENT);

        MapboxTileLayer outerloopOverlay = new MapboxTileLayer("cammace.nmo5dh63");
        MapTileLayerBase test1 = new MapTileLayerBasic(getApplicationContext(), outerloopOverlay, mv);
        outerLoopTiles = new TilesOverlay(test1);
        outerLoopTiles.setDrawLoadingTile(false);
        outerLoopTiles.setLoadingBackgroundColor(Color.TRANSPARENT);

        MapboxTileLayer erpExpressOverlay = new MapboxTileLayer("cammace.nn02920e");
        MapTileLayerBase test2 = new MapTileLayerBasic(getApplicationContext(), erpExpressOverlay, mv);
        erpExpressTiles = new TilesOverlay(test2);
        erpExpressTiles.setDrawLoadingTile(false);
        erpExpressTiles.setLoadingBackgroundColor(Color.TRANSPARENT);

        MapboxTileLayer eastwoodErpLineOverlay = new MapboxTileLayer("cammace.nn02f097");
        MapTileLayerBase test3 = new MapTileLayerBasic(getApplicationContext(), eastwoodErpLineOverlay, mv);
        eastwoodErpLineTiles = new TilesOverlay(test3);
        eastwoodErpLineTiles.setDrawLoadingTile(false);
        eastwoodErpLineTiles.setLoadingBackgroundColor(Color.TRANSPARENT);

        // Uncomment line below to enable map rotation
        // Disabled because text on map doesn't rotate
        //mv.setMapRotationEnabled(true);

        // We'd uncomment this code below and use it to perform an action every time the user touches
        // the map. However, for now we have no use for it and is only included in this code for
        // testing purposes.
        //mv.setOnTouchListener(new View.OnTouchListener(){
        //
        //    @Override
        //public boolean onTouch(View v, MotionEvent e){
        //        System.out.println(mv.getZoomLevel());
        //        return true;
        //    }
        //});
    }

    private void handleIntent(Intent intent){
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // if POI or a bus route are currently displayed on map, disable.
            if(poiShow) poiShow = false;
            if(busRouteShow) stopBusRoute(currentBusRouteShowing);
            if(campusIssueMarkersVisable) campusIssueMarkersVisable = false;
            currentChildMenuItem = "";
            currentMapViewOverlay = "";

            // Notify the drawer adapter so it can redraw all items
            mMenuAdapter.notifyDataSetChanged();

            // This is called so that the currently selected navigation draw item is no longer selected
            if(previousDrawerMenuItem != null) previousDrawerMenuItem.setChecked(false);


            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestion.AUTHORITY, SearchSuggestion.MODE);
            suggestions.saveRecentQuery(query, null);

            Search search = new Search();
            // Get the search query

            search.executeSearch(this, mv, query, progressBar);


        }
    }

    public void displayRouting(Context context, MapView mapview, double desLat, double desLon){

        if(mapview == null) mapview = mv;
        if(context == null) context = this;

        Graphhopper graphhopper = new Graphhopper();
        graphhopper.executeRoute(context, desLat, desLon, mapview.getUserLocationOverlay().getMyLocation());

        //getFragmentManager().beginTransaction().add(R.id.route_detail_container, blah).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(campusIssueMarkersVisable){
            mv.clear();
            displayCampusIssues();
        }

        mv.setUserLocationEnabled(true);
        mv.getUserLocationOverlay().setDirectionArrowBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_arrow));
        mv.getUserLocationOverlay().setPersonBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_dot));
        //mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);
        //mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.NONE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.setUserLocationEnabled(false);
    }

    public View createProgressBar() {
        final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
        final ProgressBar PB = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        PB.setIndeterminate(true);
        PB.setLayoutParams(lp);
        PB.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


        final ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        decorView.addView(PB);

        ViewTreeObserver observer = PB.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View contentView = decorView.findViewById(android.R.id.content);
                PB.setY(contentView.getY() + toolbar.getHeight() + getStatusBarHeight() - 15);

                ViewTreeObserver observer = PB.getViewTreeObserver();
                observer.removeGlobalOnLayoutListener(this);
            }
        });
        return PB;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void changeFloorLevel(Context context, MapView mv, int level) {
        // This method is used to change the mapView floor level. Only one level can be shown to the user.
        String mapKey;

        // This checks the mapView overlay list and is only executed if a floorlevel overlay is
        // found in the list.
        if (mv.getOverlays().contains(floorLevel)) {
            // First we check and see if the floor level we are changing to is already being shown.
            // If so nothing needs to be done, therefore we return.
            if (level == currentFloor) return;

            // We need to remove the floorlevel currently within the overlay list before adding the
            // new floorlevel to the mapView.
            mv.getOverlays().remove(floorLevel);
        }

        // this switch associates the method level number (given when we call this method) with the
        // floor mapKey.
        switch (level) {
            default:
                mapKey = "cammace.nc76p7k8"; //getString(R.string.floor_0_key);
                break;
            case 1:
                mapKey = "cammace.nc77c38k"; //getString(R.string.floor_1_key);
                break;
            case 2:
                mapKey = "cammace.nc77kk5a"; //getString(R.string.floor_2_key);
                break;
        }

        // We now create the floor overlay.
        MapboxTileLayer mapboxTileLayer = new MapboxTileLayer(mapKey);
        MapTileLayerBase mapTileLayerBase = new MapTileLayerBasic(context, mapboxTileLayer, mv);
        floorLevel = new TilesOverlay(mapTileLayerBase);
        floorLevel.setDrawLoadingTile(false);
        floorLevel.setLoadingBackgroundColor(Color.TRANSPARENT);

        // add the floorLevel to the mapView overlay list and display it to the user. Call
        // invalidate so that the mapView overlays are refreshed and the new floorLevel is
        // instantly shown
        mv.getOverlays().add(0, floorLevel);
        mv.invalidate();

        // Lastly, we need to update the currentFloor level int so we easily can know which level is
        // being shown to the user currently.
        currentFloor = level;
    }// end changeFloorLevel

    public void playBusRoute(final int busRoute){
        // This method creates a handler that moves the bus markers every given second. This makes
        // the markers move smoother on the map instead of jumping around. After x amount of time,
        // the actual GPS location of the buses are updated.

        // Go ahead and clear the map
        mv.clearMarkerFocus();
        mv.clear();

        // First we check to ensure no other transit overlay is present. If there is one we remove it
        if(mv.getOverlays().contains(campusLoopTiles)) stopBusRoute(1);
        if(mv.getOverlays().contains(outerLoopTiles)) stopBusRoute(3);
        if(mv.getOverlays().contains(eastwoodErpLineTiles))stopBusRoute(2);
        if(mv.getOverlays().contains(erpExpressTiles))stopBusRoute(4);

        // Set firstBusCheck to true so we know its the first time running updateCurrentBusLocation
        firstBusCheck = true;

        // We change this int so that globally we can tell which bus route is currently being shown
        // on the mapView
        currentBusRouteShowing = busRoute;

        // We also need to globally notify everything interested that we are now showing a bus route
        busRouteShow = true;

        // Clear all list so no leftover data interferes with the new bus routes
        start.clear();
        finish.clear();
        busPlot.clear();

        // Create volley queue
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // Go ahead and check at beginning to see where buses currently are at.
        updateCurrentBusLocation(getResources().getString(R.string.busTrackingURL), busRoute);

        // Setup the handler that is executed every second.
        transitHandler.postDelayed(new Runnable() {
            public void run() {
                // Check if timer has gone off and if it has, go ahead and update bus current GPS
                // location and reset timer.
                if (updateBusTimer >= 20) {
                    updateBusTimer = 0;
                    updateCurrentBusLocation(getResources().getString(R.string.busTrackingURL), busRoute);
                }

                // This if statement checks if list is empty, this ensures that we don't try moving
                // marker to a null location and crashing the app.
                if (busPlot.isEmpty()) Log.wtf("busRoute", "busMarkerList is empty");
                else {
                    // Clear all the markers on the map
                    mv.clearMarkerFocus();
                    mv.clear();

                    // Draw the bus markers
                    for (int i = 0; i < buses.size(); i++) {
                        List<LatLng> tempBus = buses.get(i);
                        Marker busMarker = new Marker(null, null, tempBus.get(0));

                        switch(busRoute){
                            case 1:
                                busMarker.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_bus_marker));
                                break;
                            case 2:
                                busMarker.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.drawable.brown_bus_marker));
                                break;
                            case 3:
                                busMarker.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purple_bus_marker));
                                break;
                            case 4:
                                busMarker.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.drawable.orange_bus_marker));
                                break;
                        }
                        mv.addMarker(busMarker);
                        tempBus.remove(0);
                    }
                }

                // add one second to updateBusTimer
                updateBusTimer++;

                transitHandler.postDelayed(this, 500);
            }
        }, 500);

        // Padding value around zoom boundingbox
        double padding;

        // Finally, overlay the bus route tile on top of the map and change the map camera view to fit route
        switch(busRoute){
            case 1:
                mv.getOverlays().add(0, campusLoopTiles);
                padding = 0.0;
                mv.zoomToBoundingBox(new BoundingBox(
                        29.725980077848597 + padding, // North
                        -95.33725261688232 + padding, // East
                        29.71621539329541 - padding,// South
                        -95.34826040267944 - padding // West
                        ), true, true);
                break;
            case 2:
                mv.getOverlays().add(0, eastwoodErpLineTiles);
                padding = 0.005;
                mv.zoomToBoundingBox(new BoundingBox(
                        29.72886830436441 + padding, // North
                        -95.32023668289185 + padding, // East
                        29.716345843815365 - padding,// South
                        -95.34223079681395 - padding // West
                ), true, true);
                break;
            case 3:
                mv.getOverlays().add(0, outerLoopTiles);
                padding = 0.0;
                mv.zoomToBoundingBox(new BoundingBox(
                        29.722905422785455 + padding, // North
                        -95.34014940261841 + padding, // East
                        29.710438128858048 - padding,// South
                        -95.35074949264526 - padding // West
                ), true, true);
                break;
            case 4:
                mv.getOverlays().add(0, erpExpressTiles);
                padding = 0.005;
                mv.zoomToBoundingBox(new BoundingBox(
                        29.72886830436441 + padding, // North
                        -95.32023668289185 + padding, // East
                        29.716345843815365 - padding,// South
                        -95.34223079681395 - padding // West
                ), true, true);
                break;
        }
        // Refresh map so it shows the overlay
        mv.invalidate();
    }

    private void updateCurrentBusLocation(String URI, final int busRoute) {
        // This method is used to fetch json, parse it, and then update/create the list that tells
        // where to move the bus markers next.

        // we begin by fetching the json data using volley
        JsonObjectRequest req = new JsonObjectRequest(URI, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Parse the JSON response
                Gson gson = new Gson();
                String busData = response.toString();
                BusLocation busInfo = gson.fromJson(busData, BusLocation.class);

                // Create buses in service counter. This narrows the json results to just the buses
                // that are currently in service
                int busesInService = 0;

                // Clear the buses list so its ready to be repopulated with each bus and its location.
                buses.clear();

                // Go through the json list (busInfo) and check if it's in service and in the route we need
                for (int i = 0; i < busInfo.getGetVehicles().size(); i++)
                    if (busInfo.getGetVehicles().get(i).getInService() == 1
                            && busInfo.getGetVehicles().get(i).getRouteID() == busRoute) {

                        busesInService++;

                        // If it's the first time running this method for the route selected, the
                        // start list will be null, therefore, we need to set it to the same as finish
                        // so that later on we wont get a null error.
                        if(firstBusCheck) start.add(new LatLng(busInfo.getGetVehicles().get(i).getLat(), busInfo.getGetVehicles().get(i).getLng()));
                        finish.add(new LatLng(busInfo.getGetVehicles().get(i).getLat(), busInfo.getGetVehicles().get(i).getLng()));
                    }

                // Once we have the amount of buses on route and their locations, we can then operate on them.
                // If the route currently contains no buses, we display a message telling the user
                if(busesInService == 0 && firstBusCheck) Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.noBusOnRoute), Toast.LENGTH_SHORT).show();

                // This is where we populate the busPlot list with the results given by the pointsBetween
                // method. This essentially smooths the marker movement so it doesn't jump so much.
                for(int i = 0; i<busesInService; i++) {
                    busPlot = twoPointMath.pointsBetween(start.get(i), finish.get(i), 20);
                    buses.add(busPlot);
                }

                // Clear the start list and repopulate it with what used to be in the finish. This
                // prepares it for next time we run this method it will have the correct positions.
                start.clear();
                for(int i = 0; i<busesInService; i++) {
                    start.add(finish.get(i));
                }

                // Lastly, set the firstBusCheck to false and clear the finish list for next time.
                firstBusCheck = false;
                finish.clear();
            }
        }, new Response.ErrorListener() {
            // This is in case an error occurs when fetching the json.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("busRoute", "Error occurred when trying to update bus GPS locations" + error);
            }
        });
        mRequestQueue.add(req);

        // This allows volley to retry request if for some reason it times out the first time
        // More info can be found in this question:
        // http://stackoverflow.com/questions/17094718/android-volley-timeout
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}