package com.beanthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.beanthere.R;
import com.beanthere.dialoghelper.NoticeDialogFragment;

/**
 * Created by staccie on 9/26/15.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showNoticeDialog(String title, String message, String neutralButton) {
        FragmentManager fm = getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
        noticeDialog.show(fm, "noticeDialog");
    }
}
