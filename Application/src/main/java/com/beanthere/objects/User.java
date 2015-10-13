package com.beanthere.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by staccie on 9/8/15.
 */
public class User {

    public int user_type;
    public String password;

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String error_message;

    @SerializedName("u_id")
    public String id;

    @SerializedName("email")
    public String email;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("dob")
    public String dob;

    @SerializedName("fb_user_id")
    public String fb_user_id;

    @SerializedName("fb_auth_token")
    public String fb_auth_token;

    @SerializedName("apiKey")
    public String api_key;

    @SerializedName("createdAt")
    public String created;

    @Override
    public String toString() {
        return "error:" + error + "\nemail:" + email + "; first_name: " + first_name + "; last_name: " + last_name + "; fb_user_id: " + fb_user_id + "; fb_auth_token: " + fb_auth_token + "; apiKey: " + api_key + "; createdAt: " + created ;
    }


}
