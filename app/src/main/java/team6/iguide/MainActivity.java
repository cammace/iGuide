package team6.iguide;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsOverlay;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBasic;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;

import java.lang.reflect.Field;


// http://stackoverflow.com/questions/20610253/how-to-enable-longclick-on-map-with-osmdroid-in-supportmapfragment

// This helped solve search issues:
// http://blog.dpdearing.com/2011/05/getting-android-to-call-onactivityresult-after-onsearchrequested/

// This link will show you an exmaple of the JSON get request and response using the Overpass API
// http://overpass-turbo.eu/s/bdv



public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem searchItem;
    private SearchView searchView;
    private MapView mv;
    private TilesOverlay transitLines;
    public  View progressBar;

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
                        if(menuItem.isChecked()) {
                            PointOfInterest pointOfInterest = new PointOfInterest();
                            pointOfInterest.getPOI(MainActivity.this, mv);
                        }else{
                            mv.clear();
                            mv.clearMarkerFocus();
                        }

                        return true;
                    case R.id.settings:
                        intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);

                        //overridePendingTransition(R.anim.pull_in, R.anim.hold);

                        return true;
                    case R.id.help:
                        drawerLayout.closeDrawers();

                        DialogFragment newFragmentHelp = new Help();
                        newFragmentHelp.show(getSupportFragmentManager(), "Help & Feedback");
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
        FloatingActionButton FAB;
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        if (id == R.id.floor) {

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.floor));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.floor_level, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                if(item.getTitle().equals("Floor 1")) {
                    System.out.println("Floor 1");
                    /*
                    if (floorLevel.getCurrentFloorLevel() == 0) Toast.makeText(getApplicationContext(), "Already showing Floor 1", Toast.LENGTH_SHORT).show();
                    else{
                        floorLevel.changeFloorLevel(getApplicationContext(), mv, 0);
                        floorLevel.setCurrentFloorLevel(0);
                    }*/
                }
                if(item.getTitle().equals("Floor 2")) {
                    System.out.println("Floor 2");
                    /*if (floorLevel.getCurrentFloorLevel() == 1) Toast.makeText(getApplicationContext(), "Already showing Floor 2", Toast.LENGTH_SHORT).show();
                    else{
                        floorLevel.changeFloorLevel(getApplicationContext(), mv, 1);
                        floorLevel.setCurrentFloorLevel(1);
                    }*/
                }
                if(item.getTitle().equals("Floor 3")) {
                    System.out.println("Floor 3");
                    /*if (floorLevel.getCurrentFloorLevel() == 2) Toast.makeText(getApplicationContext(), "Already showing Floor 3", Toast.LENGTH_SHORT).show();
                    else{
                        floorLevel.changeFloorLevel(getApplicationContext(), mv, 2);
                        floorLevel.setCurrentFloorLevel(2);
                    }*/
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



        //myLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(this), mv);
        //myLocationOverlay.enableMyLocation();
       // myLocationOverlay.setDrawAccuracyEnabled(true);

        //mv.getOverlays().add(myLocationOverlay);

        BoundingBox scrollLimit = new BoundingBox(29.731896194504913, -95.31928449869156, 29.709354854765827, -95.35668790340424);
        mv.setScrollableAreaLimit(scrollLimit);
        mv.setMinZoomLevel(16);

        MapboxTileLayer transitLineOverlay = new MapboxTileLayer("cammace.n2jn0loh");
        MapTileLayerBase test = new MapTileLayerBasic(getApplicationContext(), transitLineOverlay, mv);
        transitLines = new TilesOverlay(test);
        transitLines.setDrawLoadingTile(false);
        transitLines.setLoadingBackgroundColor(Color.TRANSPARENT);






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
            //progressBar.setVisibility(View.VISIBLE);
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
                PB.setY(contentView.getY() + toolbar.getHeight() + getStatusBarHeight() -15);

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






}