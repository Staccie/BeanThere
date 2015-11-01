package com.beanthere.webservice;

import android.util.Log;

import com.beanthere.objects.AppObject;
import com.beanthere.utils.Logger;

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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by staccie
 */
public class HttpHandler {

    public String register(String apikey, String email, String password, String firstName, String lastName, String dob, String fb_user_id, String fb_auth_token) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("dob", dob);
        map.put("fb_user_id", fb_user_id);
        map.put("fb_auth_token", fb_auth_token);
        Logger.e("register", email + "; " + password + "; " + firstName + "; " + lastName + "; " + dob + "; " + fb_user_id + "; " + fb_auth_token);
        return httpPost(map, "register", "");
    }

    public String login(String email, String password, int type) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("logintype", type);

        Logger.e("login", email + "; " + password + "; " + type);

        return httpPost(map, "login", "");
    }

    public String forgotpassword(String apikey, String email) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);

        return httpPost(map, "user/forgotpassword", apikey);
    }

    public String getMerchantList(String apikey, Double latitude, Double longitude) {

        String latln = "";

        if (latitude != null && longitude != null) {
            latln = "/" + latitude + "/" + longitude;
        }

        return httpGet("merchants/listing" + latln, apikey);
    }

    public String getFavouriteList(String apikey) {
        return httpGet("merchants/favourites", apikey);
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

    public String getCafeFilterList(String searchText, String apikey) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("search", searchText);
        map.put("latitude", "");
        map.put("longitude", "");

        return httpPost(map, "merchants/search", apikey);
    }

    public String addFavourite(String apikey, String merchantId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("merchant_id", merchantId);

        return httpPost(map, "favourites", apikey);
    }

    public String deleteFavourite(String apikey, String merchantId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("merchant_id", merchantId);

        return httpDelete(map, "favourites/" + merchantId, apikey);
    }

    private String httpPost(Map<String, Object> map, String action, String apikey) {

        String requestURL = (AppObject.IS_DEV ? AppObject.SERVER_DEV_URL : AppObject.SERVER_DEV_URL) + action;

        StringBuilder stringBuilder = new StringBuilder();

        HttpURLConnection conn = null;

        try {
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

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            if (!apikey.isEmpty()) {
                conn.setRequestProperty("Authorization", apikey);
            }

            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            Log.e("httpPost", stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e("@HttpHandler:httpPost", "MalformedURLException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("@HttpHandler:httpPost", "UnsupportedEncodingException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("@HttpHandler:httpPost", "IOException: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return stringBuilder.toString();
    }

    private String httpGet(String action, String apikey) {

        String response = null;

        String requestURL = (AppObject.IS_DEV ? AppObject.SERVER_DEV_URL : AppObject.SERVER_DEV_URL) + action;

        HttpURLConnection conn = null;

        try {

            URL obj = new URL(requestURL);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("Authorization", apikey);

            Logger.e("httpGet requestURL", requestURL);
            Logger.e("httpGet apikey", apikey);

//            String basicAuth = "Basic " + new String(Base64.encode(apikey.getBytes(), 0));


            int responseCode = conn.getResponseCode();
            Log.e("GET Response Code :: ",  String.valueOf(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    private String httpDelete(Map<String, Object> map, String action, String apikey) {

        String requestURL = (AppObject.IS_DEV ? AppObject.SERVER_DEV_URL : AppObject.SERVER_DEV_URL) + action;

        StringBuilder stringBuilder = new StringBuilder();

        HttpURLConnection conn = null;

        try {
            Log.e("httpDelete", requestURL);

            URL url = new URL(requestURL);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : map.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            if (!apikey.isEmpty()) {
                conn.setRequestProperty("Authorization", apikey);
            }

            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            Log.e("httpPost", stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e("@HttpHandler:httpPost", "MalformedURLException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("@HttpHandler:httpPost", "UnsupportedEncodingException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("@HttpHandler:httpPost", "IOException: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return stringBuilder.toString();
    }
}
