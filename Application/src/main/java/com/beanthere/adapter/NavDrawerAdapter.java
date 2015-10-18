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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.utils.DateFormat;
import com.beanthere.utils.Logger;

import java.io.File;

/**
 * Adapter for the planet data used in our drawer menu,
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private Context mContext;
    private String mNavTitles[];
    private int mIcons[];
    private String mUserName;
    private String mDateJoin;

    private OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public NavDrawerAdapter(Context context, String titles[], int[] icons, String username, String dateJoin, OnItemClickListener listener){
        mContext = context;
        mNavTitles = titles;
        mIcons = icons;
        mUserName = username;
        mDateJoin = dateJoin;
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        int type;
        View view;
        TextView tvItem;
        TextView tvName;
        ImageView ivIcon;
        ImageView ivProfile;
        TextView tvDateJoin;

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
                tvDateJoin = (TextView) itemView.findViewById(R.id.textViewJoinDate);
                tvName = (TextView) itemView.findViewById(R.id.textViewFBName);
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
            Logger.e("onBindViewHolder", "position " + position);
            holder.ivIcon.setImageResource(mIcons[position - 1]);
        }
        else {
            File sd = mContext.getExternalFilesDir(null);
            Logger.e("sd.path", "" + sd.getAbsolutePath());
            File image = new File(sd, "fbprofilepic");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

            if (bitmap != null) {
                holder.ivProfile.setImageBitmap(bitmap);
            } else {
                holder.ivProfile.setImageResource(R.drawable.placeholder_people);
            }

            holder.tvName.setText(mUserName);

            String date = DateFormat.formatDateJoin(mDateJoin);
            holder.tvDateJoin.setText(String.format(mContext.getString(R.string.date_join), date));

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
