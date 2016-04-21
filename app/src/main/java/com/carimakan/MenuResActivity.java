package com.carimakan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.carimakan.app.MyApplication;

/**
 * Created by Ersa Rizki Dimitri on 17/06/2015.
 */
public class MenuResActivity extends AppCompatActivity {
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    private Toolbar toolbar;
    private String menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_res);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        Intent i = getIntent();
        menu = i.getStringExtra("menu");

        NetworkImageView imageMenu = (NetworkImageView)findViewById(R.id.imageMenu);

        imageMenu.setImageUrl(menu, imageLoader);

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

}
