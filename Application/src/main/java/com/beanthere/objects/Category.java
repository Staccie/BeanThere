package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by staccie on 9/25/15.
 */
public class Category {

    @SerializedName("category_id")
    public String categoryId;

    @SerializedName("category_name")
    public String categoryName;

    @SerializedName("menus")
    public List<CafeMenu> cafeMenuList;


}
