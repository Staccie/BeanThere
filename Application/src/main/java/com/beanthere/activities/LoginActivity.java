package com.beanthere.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.objects.AppObject;
import com.beanthere.objects.User;
import com.beanthere.utils.Logger;
import com.beanthere.utils.Validator;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements BeanDialogInterface.OnProgressDialogCancelled {

    private LoginTask mLoginTask;
    private ForgotPasswordTask mForgotPasswordTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (AppObject.IS_DEV) {
            ((EditText) findViewById(R.id.editTextLoginEmail)).setText("ice@gmail.com");
            ((EditText) findViewById(R.id.editTextLoginPassword)).setText("123456");
        }

//        LayoutInflater mInflater = LayoutInflater.from(this);
//        View mCustomView = mInflater.inflate(R.layout.actionbar, null);
//        getActionBar().setCustomView(mCustomView);

    }

    public void onClickLogin(View view) {
        String email = ((EditText) findViewById(R.id.editTextLoginEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.editTextLoginPassword)).getText().toString().trim();

        if (Validator.isComplete(email, password)) {
            mLoginTask = new LoginTask();
            mLoginTask.execute(email, password);
        } else {
            DialogHelper.showErrorDialog(LoginActivity.this, getString(R.string.error_fill_in_all));
        }
    }

    public void onClickForgotPassword(View view) {

        String email = ((TextView) findViewById(R.id.editTextLoginEmail)).getText().toString().trim();

        if (email.isEmpty()) {
            DialogHelper.showErrorDialog(LoginActivity.this, getString(R.string.fill_in_email));
        } else {
            mForgotPasswordTask = new ForgotPasswordTask();
            mForgotPasswordTask.execute(email);
        }
    }

    private void processLogin(User user) {

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
        editor.putInt("logintype", 1);
        editor.putBoolean("checkLocation", true);

        // TODO encrypt
        String p = ((TextView) findViewById(R.id.editTextLoginPassword)).getText().toString().trim();
        editor.putString("p", p);

        editor.commit();

        Intent intent = new Intent(this, CafeListActivity.class);
        startActivity(intent);
        finish();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(LoginActivity.this, "progresslogin", "Authenticating...");
        }

        @Override
        protected String doInBackground(String... params) {

            Logger.e("LoginActivity.LoginTask", "doInBackground");
            return new HttpHandler().login(params[0], params[1], 1);

        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            // TODO stop HttpURLConnection
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (!isCancelled() && !isFinishing()) {

                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(LoginActivity.this);
//                    showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                } else {

                    try {
                        Gson gson = new Gson();
                        User user = gson.fromJson(response, User.class);

                        if (user.error) {
                            DialogHelper.showErrorDialog(LoginActivity.this, user.error_message);
//                        FragmentManager fm = getFragmentManager();
//                        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), user.error_message, "");
//                        noticeDialog.show(fm, "");
                        } else {
                            processLogin(user);
                        }
                    } catch (JsonSyntaxException e) {
                        Logger.e("@LoginActivity.LoginTask", e.getMessage());
                        DialogHelper.showInvalidServerResponse(LoginActivity.this);
                    }
                }
            }
        }
    }

    private class ForgotPasswordTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(LoginActivity.this, "progressforgotpassword", getString(R.string.please_wait));
        }

        @Override
        protected String doInBackground(String... params) {
            Logger.e("ForgotPassword", "doInBackground");
            return new HttpHandler().forgotpassword(SharedPreferencesManager.getAPIKey(LoginActivity.this), params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (!isCancelled() && !isFinishing()) {
                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(LoginActivity.this);
//                    showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
                } else {

                    JSONObject obj;

                    try {
                        obj = new JSONObject(response);
                        String message = obj.optString("message", getString(R.string.unknown_response));
                        DialogHelper.showErrorDialog(LoginActivity.this, message);

                    } catch (JSONException e) {
                        Logger.e("@LoginActivity.ForgotPasswordTask", e.getMessage());
                        DialogHelper.showInvalidServerResponse(LoginActivity.this);
                    }
                }
            }
        }
    }

    @Override
    public void onProgressDialogCancelled(String tag) {
        if (tag.equals("progresslogin")) {
            if (mLoginTask != null) {
                mLoginTask.cancel(true);
            }
        } else if (tag.equals("progressforgotpassword")) {
            if (mForgotPasswordTask != null) {
                mForgotPasswordTask.cancel(true);
            }
        }
    }

}
