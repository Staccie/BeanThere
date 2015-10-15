package com.beanthere.dialoghelper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by staccie
 */
public class BeanProgressDialog extends DialogFragment {


    public static BeanProgressDialog newInstance(String message) {
        BeanProgressDialog dialog = new BeanProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        String message = getArguments().getString("message");
        if (message != null) dialog.setMessage(message);

        return dialog;
    }
}
