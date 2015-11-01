package com.beanthere.activities;

import android.app.Activity;
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
import com.beanthere.dialoghelper.BeanProgressDialog;
import com.beanthere.dialoghelper.DialogHelper;
import com.beanthere.dialoghelper.NoticeDialogFragment;
import com.beanthere.objects.AppObject;
import com.beanthere.utils.Logger;
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
public class RegisterActivity extends BaseActivity
        implements OnDataSetListener,
        BeanDialogInterface.OnPositiveClickListener,
        BeanDialogInterface.OnProgressDialogCancelled {

    private RegisterTask mRegisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // TODO remove testing code
        if (AppObject.IS_DEV) {
            ((EditText) findViewById(R.id.registerEmail)).setText("ice@gmail.com");
            ((EditText) findViewById(R.id.firstName)).setText("ice");
            ((EditText) findViewById(R.id.lastName)).setText("ice");
            ((EditText) findViewById(R.id.password)).setText("123456");
            ((EditText) findViewById(R.id.confirmPassword)).setText("123456");
            ((TextView) findViewById(R.id.dob)).setText("1980-01-01");
        }

    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogHelper.showProgressDialog(RegisterActivity.this, "progressRegisterR", "");
        }

        @Override
        protected String doInBackground(String... params) {
            Logger.e("Register", "doInBackground");
            return new HttpHandler().register(SharedPreferencesManager.getAPIKey(RegisterActivity.this), params[0], params[1], params[2], params[3], params[4], "", "");
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            // TODO stop httpurlconnection
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (!isCancelled() && !isFinishing()) {
                if (response == null || response.isEmpty()) {
                    DialogHelper.showInvalidServerResponse(RegisterActivity.this);
                } else {

                    JSONObject obj;

                    try {
                        obj = new JSONObject(response);
                        boolean error = obj.optBoolean("error", true);
                        String message = obj.optString("message", getString(R.string.unknown_signup_error));

                        if (error) {
                            DialogHelper.showErrorDialog(RegisterActivity.this, message);
                        } else {
                            FragmentManager fm = getFragmentManager();
                            NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance("'", getString(R.string.success_register), null);
                            noticeDialog.show(fm, "successregister");
                        }

                    } catch (JSONException e) {
                        Logger.e("@RegisterActivity.RegisterTask", e.getMessage());
                        DialogHelper.showInvalidServerResponse(RegisterActivity.this);
                    }
                }
            }
        }
    }

    public void onClickRegister(View view) {

        String email = ((EditText) findViewById(R.id.registerEmail)).getText().toString().trim();
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        String dob = ((TextView) findViewById(R.id.dob)).getText().toString().trim();

        // TODO Validate using Validator.validateRequired() and Validator.clearValidationMessage()

        if (!Validator.isComplete(email, firstName, lastName, password, confirmPassword, dob)) {
            DialogHelper.showErrorDialog(this, getString(R.string.error_fill_in_all));
        } else if (!firstName.equals(lastName)) {
            DialogHelper.showErrorDialog(this, getString(R.string.password_not_match));
        } else {
            mRegisterTask = new RegisterTask();
            mRegisterTask.execute(email, password, firstName, lastName, dob);
        }
    }

    public void onclickDOBDate(View view) {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Date of Birth", "1980-01-01");
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
        if (tag.equals("successregister")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onProgressDialogCancelled(String tag) {
        if (tag.equals("progressRegisterR")) {
            if (mRegisterTask != null) {
                mRegisterTask.cancel(true);
            }
        }
    }
}
