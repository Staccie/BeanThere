package com.beanthere.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.objects.Cafe;
import com.beanthere.utils.CommonUtils;
import com.beanthere.utils.ImageViewDownloader;

import java.util.List;

/**
 * Created by staccie
 */
public class CafeFilterAdapter extends BaseAdapter {
    Context mContext;
    List<Cafe> mList;

    public CafeFilterAdapter(Context context, List<Cafe> list) {
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
            holder.tvName = (TextView) container.findViewById(R.id.textViewCafeName);
            container.setTag(holder);

        } else {
            holder = (ViewHolder) container.getTag();
        }

        Cafe cafe = mList.get(position);

        Log.e("view holder " + position, cafe.name);

        holder.tvName.setText(cafe.name);

        return container;
    }

    class ViewHolder {
        TextView tvName;
    }
}
