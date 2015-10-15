/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.beanthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.objects.AppObject;
import com.beanthere.objects.User;
import com.beanthere.utils.CommonUtils;
import com.beanthere.utils.IOUtils;
import com.beanthere.webservice.HttpHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Launcher activity
 */
public class MainActivity extends Activity {

    CallbackManager callbackManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        CommonUtils.setScreenWidth(this);

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        callbackManager = CallbackManager.Factory.create();

//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        final AccessToken token = loginResult.getAccessToken();

                        // Get user profile
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // TODO handle error
                                            Log.e("onCompleted", "ERROR");
                                        } else {
                                            registerLoginUser(token.toString(), json);
                                        }
                                    }

                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        autoLogin();


    }

    private void autoLogin() {

        int loginType = SharedPreferencesManager.getInt(this, "logintype");

        String email = SharedPreferencesManager.getString(this, "email");
        String pw = SharedPreferencesManager.getString(this, "p");

        if (loginType == 1) {
            if (!email.isEmpty() && !pw.isEmpty()) {
                new Login().execute(email, pw);
            }
        } else if (loginType == 0) {
            if (email.isEmpty()) {
                // Prompt for email
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
            }
        }

    }

    private void registerLoginUser(String accessToken, JSONObject json) {

        String jsonresult = String.valueOf(json);
        Log.e("JSON Result", jsonresult);

        String email = json.optString("email");
        String id = json.optString("id");
        String firstName = json.optString("first_name");
        String lastName = json.optString("last_name");

        SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fbname", firstName);
        editor.commit();

        new DownloadProfilePic().execute(id);

    }

    private void processLogin(User user) {

        SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("u_id", user.id);
        editor.putString("apikey", user.api_key);
        editor.putInt("logintype", 1);
        editor.putBoolean("checkLocation", true);

        editor.commit();

        Intent intent = new Intent(this, CafeListActivity.class);
        startActivity(intent);
        finish();
    }

    // TODO merge with LoginActivity
    class Login extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("Login", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.login(params[0], params[1], 1);

            return response;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response == null || response.isEmpty()) {
                FragmentManager fm = getFragmentManager();
                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                noticeDialog.show(fm, "");
            } else {

                Gson gson = new Gson();
                User user;

                user = gson.fromJson(response, User.class);

                if (user.error) {
                    FragmentManager fm = getFragmentManager();
                    NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), user.error_message, "");
                    noticeDialog.show(fm, "");
                } else {
                    processLogin(user);
                }



            }
        }
    }

    class DownloadProfilePic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bm = null;

            if (!params[0].isEmpty()) {

                URL image_value;

                try {
                    image_value = new URL("http://graph.facebook.com/" + params[0] + "/picture?type=large");
                    Log.e("image_value", image_value.toString());
                    bm = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
                    Log.e("bm", "" + (bm == null));

//                    URL aURL = new URL("http://graph.facebook.com/"+params[0]+"/picture?type=large");
//                    URLConnection conn = aURL.openConnection();
//                    conn.setUseCaches(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    BufferedInputStream bis = new BufferedInputStream(is);
//                    bm = BitmapFactory.decodeStream(bis);
//                    bis.close();
//                    is.close();


//                    bm = BitmapFactory.decodeStream(is);
//                    IOUtils.copyInputStreamToFile(MainActivity.this, false, is, "fbprofilepic");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
//            startActivity(new Intent(MainActivity.this, CafeListActivity.class));
//            ivTest.setImageBitmap(bm);
        }
    }

    public void onClickLoginEmail(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClickSignUp(View view) {
        startActivity( new Intent(this, RegisterActivity.class) );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "fdsff");
    }


}
