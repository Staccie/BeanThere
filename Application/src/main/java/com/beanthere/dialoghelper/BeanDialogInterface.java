package com.beanthere.dialoghelper;

public interface BeanDialogInterface {

	interface OnPositiveClickListener {
		void onPositiveClick(String tag, int which);
	}

	interface OnNegativeClickListener {
		void onNegativeClick(String tag, int which);
	}

	interface OnInputDialogDismissListener {
		void onInputDialogDismiss(String tag, String data);
	}

	interface OnLocationDialogDismissListener {
		void onPositiveClick(String id, String comment);
		void onNegativeClick(String id);
	}

}
