package com.beanthere.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.objects.Cafe;
import com.beanthere.utils.CommonUtils;
import com.beanthere.utils.ImageViewDownloader;

import java.util.List;

public class CafeListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Cafe> mList;
    private boolean distanceVisibility;

    public CafeListAdapter(Context context, List<Cafe> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : (mList.size() == 0 ? null : mList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mList == null ? -1 : (mList.size() == 0 ? -1 : mList.get(position).id);
    }

    @Override
    public View getView(int position, View container, ViewGroup viewGroup) {
        ViewHolder holder;

        if (container == null) {

            container = LayoutInflater.from(mContext).inflate(R.layout.lv_item_cafe, null);

            holder = new ViewHolder();
            holder.ivCafe = (ImageView) container.findViewById(R.id.imageViewCafe);
            holder.tvName = (TextView) container.findViewById(R.id.textViewCafeName);
            holder.tvDistance = (TextView) container.findViewById(R.id.textViewCafeDistance);
            holder.tvAddress1 = (TextView) container.findViewById(R.id.textViewCafeAddress1);
//            holder.tvAddress2 = (TextView) container.findViewById(R.id.textViewCafeAddress2);


            container.setTag(holder);

        } else {
            holder = (ViewHolder) container.getTag();
        }

        Cafe cafe = mList.get(position);

//        Log.e("view holder " + position, cafe.name);

        if (cafe.images_1 == null || cafe.images_1.trim().isEmpty()) {
            holder.ivCafe.setImageResource(R.drawable.placeholder);
        } else {
            new ImageViewDownloader(holder.ivCafe, false).execute(cafe.images_1);
        }

        holder.tvName.setText(cafe.name);

        if (this.distanceVisibility) {
            String distance = CommonUtils.getDistance(cafe.distance);
            distance = distance == "" ? "" : distance + "KM";
            holder.tvDistance.setText(distance);
            holder.tvDistance.setVisibility(View.VISIBLE);
        } else {
            holder.tvDistance.setVisibility(View.GONE);
        }

        holder.tvAddress1.setText(cafe.areaTags == null ? "" : cafe.areaTags);
//        holder.tvAddress1.setText((cafe.address_1 == null ? "" : (cafe.address_1 + " ")) + (cafe.address_2 == null ? "" : cafe.address_2));
//        holder.tvAddress2.setText(cafe.address_2);

        return container;
    }

    public void setDistanceVisibility(boolean distanceVisibility) {
        this.distanceVisibility = distanceVisibility;
    }

    class ViewHolder {
        ImageView ivCafe;
        TextView tvName;
        TextView tvDistance;
        TextView tvAddress1;
//        TextView tvAddress2;
    }
}
