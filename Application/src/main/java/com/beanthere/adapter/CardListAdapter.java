package com.beanthere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.objects.Card;

import java.util.List;

public class CardListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Card> mList;

    public CardListAdapter(Context context, List<Card> list) {
        this.mContext = context;
        this.mList = list;
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
        return mList == null ? -1 : (mList.size() == 0 ? -1 : Integer.valueOf(mList.get(position).cardId));
    }

    @Override
    public View getView(int position, View container, ViewGroup viewGroup) {
        ViewHolder holder;

        if (container == null) {

//            container = LayoutInflater.from(mContext).inflate(R.layout.lv_item_cafe, null);
//
//            holder = new ViewHolder();
//            holder.ivCard = (ImageView) container.findViewById(R.id.imageViewCafe);
//            holder.tvName = (TextView) container.findViewById(R.id.textViewCafeName);
//            holder.tvExpire = (TextView) container.findViewById(R.id.textViewCafeDistance);
//            holder.tvDesc = (TextView) container.findViewById(R.id.textViewCafeAddress1);


//            container.setTag(holder);

        } else {
            holder = (ViewHolder) container.getTag();
        }

        Card card = mList.get(position);

//        Log.e("view holder " + position, card.name);
//
//        if (card.images_1 == null || card.images_1.trim().isEmpty()) {
//            holder.ivCafe.setImageResource(R.drawable.placeholder_black);
//        } else {
//            new ImageDownloader(holder.ivCafe).execute(cafe.images_1);
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
        ImageView ivCard;
        TextView tvName;
        TextView tvExpire;
        TextView tvDesc;
    }
}
