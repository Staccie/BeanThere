package com.beanthere.activities;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.objects.Cafe;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.listeners.BeanLocationListener;
import com.beanthere.webservice.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by staccie on 9/20/15.
 */
public class CafeActivity extends NavDrawerActivity implements OnMapReadyCallback {

    private View mView;
    private MapFragment mMapFragment;
    private GoogleMap mMap;

    private LocationManager mLocationManager;
    private BeanLocationListener mLocationListener;

    private Cafe mCafe;

    private String mCafeId;
    private boolean isLoading;
    private int mCurrentView;
    private String mExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate", "CafeActivity");

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.activity_cafe, null, false);
        frameLayout.addView(mView);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        String apikey = SharedPreferencesManager.getString(this, "apikey");

        if (savedInstanceState == null) {

            mCafe = null;

            Bundle extras = getIntent().getExtras();
            if (extras == null) finish();

            isLoading = false;
            mCafeId = extras.getString("cafeId", "");

            Log.e("saved is null", "cafeid=" + mCafeId + "; isLoading: " + String.valueOf(isLoading) );

            if (mCafeId == "") finish();

            new GetCafeDetailsTask().execute(apikey);
        } else {
            mCafeId = savedInstanceState.getString("mCafeId", "0");
            mCurrentView = savedInstanceState.getInt("cafeId", 0);
            new GetCafeDetailsTask().execute(apikey);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mCafeId", mCafeId);
        outState.putBoolean("isLoading", isLoading);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCafeId = savedInstanceState.getString("mCafeId", "");
        isLoading = savedInstanceState.getBoolean("isLoading", false);
    }

    class GetCafeDetailsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("GetCafeDetailsTask", "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Log.e("doinbackground", "mCafeId: " + mCafeId + "; isLoading: " + String.valueOf(isLoading));
            if (mCafeId != "" && !isLoading) {
                isLoading = true;
                Request req = new Request();
                String response = req.getMerchantDetails(mCafeId, params[0]);

                if (response == null || response == "") {
                    //  Dialog cannot get cafe detail
                    return false;
                } else {
                    Log.e("fdfsdfs", "fdsfdsfds");

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

//                    TextView tvName = (TextView) mView.findViewById(R.id.textViewAbout);
                            TextView tvAbout = (TextView) mView.findViewById(R.id.textViewAbout);
                            TextView tvAdd1 = (TextView) mView.findViewById(R.id.textViewAdd1);
                            TextView tvAdd2 = (TextView) mView.findViewById(R.id.textViewAdd2);
                            TextView tvContact = (TextView) mView.findViewById(R.id.textViewContactNum);
                            TextView tvDistance = (TextView) mView.findViewById(R.id.textViewCafeDistance);
                            TextView tvHours1 = (TextView) mView.findViewById(R.id.textViewHours1);
//                    Log.e("res", cafe.toString());

//                    tvName.setText(cafe.name);
                            tvAbout.setText(mCafe.description);
                            tvAdd1.setText(mCafe.address_1);

                            if (mCafe.address_2 == null || mCafe.address_2 == "") {
                                tvAdd2.setVisibility(View.INVISIBLE);
                            } else {
                                tvAdd2.setVisibility(View.VISIBLE);
                                tvAdd2.setText(mCafe.address_2);
                            }
                            tvContact.setText(mCafe.contact);
//                    tvDistance.setText(CommonUtils.getDistance(cafe.distance));


                            // TODO loop operating hours convert to plain text
//                            tvHours1.setText(getOperatingHours(cafe.operatingHourList));
//                    tvDistance.setText();

                            // TODO loop category
                            // ----> TODO loop category menu -> setup menu adapter

                            setMapLatLng();
                            switchView(0);
                        }
                    }
                }
            }
        });

        return true;
    }

    private void switchView(int id) {

        if (id == 0) {
            // Switch to About layout
            mView.findViewById(R.id.llCafeAbout).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.llCafeMenu).setVisibility(View.GONE);
        } else {
            // Switch to Menu layout
            mView.findViewById(R.id.llCafeAbout).setVisibility(View.GONE);
            mView.findViewById(R.id.llCafeMenu).setVisibility(View.VISIBLE);
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
//        LatLng sydney = new LatLng(-33.867, 151.206);
//
//        map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
//
//        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));
        if (mCafe != null) {
            if (mCafe.latitude != "" && mCafe.longitude !="") {
                LatLng sydney = new LatLng(-33.867, 151.206);

                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                map.addMarker(new MarkerOptions()
                        .title("Sydney")
                        .snippet("The most populous city in Australia.")
                        .position(sydney));
            }
        }
    }

    /*// Assumption: "operating" list will always come in a size of 7
    private String getOperatingHours(List<OperatingHour> list) {
        Resources res = getResources();
        String[] days = res.getStringArray(R.array.day_array);

        String text = "";
        OperatingHour tmp;

        if (list == null || list.size() == 0) {
            text = getString(R.string.no_info);
        } else {

            // Get data of day index 1
            tmp = new OperatingHour(list.get(0).day, list.get(0).startTime, list.get(0).endTime);

            for(int i = 1; i < list.size(); i++) {
                Log.e("i is", "" + String.valueOf(i));
                if (list.get(i).startTime != tmp.startTime && list.get(i).endTime != tmp.endTime) {
                    String endDay = "";
                    if (Integer.parseInt(tmp.day) != i) {
                        endDay = "- " + days[Integer.parseInt(list.get(i).day)];
                    }

                    text += days[Integer.parseInt(tmp.day)] + " " + endDay + tmp.startTime + " - " + tmp.endTime + "\n";
                    tmp = new OperatingHour(list.get(i).day, list.get(i).startTime, list.get(i).endTime);
                }
            }

            if (Integer.parseInt(tmp.day) == 5) {
                text += days[Integer.parseInt(tmp.day)] + " " + tmp.startTime + " - " + tmp.endTime;
            } else {
                text += days[Integer.parseInt(tmp.day)] + " - " + days[Integer.parseInt(list.get(6).day)] + " " + tmp.startTime + " - " + tmp.endTime;
            }

        }

        return text;
    }*/

//    private String formatOperatingHour(OperatingHour prev, OperatingHour current) {
//        String endDay = "";
//        if (Integer.parseInt(prev.day) != i-1) {
//            endDay += "- " + days[Integer.parseInt(list.get(i).day)];
//        }
//
//        text += days[Integer.parseInt(tmp.day)] + " " + endDay + tmp.startTime + " - " + tmp.endTime + "\n";
//        tmp = new OperatingHour(list.get(i).day, list.get(i).startTime, list.get(i).endTime);
//    }
//
}
