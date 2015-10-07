package com.beanthere.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beanthere.R;
import com.beanthere.data.SharedPreferencesManager;
import com.beanthere.dialoghelper.BeanDialogInterface;
import com.beanthere.dialoghelper.DatePickerFragment;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.dialoghelper.OnDataSetListener;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.objects.User;
import com.beanthere.utils.Validator;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends NavDrawerActivity implements OnDataSetListener, BeanDialogInterface.OnPositiveClickListener {

    private int mLoginType;
    private String dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_profile, null,false);
        frameLayout.addView(view);

        // TODO Populate user details
        ((EditText) findViewById(R.id.firstName)).setText(SharedPreferencesManager.getString(this, "first_name"));
        ((EditText) findViewById(R.id.lastName)).setText(SharedPreferencesManager.getString(this, "last_name"));
        ((EditText) findViewById(R.id.password)).setText(SharedPreferencesManager.getString(this, "p"));
        ((EditText) findViewById(R.id.confirmPassword)).setText(SharedPreferencesManager.getString(this, "p"));
        dob = SharedPreferencesManager.getString(this, "dob");
        ((TextView) findViewById(R.id.dob)).setText(dob);

        mLoginType = SharedPreferencesManager.getInt(this, "logintype");

    }


    public void onClickUpdateProfile(View view) {
        validate();
    }

    private void validate() {

        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        String dob = ((TextView) findViewById(R.id.dob)).getText().toString().trim();

        // TODO add TextWatcher or onchange listener for password fields
        // TODO do one-by-one checking show tooltip if not match
        Log.e("validate", "profileactivity");
//Log.e("", firstName + "," + lastName + "," + password + "," + confirmPassword + "," + dob);
        if (!Validator.isComplete(firstName, lastName, password, confirmPassword, dob)) {
            showNoticeDialog("", getString(R.string.error_title), getString(R.string.error_fill_in_all), null);
        } else if (!firstName.equals(lastName)) {
            showNoticeDialog("", getString(R.string.error_title), "Password not match.", null);
        } else {
            new UpdateProfileTask().execute("", password, firstName, lastName, dob);
        }
    }

    public void onclickDOBDate(View view) {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Date of Birth", dob);
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
        if (tag.equals("successupdateprofile")) {
            logout();
        }
    }

    class UpdateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("Register", "doInBackground");

            HttpHandler req = new HttpHandler();
            String response = req.updateProfile(params[0], params[1], params[2], params[3], params[4], "", "");

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
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.invalid_server_response), null);
            } else {

                Gson gson = new Gson();
                GeneralResponse res = gson.fromJson(response, GeneralResponse.class);

                if (res.error) {
                    showNoticeDialog("", getString(R.string.error_title), res.error_message, null);
                } else {
                    showNoticeDialog("successupdateprofile", getString(R.string.error_title), "Profile updated successfully. Please login again to continue", null);
                }
            }
        }
    }

}
