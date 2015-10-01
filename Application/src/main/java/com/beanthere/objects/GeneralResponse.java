package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by staccie on 9/21/15.
 */
public class GeneralResponse {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String error_message;

    @SerializedName("merchants")
    public List<Cafe> cafeList;

}
