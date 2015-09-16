package com.beanthere.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.beanthere.common.DateFormat;
import com.beanthere.http.Request;
import com.beanthere.navigationdrawer.R;

/**
 * Created by staccie on 9/14/15.
 */
public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void onClickRegister(View view) {

    }

    private void validate() {

        String email = ((EditText) findViewById(R.id.registerEmail)).getText().toString().trim();
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        String dob = DateFormat.inputToString(((EditText) findViewById(R.id.dob)).getText().toString());

        // TODO add TextWatcher or onchange listener for password fields
        // TODO do one-by-one checking

        if (email.length() * firstName.length() * lastName.length() * password.length() * confirmPassword.length() * dob.length() == 0) {

        } else if (firstName != lastName) {

        }

        new Register().execute(email, password, firstName, lastName, dob);
    }

    class Register extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("RetrieveFeedTask", "doInBackground");

            Request req = new Request();
            String response = req.sendRegisterRequest(params[0], params[1], params[2], params[3], params[4], "", "");

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // TODO dosuccess -- redirect
            // TODO dofail
        }
    }
}
