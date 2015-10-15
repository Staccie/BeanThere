package com.beanthere.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.adapter.CafeFilterAdapter;
import com.beanthere.adapter.CafeListAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.objects.Cafe;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by staccie
 */
public class CafeFilterActivity extends NavDrawerActivity {

    private List<Cafe> mList;
    private CafeFilterAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_cafe_filter, null, false);
        frameLayout.addView(view);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarCafeFilterList);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        mSearchView.setIconifiedByDefault(false);

//        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        TextView textView = (TextView) mSearchView.findViewById(id);
//        textView.setTextColor(Color.BLACK);

        mSearchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange( String newText ) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    new LoadCafeList().execute(query, SharedPreferencesManager.getAPIKey(CafeFilterActivity.this));
                }
                return true;
            }
        });

        if (mList == null) {
            mList = new ArrayList<Cafe>();
        }

        mAdapter = new CafeFilterAdapter(this, mList);
        mListView = (ListView) view.findViewById(R.id.listViewCafeFilters);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                Cafe selectedCafe = (Cafe) listView.getItemAtPosition(position);
                startCafeActivity(selectedCafe.id);
            }
        });

//        new LoadCafeList().execute("", SharedPreferencesManager.getAPIKey(this));
    }

    private void updateCafeList(List<Cafe> cafeList) {
        mList.addAll(cafeList);
        ((CafeFilterAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private void startCafeActivity(int id) {
        Log.e("startCafeActivity", "id: " + String.valueOf(id));
        Intent intent = new Intent(this, CafeActivity.class);
        intent.putExtra("cafeId", String.valueOf(id));
        startActivityForResult(intent, 0);
    }

    class LoadCafeList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("Login", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.getCafeFilterList(params[0], mLatitude, mLongitude, params[1]);

            return response;

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            if (response == null || response.isEmpty()) {
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
            } else {

                Gson gson = new Gson();
                GeneralResponse genResponse;

                genResponse = gson.fromJson(response, GeneralResponse.class);

                if (genResponse.error) {
                    showNoticeDialog("", getString(R.string.login_failed), genResponse.error_message, "");
                } else {
                    updateCafeList(genResponse.cafeList);
                }
            }
        }
    }

}
