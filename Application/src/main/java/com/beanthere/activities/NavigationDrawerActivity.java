/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beanthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beanthere.R;
import com.beanthere.adapter.MenuAdapter;
import com.beanthere.dialoghelper.ConfirmationDialog;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.listeners.BeanLocationListener;


public class NavigationDrawerActivity extends Activity implements MenuAdapter.OnItemClickListener, LocationListener, BeanLocationListener.LocationReceiver {

    private static LocationManager mCoreLocationManager;
    private static Location mCoreLocation;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    private LocationManager mLocationManager;
    private BeanLocationListener mLocationListener;
    protected Double mLatitude;
    protected Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);


        mDrawerList.setAdapter(new MenuAdapter(mPlanetTitles, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
//            selectItem(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {

        switch (position) {

            case 0:
                startActivity(new Intent(this, CafeListActivity.class));
                finish();
                break;
            case 1:
                startActivity(new Intent(this, MapActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, VoucherListActivity.class));
                break;
            default: break;

        }

        // update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
//        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startShortLocationListener();
    }

    @Override
    protected void onDestroy() {
        removeLocationListener();
        super.onDestroy();
    }

    protected boolean checkLocationService() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isEnabled) {
            promptEnableGPS();
        }
        return false;
    }

    protected void getLocation(String tag, Long fieldId) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(new Criteria(), true);
        if (provider != null) {
            mLocationListener = new BeanLocationListener(this, tag, fieldId);
            mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
        }
    }

    protected void showNoticeDialog(String title, String message, String neutralButton) {
        FragmentManager fm = getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
        noticeDialog.show(fm, "noticeDialog");
    }

    private void promptEnableGPS() {
        FragmentManager fm = getFragmentManager();
        ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(getString(R.string.enable_gps_title), getString(R.string.enable_gps_message));
        confirmationDialog.show(fm, "getGeoLocation");
    }

    protected void startLongLocationListener() {
        startLocationListener(5 * 60 * 1000);
    }

    protected void startShortLocationListener() {
        startLocationListener(3000);
    }

    private void startLocationListener(long interval) {
        mCoreLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //		String provider = mCoreLocationManager.getBestProvider(new Criteria(), true);

        removeLocationListener();

        // always listen to two providers
        mCoreLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 1, this);
        mCoreLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 1, this);
    }

    protected void removeLocationListener() {
        mCoreLocationManager.removeUpdates(this);
    }

    protected Location getCoreLocation() {
        return mCoreLocation;
    }

    protected void clearLocation() {
        mCoreLocation = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCoreLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationReceive(String tag, Long fieldId, Location location) {
//        String geolocation = "(" + location.getLatitude() + ", " + location.getLongitude() + ")";

        // remove update else will consume battery
//        mLocationManager.removeUpdates(mLocationListener);

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

    }
}
