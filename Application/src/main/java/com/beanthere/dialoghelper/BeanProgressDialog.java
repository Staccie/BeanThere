package com.beanthere.dialoghelper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by staccie
 */
public class BeanProgressDialog extends DialogFragment implements Dialog.OnCancelListener {

//    private View view;

    public static BeanProgressDialog show(String message) {
        BeanProgressDialog dialog = new BeanProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(view);

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        String message = getArguments().getString("message");
        if (message != null) dialog.setMessage(message);

//        Dialog dialog = builder.create();
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        ((BeanDialogInterface.OnProgressDialogCancelled) getActivity()).onProgressDialogCancelled(getTag());
    }
}
