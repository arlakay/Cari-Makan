package com.carimakan;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.carimakan.app.MyApplication;
import com.carimakan.util.FontCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * Created by Ersa Rizki Dimitri on 17/05/2015.
 */
public class DetailRestaurantActivity extends AppCompatActivity {
    private static final String TAG = DetailRestaurantActivity.class.getSimpleName();

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private GoogleMap mMap;
    private String nama, alamat, detail, menu;
    private Typeface fontLemonMilk, fontHabibi;
    private double lat, lng;
    private Context mAppContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_restaurant);

        setUpMapIfNeeded();

        fontLemonMilk       = FontCache.get(getApplicationContext(), "LemonMilk");
        fontHabibi          = FontCache.get(getApplicationContext(), "Habibi-Regular");

        Intent i = getIntent();
        nama = i.getStringExtra("nama");
        alamat = i.getStringExtra("alamat");
        detail = i.getStringExtra("detail");
        String urlRes = i.getStringExtra("url");
        String kat = i.getStringExtra("kat");
        menu = i.getStringExtra("menu");
        lat = i.getDoubleExtra("lat", 0.00000);
        lng = i.getDoubleExtra("lng", 0.00000);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(nama);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbar_text);

        LinearLayout l1 = (LinearLayout)findViewById(R.id.lintest);
        ViewCompat.setNestedScrollingEnabled(l1, true);

        //TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        //TextView txtAlamat = (TextView)findViewById(R.id.txtValueAlamat);
        final TextView txtOpenHour = (TextView)findViewById(R.id.txtTitleOpenHour);
        final NetworkImageView image = (NetworkImageView)findViewById(R.id.gambarRes);
        ImageButton direction = (ImageButton)findViewById(R.id.btnDirection);
        ImageButton Review = (ImageButton)findViewById(R.id.btnReview);
        //ImageButton Call = (ImageButton)findViewById(R.id.btnCall);

        //TextView txtPhone = (TextView)findViewById(R.id.txtPhone);
        TextView txtRute = (TextView)findViewById(R.id.txtRute);
        TextView txtReview = (TextView)findViewById(R.id.txtReview2);
        TextView txtShare = (TextView)findViewById(R.id.txtShare2);

        //txtPhone.setTypeface(fontLemonMilk);
        txtRute.setTypeface(fontLemonMilk);
        txtReview.setTypeface(fontLemonMilk);
        txtShare.setTypeface(fontLemonMilk);
        //txtTitle.setTypeface(fontLemonMilk);
        //txtAlamat.setTypeface(fontHabibi);
        txtOpenHour.setTypeface(fontHabibi);

        //txtTitle.setText(nama);
        //txtAlamat.setText(alamat);
        txtOpenHour.setText(detail);
        Linkify.addLinks(txtOpenHour, Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);

        image.setImageUrl(urlRes, imageLoader);

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f (%s)", lat, lng, nama);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(getApplicationContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailRestaurantActivity.this, MenuResActivity.class);
                intent.putExtra("menu",menu);
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
        mMap.getUiSettings().setMapToolbarEnabled(false);

        Intent i = getIntent();
        lat = i.getDoubleExtra("lat", 0.00000);
        lng = i.getDoubleExtra("lng", 0.00000);
        nama = i.getStringExtra("nama");

        LatLng resMark = new LatLng(lat,lng);

        CameraPosition cp = new CameraPosition.Builder()
                .target(resMark)
                .zoom(17)
                .bearing(0)
                .tilt(30)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

        Marker resspot = mMap.addMarker(new MarkerOptions()
                .title(nama)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.marker))
                .position(resMark));
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
