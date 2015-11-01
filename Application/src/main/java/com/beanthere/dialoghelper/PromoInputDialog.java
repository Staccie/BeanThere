package com.beanthere.dialoghelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beanthere.R;

import org.w3c.dom.Text;

public class PromoInputDialog extends DialogFragment implements DialogInterface.OnCancelListener {

    private View view;

    /**
     * Returns a new instance of PromoInputDialog
     */
    public static PromoInputDialog newInstance(String voucherId) {

        PromoInputDialog dialog = new PromoInputDialog();
        Bundle args = new Bundle();
        args.putString("voucherId", voucherId);
        dialog.setArguments(args);
        return dialog;

    }

    public static PromoInputDialog newEmailInput(String email) {

        PromoInputDialog dialog = new PromoInputDialog();
        Bundle args = new Bundle();
        args.putString("email", email);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String tag = getTag() == null ? "" : getTag();

        final String voucherId = getArguments().getString("voucherId");
        final String email = getArguments().getString("email", "");

        if (tag.equals("getpromo"))  {
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_promo_code, null);
        } else if (tag.equals("redeempromo")) {
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_redeem_voucher, null);
        } else if (tag.equals("emailforfb")){
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_email, null);
            ((TextView) this.view.findViewById(R.id.editTextDialogInput)).setText(email);
        } else {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final TextView tvRequired = (TextView) view.findViewById(R.id.tvRequired);

        // Create positive button to dialog
        Button btnOk = (Button) view.findViewById(R.id.buttonOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = ((EditText) view.findViewById(R.id.editTextDialogInput)).getEditableText().toString().trim();
                if (data.isEmpty()) {
                    tvRequired.setVisibility(View.VISIBLE);
                } else {
                    tvRequired.setVisibility(View.GONE);
                    if (tag.equals("emailforfb")) {
                        ((BeanDialogInterface.OnInputDialogDismissListener) getActivity()).onInputDialogDismiss(getTag(), data);
                    } else {
                        ((BeanDialogInterface.OnInputDialogDismissListener) getActivity()).onInputDialogDismiss(getTag(), voucherId + "," + data);
                    }
                    dismiss();
                }
            }
        });

        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        ((BeanDialogInterface.OnInputDialogDismissListener) getActivity()).onInputDialogDismiss(getTag(), null);
    }
}
