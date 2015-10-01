package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by staccie on 9/25/15.
 */
public class OperatingHour {

    @SerializedName("operating_id")
    public String id;

    @SerializedName("day_index")
    public String day;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("end_time")
    public String endTime;


    public OperatingHour(String day, String startTime, String endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
