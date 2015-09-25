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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by staccie on 9/13/15.
 */
public class Request {

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
        return httpPost(map, "register");
    }

    public String login(String email, String password, int type) {


        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("logintype", type);

        return httpPost(map, "login");
    }

    public String forgotpassword(String email) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);

        return httpPost(map, "forgotpassword");
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

    private String httpPost(Map<String, Object> map, String action) {

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
                Log.e("append", "line");
                stringBuilder.append(line);
            }
            Log.e("httpPost", stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e("@Request:httpPost", "MalformedURLException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("@Request:httpPost", "UnsupportedEncodingException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("@Request:httpPost", "IOException: " + e.getMessage());
        }

        return stringBuilder.toString();
    }

    private String httpGet(String action, String apikey) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            String requestURL = (AppObject.isDev ? AppObject.url_dev : AppObject.url_dev) + action;

            URL obj = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            Log.e("httpGet apikey", apikey);

//            String basicAuth = "Basic " + new String(Base64.encode(apikey.getBytes(), 0));
            con.setRequestProperty ("Authorization", apikey);


            int responseCode = con.getResponseCode();
            Log.e("GET Response Code :: ",  String.valueOf(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                in.close();

                Log.e("httpGet", stringBuilder.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();

    }

//    private String httpGet() {
//        HttpURLConnection connection = null;
//        OutputStreamWriter wr = null;
//        BufferedReader rd  = null;
//        StringBuilder sb = null;
//        String line = null;
//
//        URL serverAddress = null;
//
//        try {
//            serverAddress = new URL("http://localhost");
//            //set up out communications stuff
//            connection = null;
//
//            //Set up the initial connection
//            connection = (HttpURLConnection)serverAddress.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setDoOutput(true);
//            connection.setReadTimeout(10000);
//
//            connection.connect();
//
//            //get the output stream writer and write the output to the server
//            //not needed in this example
//            //wr = new OutputStreamWriter(connection.getOutputStream());
//            //wr.write("");
//            //wr.flush();
//
//            //read the result from the server
//            rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            sb = new StringBuilder();
//
//            while ((line = rd.readLine()) != null)
//            {
//                sb.append(line + '\n');
//            }
//
//            System.out.println(sb.toString());
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally
//        {
//            //close the connection, set all objects to null
//            connection.disconnect();
//            rd = null;
//            sb = null;
//            wr = null;
//            connection = null;
//        }
//    }


//    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
//
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//        for(Map.Entry<String, String> entry : params.entrySet()){
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//        }
//
//        return result.toString();
//    }

}
