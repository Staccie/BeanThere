package com.beanthere.activities;

import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.adapter.CafeMenuAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.listeners.BeanLocationListener;
import com.beanthere.objects.AppObject;
import com.beanthere.objects.Cafe;
import com.beanthere.objects.CafeMenu;
import com.beanthere.objects.Category;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.objects.OperatingHour;
import com.beanthere.utils.ImageViewDownloader;
import com.beanthere.utils.Logger;
import com.beanthere.webservice.HttpHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CafeActivity extends BaseActivity implements OnMapReadyCallback {

//    private View mView;
    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private List<String> mCategories;
    private HashMap<String, List<CafeMenu>> mCafeMenuList;
    private ExpandableListView mMenuListView;
    private CafeMenuAdapter mAdapter;

    private LocationManager mLocationManager;
    private BeanLocationListener mLocationListener;

    private Cafe mCafe;

    private String mCafeId;
    private String mCafeName;
    private boolean isLoading;
    private int mCurrentView;
//    private String mExtra;
    private boolean isFavourite;

    private LinearLayout llCafeDetails;
    private ProgressBar mProgressBar;
    private ImageView ibFavourite;

    private UpdateFavouriteTask mUpdateFavouriteTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate", "CafeActivity");
        setContentView(R.layout.activity_cafe);

        ibFavourite = (ImageView) findViewById(R.id.imageButtonFavourite);
        ibFavourite.setVisibility(View.VISIBLE);
        ibFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateFavourite();
            }
        });

        llCafeDetails = (LinearLayout) findViewById(R.id.llCafeDetails);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarCafeDetails);

        mAdapter = new CafeMenuAdapter(this, mCategories, mCafeMenuList);
        mMenuListView = (ExpandableListView) findViewById(R.id.listViewCafeMenu);
        mMenuListView.setAdapter(mAdapter);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (mCategories == null) {
            mCategories = new ArrayList<String>();
            mCafeMenuList = new HashMap<String, List<CafeMenu>>();
        }

        if (savedInstanceState == null) {

            mCafe = null;

            Bundle extras = getIntent().getExtras();
            if (extras == null) finish();

            isLoading = false;
            mCafeId = extras.getString("cafeId", "");
            mCafeName = extras.getString("cafeName", "Cafe");

            Log.e("saved is null", "cafeid=" + mCafeId + "; isLoading: " + String.valueOf(isLoading));

            if (mCafeId.isEmpty()) finish();

            new GetCafeDetailsTask().execute(SharedPreferencesManager.getAPIKey(this));
        } else {
            mCafeId = savedInstanceState.getString("mCafeId", "0");
            mCafeName = savedInstanceState.getString("mCafeName", "Cafe");
            mCurrentView = savedInstanceState.getInt("cafeId", 0);
            new GetCafeDetailsTask().execute(SharedPreferencesManager.getAPIKey(this));
        }

        setTitle(mCafeName);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mCafeId", mCafeId);
        outState.putString("mCafeName", mCafeName);
        outState.putBoolean("isLoading", isLoading);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCafeId = savedInstanceState.getString("mCafeId", "");
        mCafeName = savedInstanceState.getString("mCafeName", "Cafe");
        isLoading = savedInstanceState.getBoolean("isLoading", false);
    }

    class GetCafeDetailsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("GetCafeDetailsTask", "onPreExecute");

            mProgressBar.setVisibility(View.VISIBLE);
            llCafeDetails.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Logger.e("doinbackground", "mCafeId: " + mCafeId + "; isLoading: " + String.valueOf(isLoading));
            if (mCafeId != "" && !isLoading) {
                isLoading = true;
                HttpHandler req = new HttpHandler();
                String response = req.getMerchantDetails(mCafeId, params[0]);

                if (response == null || response.isEmpty()) {
                    showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                    return false;
                } else {
                    return loadCafeDetails(response);
                }

            }
            return false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isLoading = false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Log.e("GetCafeDetailsTask", String.valueOf(success));
            isLoading = false;

            mProgressBar.setVisibility(View.GONE);
            llCafeDetails.setVisibility(View.VISIBLE);
        }
    }

    class UpdateFavouriteTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("UpdateFavouriteTask", "onPreExecute");

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            if (mCafeId != "" && !isLoading) {

                isLoading = true;
                String response;
                HttpHandler req = new HttpHandler();

//                isFavourite = mCafe.isFavourite.equals("1");

                if (isFavourite) {
                    response = req.deleteFavourite(SharedPreferencesManager.getAPIKey(CafeActivity.this), mCafeId);
                } else {
                    response = req.addFavourite(SharedPreferencesManager.getAPIKey(CafeActivity.this), mCafeId);
                }

                if (response == null || response.isEmpty()) {
                    showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                    return false;
                } else {

                    GeneralResponse json = new Gson().fromJson(response, GeneralResponse.class);

                    if (json.error) {
                        String errorMsg = json.error_message;
                        showNoticeDialog("", "", ((errorMsg == null || errorMsg.isEmpty()) ? getString(R.string.unknown_response) : errorMsg), "");
                        return false;
                    } else {
                        isFavourite = !isFavourite;
                        mCafe.isFavourite = isFavourite ? "1" : "0";
                    }
                }

            }
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isLoading = false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Log.e("UpdateFavouriteTask", String.valueOf(success));
            isLoading = false;

            if (!isCancelled() && !isFinishing()) {
                updateFavouriteButton(success);
                mProgressBar.setVisibility(View.GONE);
            }

        }
    }

    private void updateFavouriteButton(boolean success) {
        if (success) {
           ibFavourite.setImageResource(isFavourite ? R.drawable.ic_is_favourite : R.drawable.ic_menu_favourite);

//                    ((ImageButton) findViewById(R.id.imageButtonFavourite)).setImageResource(isFavourite ? R.drawable.ib_fav_true : R.drawable.ib_fav_false);
//                    ((ImageView) findViewById(R.id.imageButtonFavourite)).setImageResource(isFavourite ? R.drawable.ic_is_favourite : R.drawable.ic_menu_favourite);
        }

    }

    private boolean loadCafeDetails(String response) {

        boolean isSuccess = false;
        final String res = response;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                GeneralResponse jsonObj = gson.fromJson(res, GeneralResponse.class);

                if (jsonObj == null) {

                } else {
                    // Due to redundant array in http response
                    List<Cafe> cafeList = jsonObj.cafeList;
                    if (cafeList == null || cafeList.size() == 0) {

                    } else {

                        mCafe = cafeList.get(0);
                        if (mCafe == null) {

                        } else {

//                    TextView tvName = (TextView) findViewById(R.id.textViewAbout);
                            ImageView ivCafe = (ImageView) findViewById(R.id.imageViewCafeMain);
                            if (mCafe.images_1 == null || mCafe.images_1.trim().isEmpty()) {
                                ivCafe.setImageResource(R.drawable.placeholder);
                            } else {
                                new ImageViewDownloader(ivCafe, false).execute(mCafe.images_1);
                            }

                            ImageView ivLogo = (ImageView) findViewById(R.id.imageViewCafeLogo);
                            if (mCafe.logo == null || mCafe.logo.trim().isEmpty()) {
//                                ivCafe.setImageResource(R.drawable.placeholder);
                            } else {
//                                float scaledWidth = (AppObject.screenWidth > 0) ? Math.round((float)AppObject.screenWidth / 2) : 250;
//                                float scaledHeight = Math.round(scaledWidth / width * height);
//
//                                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                                new ImageViewDownloader(ivLogo, true, CafeActivity.this).execute(mCafe.logo);
                            }


                            TextView tvAbout = (TextView) findViewById(R.id.textViewAbout);
                            TextView tvBeans = (TextView) findViewById(R.id.textViewBean);
                            ImageView ivWifi = (ImageView) findViewById(R.id.imageViewWifi);
                            ImageView ivFood = (ImageView) findViewById(R.id.imageViewFood);
                            TextView tvCoffeeMachine = (TextView) findViewById(R.id.textViewCoffeeMachine);
                            TextView tvAdd1 = (TextView) findViewById(R.id.textViewAdd1);
                            TextView tvAdd2 = (TextView) findViewById(R.id.textViewAdd2);
                            TextView tvContact = (TextView) findViewById(R.id.textViewContactNum);
                            TextView tvDistance = (TextView) findViewById(R.id.textViewCafeDistance);
                            TextView tvHours1 = (TextView) findViewById(R.id.textViewHours1);
//                    Log.e("res", cafe.toString());

//                    tvName.setText(cafe.name);
                            tvAbout.setText(mCafe.description);

                            if (mCafe.coffee_beans == null || mCafe.coffee_beans.isEmpty()) {
                                tvBeans.setVisibility(View.GONE);
                                findViewById(R.id.textViewTitleBean).setVisibility(View.GONE);
                            } else {
                                tvBeans.setText(mCafe.coffee_beans);
                            }

                            if (mCafe.coffee_machine == null || mCafe.coffee_machine.isEmpty()) {
                                tvCoffeeMachine.setVisibility(View.GONE);
                                findViewById(R.id.textViewTitleCoffeeMachine).setVisibility(View.GONE);
                            } else {
                                tvCoffeeMachine.setText(mCafe.coffee_machine);
                            }

                            if (mCafe.hasFood.equals("1")) {
                                ivFood.setVisibility(View.VISIBLE);
                            }

                            if (mCafe.hasWifi.equals("1")) {
                                ivWifi.setVisibility(View.VISIBLE);
                            }

                            isFavourite = mCafe.isFavourite == null ? false : (mCafe.isFavourite.equals("1") ? true : false);
                            ibFavourite.setImageResource(isFavourite ? R.drawable.ic_is_favourite : R.drawable.ic_menu_favourite);

                            if (mCafe.address_1 == null || mCafe.address_1.isEmpty()) {
                                tvAdd1.setVisibility(View.GONE);
                            } else {
                                tvAdd1.setText(mCafe.address_1);
                                tvAdd1.setVisibility(View.VISIBLE);
                            }

                            if (mCafe.address_2 == null || mCafe.address_2.isEmpty()) {
                                tvAdd2.setVisibility(View.INVISIBLE);
                            } else {
                                tvAdd2.setVisibility(View.VISIBLE);
                                tvAdd2.setText(mCafe.address_2);
                            }

                            if (mCafe.contact == null || mCafe.contact.isEmpty()) {

                            } else {
                                tvContact.setText(mCafe.contact);
                            }
//                    tvDistance.setText(CommonUtils.getDistance(cafe.distance));

                            if (mCafe.operatingHourList != null) {
                                String text = "";
                                String[] dayArray = getResources().getStringArray(R.array.day_array);
                                int len = mCafe.operatingHourList.size();
                                for (int i = 0; i < len; i++) {
                                    OperatingHour oh = mCafe.operatingHourList.get(i);
                                    text += dayArray[Integer.parseInt(oh.day)-1] + " " + oh.startTime.substring(0, 5) + " - " + oh.endTime.substring(0, 5) + "\n";
                                }
                                tvHours1.setText(text);
                            }

//                    tvDistance.setText();

//                            prepareCafeMenu(mCafe.categoryList);
                            setMapLatLng();
                            switchView(0);
                        }
                    }
                }
            }
        });

        return true;
    }

    private void prepareCafeMenu(List<Category> categories) {

        Log.e("prepareCafeMenu", "lll");

        if (categories == null || categories.size() == 0) {
            Log.e("prepareCafeMenu", "null?");
            // TODO show no vouchers
        } else {
            Log.e("prepareCafeMenu", "" + categories.size());
            mCategories.clear();
            mCafeMenuList.clear();

            int len = categories.size();
            for (int i = 0; i < len; i++) {
                mCategories.add(categories.get(i).categoryName);
                mCafeMenuList.put(categories.get(i).categoryName, categories.get(i).cafeMenuList);
            }
            ((CafeMenuAdapter) mMenuListView.getExpandableListAdapter()).setData(mCategories, mCafeMenuList);
            ((CafeMenuAdapter) mMenuListView.getExpandableListAdapter()).notifyDataSetChanged();
        }

    }

    private void switchView(int id) {

        if (id == 0) {
            // Switch to About layout
            findViewById(R.id.llCafeAbout).setVisibility(View.VISIBLE);
            findViewById(R.id.llCafeMenu).setVisibility(View.GONE);
        } else {
            // Switch to Menu layout
            findViewById(R.id.llCafeAbout).setVisibility(View.GONE);
            findViewById(R.id.llCafeMenu).setVisibility(View.VISIBLE);
        }
    }

    private void setMapLatLng() {
        if (mMap != null && mCafe != null) {
            if (mCafe.latitude != "" && mCafe.longitude !="") {
                LatLng latLng = new LatLng(Double.valueOf(mCafe.latitude), Double.valueOf(mCafe.longitude));

                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                mMap.addMarker(new MarkerOptions()
                        .title(mCafe.name)
                        .snippet(mCafe.address_1 + " " + mCafe.address_2)
                        .position(latLng));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    public void onClickCafeAbout(View view) {
        switchView(0);
    }

    public void onClickCafeMenu(View view) {
        switchView(1);
    }

//    public void onClickUpdateFavourite(View view) {
//        mUpdateFavouriteTask = new UpdateFavouriteTask();
//        mUpdateFavouriteTask.execute();
//    }

    private void onClickUpdateFavourite() {
        mUpdateFavouriteTask = new UpdateFavouriteTask();
        mUpdateFavouriteTask.execute();
    }

}
