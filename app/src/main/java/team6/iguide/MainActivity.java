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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.MapEventsOverlay;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBasic;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import team6.iguide.BusLocation.BusLocation;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem searchItem;
    private SearchView searchView;
    private MapView mv;
    public  View progressBar;
    int currentFloor;
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

    // Tiles
    private TilesOverlay campusLoopTiles;
    private TilesOverlay outerLoopTiles;
    private TilesOverlay eastwoodErpLineTiles;
    private TilesOverlay erpExpressTiles;
    TilesOverlay floorLevel;



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

        changeFloorLevel(getApplicationContext(), mv, 0);
    }

    private void navigationDrawer(){

        //String[] transitItems = {"transit 1", "transit 2", "transit 3"};

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        /*ExpandableListView drawerList = (ExpandableListView) findViewById(R.id.left_drawer2);


        //drawerList.setAdapter(new SimpleExpandableListAdapter(this, groupData, android.R.layout.simple_list_item_1, new String[] {"blahblah"},));
*/
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                Intent intent;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.parking:
                        Toast.makeText(getApplicationContext(), "Parking feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.campus_loop:
                        // If bus route isn't showing on mapView then display it, else remove it.
                        if (menuItem.isChecked()) playBusRoute(1);
                        else stopBusRoute(1);
                        break;
                    case R.id.outer_loop:
                        if (menuItem.isChecked()) playBusRoute(3);
                        else stopBusRoute(3);
                        break;
                    case R.id.eastwood_erp_line:
                        if (menuItem.isChecked()) playBusRoute(2);
                        else stopBusRoute(2);
                        break;
                    case R.id.erp_express:
                        if (menuItem.isChecked()) playBusRoute(4);
                        else stopBusRoute(4);
                        break;
                    case R.id.poi:
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
                        break;
                    case R.id.settings:
                        intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.help:
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
        });
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





    // This method sets up the floating action button (FAB) and handles the on click.
    private void userLocationFAB(){
        // FAB for myLocationButton
        FloatingActionButton FAB;
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mv.goToUserLocation(true);

            }
        });
    }

    public void stopBusRoute(int busRoute){
        // Stop handler from moving and updating bus marker
        transitHandler.removeCallbacksAndMessages(null);
        // Clear all bus markers
        // TODO fix so that it only clears bus markers instead of all
        mv.clear();

        // Since we are removing whatever current route is showing, we also want to uncheck the item
        // in the navigation drawer
        previousDrawerMenuItem.setChecked(false);

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
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.floor) {

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.floor));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.floor_level, popup.getMenu());

        //registering popup with OnMenuItemClickListener
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
                    System.out.println("Floor 3");
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

        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(29.7199489, -95.3422334));
        mv.setZoom(17);

       // mv.getUserLocationOverlay().setDirectionArrowBitmap();
        PointOfInterest pointOfInterest = new PointOfInterest();
        poiMarkers = pointOfInterest.getPOI(MainActivity.this, mv);

        mv.addListener(new DelayedMapListener(new MapListener() {
            @Override
            public void onScroll(ScrollEvent event) {

            }

            @Override
            public void onZoom(ZoomEvent event) {
                //System.out.println(mv.getZoomLevel());
                mv.closeCurrentTooltip();
                //if(mv.getOverlays().contains(poiMarkers.get(0))) mv.getOverlays().remove(poiMarkers.get(0));
                //if(mv.getOverlays().contains(poiMarkers.get(1))) mv.getOverlays().remove(poiMarkers.get(1));
                if(poiShow) mv.clear();

                if (mv.getZoomLevel() >= 18 && poiShow) {
                    mv.addMarkers(poiMarkers.get(1));
                }
                if (mv.getZoomLevel() >= 16 && poiShow) {
                    mv.addMarkers(poiMarkers.get(0));
                }
            }

            @Override
            public void onRotate(RotateEvent event) {

            }
        }));

        //myLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(this), mv);
        //myLocationOverlay.enableMyLocation();
       // myLocationOverlay.setDrawAccuracyEnabled(true);

        //mv.getOverlays().add(myLocationOverlay);

        BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);

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

        //PathEffect pathEffect = new PathDashPathEffect(makePathDash(), 12, 10, PathDashPathEffect.Style.MORPH);

