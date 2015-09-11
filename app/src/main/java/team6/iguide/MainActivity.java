package team6.iguide;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.MapEventsOverlay;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBasic;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import team6.iguide.OverpassModel.OverpassElement;
import team6.iguide.OverpassModel.OverpassModel;

// http://stackoverflow.com/questions/20610253/how-to-enable-longclick-on-map-with-osmdroid-in-supportmapfragment

// This helped solve search issues:
// http://blog.dpdearing.com/2011/05/getting-android-to-call-onactivityresult-after-onsearchrequested/

// This link will show you an exmaple of the JSON get request and response using the Overpass API
// http://overpass-turbo.eu/s/bdv



public class MainActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private FloatingActionButton FAB;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem searchItem;
    private SearchView searchView;
    private SearchRecentSuggestions suggestions;
    private MapView mv;
    private UserLocationOverlay myLocationOverlay;
    private TilesOverlay level_0;
    private TilesOverlay level_1;
    private TilesOverlay level_2;
    private TilesOverlay transitLines;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userLocationFAB();

        suggestions = new SearchRecentSuggestions(this, SearchSuggestion.AUTHORITY, SearchSuggestion.MODE);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);


        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setMaxWidth(1000);


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
        setMap();


    }

    private void navigationDrawer(){
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                //drawerLayout.closeDrawers();
                Intent intent;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.parking:
                        drawerLayout.closeDrawers();
                        Toast.makeText(getApplicationContext(), "Parking", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.transit:
                        drawerLayout.closeDrawers();

                        if(menuItem.isChecked()) {
                            mv.getOverlays().add(transitLines);

                            mv.invalidate();

                        }
                        else{
                            mv.getOverlays().remove(transitLines);
                            mv.invalidate();

                        }



                        return true;
                    case R.id.poi:
                        drawerLayout.closeDrawers();

                        final Intent searchIntent = new Intent(getApplicationContext(),
                                SearchResults.class);
                        // add query to the Intent Extras
                        String query = "atm";
                        searchIntent.putExtra(SearchManager.QUERY, query);
                        startActivityForResult(searchIntent, 444);

                        return true;
                    /*case R.id.store:
                        drawerLayout.closeDrawers();
                        Toast.makeText(getApplicationContext(), "Store", Toast.LENGTH_SHORT).show();
                        return true;*/
                    case R.id.settings:
                        intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);

                        overridePendingTransition(R.anim.pull_in, R.anim.hold);

                        return true;
                    case R.id.help:
                        drawerLayout.closeDrawers();

                        DialogFragment newFragmentHelp = new Help();
                        newFragmentHelp.show(getSupportFragmentManager(), "Help & Feedback");
                       /* Help fragment = new Help();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.commit();*/
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
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

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    // This method sets up the floating action button (FAB) and handles the on click.
    private void userLocationFAB(){
        // FAB for myLocationButton
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mv.getController().animateTo();

                //mv.setUserLocationRequiredZoom(mv.getZoomLevel());
                mv.goToUserLocation(true);
            }
        });
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

        if(id == R.id.directions){
            for(int i=0; i < mv.getOverlays().size(); i++){
                System.out.println(mv.getOverlays().get(i).toString());
            }

        }

        if (id == R.id.floor) {

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.floor));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.floor_level, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                if(item.getTitle().equals("Floor 1")) {
                    if (currentLevel == 1) Toast.makeText(getApplicationContext(), "Already showing Floor 1", Toast.LENGTH_SHORT).show();
                    else setCurrentLevel1();
                }
                if(item.getTitle().equals("Floor 2")) {
                    if (currentLevel == 2) Toast.makeText(getApplicationContext(), "Already showing Floor 2", Toast.LENGTH_SHORT).show();
                    else setCurrentLevel2();
                }
                if(item.getTitle().equals("Floor 3")) {
                    if (currentLevel == 3) Toast.makeText(getApplicationContext(), "Already showing Floor 3", Toast.LENGTH_SHORT).show();
                    else setCurrentLevel3();
                }








                return true;
            }
        });

        popup.show(); //showing popup menu*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setCurrentLevel1(){
        mv.getOverlays().remove(level_1);
        mv.getOverlays().remove(level_2);
        mv.getOverlays().add(level_0);
        mv.invalidate();
        currentLevel = 1;
    }

    private void setCurrentLevel2(){
        mv.getOverlays().remove(level_0);
        mv.getOverlays().remove(level_2);
        mv.getOverlays().add(level_1);
        mv.invalidate();
        currentLevel = 2;
    }

    private void setCurrentLevel3() {
        mv.getOverlays().remove(level_0);
        mv.getOverlays().remove(level_1);
        mv.getOverlays().add(level_2);
        mv.invalidate();
        currentLevel = 3;
    }

    public void setMap() {

        mv = (MapView) this.findViewById(R.id.mapview);
        mv.setCenter(new LatLng(29.7199489, -95.3422334));
        mv.setZoom(17);
        mv.setUserLocationEnabled(true);

        BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);

        // Create level_0 layer
        mv.setAccessToken("sk.eyJ1IjoiY2FtbWFjZSIsImEiOiI1MDYxZjA1MDc0YzhmOTRhZWFlODBlNGVlZDgzMTcxYSJ9.Ryw8G5toQp5yloce36hu2A");
        MapboxTileLayer level0Overlay = new MapboxTileLayer("cammace.nc76p7k8");
        MapTileLayerBase level0OverlayBase = new MapTileLayerBasic(getApplicationContext(), level0Overlay, mv);
        level_0 = new TilesOverlay(level0OverlayBase);
        level_0.setDrawLoadingTile(false);
        level_0.setLoadingBackgroundColor(Color.TRANSPARENT);

        // Create level_1 layer
        mv.setAccessToken("sk.eyJ1IjoiY2FtbWFjZSIsImEiOiI1MDYxZjA1MDc0YzhmOTRhZWFlODBlNGVlZDgzMTcxYSJ9.Ryw8G5toQp5yloce36hu2A");
        MapboxTileLayer level1Overlay = new MapboxTileLayer("cammace.nc77c38k");
        MapTileLayerBase level1OverlayBase = new MapTileLayerBasic(getApplicationContext(), level1Overlay, mv);
        level_1 = new TilesOverlay(level1OverlayBase);
        level_1.setDrawLoadingTile(false);
        level_1.setLoadingBackgroundColor(Color.TRANSPARENT);

        // Create level_2 layer
        mv.setAccessToken("sk.eyJ1IjoiY2FtbWFjZSIsImEiOiI1MDYxZjA1MDc0YzhmOTRhZWFlODBlNGVlZDgzMTcxYSJ9.Ryw8G5toQp5yloce36hu2A");
        MapboxTileLayer level2Overlay = new MapboxTileLayer("cammace.nc77kk5a");
        MapTileLayerBase level2OverlayBase = new MapTileLayerBasic(getApplicationContext(), level2Overlay, mv);
        level_2 = new TilesOverlay(level2OverlayBase);
        level_2.setDrawLoadingTile(false);
        level_2.setLoadingBackgroundColor(Color.TRANSPARENT);


        MapboxTileLayer transitLineOverlay = new MapboxTileLayer("cammace.n2jn0loh");
        MapTileLayerBase test = new MapTileLayerBasic(getApplicationContext(), transitLineOverlay, mv);
        transitLines = new TilesOverlay(test);
        transitLines.setDrawLoadingTile(false);
        transitLines.setLoadingBackgroundColor(Color.TRANSPARENT);

        //DrawRoute drawRoute = new DrawRoute();
        //mv.getOverlays().add(drawRoute)

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


        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapUpHelper(ILatLng pressLatLon) {
                // Convert pressed latitude and longitude to string for URI
                //String latitude = Double.toString(pressLatLon.getLatitude());
                //String longitude = Double.toString(pressLatLon.getLongitude());
                //mv.clear(); // Clears the map when press occurs
                mv.closeCurrentTooltip();
                System.out.println("Single press");


                return true;
            }

            @Override
            public boolean longPressHelper(ILatLng pressLatLon) {

                // TODO long press will give user immediate route results
                System.out.println("Long press");

                return true;
            }
        };

        MapEventsOverlay gestureOverlay = new MapEventsOverlay(this, mReceive);
        mv.getOverlays().add(gestureOverlay);

        myLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(this), mv);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);


       // mv.getUserLocationOverlay().setDirectionArrowBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red_pin));
       // mv.getUserLocationOverlay().setPersonBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.green_pin));
       // mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);
       // mv.getUserLocationOverlay().setTrackingMode(UserLocationOverlay.TrackingMode.NONE);
        mv.getOverlays().add(myLocationOverlay);

    }

    private void handleIntent(Intent intent){
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestion.AUTHORITY, SearchSuggestion.MODE);
            suggestions.saveRecentQuery(query, null);

            // manually launch the real search activity
            final Intent searchIntent = new Intent(getApplicationContext(),
                    SearchResults.class);

            // add query to the Intent Extras
            searchIntent.putExtra(SearchManager.QUERY, query);
            startActivityForResult(searchIntent, 222);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
