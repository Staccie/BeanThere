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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.dialoghelper.PromoInputDialog;
import com.beanthere.objects.AppObject;
import com.beanthere.objects.User;
import com.beanthere.utils.CommonUtils;
import com.beanthere.utils.IOUtils;
import com.beanthere.utils.Logger;
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

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Launcher activity
 */
public class MainActivity extends Activity implements BeanDialogInterface.OnInputDialogDismissListener, BeanDialogInterface.OnProgressDialogCancelled {

    CallbackManager callbackManager;

    private String mFBEmail;
    private String mFBFirstName;
    private String mFBLastName;
    private String mFBId;
    private String mFBToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        autoOfflineLogin();

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
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    DialogHelper.showErrorDialog(MainActivity.this, response.getError().getErrorMessage());
                                } else {
                                    registerLoginFBUser(token.getToken(), json);
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

        if (CommonUtils.isConnected(MainActivity.this)) {

            int loginType = SharedPreferencesManager.getInt(this, "logintype");

            if (loginType == 1) {

                String email = SharedPreferencesManager.getString(this, "email");
                String pw = SharedPreferencesManager.getString(this, "p");

                if (!email.isEmpty() && !pw.isEmpty()) {
                    new LoginTask().execute("1", email, pw);
                }
            } else if (loginType == 2) {

                String email = SharedPreferencesManager.getString(this, "fb_email");

                if (!email.isEmpty()) {
                    new LoginTask().execute("2", email, AppObject.DEFAULT_P);
                }
            }
        }

    }

    private void autoOfflineLogin() {

        if (SharedPreferencesManager.getInt(this, "logintype") != -1 && !SharedPreferencesManager.getAPIKey(this).isEmpty()) {

            SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("checkLocation", true);
            editor.commit();

            Intent intent = new Intent(this, CafeListActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void registerLoginFBUser(String accessToken, JSONObject json) {

        String jsonresult = String.valueOf(json);
        Logger.e("JSON Result", jsonresult);

        String email = json.optString("email", "");
        mFBId = json.optString("id");
        mFBFirstName = json.optString("name", email);
        mFBLastName = json.optString("last_name", "last name");
        mFBToken = accessToken;

//        Only save them after successfully login
//        SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("fb_email", email);
//        editor.putString("fb_user_id", mFBId);
//        editor.putString("fb_auth_token", accessToken);
//        editor.putString("fbname", mFBFirstName);
//        editor.commit();

        String prevFBId = SharedPreferencesManager.getString(this, "fb_user_id");
        String prevFBEmail = SharedPreferencesManager.getString(this, "fb_email");

        Logger.e("EFESS", "prevFBId: " + prevFBId + "; prevFBEmail: " + prevFBEmail + "; current fbid: " + mFBId);

        if (!prevFBId.isEmpty() && prevFBId.equals(mFBId) && !prevFBEmail.isEmpty()) {
            // Same user logged in before. Go directly to login
            new LoginTask().execute(prevFBEmail, AppObject.DEFAULT_P);
        } else {
            // No user logged in before or different user logged in. Prompt for email.
            // Clear SharedPreference

            SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("fb_email");
            editor.remove("fb_user_id");
            editor.remove("fb_auth_token");
            editor.remove("fbname");
            editor.commit();

            FragmentManager fm = getFragmentManager();
            PromoInputDialog inputDialog = new PromoInputDialog().newEmailInput(email);
            inputDialog.show(fm, "emailforfb");
        }

        new DownloadProfilePic().execute();

    }

    private void processLogin(int loginType, User user) {

        SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("u_id", user.id);
        editor.putString("apikey", user.api_key);
        editor.putString("email", user.email);
        editor.putString("first_name", user.first_name);
        editor.putString("last_name", user.last_name);
        editor.putString("dob", user.dob.substring(0, 10));
        editor.putString("fb_user_id", user.fb_user_id);
        editor.putString("fb_auth_token", user.fb_auth_token);
        editor.putString("date_join", user.created);
        editor.putInt("logintype", loginType);

        if (loginType == 2) {
            editor.putString("fb_email", mFBEmail);
            // user id returned from server is different!!!
//            editor.putString("fb_user_id", user.fb_user_id);
            editor.putString("fb_user_id", mFBId);
        }

        editor.putBoolean("checkLocation", true);
        editor.commit();

        Intent intent = new Intent(this, CafeListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onInputDialogDismiss(String tag, String data) {
        if (tag.equals("emailforfb")) {
            if (data == null || data.isEmpty()) {
                LoginManager.getInstance().logOut();
            } else {
                mFBEmail  = data;
                new RegisterTask().execute();
            }
        }
    }

    @Override
    public void onProgressDialogCancelled(String tag) {

    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(MainActivity.this, "registerfbuser", getString(R.string.please_wait));
        }

        @Override
        protected String doInBackground(String... params) {
            Logger.e("Register", "doInBackground");
            HttpHandler req = new HttpHandler();
            return req.register("", mFBEmail, AppObject.DEFAULT_P, mFBFirstName, mFBLastName, "1900-01-01", mFBId, mFBToken);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            DialogHelper.dismissProgressDialog(MainActivity.this, "registerfbuser");
            LoginManager.getInstance().logOut();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            DialogHelper.dismissProgressDialog(MainActivity.this, "registerfbuser");

            if (response == null || response.isEmpty()) {
                DialogHelper.showInvalidServerResponse(MainActivity.this);
                LoginManager.getInstance().logOut();
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
            } else {
                // TODO register response should return error code, if account existed or registered successful then only login
                // Try login anyway until we get a meaningful code from server
                new LoginTask().execute("2", mFBEmail, AppObject.DEFAULT_P);

            }
        }
    }

    // TODO merge with LoginActivity
    private class LoginTask extends AsyncTask<String, Void, String> {

        private int mLoginType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(MainActivity.this, "loginfbuser", getString(R.string.please_wait));
        }

        @Override
        protected String doInBackground(String... params) {

            if (params[0].equals("1")) {
                mLoginType = 1;
                return new HttpHandler().login(params[1], params[2], 1);
            } else if (params[0].equals("2")) {
                mLoginType = 2;
                return new HttpHandler().login(params[1], params[2], 2);
            } else {
                return null;
            }

        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            DialogHelper.dismissProgressDialog(MainActivity.this, "loginfbuser");

            if (mLoginType == 2 ) {
                LoginManager.getInstance().logOut();
            }
            // Cancel login
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            DialogHelper.dismissProgressDialog(MainActivity.this, "loginfbuser");

            if (!isCancelled() && !isFinishing()) {

                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(MainActivity.this);

                    if (mLoginType == 2 ) {
                        LoginManager.getInstance().logOut();
                    }
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
                } else {

                    Gson gson = new Gson();
                    User user = gson.fromJson(response, User.class);

                    if (user.error) {
                        DialogHelper.showErrorDialog(MainActivity.this, user.error_message);

                        if (mLoginType == 2 ) {
                            LoginManager.getInstance().logOut();
                        }
//                        FragmentManager fm = getFragmentManager();
//                        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance("", user.error_message, "");
//                        noticeDialog.show(fm, "");
                    } else {
                        processLogin(mLoginType, user);
                    }
                }
            }
        }
    }

    private class DownloadProfilePic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bm = null;

            if (!mFBId.isEmpty()) {

                URL url;

                try {
                    url = new URL("http://graph.facebook.com/" + mFBId + "/picture?type=large");
                    Logger.e("url", url.toString());

                    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                    ucon.setInstanceFollowRedirects(false);
                    URL secondURL = new URL(ucon.getHeaderField("Location"));
                    URLConnection conn = secondURL.openConnection();

//                    InputStream is = (InputStream) image_value.openConnection().getContent();
//                    bm = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
                    // TODO scale down
//                    bm = BitmapFactory.decodeStream(conn.getInputStream());
                    Logger.e("bm", "" + (bm == null));

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
                    IOUtils.copyInputStreamToFile(MainActivity.this, false, conn.getInputStream(), mFBId);
                } catch (MalformedURLException e) {
                    Logger.e("@MainActivity.DownloadProfilePic", e.getMessage());
                } catch (IOException e) {
                    Logger.e("@MainActivity.DownloadProfilePic", e.getMessage());
                }
            }

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
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
    }


}
