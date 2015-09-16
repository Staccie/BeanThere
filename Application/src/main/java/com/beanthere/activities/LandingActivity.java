package com.beanthere.activities;

import android.app.Activity;
import android.os.Bundle;

import com.beanthere.navigationdrawer.R;
import com.facebook.FacebookSdk;


public class LandingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_splash);

    }

//    public void onClickLoginEmail(View view) {
//        startActivity( new Intent(this, LoginActivity.class) );
//    }
//
//    public void onClickSignUp(View view) {
//        startActivity( new Intent(this, RegisterActivity.class) );
//    }
}
