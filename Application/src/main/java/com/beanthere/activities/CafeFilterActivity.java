package com.beanthere.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.beanthere.R;
import com.beanthere.adapter.CafeFilterAdapter;
import com.beanthere.adapter.CafeListAdapter;
import com.beanthere.objects.Cafe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by staccie
 */
public class CafeFilterActivity extends NavDrawerActivity {

    private List<Cafe> mList;
    private ListAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_cafe_list, null, false);
        frameLayout.addView(view);

        if (mList == null) {
            mList = new ArrayList<Cafe>();
        }

        mAdapter = new CafeListAdapter(this, mList);
        mListView = (ListView) view.findViewById(R.id.listViewCafe);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {

                Cafe selectedCafe = (Cafe) listView.getItemAtPosition(position);
                startCafeActivity(selectedCafe.id);
            }
        });
    }

    private void startCafeActivity(int id) {

    }
}
