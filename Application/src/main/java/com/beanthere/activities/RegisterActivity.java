package com.beanthere.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.utils.Validator;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.DatePickerFragment;
import com.beanthere.dialoghelper.OnDataSetListener;
import com.beanthere.webservice.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by staccie
 */
public class RegisterActivity extends BaseActivity implements OnDataSetListener, BeanDialogInterface.OnPositiveClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // TODO remove testing code
        ((EditText) findViewById(R.id.registerEmail)).setText("ice@gmail.com");
        ((EditText) findViewById(R.id.firstName)).setText("ice");
        ((EditText) findViewById(R.id.lastName)).setText("ice");
        ((EditText) findViewById(R.id.password)).setText("123456");
        ((EditText) findViewById(R.id.confirmPassword)).setText("123456");
        ((TextView) findViewById(R.id.dob)).setText("1980-01-01");

    }

    public void onClickRegister(View view) {
        validate();
    }

    private void validate() {

        String email = ((EditText) findViewById(R.id.registerEmail)).getText().toString().trim();
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        String dob = ((TextView) findViewById(R.id.dob)).getText().toString().trim();

        // TODO add TextWatcher or onchange listener for password fields
        // TODO do one-by-one checking show tooltip if not match

        if (!Validator.isComplete(email, firstName, lastName, password, confirmPassword, dob)) {
            showNoticeDialog("", getString(R.string.error_title), getString(R.string.error_fill_in_all), null);
        } else if (!firstName.equals(lastName)) {
            showNoticeDialog("", getString(R.string.error_title), "Password not match.", null);
        } else {
            new Register().execute(email, password, firstName, lastName, dob);
        }
    }

    public void onclickDOBDate(View view) {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Date of Birth", "1980-01-01");
        datePickerFragment.show(fm, "datePickerFragment");
    }

    @Override
    public void onListItemSet(long fieldId, String ids, String values) {

    }

    @Override
    public void onDateSet(long fieldId, String date) {
        ((TextView) findViewById(R.id.dob)).setText(date);
    }

    @Override
    public void onTimeSet(long fieldId, String time) {

    }

    @Override
    public void onTemplateChosen(long coreId, String title) {

    }

    @Override
    public void onPositiveClick(String tag, int which) {
        if (tag.equals("successlogin")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    class Register extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("Register", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.register(SharedPreferencesManager.getAPIKey(RegisterActivity.this), params[0], params[1], params[2], params[3], params[4], "", "");

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
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), null);
            } else {

                JSONObject obj;
                boolean error = true;
                String message = "";

                try {
                    obj = new JSONObject(response);

                     error = obj.optBoolean("error", true);
                     message = obj.optString("message", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                    showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), null);
                }

                if (error) {
                    showNoticeDialog("", getString(R.string.error_title), message, null);
                } else {
                    showNoticeDialog("successlogin", "Success", "Account successfully created. Please login to continue.", null);
                }
            }
        }
    }
}
