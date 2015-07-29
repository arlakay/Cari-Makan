package com.carimakan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Ersa Rizki Dimitri on 24/06/2015.
 */
public class HideMenuActivity extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hide_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ImageButton sign = (ImageButton)findViewById(R.id.sign);
        ImageButton profile = (ImageButton)findViewById(R.id.profile);
        ImageButton addnew = (ImageButton)findViewById(R.id.addnew);
        ImageButton about = (ImageButton)findViewById(R.id.about);

        sign.setOnClickListener(this);
        profile.setOnClickListener(this);
        addnew.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign:
                Intent intent = new Intent(HideMenuActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:
                Intent intent2 = new Intent(HideMenuActivity.this, AfterLoginActivity.class);
                startActivity(intent2);
                break;
            case R.id.addnew:
                Intent intent3 = new Intent(HideMenuActivity.this, AddNewPlaceActivity.class);
                startActivity(intent3);
                break;
            case R.id.about:
                break;
            case View.NO_ID:
                default:
                break;
        }
    }
}
