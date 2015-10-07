package com.beanthere.dialoghelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beanthere.R;

import org.w3c.dom.Text;

public class PromoInputDialog extends DialogFragment {

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getTag().equals("getpromo"))  {
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_promo_code, null);
        } else if (getTag().equals("redeempromo")) {
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_redeem_voucher, null);
        } else {
            // Temporarily set this as default
            this.view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_redeem_voucher, null);
        }

        final TextView tvRequired = (TextView) view.findViewById(R.id.tvRequired);

        builder.setView(view);

        final String voucherId = getArguments().getString("voucherId");

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
                    ((BeanDialogInterface.OnInputDialogDismissListener) getActivity()).onInputDialogDismiss(getTag(), voucherId + "," + data);
                    dismiss();
                }
            }
        });

        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return dialog;
    }

}
