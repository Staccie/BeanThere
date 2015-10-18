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
import com.beanthere.dialoghelper.PromoInputDialog;
import com.beanthere.objects.AppObject;
import com.beanthere.objects.User;
import com.beanthere.utils.CommonUtils;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Launcher activity
 */
public class MainActivity extends Activity implements BeanDialogInterface.OnInputDialogDismissListener {

    CallbackManager callbackManager;

    private String mFBEmail;
    private String mFBFirstName;
    private String mFBLastName;
    private String mFBId;
    private String mFBToken;

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
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    DialogHelper.showErrorDialog(MainActivity.this, response.getError().getErrorMessage());
                                } else {
                                    registerLoginFBUser(token.toString(), json);
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

            String email = SharedPreferencesManager.getString(this, "email");
            String pw = SharedPreferencesManager.getString(this, "p");

            if (loginType == 1) {
                if (!email.isEmpty() && !pw.isEmpty()) {
                    new LoginTask().execute("1", email, pw);
                }
            }
//        else if (loginType == 2) {
//            if (email.isEmpty()) {
            // Prompt for email
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
//            }
//        }
        }

    }

    private void registerLoginFBUser(String accessToken, JSONObject json) {

        String jsonresult = String.valueOf(json);
        Log.e("JSON Result", jsonresult);

        String email = json.optString("email", "");
        mFBId = json.optString("id");
        mFBFirstName = json.optString("name", email);
        mFBLastName = json.optString("last_name", "last name");
        mFBToken = accessToken;

        SharedPreferences sp = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fb_user_id", mFBId);
        editor.putString("fb_auth_token", accessToken);
        editor.putString("fbname", mFBFirstName);
        editor.commit();

        int loginType = SharedPreferencesManager.getInt(this, "logintype");
        if (loginType != 1) {

            if (SharedPreferencesManager.getString(this, "email").isEmpty()) {
                // Prompt for email input;
                FragmentManager fm = getFragmentManager();
                PromoInputDialog inputDialog = new PromoInputDialog().newEmailInput(email);
                inputDialog.show(fm, "emailforfb");
            } else {
                // Automaically login for FB user who has registered and logged into app before
                new LoginTask().execute(SharedPreferencesManager.getString(this, "email"), SharedPreferencesManager.getString(this, "p"));
            }
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
        editor.putBoolean("checkLocation", true);
        editor.commit();

        Intent intent = new Intent(this, CafeListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onInputDialogDismiss(String tag, String data) {
        if (tag.equals("emailforfb")) {
            if (!data.isEmpty()) {
                mFBEmail  = data;
                new RegisterTask().execute();
            }
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response == null || response.isEmpty()) {
                DialogHelper.showInvalidServerResponse(MainActivity.this);
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
            } else {
                // TODO register response should return error code, if account existed or registered successful then only login
                // Try login anyway until we get a meaningful code from server
                new LoginTask().execute();

            }
        }
    }

    // TODO merge with LoginActivity
    private class LoginTask extends AsyncTask<String, Void, String> {

        private int mLoginType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // TODO Show progress dialog
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
            // Cancel login
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (!isCancelled() && !isFinishing()) {

                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(MainActivity.this);
//                FragmentManager fm = getFragmentManager();
//                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.invalid_server_response), "");
//                noticeDialog.show(fm, "");
                } else {

                    Gson gson = new Gson();
                    User user = gson.fromJson(response, User.class);

                    if (user.error) {
                        DialogHelper.showErrorDialog(MainActivity.this, user.error_message);
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

                URL image_value;

                try {
                    image_value = new URL("http://graph.facebook.com/" + mFBId + "/picture?type=large");
                    Logger.e("image_value", image_value.toString());
                    bm = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
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
//                    IOUtils.copyInputStreamToFile(MainActivity.this, false, is, "fbprofilepic");
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
