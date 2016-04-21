package com.carimakan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.carimakan.fragments.OneFragment;
import com.carimakan.helper.Restaurant;
import com.carimakan.helper.SimpleRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ScrollableTabsActivity extends AppCompatActivity {
    SimpleRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    NavigationView navigationView;
    private List<Restaurant> restaurantList;
    private DrawerLayout mDrawerLayout;
    private int mCurrentSelectedPosition;
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollable_tabs);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .show(OneFragment.newInstance())
                    //.replace(R.id.viewpager, OneFragment.newInstance())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_sign_in:
                        //Snackbar.make(mContentFrame, "", Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(ScrollableTabsActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        mCurrentSelectedPosition = 0;
                        return true;
                    case R.id.navigation_item_profile:
                        //Snackbar.make(mContentFrame, "", Snackbar.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(ScrollableTabsActivity.this, AfterLoginActivity.class);
                        startActivity(intent2);
                        mCurrentSelectedPosition = 1;
                        return true;
                    case R.id.navigation_item_add_new_location:
                        //Snackbar.make(mContentFrame, "", Snackbar.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(ScrollableTabsActivity.this, AddNewPlaceActivity.class);
                        startActivity(intent3);
                        mCurrentSelectedPosition = 2;
                        return true;
                    case R.id.navigation_item_help:
                        //Snackbar.make(mContentFrame, "", Snackbar.LENGTH_SHORT).show();
                        Intent intent4 = new Intent(ScrollableTabsActivity.this, HelpActivity.class);
                        startActivity(intent4);
                        mCurrentSelectedPosition = 3;
                        return true;
                    case R.id.navigation_item_about:
                        //Snackbar.make(mContentFrame, "", Snackbar.LENGTH_SHORT).show();
                        Intent intent5 = new Intent(ScrollableTabsActivity.this, AboutActivity.class);
                        startActivity(intent5);
                        mCurrentSelectedPosition = 4;
                        return true;
                    default:
                        return true;
                }

                //mDrawerLayout.closeDrawers();
                //Toast.makeText(ScrollableTabsActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                //return true;
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = navigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
        recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "ALL");
        //adapter.addFrag(new TwoFragment(), "TRADITIONAL");
        //adapter.addFrag(new ThreeFragment(), "SOTO & SOUPS");
        //adapter.addFrag(new FourFragment(), "VEGETARIAN");
        //adapter.addFrag(new FiveFragment(), "OTHER");
        //adapter.addFrag(new SixFragment(), "SIX");
        //adapter.addFrag(new SevenFragment(), "SEVEN");
        //adapter.addFrag(new EightFragment(), "EIGHT");
        //adapter.addFrag(new NineFragment(), "NINE");
        //adapter.addFrag(new TenFragment(), "TEN");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
