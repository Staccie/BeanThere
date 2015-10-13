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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanthere.R;

/**
 * Adapter for the planet data used in our drawer menu,
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private String mNavTitles[];
    private int mIcons[];

    private OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private String name;        //String Resource for header View Name
    private int profile;        //int Resource for header view profile picture
    private String email;       //String Resource for header view email

    public NavDrawerAdapter(String titles[], int[] icons, OnItemClickListener listener){
        mNavTitles = titles;
        mIcons = icons;
        mListener = listener;
//        email = Email;
//        profile = Profile;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        int type;
        View view;
        TextView tvItem;
        ImageView ivIcon;
        ImageView ivProfile;
        TextView dateJoin;

        public ViewHolder(View itemView,int ViewType) {
            super(itemView);

            view = itemView;

            if(ViewType == 1) {
                tvItem = (TextView) itemView.findViewById(R.id.textViewDrawerItem);
                ivIcon = (ImageView) itemView.findViewById(R.id.imageViewDrawerIcon);
                type = 1;
            }
            else{
                ivProfile = (ImageView) itemView.findViewById(R.id.imageViewProfile);
                dateJoin = (TextView) itemView.findViewById(R.id.textViewJoinDate);
                type = 0;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item,parent,false);
            ViewHolder holder = new ViewHolder(view, viewType);
            return holder;
        } else if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_profile,parent,false);
            ViewHolder holder = new ViewHolder(view, viewType);
            return holder;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {

        if(holder.type ==1) {
            holder.tvItem.setText(mNavTitles[position - 1]);
            Log.e("imageresource", "position=" + position + "; " + mIcons[position - 1]);
            holder.ivIcon.setImageResource(mIcons[position - 1]);
        }
        else{
            holder.ivProfile.setImageResource(R.drawable.placeholder_people);
//            holder.Name.setText(name);
//            holder.email.setText(email);
        }

        final int pos = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

}
