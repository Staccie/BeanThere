package com.beanthere.objects;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Voucher {

    @SerializedName("voucher_id")
    public String voucher_id;

    @SerializedName("name")
    public String name;

    @SerializedName("expiry")
    public String expiry;

    @SerializedName("custom_naming")
    public String status;

    @SerializedName("merchants")
    public List<Cafe> cafeList;


}
