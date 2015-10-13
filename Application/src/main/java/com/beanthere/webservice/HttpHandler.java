package com.beanthere.webservice;

import android.util.Log;

import com.beanthere.objects.AppObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by staccie on 9/13/15.
 */
public class HttpHandler {

    public String register(String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("dob", dob);
//        map.put("fb_user_id", fb_user_id);
//        map.put("fb_auth_token", fb_auth_token);
        Log.e("register", email + "; " + password + "; " + firstName + "; " + lastName + "; " + dob);
        return httpPost(map, "register", "");
    }

    public String login(String email, String password, int type) {


        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("logintype", type);

        return httpPost(map, "login", "");
    }

    public String forgotpassword(String email) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);

        return httpPost(map, "forgotpassword", "");
    }

    public String getMerchantList(String apikey, Double latitude, Double longitude) {

        String latln = "";

        if (latitude != null && longitude != null) {
            latln = "/" + latitude + "/" + longitude;
        }

        return httpGet("merchants/listing" + latln, apikey);
    }

    public String getMerchantDetails(String id, String apikey) {
        Log.e("getMerchantDetails", "id: " + id + "; apikey: " + apikey);
        return httpGet("merchants/detail/"+id, apikey);
    }

    public String getWalletCardList(String apikey) {
        return httpGet("cards/wallet", apikey);
    }

    public String getWalletVoucherList(String apiKey) {
        return httpGet("vouchers", apiKey);
    }

    public String getVoucher(String voucher_code, String apikey) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("voucher_code", voucher_code);

        return httpPost(map, "vouchers/claim", apikey);
    }

    public String redeemVoucher(String voucherId, String code, String apikey) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("tx_vouchers_id", voucherId);
        map.put("pin", code);

        return httpPost(map, "vouchers/redeem", apikey);
    }

    public String updateProfile(String apikey, String user_id, String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("dob", dob);
        map.put("fb_user_id", fb_user_id);
        map.put("fb_auth_token", fb_auth_token);
        map.put("_METHOD", "PUT");
//        Log.e("register", email + "; " + password + "; " + firstName + "; " + lastName + "; " + dob);
        // TODO get user id in Login API and send over?
        return httpPost(map, "user/" + user_id, apikey);
    }

    public String getCafeFilterList(String searchText, Double mLatitude, Double mLongitude, String apikey) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("search", searchText);
        map.put("latitude", mLatitude == null ? "" : mLatitude);
        map.put("longitude", mLongitude == null ? "" : mLongitude);

        return httpPost(map, "merchants/search", apikey);
    }

    /*public JSONObject setUpdateProfile(String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

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
    }*/

    private String httpPost(Map<String, Object> map, String action, String apikey) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            String requestURL = (AppObject.isDev ? AppObject.url_dev : AppObject.url_dev) + action;

            Log.e("httpPost", requestURL);

//            printMap(map);

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
            if (!apikey.isEmpty()) {
                conn.setRequestProperty("Authorization", apikey);
            }
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Log.e("httpPost", "writing response");
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                Log.e("append", "line");
                stringBuilder.append(line);
            }
            Log.e("httpPost", stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e("@HttpHandler:httpPost", "MalformedURLException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("@HttpHandler:httpPost", "UnsupportedEncodingException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("@HttpHandler:httpPost", "IOException: " + e.getMessage());
        }

        return stringBuilder.toString();
    }

    private String httpGet(String action, String apikey) {

        String response = null;

        try {
            String requestURL = (AppObject.isDev ? AppObject.url_dev : AppObject.url_dev) + action;

            URL obj = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            Log.e("httpGet requestURL", requestURL);

            Log.e("httpGet apikey", apikey);

//            String basicAuth = "Basic " + new String(Base64.encode(apikey.getBytes(), 0));
            con.setRequestProperty ("Authorization", apikey);


            int responseCode = con.getResponseCode();
            Log.e("GET Response Code :: ",  String.valueOf(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                in.close();

                response = stringBuilder.toString();

                Log.e("httpGet", response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
}
