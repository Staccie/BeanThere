package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by staccie on 9/25/15.
 */
public class CafeDetails {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String error_message;

    @SerializedName("merchants")
    public List<Cafe> cafeList;

}