/*
        PathOverlay pathOverlay = new PathOverlay();
        pathOverlay.addPoint(29.7222194,-95.3431569);
        pathOverlay.addPoint(29.729268922766913, -95.34362554550171);
        pathOverlay.setOptimizePath(false);

        pathOverlay.getPaint().setColor(Color.BLACK);
        pathOverlay.getPaint().setStrokeCap(Paint.Cap.ROUND);
        //pathOverlay.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        //pathOverlay.getPaint().setPathEffect(pathEffect);
        pathOverlay.getPaint().setStrokeWidth(10);


        mv.getOverlays().add(pathOverlay);
        //mv.invalidate();
*/

        // Uncomment line below to enable map rotation
        // Disabled because text on map doesn't rotate
        //mv.setMapRotationEnabled(true);



/*
mv.setOnTouchListener(new View.OnTouchListener(){

    @Override
public boolean onTouch(View v, MotionEvent e){
        System.out.println(mv.getZoomLevel());
        return true;
    }
});
*/

                MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapUpHelper(ILatLng pressLatLon) {
                // Convert pressed latitude and longitude to string for URI
                //String latitude = Double.toString(pressLatLon.getLatitude());
                //String longitude = Double.toString(pressLatLon.getLongitude());
                //mv.clear(); // Clears the map when press occurs
                mv.closeCurrentTooltip();


                return true;
            }

            @Override
            public boolean longPressHelper(ILatLng pressLatLon) {
                return true;
            }
        };

        MapEventsOverlay gestureOverlay = new MapEventsOverlay(this, mReceive);
        mv.getOverlays().add(gestureOverlay);


    }


    private void handleIntent(Intent intent){
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

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

        System.out.println(mapview);
        System.out.println(mapview.getUserLocationOverlay().getMyLocation());
        Graphhopper graphhopper = new Graphhopper();
        graphhopper.executeRoute(context, mapview, desLat, desLon, mapview.getUserLocationOverlay().getMyLocation());

        //getFragmentManager().beginTransaction().add(R.id.route_detail_container, blah).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public int getCurrentFloor(){
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor){
        this.currentFloor = currentFloor;
    }

    public void changeFloorLevel(Context context, MapView mv, int level){

        if(level == currentFloor) return;
        String mapKey;
        Overlay temp = floorLevel;

        //System.out.println(mv.getOverlays());
        //if(mv.getOverlays().contains(floorLevel))
        //System.out.println(mv.getOverlays().get(mv.getOverlays().indexOf(floorLevel)));

        if(mv.getOverlays().contains(floorLevel)){
            temp = mv.getOverlays().get(mv.getOverlays().indexOf(floorLevel));
            mv.getOverlays().remove(floorLevel);
        }

       // if(mv.getOverlays().contains(level_1)) mv.getOverlays().remove(level_1);
       // if(mv.getOverlays().contains(level_2)) mv.getOverlays().remove(level_2);

        //if(mv.getOverlays().get(mv.getOverlays().indexOf(floorLevel)) == )

        //if(level == 0) mapKey = getString(R.string.floor_0_key);
        switch(level){
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

            MapboxTileLayer mapboxTileLayer = new MapboxTileLayer(mapKey);
            MapTileLayerBase mapTileLayerBase = new MapTileLayerBasic(context, mapboxTileLayer, mv);
            floorLevel = new TilesOverlay(mapTileLayerBase);
            floorLevel.setDrawLoadingTile(false);
            floorLevel.setLoadingBackgroundColor(Color.TRANSPARENT);

        System.out.println("temp=    " + temp);
        System.out.println("floorlevel=    " + floorLevel);

        if(temp == floorLevel) System.out.println("400000000");
            mv.getOverlays().add(0, floorLevel);
            mv.invalidate();

            currentFloor = level;

        }







    public void playBusRoute(final int busRoute){
        // This method creates a handler that moves the bus markers every given second. This makes
        // the markers move smoother on the map instead of jumping around. After x amount of time,
        // the actual GPS location of the buses are updated.

        // First we check to ensure no other transit overlay is present. If there is one we remove it
        if(mv.getOverlays().contains(campusLoopTiles)) stopBusRoute(1);
        if(mv.getOverlays().contains(outerLoopTiles)) stopBusRoute(3);
        if(mv.getOverlays().contains(eastwoodErpLineTiles))stopBusRoute(2);
        if(mv.getOverlays().contains(erpExpressTiles))stopBusRoute(4);

        // Set firstBusCheck to true so we know its the first time running updateCurrentBusLocation
        firstBusCheck = true;

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
                    // TODO fix this so it only removes the bus markers instead of all markers.
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