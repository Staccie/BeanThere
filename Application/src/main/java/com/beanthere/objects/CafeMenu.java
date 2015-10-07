package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by staccie on 9/25/15.
 */
public class CafeMenu {

    @SerializedName("menu_id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String desc;

    @SerializedName("image")
    public String image;
}
