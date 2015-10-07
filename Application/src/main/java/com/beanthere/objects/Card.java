package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

public class Card {

    @SerializedName("name")
    public String name;

    @SerializedName("wallet_id")
    public String walletId;

    @SerializedName("card_id")
    public String cardId;

    @SerializedName("expiry")
    public String daysExpiring;

    @SerializedName("images_1")
    public String image;
}
