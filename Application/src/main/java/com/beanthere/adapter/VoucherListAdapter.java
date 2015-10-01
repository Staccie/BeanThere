package com.beanthere.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.listeners.BeanAdapterInterface;
import com.beanthere.objects.Voucher;

import java.util.List;

/**
 * Created by staccie on 9/20/15.
 */
public class VoucherListAdapter extends BaseAdapter {

    Context mContext;
    List<Voucher> mList;

    public VoucherListAdapter(Context context, List<Voucher> list) {
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
        return mList == null ? -1 : (mList.size() == 0 ? -1 : Long.valueOf(mList.get(position).voucher_id));
    }

    @Override
    public View getView(int position, View container, ViewGroup viewGroup) {
        ViewHolder holder;

        if (container == null) {

            container = LayoutInflater.from(mContext).inflate(R.layout.lv_item_promo, null);

            holder = new ViewHolder();
            holder.ivVoucher = (ImageView) container.findViewById(R.id.imageViewVoucher);
            holder.tvName = (TextView) container.findViewById(R.id.textViewVoucherTitle);
            holder.tvExpiryDate = (TextView) container.findViewById(R.id.textViewVoucherExpiryDate);
            holder.btnRedeem = (Button) container.findViewById(R.id.buttonRedeem);

            container.setTag(holder);

        } else {
            holder = (ViewHolder) container.getTag();
        }

        Voucher voucher = mList.get(position);

        Log.e("view holder " + position, voucher.name);
        final int voucherId = Integer.valueOf(voucher.voucher_id);
        final int pos = position;

        holder.tvName.setText(voucher.name);
        holder.tvExpiryDate.setText(voucher.expiry);
        holder.btnRedeem.setTag(position);
        holder.btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BeanAdapterInterface.OnButtonClickListener) mContext).onButtonClick("voucherDetails", voucherId, pos);
            }
        });

        return container;
    }

    class ViewHolder {
        ImageView ivVoucher;
        TextView tvName;
        TextView tvExpiryDate;
        Button btnRedeem;
    }
}
