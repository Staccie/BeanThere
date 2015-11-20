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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.beanthere.R;
import com.beanthere.adapter.CafeListAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.objects.Cafe;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.utils.CommonUtils;
import com.beanthere.utils.Logger;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CafeListActivity extends NavDrawerActivity implements BeanDialogInterface.OnNegativeClickListener, BeanDialogInterface.OnPositiveClickListener {

    private View mParentView;
    private List<Cafe> mList;
    private CafeListAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshLayout;

    public static boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mParentView = layoutInflater.inflate(R.layout.activity_cafe_list, null, false);
        frameLayout.addView(mParentView);

        mRefreshLayout = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipeContainer);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runLoadCafeListTask(false);
            }
        });

        mProgressBar = (ProgressBar) mParentView.findViewById(R.id.progressBarCafeList);

        if (mList == null) {
            mList = new ArrayList<>();
        }

        mAdapter = new CafeListAdapter(this, mList);
        mListView = (ListView) mParentView.findViewById(R.id.listViewCafe);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {

                Cafe selectedCafe = (Cafe) listView.getItemAtPosition(position);
                startCafeActivity(selectedCafe.id, selectedCafe.name);
            }
        });

        checkLocationService();

        runLoadCafeListTask(false);

//        mWebServiceCallback = new WebServiceLoaderCallback();
//        mDBCallback = new DBLoaderCallback();
//
//        if (!isLoading) refresh();
    }

    private void runLoadCafeListTask(boolean isInitial) {
        if (CommonUtils.isConnected(CafeListActivity.this)) {
            mParentView.findViewById(R.id.llNoInternet).setVisibility(View.GONE);
            new LoadCafeListTask(isInitial).execute(SharedPreferencesManager.getAPIKey(this));
        } else {
            mParentView.findViewById(R.id.llNoInternet).setVisibility(View.VISIBLE);
        }
    }

    private class LoadCafeListTask extends AsyncTask<String, Void, String> {

        private boolean mIsInitialLoad;
        private boolean showDistance;

        protected LoadCafeListTask(boolean isInitialLoad) {
            mIsInitialLoad = isInitialLoad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIsInitialLoad) {
                mProgressBar.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Logger.e("@CafeListActivity.LoadCafeListTask", "doInBackground");
            showDistance = !(mLatitude == null || mLongitude == null);
            return new HttpHandler().getMerchantList(params[0], mLatitude, mLongitude);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (mIsInitialLoad) {
                mProgressBar.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            mRefreshLayout.setRefreshing(false);

            if (!isCancelled() && !isFinishing()) {
                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(CafeListActivity.this);
                } else {

                    Gson gson = new Gson();
                    GeneralResponse genResponse = gson.fromJson(response, GeneralResponse.class);

                    if (genResponse.error) {
                        DialogHelper.showErrorDialog(CafeListActivity.this, genResponse.error_message);
                    } else {
                        updateCafeList(genResponse.cafeList, showDistance);
                    }
                }
            }
        }
    }

    private void updateCafeList(List<Cafe> cafeList, boolean showDistance) {
        mList.clear();
        mList.addAll(cafeList);
        mAdapter.setDistanceVisibility(showDistance);
        ((CafeListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private void startCafeActivity(int id, String name) {
        Log.e("startCafeActivity", "id: " + String.valueOf(id));
        Intent intent = new Intent(this, CafeActivity.class);
        intent.putExtra("cafeId", String.valueOf(id));
        intent.putExtra("cafeName", name);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onNegativeClick(String tag, int which) {

    }

    @Override
    public void onPositiveClick(String tag, int which) {
        if (tag.equals("getGeoLocation")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
}
