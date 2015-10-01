package com.beanthere.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.objects.Category;

import java.util.List;

/**
 * Created by staccie on 9/29/15.
 */
public class CafeMenuAdapter extends BaseAdapter {


    Context mContext;
    List<Category> mList;

    public CafeMenuAdapter(Context context, List<Category> list) {
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
        return mList == null ? -1 : (mList.size() == 0 ? -1 : Long.getLong(mList.get(position).categoryId));
    }

    @Override
    public View getView(int position, View container, ViewGroup viewGroup) {
        ViewHolder holder;

//        if (container == null) {
//
//            container = LayoutInflater.from(mContext).inflate(R.layout.lv_item_cafe, null);
//
//            holder = new ViewHolder();
//            holder.ivCafe = (ImageView) container.findViewById(R.id.imageViewCafe);
//            holder.tvName = (TextView) container.findViewById(R.id.textViewCafeName);
//            holder.tvDistance = (TextView) container.findViewById(R.id.textViewCafeDistance);
//            holder.tvAddress1 = (TextView) container.findViewById(R.id.textViewCafeAddress1);
//            holder.tvAddress2 = (TextView) container.findViewById(R.id.textViewCafeAddress2);
//
//
//            container.setTag(holder);
//
//        } else {
//            holder = (ViewHolder) container.getTag();
//        }
//
//        Cafe cafe = mList.get(position);
//
//        Log.e("view holder " + position, cafe.name);
//
//        if (cafe.images_1 == null || cafe.images_1.trim() == "") {
//            holder.ivCafe.setImageResource(R.drawable.placeholder);
//        } else {
//            holder.ivCafe.setImageURI(Uri.parse(cafe.images_1));
//        }
//        holder.tvName.setText(cafe.name);
//
//        String distance = CommonUtils.getDistance(cafe.distance);
//        distance = distance == "" ? "" : distance + "KM";
//
//        holder.tvDistance.setText(distance);
//        holder.tvAddress1.setText(cafe.address_1);
//        holder.tvAddress2.setText(cafe.address_2);

        return container;
    }

    class ViewHolder {
        ImageView ivCafe;
        TextView tvName;
        TextView tvDistance;
        TextView tvAddress1;
        TextView tvAddress2;
    }
}
