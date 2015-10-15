package com.beanthere.dialoghelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.beanthere.R;

/**
 * Created by staccie
 */
public class DialogHelper {

    public static void showInvalidServerResponse(Activity activity, Context context) {
        FragmentManager fm = activity.getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(context.getString(R.string.error_title), context.getString(R.string.invalid_server_response), "");
        noticeDialog.show(fm, "");
    }
//
//    public void showNoticeDialog(Context context, String title, String message) {
//        FragmentManager fm = getFragmentManager();
//        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance(getString(R.string.app_name), message, neutralButton);
//        noticeDialog.show(fm, tag);
//    }
}
