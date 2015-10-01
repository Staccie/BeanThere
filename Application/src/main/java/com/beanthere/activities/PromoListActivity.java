package com.beanthere.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.beanthere.R;
import com.beanthere.adapter.VoucherListAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.listeners.BeanAdapterInterface;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.objects.Voucher;
import com.beanthere.webservice.Request;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by staccie on 9/20/15.
 */
public class PromoListActivity extends NavDrawerActivity implements BeanAdapterInterface.OnButtonClickListener {

    private List<Voucher> mList;
    private VoucherListAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_promo_list, null,false);
        frameLayout.addView(view);

        if (mList == null) {
            mList = new ArrayList<Voucher>();
        }

        mAdapter = new VoucherListAdapter(this, mList);
        mListView = (ListView) view.findViewById(R.id.listViewPromotion);
        mListView.setAdapter(mAdapter);

        String apikey = SharedPreferencesManager.getString(this, "apikey");

        new LoadVoucherList().execute(apikey);

//        mWebServiceCallback = new WebServiceLoaderCallback();
//        mDBCallback = new DBLoaderCallback();
//
//        if (!isLoading) refresh();
    }

    class LoadVoucherList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.e("Login", "doInBackground");

            Request req = new Request();
            String response = req.getMerchantList(params[0], mLatitude, mLongitude);

            return response;

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response == null && response.isEmpty()) {
                showNoticeDialog(getString(R.string.login_failed), getString(R.string.invalid_server_response), "");
            } else {

                Gson gson = new Gson();
                GeneralResponse genResponse;

                genResponse = gson.fromJson(response, GeneralResponse.class);

                if (genResponse.error) {
                    showNoticeDialog(getString(R.string.login_failed), genResponse.error_message, "");
                } else {
//                    updateVoucherList(genResponse.cafeList);
                }
            }
        }
    }

    private void updateVoucherList(List<Voucher> voucherList) {
        mList.addAll(voucherList);
        ((VoucherListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private void startVoucherActivity(String voucherId) {
//        Log.e("startCafeActivity", "voucherId: " + voucherId);
//        Intent intent = new Intent(this, VoucherActivity.class);
//        intent.putExtra("voucherId", voucherId);
//        startActivityForResult(intent, 0);
    }

    @Override
    public void onButtonClick(String tag, int id, int position) {

        if (tag == "voucherDeati") {
            startVoucherActivity(tag);
        }
    }

}
