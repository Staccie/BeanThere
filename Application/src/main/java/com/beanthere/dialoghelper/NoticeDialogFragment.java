package com.beanthere.dialoghelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * DialogFragment with only neutral button i.e. 'OK' button if not specified. No
 * Callback listen to the neutral button. Ideally used for notifying user of
 * their action status which really needs them to be aware of. Use Toast instead
 * if in the case where user attention may not important.
 * 
 * @author Staccie
 * 
 */
public class NoticeDialogFragment extends DialogFragment {

	public static int REGISTER_SUCCESS = 1;

	/**
	 * Returns a new instance of AlertDialogFragment
	 * 
	 * @param title
	 * @param message
	 * @param neutralButton
	 * @return
	 */
	public static NoticeDialogFragment newInstance(String title, String message, String neutralButton) {

		NoticeDialogFragment fragment = new NoticeDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putString("positiveButton", neutralButton);
		fragment.setArguments(args);
		return fragment;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = getArguments().getString("title");
		String message = getArguments().getString("message");
		String neutralButton = getArguments().getString("neutralButton") == null ? getString(android.R.string.ok) : getArguments().getString("neutralButton");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (title != null) builder.setTitle(title);
		if (message != null) builder.setMessage(message);

		builder.setNeutralButton(neutralButton, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (!getTag().isEmpty()) {
					((BeanDialogInterface.OnPositiveClickListener) getActivity()).onPositiveClick(getTag(), whichButton);
				}
			}
		});

		return builder.create();
	}
}
