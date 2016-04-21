package com.carimakan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.carimakan.DetailRestaurantActivity;
import com.carimakan.MapsResActivity;
import com.carimakan.R;
import com.carimakan.app.MyApplication;
import com.carimakan.helper.Restaurant;
import com.carimakan.helper.SimpleRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OneFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static OneFragment newInstance() {
        return new OneFragment();
    }

    private SimpleRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String TAG = OneFragment.class.getSimpleName();
    private List<Restaurant> restaurantList;
    private static String url = "http://ersarizkidimitri.ga/carimakan/getusers.php";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //adapter = new SwipeListAdapter(getActivity(), restaurantList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_one, container, false);

        //final FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.dummyfrag_bg);
        //frameLayout.setBackgroundColor(color);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.dummyfrag_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        restaurantList = new ArrayList<>();
        //for (int i = 0; i < VersionModel.data.length; i++) {
        //list.add(VersionModel.data[i]);
        //}

        adapter = new SimpleRecyclerAdapter(restaurantList, new SimpleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant model) {
                //Toast.makeText(getContext(), "Item Clicked" + model.getId() , Toast.LENGTH_LONG).show();

                String namaRes = model.getTitle();
                String alamatRes = model.getAlamat();
                String detail = model.getDetail();
                String lat = String.valueOf(model.getLati());
                String lng = String.valueOf(model.getLongi());
                String kategori = model.getKategori();
                String urlPicRes = model.getUrlpic();
                String menu = model.getMenu();

                Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);

                intent.putExtra("nama", namaRes);
                intent.putExtra("alamat", alamatRes);
                intent.putExtra("detail", detail);
                intent.putExtra("url", urlPicRes);
                intent.putExtra("kat", kategori);
                intent.putExtra("menu", menu);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
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

/*
        adapter.SetOnItemClickListener(new SimpleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Restaurant dataModel = restaurantList.get(position);

                //Log.d(TAG, "string: " + dataModel.getTitle());
                String namaRes = dataModel.getTitle();
                String alamatRes = dataModel.getAlamat();
                String detail = dataModel.getDetail();
                String lat = String.valueOf(dataModel.getLati());
                String lng = String.valueOf(dataModel.getLongi());
                String kategori = dataModel.getKategori();
                String urlPicRes = dataModel.getUrlpic();
                String menu = dataModel.getMenu();

                Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);

                intent.putExtra("nama", namaRes);
                intent.putExtra("alamat", alamatRes);
                intent.putExtra("detail", detail);
                intent.putExtra("url", urlPicRes);
                intent.putExtra("kat", kategori);
                intent.putExtra("menu", menu);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                startActivity(intent);
            }
        });
*/

        return rootView;

    }

    //This method is called when swipe refresh is pulled down
    @Override
    public void onRefresh() {
        restaurantList.clear();
        swipeRefreshLayout.setRefreshing(true);
        fetchMovies();
    }

    //Fetching movies json by making http call
    private void fetchMovies() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        // appending offset to url
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
                                    String detail = movieObj.getString("detail");
                                    String urlpic = movieObj.getString("link_gambar");
                                    String kategori = movieObj.getString("kategori");
                                    String menu = movieObj.getString("menu");
                                    Double lati =  movieObj.getDouble("lat");
                                    Double longi = movieObj.getDouble("long");


                                    Restaurant m = new Restaurant(rank, title, alamat, detail, urlpic, lati, longi, kategori, menu);
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
                Toast.makeText(getActivity().getApplicationContext(), "Error Retrieving Data\nPull Down to Refresh", Toast.LENGTH_LONG).show();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        //super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                Intent intent = new Intent(getActivity(), MapsResActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Restaurant> filteredModelList = filter(restaurantList, newText);
        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Restaurant> filter(List<Restaurant> models, String query) {
        query = query.toLowerCase();

        final List<Restaurant> filteredModelList = new ArrayList<>();
        //restaurantList = new ArrayList<>();

        for (Restaurant model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SimpleRecyclerAdapter(filteredModelList, new SimpleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant model) {
                //Toast.makeText(getContext(), "Item Clicked" + model.getId() , Toast.LENGTH_LONG).show();

                String namaRes = model.getTitle();
                String alamatRes = model.getAlamat();
                String detail = model.getDetail();
                String lat = String.valueOf(model.getLati());
                String lng = String.valueOf(model.getLongi());
                String kategori = model.getKategori();
                String urlPicRes = model.getUrlpic();
                String menu = model.getMenu();

                Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);

                intent.putExtra("nama", namaRes);
                intent.putExtra("alamat", alamatRes);
                intent.putExtra("detail", detail);
                intent.putExtra("url", urlPicRes);
                intent.putExtra("kat", kategori);
                intent.putExtra("menu", menu);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // data set changed
        return filteredModelList;
    }

}
