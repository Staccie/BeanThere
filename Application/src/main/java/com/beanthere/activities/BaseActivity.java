package com.beanthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.beanthere.R;
import com.beanthere.dialoghelper.NoticeDialogFragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by staccie on 9/26/15.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showNoticeDialog(String tag, String title, String message, String neutralButton) {
        FragmentManager fm = getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
        noticeDialog.show(fm, tag);
    }

    // TODO use this instead
    protected void showNoticeDialog(Activity activity, String tag, String title, String message, String neutralButton) {
        FragmentManager fm = activity.getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
        noticeDialog.show(fm, tag);
    }

    // TODO remove all redundant and use this
    protected void showInvalidResponseDialog(Activity activity) {
        showNoticeDialog(activity, "", getString(R.string.error_title), getString(R.string.invalid_server_response), "");
    }

}
