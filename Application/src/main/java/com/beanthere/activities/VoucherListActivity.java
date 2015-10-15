package com.beanthere.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.beanthere.R;
import com.beanthere.adapter.VoucherListAdapter;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.listeners.BeanAdapterInterface;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.objects.Voucher;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by staccie
 */
public class VoucherListActivity extends NavDrawerActivity implements BeanAdapterInterface.OnButtonClickListener,
        BeanDialogInterface.OnInputDialogDismissListener, BeanDialogInterface.OnPositiveClickListener {

    private List<Voucher> mList;
    private VoucherListAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_promo_list, null,false);
        frameLayout.addView(view);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadVoucherList().execute(SharedPreferencesManager.getAPIKey(VoucherListActivity.this));
            }
        });

        if (mList == null) {
            mList = new ArrayList<Voucher>();
        }

        mAdapter = new VoucherListAdapter(this, mList);
        mListView = (ListView) view.findViewById(R.id.listViewPromotion);
        mListView.setAdapter(mAdapter);;

        new LoadVoucherList().execute(SharedPreferencesManager.getAPIKey(this));

//        mWebServiceCallback = new WebServiceLoaderCallback();
//        mDBCallback = new DBLoaderCallback();
//
//        if (!isLoading) refresh();
    }

    class LoadVoucherList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.e("Login", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.getWalletVoucherList(params[0]);

            return response;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            mRefreshLayout.setRefreshing(false);

            if (response == null || response.isEmpty()) {
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
            } else {

                Gson gson = new Gson();
                GeneralResponse genResponse;
                Log.e("response", response);

                genResponse = gson.fromJson(response, GeneralResponse.class);

                if (genResponse.error) {
                    showNoticeDialog("", getString(R.string.error_title), genResponse.error_message, "");
                } else {
                    updateVoucherList(genResponse.voucherList);
                }
            }
        }
    }

    private void updateVoucherList(List<Voucher> voucherList) {
        if (voucherList == null || voucherList.size() == 0) {
            // TODO show no vouchers
        } else {
            mList.clear();
            mList.addAll(voucherList);
            ((VoucherListAdapter) mListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void startPromoActivity(String voucherId) {
//        Log.e("startCafeActivity", "voucherId: " + voucherId);
//        Intent intent = new Intent(this, VoucherActivity.class);
//        intent.putExtra("voucherId", voucherId);
//        startActivityForResult(intent, 0);
    }

    class RedeemPromoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.e("RedeemPromoTask", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.redeemVoucher(params[0], params[1], params[2]);

            return response;

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response == null || response.isEmpty()) {
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
            } else {
                Gson gson = new Gson();
                GeneralResponse genResponse;

                genResponse = gson.fromJson(response, GeneralResponse.class);

                if (genResponse.error) {
                    showNoticeDialog("", getString(R.string.error_title), genResponse.error_message, "");
                } else {
                    showNoticeDialog("refreshPromoList", "", genResponse.error_message, "");
                }
            }
        }
    }

    @Override
    public void onButtonClick(String tag, String id, int position) {

        if (tag.equals("voucherDetail")) {
            startPromoActivity(tag);
        } else if (tag.equals("redeemPromo")) {
            showPromoDialog("redeemPromo", id);
        }
    }

    @Override
    public void onInputDialogDismiss(String tag, String data) {
        if (tag.equals("redeemPromo")) {
            String[] params = data.split(",");
            new RedeemPromoTask().execute(params[0], params[1], SharedPreferencesManager.getAPIKey(this));
        }
    }

    @Override
    public void onPositiveClick(String tag, int which) {
        if (tag.equals("refreshPromoList")) {
            new LoadVoucherList().execute(SharedPreferencesManager.getAPIKey(this));
        }
    }

}
