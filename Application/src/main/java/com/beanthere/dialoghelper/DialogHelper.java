package com.beanthere.dialoghelper;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;

import com.beanthere.R;

/**
 * Created by staccie
 */
public class DialogHelper {

    public static void showInvalidServerResponse(Context context) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance("", context.getString(R.string.invalid_server_response), "");
        noticeDialog.show(fm, "");
    }

    public static void showErrorDialog(Context context, String message) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance("", message, "");
        noticeDialog.show(fm, "");
    }

    public static void showConnectionTimeout(Context context) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
        NoticeDialogFragment noticeDialog = NoticeDialogFragment.newInstance("", context.getString(R.string.connection_timeout), "");
        noticeDialog.show(fm, "");
    }

    public static void showProgressDialog(Context context, String tag, String message) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
        BeanProgressDialog progressDialog = BeanProgressDialog.show(message);
        progressDialog.show(fm, tag);
    }

    public static void dismissProgressDialog(Context context, String tag) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
        DialogFragment dialogFragment = (DialogFragment) fm.findFragmentByTag(tag);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }
}
