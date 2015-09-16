package com.beanthere.http;

import android.util.Log;

import com.beanthere.objects.AppObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by staccie on 9/13/15.
 */
public class Request {

    public String sendRegisterRequest(String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("dob", dob);
        map.put("fb_user_id", fb_user_id);
        map.put("fb_auth_token", fb_auth_token);

        return httpPost(map, "register");
    }

    public JSONObject setLoginObject(String email, String password, int type) {


        JSONObject json = new JSONObject();

        try {
            json.put("email", email);
            json.put("password", password);
            json.put("logintype", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject setUpdateProfile(String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

        JSONObject json = new JSONObject();

        try {
            json.put("email", email);
            json.put("password", password);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("dob", dob);
            json.putOpt("fb_user_id", fb_user_id);
            json.putOpt("fb_auth_token", fb_auth_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject setForgotPassword(String email) {

        JSONObject json = new JSONObject();

        try {
            json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject setMerchantListing(float latitude, float longitude) {

        JSONObject json = new JSONObject();

        try {
            json.put("latitude", latitude);
            json.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject setMerchantDetails(String id) {

        JSONObject json = new JSONObject();

        try {
            json.put("merchant_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject setMerchantSearch(String text, float latitude, float longitude) {

        JSONObject json = new JSONObject();

        try {
            json.putOpt("search", text);
            json.putOpt("latitude", latitude);
            json.putOpt("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String httpPost(Map<String, Object> map, String action) {

        String response = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String requestURL = (AppObject.isDev ? AppObject.url_dev : AppObject.url_dev) + action;

            Log.e("httpPost", requestURL);

            URL url = new URL(requestURL);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : map.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Log.e("httpPost", "writing response");
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (MalformedURLException e) {
            Log.e("@test", e.getMessage() == null ? "No Error Message!!!" : e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("@test", e.getMessage() == null ? "No Error Message!!!" : e.getMessage());
        } catch (IOException e) {
            Log.e("@test", e.getMessage() == null ? "No Error Message!!!" : e.getMessage());
        }

        Log.e("res", stringBuilder.toString());

        return stringBuilder.toString();
    }



}
