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

package com.beanthere.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanthere.R;

/**
 * Adapter for the planet data used in our drawer menu,
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private String[] mDataset;
    private OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

    /**
     * Custom viewholder for our planet views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLayout;
        private TextView mTextView;

        public ViewHolder(LinearLayout ll, TextView v) {
            super(v);
            mTextView = v;
            mLayout = ll;
        }
    }

    public MenuAdapter(String[] myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 0) {
            View view = inflater.inflate(R.layout.drawer_profile, null);
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearLayoutDrawerProfile);
            TextView tv = (TextView) view.findViewById(R.id.textViewJoinDate);
            return new ViewHolder(ll, tv);
        } else if (viewType == 1){
            View view = inflater.inflate(R.layout.drawer_list_item, null);
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearLayoutDrawerItem);
            TextView tv = (TextView) view.findViewById(R.id.textViewDrawerItem);
            return new ViewHolder(ll, tv);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mDataset[position]);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
