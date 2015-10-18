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
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.dialoghelper.OnDataSetListener;
import com.beanthere.objects.GeneralResponse;
import com.beanthere.utils.Logger;
import com.beanthere.utils.Validator;
import com.beanthere.webservice.HttpHandler;
import com.google.gson.Gson;

public class ProfileActivity extends NavDrawerActivity
        implements OnDataSetListener, BeanDialogInterface.OnPositiveClickListener,
                    BeanDialogInterface.OnProgressDialogCancelled {

    private int mLoginType;
    private String dob;
    private UpdateProfileTask mUpdateProfileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_profile, null, false);
        frameLayout.addView(view);

        // Populate user details
        ((EditText) findViewById(R.id.firstName)).setText(SharedPreferencesManager.getString(this, "first_name"));
        ((EditText) findViewById(R.id.lastName)).setText(SharedPreferencesManager.getString(this, "last_name"));
        dob = SharedPreferencesManager.getString(this, "dob");
        ((TextView) findViewById(R.id.dob)).setText(dob);

        mLoginType = SharedPreferencesManager.getInt(this, "logintype");

        if (mLoginType == 2) {
            findViewById(R.id.password).setVisibility(View.GONE);
            findViewById(R.id.confirmPassword).setVisibility(View.GONE);
        } else {
            ((EditText) findViewById(R.id.password)).setText(SharedPreferencesManager.getString(this, "p"));
            ((EditText) findViewById(R.id.confirmPassword)).setText(SharedPreferencesManager.getString(this, "p"));
        }

    }


    public void onClickUpdateProfile(View view) {
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        String dob = ((TextView) findViewById(R.id.dob)).getText().toString().trim();

        // TODO add TextWatcher or onchange listener for password fields
        // TODO do one-by-one checking show tooltip if not match
        Logger.e("validate", "profileactivity");
//Log.e("", firstName + "," + lastName + "," + password + "," + confirmPassword + "," + dob);
        if (mLoginType == 1) {
            if (!Validator.isComplete(firstName, lastName, password, confirmPassword, dob)) {
                showNoticeDialog("", getString(R.string.error_title), getString(R.string.error_fill_in_all), null);
            } else if (!firstName.equals(lastName)) {
                showNoticeDialog("", getString(R.string.error_title), "Password not match.", null);
            } else {
                mUpdateProfileTask = new UpdateProfileTask();
                mUpdateProfileTask.execute(SharedPreferencesManager.getAPIKey(this), SharedPreferencesManager.getString(this, "u_id"), SharedPreferencesManager.getString(this, "email"), password, firstName, lastName, dob);
            }
        } else {
            if (!Validator.isComplete(firstName, lastName, dob)) {
                showNoticeDialog("", "", getString(R.string.error_fill_in_all), null);
            } else {
                mUpdateProfileTask = new UpdateProfileTask();
                mUpdateProfileTask.execute(SharedPreferencesManager.getAPIKey(this),
                        SharedPreferencesManager.getString(this, "u_id"),
                        SharedPreferencesManager.getString(this, "email"),
                        SharedPreferencesManager.getString(this, "p"),
                        firstName, lastName, dob);
            }
        }
    }

    public void onclickDOBDate(View view) {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Date of Birth", dob);
        datePickerFragment.show(fm, "datePickerFragment");
    }

    @Override
    public void onDateSet(long fieldId, String date) {
        ((TextView) findViewById(R.id.dob)).setText(date);
    }

    @Override
    public void onTimeSet(long fieldId, String time) {

    }

    @Override
    public void onPositiveClick(String tag, int which) {
        if (tag.equals("successupdateprofile")) {
            logout();
        }
    }

    @Override
    public void onProgressDialogCancelled(String tag) {
        if (tag.equals("updateprofile")) {
            if (mUpdateProfileTask != null) {
                mUpdateProfileTask.cancel(true);
            }

        }
    }

    private class UpdateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(ProfileActivity.this, "updateprofile", "Updating...");
        }

        @Override
        protected String doInBackground(String... params) {
            Logger.e("@RegisterActivity.UpdateProfileTask", "doInBackground");
            return new HttpHandler().updateProfile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], "", "");
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (!isCancelled() && !isFinishing()) {
                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(ProfileActivity.this);
                } else {

                    GeneralResponse res = new Gson().fromJson(response, GeneralResponse.class);

                    if (res.error) {
                        DialogHelper.showErrorDialog(ProfileActivity.this, res.error_message);
                    } else {
                        showNoticeDialog("successupdateprofile", getString(R.string.error_title), getString(R.string.update_profile_success), null);
                    }
                }
            }
        }
    }

}
