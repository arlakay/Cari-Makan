package com.carimakan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.carimakan.app.MyApplication;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ersa Rizki Dimitri on 17/05/2015.
 */
public class DetailRestaurantActivity extends ActionBarActivity{
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    private Toolbar toolbar;
    private GoogleMap mMap;
    private String nama, alamat, lat, lng;
    private boolean zoomOut =  false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_restaurant);

        setUpMapIfNeeded();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        // Get the result of rank
        nama = i.getStringExtra("nama");
        alamat = i.getStringExtra("alamat");
        String urlRes = i.getStringExtra("url");
        lat = i.getStringExtra("lat");
        lng = i.getStringExtra("lng");

        TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        TextView txtAlamat = (TextView)findViewById(R.id.txtValueAlamat);
        TextView txtOpenHour = (TextView)findViewById(R.id.txtValueOpenHour);
        final NetworkImageView image = (NetworkImageView)findViewById(R.id.gambarRes);
        ImageButton direction = (ImageButton)findViewById(R.id.btnDirection);
        ImageButton share = (ImageButton)findViewById(R.id.btnShare);
        ImageButton Rate = (ImageButton)findViewById(R.id.btnRate);
        ImageButton Review = (ImageButton)findViewById(R.id.btnReview);


        txtTitle.setText(nama);
        txtAlamat.setText(alamat);
        image.setImageUrl(urlRes, imageLoader);

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DetailRestaurantActivity.this, MapsActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);

            }
        });

        Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailRestaurantActivity.this, RatingActivity.class);
                startActivity(intent);
            }
        });

        Review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailRestaurantActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public void actionShare(View v){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "I'm at " + nama;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
