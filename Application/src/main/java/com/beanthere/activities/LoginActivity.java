package com.beanthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.objects.User;
import com.beanthere.utils.Validator;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        // TODO Remove test data
        ((TextView) findViewById(R.id.editTextLoginEmail)).setText("ice@gmail.com");
        ((TextView) findViewById(R.id.editTextLoginPassword)).setText("123456");
    }

    public void onClickLogin(View view) {
        String email = ((TextView) findViewById(R.id.editTextLoginEmail)).getText().toString().trim();
        String password = ((TextView) findViewById(R.id.editTextLoginPassword)).getText().toString().trim();

        if (Validator.isComplete(email, password)) {
            new Login().execute(email, password);
        } else {
            FragmentManager fm = getFragmentManager();
            NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), getString(R.string.error_fill_in_all), "");
            noticeDialog.show(fm, "");
        }
    }

    public void onClickForgotPassword(View view) {

        String email = ((TextView) findViewById(R.id.editTextLoginEmail)).getText().toString().trim();

        if (Validator.isComplete(email)) {
            new ForgotPassword().execute(email);
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
        editor.putString("dob", user.dob);
        editor.putString("fb_user_id", user.fb_user_id);
        editor.putString("fb_auth_token", user.fb_auth_token);
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

            if (response == null && response.isEmpty()) {
                // TODO handleRequestFail
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

    class ForgotPassword extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("ForgotPassword", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.forgotpassword(params[0]);

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

            if (response == null && response.isEmpty()) {
                // TODO handleRequestFail
            } else {

                JSONObject obj = null;
                boolean error = true;
                String message = "";
//
                try {
                    obj = new JSONObject(response);

                    error = obj.optBoolean("error", true);
                    message = obj.optString("message", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO handleDataFail
                }

                FragmentManager fm = getFragmentManager();
                NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, "");
                noticeDialog.show(fm, "");
            }
        }
    }

}
