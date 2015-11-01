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

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beanthere.R;
import com.beanthere.adapter.NavDrawerAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.BeanDialogInterface.OnInputDialogDismissListener;
import com.beanthere.dialoghelper.ConfirmationDialog;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.dialoghelper.PromoInputDialog;
import com.beanthere.listeners.BeanLocationListener;
import com.beanthere.objects.DividerItemDecoration;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.utils.Logger;
import com.beanthere.webservice.HttpHandler;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;


public class NavDrawerActivity extends AppCompatActivity implements NavDrawerAdapter.OnItemClickListener,
        LocationListener, BeanLocationListener.LocationReceiver,
        OnInputDialogDismissListener, BeanDialogInterface.OnPositiveClickListener {

    private static LocationManager mCoreLocationManager;
    private static Location mCoreLocation;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavDrawerTitle;
    private int[] mNavDrawerIcon = {R.drawable.ic_menu_coffee, R.drawable.ic_menu_filter, R.drawable.ic_menu_favourite, R.drawable.ic_menu_promo_code, R.drawable.ic_menu_wallet, R.drawable.ic_menu_settings, R.drawable.ic_menu_logout};

    private LocationManager mLocationManager;
    private BeanLocationListener mLocationListener;
    protected Double mLatitude;
    protected Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mTitle = mDrawerTitle = getTitle();
        mNavDrawerTitle = getResources().getStringArray(R.array.menu_array);
//        mNavDrawerIcon = getResources().getIntArray(R.array.menu_icon);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        // add divider to list items
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mDrawerList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_nav_drawer, getTheme())));
            mDrawerList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_nav_drawer)));
        } else {
            mDrawerList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_nav_drawer)));
        }

        // set up the drawer's list view with items and click listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);

        String userName = SharedPreferencesManager.getString(this, "first_name");
        String fbid = SharedPreferencesManager.getString(this, "fb_user_id");
        String dateJoin = SharedPreferencesManager.getString(this, "date_join");

        mDrawerList.setAdapter(new NavDrawerAdapter(this, mNavDrawerTitle, mNavDrawerIcon, userName, fbid, dateJoin, this));

        // TODO change to Toolbar
        /*toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setActionBar(toolbar);*/

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
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
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        // TODO change to toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.actionbar);
//            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
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
//            case R.id.action_websearch:
//                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
            case R.id.action_favorite:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {

        switch (position) {

            case 0:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, CafeListActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, CafeFilterActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, FavouriteListActivity.class));
                break;
            case 4:
                showPromoDialog("getpromo", "0");
                break;
            case 5:
                startActivity(new Intent(this, VoucherListActivity.class));
                break;
            case 6:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case 7:
                logout();
                break;
            default:
                break;

        }

        // update selected item title, then close the drawer
//        setTitle(mNavDrawerTitle[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    protected void showPromoDialog(String tag, String id) {
        FragmentManager fm = getFragmentManager();
        PromoInputDialog inputDialog = new PromoInputDialog().newInstance(id);
        inputDialog.show(fm, tag);
    }

    protected void logout() {

        if (SharedPreferencesManager.getInt(this, "logintype") == 1) {

            SharedPreferences settings = getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
            settings.edit().clear().commit();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {

            LoginManager.getInstance().logOut();

            String fbId = SharedPreferencesManager.getString(this, "fb_user_id");
            String fbEmail = SharedPreferencesManager.getString(this, "fb_email");

            getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE).edit().clear().commit();

            // Save email and fb_id to login user next time when user login
//            SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("fb_user_id", fbId);
//            editor.putString("fb_email", fbEmail);
//            editor.commit();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
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
        // TODO prompt confirm dialog upon exiting
        removeLocationListener();
        super.onDestroy();
    }

    protected boolean checkLocationService() {

        if (SharedPreferencesManager.getBoolean(this, "checkLocation")) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            if (!isEnabled) {
                promptEnableGPS();
            }

            SharedPreferencesManager.putBoolean(this, "checkLocation", false);
        }

        return false;
    }

    protected void getLocation(String tag, Long fieldId) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(new Criteria(), true);
        if (provider != null) {
            mLocationListener = new BeanLocationListener(this, tag, fieldId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
        }
    }

    protected void showNoticeDialog(String tag, String title, String message, String neutralButton) {
        FragmentManager fm = getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
        noticeDialog.show(fm, tag);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mCoreLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 1, this);
    }

    protected void removeLocationListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mCoreLocationManager.removeUpdates(this);
    }

    protected Location getCoreLocation() {
        return mCoreLocation;
    }

    protected void clearLocation() {
        mCoreLocation = null;
    }

    @Override
    public void onPositiveClick(String tag, int which) {
        if (tag.equals("gotovoucherlist")) {
            startActivity(new Intent(this, VoucherListActivity.class));
        }
    }

    class GetPromoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Logger.e("GetPromoTask", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.getVoucher(params[0], params[1]);

            return response;

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response == null || response.isEmpty()) {
//                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                DialogHelper.showInvalidServerResponse(NavDrawerActivity.this);
            } else {

                Gson gson = new Gson();
                GeneralResponse res = gson.fromJson(response, GeneralResponse.class);

                if (res.error) {
                    showNoticeDialog("", getString(R.string.error_title), res.error_message, "");
                } else {
                    showNoticeDialog("gotovoucherlist", "", res.error_message, "");
                }
            }
        }
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

    @Override
    public void onInputDialogDismiss(String tag, String data) {
        if (tag.equals("getpromo")) {
            if (data == null || data.isEmpty() ) {

            } else {
                new GetPromoTask().execute(data, SharedPreferencesManager.getAPIKey(this));
            }
        }
    }

}
