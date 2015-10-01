package com.beanthere.dialoghelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.beanthere.R;

public class ConfirmationDialog extends DialogFragment implements DialogInterface.OnClickListener {

	/**
	 * Returns a new instance of ConfirmationDialog
	 * 
	 * @param title
	 * @param message
	 * @return
	 */
	public static ConfirmationDialog newInstance(String title, String message) {

		ConfirmationDialog dialog = new ConfirmationDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		dialog.setArguments(args);
		return dialog;

	}

	public static ConfirmationDialog newInstance(String title, String message, String positiveButton, String negativeButton, String neutralButton) {

		ConfirmationDialog dialog = new ConfirmationDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putString("negativeButton", negativeButton);
		args.putString("positiveButton", positiveButton);
		args.putString("neutralButton", neutralButton);
		dialog.setArguments(args);
		return dialog;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Set dialog title if there is
		String arg = getArguments().getString("title");
		if (arg != null) builder.setTitle(arg);

		// Set dialog message if there is
		arg = getArguments().getString("message");
		if (arg != null) builder.setMessage(arg);

		// Get positive button text. If not set use default 'Yes'
		arg = getArguments().getString("positiveButton") == null ? getString(R.string.yes) : getArguments().getString("positiveButton");
		// Create positive button to dialog
		builder.setPositiveButton(arg, this);

		// Get negative button text. If not set use default 'No'
		arg = getArguments().getString("negativeButton") == null ? getString(R.string.no) : getArguments().getString("negativeButton");
		// Create negative button to dialog
		builder.setNegativeButton(arg, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((BeanDialogInterface.OnNegativeClickListener) getActivity()).onNegativeClick(getTag(), which);
			}
		});

		// Get negative button text. If not set use default 'No'
		arg = getArguments().getString("neutralButton");
		if (arg != null) {
			builder.setNeutralButton(arg, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
		}

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		((BeanDialogInterface.OnPositiveClickListener) getActivity()).onPositiveClick(getTag(), which);
	}

}
