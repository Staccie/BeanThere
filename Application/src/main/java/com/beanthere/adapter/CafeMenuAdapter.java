package com.beanthere.adapter;

/**
 * Created by staccie on 10/4/15.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.objects.CafeMenu;
import com.beanthere.utils.ImageViewDownloader;

import java.util.HashMap;
import java.util.List;

public class CafeMenuAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mCategories;
    private HashMap<String, List<CafeMenu>> mCafeMenuList;

    public CafeMenuAdapter(Context context, List<String> categories, HashMap<String, List<CafeMenu>> cafeMenuList) {
        this.mContext = context;
        this.mCategories = categories;
        this.mCafeMenuList = cafeMenuList;
    }
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mCafeMenuList.get(this.mCategories.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        CafeMenu menu = (CafeMenu) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lv_item_cafe_menu, null);
        }


        ImageView ivMenu = (ImageView) convertView.findViewById(R.id.imageViewCafeMenu);
        TextView tvMenuTitle = (TextView) convertView.findViewById(R.id.textViewMenuTitle);
        TextView tvMenuDesc = (TextView) convertView.findViewById(R.id.textViewMenuDesc);

        if (menu.image == null || menu.image.trim().isEmpty()) {
            ivMenu.setImageResource(R.drawable.placeholder);
        } else {
            new ImageViewDownloader(ivMenu).execute(menu.image);
        }

        tvMenuTitle.setText(menu.name);
        tvMenuDesc.setText(menu.desc);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCafeMenuList == null ? 0 : this.mCafeMenuList.get(this.mCategories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategories == null ? null : this.mCategories.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mCategories == null ? 0 : this.mCategories.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mCategories == null ? -1 : groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.e("getGroupView", groupPosition + "; ");

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lv_item_cafe_menu_header, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.textViewMenuHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> categories, HashMap<String, List<CafeMenu>> menuList) {
        mCategories = categories;
        mCafeMenuList = menuList;
        notifyDataSetChanged();
    }
}
