package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by staccie on 9/8/15.
 */
public class Cafe {

    public String geo_lat;
    public String geo_long;


    @SerializedName("merchant_id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("pin")
    public String pin;

    @SerializedName("description")
    public String description;

    @SerializedName("address_1")
    public String address_1;

    @SerializedName("address_2")
    public String address_2;

    @SerializedName("contact")
    public String contact;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("tags")
    public String tags;

    @SerializedName("logo")
    public String logo;

    @SerializedName("images_1")
    public String images_1;

    @SerializedName("images_2")
    public String images_2;

    @SerializedName("images_3")
    public String images_3;

    @SerializedName("food")
    public String food;

    @SerializedName("wifi")
    public String wifi;

    @SerializedName("instagram")
    public String instagram;

    @SerializedName("facebook")
    public String facebook;

    @SerializedName("twitter")
    public String twitter;

    @SerializedName("website")
    public String website;

    @SerializedName("coffee_machine")
    public String coffee_machine;

    @SerializedName("coffee_beans")
    public String coffee_beans;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("created_by")
    public String created_by;

    @SerializedName("updated_at")
    public String updated_at;

    @SerializedName("updated_by")
    public String updated_by;

    @SerializedName("distance")
    public String distance;

    @SerializedName("operating")
    public List<OperatingHour> operatingHourList;

    @SerializedName("categories")
    public List<Category> categoryList;

}