/*
        if(requestCode == 444){
            // TODO create caching of POI so every time its toggled It doesn't have to use volley.
            if(resultCode == RESULT_OK) {

                Bundle n = data.getExtras();
                Serializable a = n.getSerializable("CAM");
                OverpassModel b = ((OverpassModel) a);

                List<OverpassElement> poi = b.getElements();

                //System.out.println(poi.get(0).getTags().getAmenity());


                for(int j = 0; j < poi.size(); j++) {
                    if (poi.get(j).getTags().getAmenity().equals("atm")) {
                        Marker marker = new Marker("ATM", "Description", new LatLng(
                                poi.get(j).getLat(), poi.get(j).getLon()));
                        marker.setToolTip(new CustomInfoWindow(mv));
                        marker.setMarker(getResources().getDrawable(R.drawable.red_pin));
                        mv.addMarker(marker);
                    }
                    System.out.println(j);
                }
                System.out.println("blah");
            }

        }

*/

        if(requestCode == 222){
            if(resultCode == RESULT_OK){

                Bundle n = data.getExtras();
                Serializable x = n.getSerializable("CAM");
                OverpassModel z = ((OverpassModel) x);

                List<OverpassElement> q = z.getElements();

                // Clear the mapview of any markers
                mv.closeCurrentTooltip();
                mv.clear();
                mv.clearMarkerFocus();

                // Check if the search returns no results
                if(q.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_SHORT).show();
                } else {
/*
                    if((q.get(0).getTags().getAmenity() != null)){
                        //System.out.println("amenity");
                        if (q.get(0).getType().equals("node")) {
                            for (int i = 0; i < q.size(); i++) {
                                Marker marker = new Marker("ATM", "Description", new LatLng(
                                        q.get(i).getLat(), q.get(i).getLon()));
                                mv.addMarker(marker);
                            }
                        } else {
                            System.out.println("amenity");
                        }
                    }*/



                    // This counts how many of the search results are valid results
                    int pinCount = 0;
                    for(int i = 0; i < q.size(); i++) {
                        if (q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")) {
                            pinCount++;
                        }
                    }

                    if(pinCount == 1) {
                        // Check if the search returned a building
                        if (q.get(0).getTags().getBuilding() != null) {
                            Marker marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                                    q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                            marker.setToolTip(new CustomInfoWindow(this, mv, q, 0));
                            marker.setMarker(getResources().getDrawable(R.drawable.red_pin));
                            mv.addMarker(marker);
                            mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                            mv.getController().setZoom(19);
                        }
                        // Check if the search returned a room
                        else if (q.get(0).getTags().getIndoor() != null) {

                            Marker marker = new Marker(q.get(0).getTags().getName(), q.get(0).getTags().getRef(), new LatLng(
                                    q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                            marker.setToolTip(new CustomInfoWindow(this, mv, q, 0));
                            marker.setMarker(getResources().getDrawable(R.drawable.red_pin));
                            if(q.get(0).getTags().getLevel().equals("0") && currentLevel != 1) setCurrentLevel1();
                            if(q.get(0).getTags().getLevel().equals("1") && currentLevel != 2) setCurrentLevel2();
                            if(q.get(0).getTags().getLevel().equals("2") && currentLevel != 3) setCurrentLevel3();
                            mv.addMarker(marker);
                            mv.getController().animateTo(new LatLng(q.get(0).getCenter().getLat(), q.get(0).getCenter().getLon()));
                            mv.getController().setZoom(21);
                        }
                    }
                    else{
                        for(int i = 0; i < pinCount; i++) {
                            if(q.get(i).getType().equals("way") || q.get(i).getType().equals("relation")){
                                Marker marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                                        q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                                marker.setToolTip(new CustomInfoWindow(this, mv, q, i));
                                marker.setMarker(getResources().getDrawable(R.drawable.red_pin));
                                mv.addMarker(marker);
                                mv.getController().setZoom(17);
                            }
                        }
                        /*for(int i = 0; i < q.size(); i++){
                            if (q.get(i).getTags().getBuilding() != null) {
                                Marker marker = new Marker(q.get(i).getTags().getName(), "Description", new LatLng(
                                        q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                                marker.setToolTip(new CustomInfoWindow(mv));
                                mv.addMarker(marker);
                            }
                            // Check if the search returned a room
                            else if (q.get(i).getTags().getIndoor() != null) {

                                Marker marker = new Marker(q.get(i).getTags().getName(), q.get(i).getTags().getRef(), new LatLng(
                                        q.get(i).getCenter().getLat(), q.get(i).getCenter().getLon()));
                                marker.setToolTip(new CustomInfoWindow(mv));
                                mv.addMarker(marker);
                            }
                        }*/

                    }
                }
            }
        }
    }

    //TODO Fix so that when app is closed it stops getting user location
    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation();
    }


}