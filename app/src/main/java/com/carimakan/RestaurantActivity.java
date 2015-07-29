package com.carimakan;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.carimakan.app.MyApplication;
import com.carimakan.helper.Restaurant;
import com.carimakan.helper.SwipeListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ersa Rizki Dimitri on 06/04/2015.
 */
public class RestaurantActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    public static final String QUERY_KEY = "query";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // DB Class to perform DB related operations
    //DBController controller = new DBController(this);

    private String TAG = RestaurantActivity.class.getSimpleName();

    private String URL_TOP_250 = "http://carimakan.icon.my.id/getusers.php";//?offset=";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView myList;
    private TabHost tabHost;
    private SwipeListAdapter adapter;
    private List<Restaurant> restaurantList;

    ArrayList<Restaurant> searchResults;
    private HashMap<Marker, HashMap<String, String>> markerHashMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant);
        Resources res = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUpMapIfNeeded();

        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("T 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("", res.getDrawable(R.drawable.ic_local_restaurant_white_18dp));

        TabHost.TabSpec spec2=tabHost.newTabSpec("T 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("", res.getDrawable(R.drawable.ic_map_white_18dp));

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        TextView toolTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolTitle.setText(R.string.app_name);
        toolTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, HideMenuActivity.class);
                startActivity(intent);
            }
        });

        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        markerHashMap = new HashMap<>();

        //Here, my data format is ArrayList<HashMap<String, String>>;
        for (int i = 0; i < markerHashMap.size(); i++) {
            final HashMap<String, String> mapData = markerHashMap.get(i);
            if (mapData.get("latitude").trim().length() > 0 && mapData.get("longitude").trim().length() > 0) {
                placeMarker(mapData);
            }
        }

        myList  = (ListView) findViewById(R.id.list_res);
        restaurantList = new ArrayList<>();
        adapter = new SwipeListAdapter(this, restaurantList);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Restaurant dataModel = (Restaurant) adapterView.getItemAtPosition(pos);
                //Log.d(TAG, "string: " + dataModel.getTitle());
                String namaRes = dataModel.getTitle();
                String alamatRes = dataModel.getAlamat();
                String lat = String.valueOf(dataModel.getLati());
                String lng = String.valueOf(dataModel.getLongi());
                String urlPicRes = dataModel.getUrlpic();

                Intent intent = new Intent(RestaurantActivity.this, DetailRestaurantActivity.class);
                intent.putExtra("nama",namaRes);
                intent.putExtra("alamat",alamatRes);
                intent.putExtra("url",urlPicRes);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Showing Swipe Refresh animation on activity create
        // As animation won't start on onCreate, post runnable is used
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchMovies();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /* Assuming this activity was started with a new intent, process the incoming information and
     * react accordingly.
     * @param intent */
    private void handleIntent(Intent intent) {
        // Special processing of the incoming intent only occurs if the if the action specified
        // by the intent is ACTION_SEARCH.
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // SearchManager.QUERY is the key that a SearchManager will use to send a query string
            // to an Activity.
            String query = intent.getStringExtra(SearchManager.QUERY);

            // We need to create a bundle containing the query string to send along to the
            // LoaderManager, which will be handling querying the database and returning results.
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);

            //ContactablesLoaderCallbacks loaderCallbacks = new ContactablesLoaderCallbacks(this);

            // Start the loader with the new query, and an object that will handle all callbacks.
            //getLoaderManager().restartLoader(CONTACT_QUERY_LOADER, bundle, loaderCallbacks);
        }
    }

    //This method is called when swipe refresh is pulled down
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        fetchMovies();
    }

    //Fetching movies json by making http call
    private void fetchMovies() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = URL_TOP_250; //+ offSet;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject movieObj = response.getJSONObject(i);

                                    int rank = movieObj.getInt("id");
                                    String title = movieObj.getString("nama");
                                    String alamat = movieObj.getString("alamat");
                                    String urlpic = movieObj.getString("link_gambar");
                                    Double lati =  movieObj.getDouble("lat");
                                    Double longi = movieObj.getDouble("long");

                                    Restaurant m = new Restaurant(rank, title, alamat, urlpic, lati, longi);

                                    restaurantList.add(m);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Error Retrieving Data\nPull Down to Refresh", Toast.LENGTH_LONG).show();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //reload tabs, i am switching my tabs, this refreshes UI
                tabHost.invalidate();
                tabHost.setCurrentTab(0);

                //searchResults=OriginalValues initially
                searchResults = new ArrayList<Restaurant>(restaurantList);

                //get the text in the EditText
                String searchString = s;
                int textLength = searchString.length();
                searchResults.clear();

                for (int i = 0; i < restaurantList.size(); i++) {
                    String playerName = restaurantList.get(i).title;
                    if (textLength <= playerName.length()) {
                        //compare the String in EditText with Names in the ArrayList
                        if (searchString.equalsIgnoreCase(playerName.substring(0, textLength)))
                            searchResults.add(restaurantList.get(i));
                        //will populate the ListView
                        adapter = new SwipeListAdapter(RestaurantActivity.this, searchResults);
                        //finally,set the adapter to the default ListView
                        myList.setAdapter(adapter);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //reload tabs, i am switching my tabs, this refreshes UI
                tabHost.invalidate();
                tabHost.setCurrentTab(0);

                //searchResults=OriginalValues initially
                searchResults = new ArrayList<Restaurant>(restaurantList);

                //get the text in the EditText
                String searchString = s;
                int textLength = searchString.length();
                searchResults.clear();

                for (int i = 0; i < restaurantList.size(); i++) {
                    String playerName = restaurantList.get(i).title;
                    if (textLength <= playerName.length()) {
                        //compare the String in EditText with Names in the ArrayList
                        if (searchString.equalsIgnoreCase(playerName.substring(0, textLength)))
                            searchResults.add(restaurantList.get(i));
                        //will populate the ListView
                        adapter = new SwipeListAdapter(RestaurantActivity.this, searchResults);

                        //finally,set the adapter to the default ListView
                        myList.setAdapter(adapter);
                    }
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                //reload tabs, i am switching my tabs, this refreshes UI
                tabHost.invalidate();
                tabHost.setCurrentTab(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //insertMarkers(restaurantList);
    }

    private void placeMarker(final HashMap<String, String> mapData) {
        try {
            marker = mMap.addMarker(new MarkerOptions().icon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.restaurant))).position(
                    new LatLng(Double.parseDouble(mapData.get("latitude")), Double.parseDouble(mapData.get("longitude")))));
            markerHashMap.put(marker, mapData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawMarker(){
        for (int i = 0; i < restaurantList.size(); i++) {
            //Double lati = Double.parseDouble(restaurantList.get(i).lati);
            //Double longLat = Double.parseDouble(restaurantList.get(i).longi);
            LatLng position = new LatLng(restaurantList.get(i).getLati(), restaurantList.get(i).getLongi());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(restaurantList.get(i).getTitle())
                    .snippet(restaurantList.get(i).getAlamat())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            //mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
            }
        }
    };

}
