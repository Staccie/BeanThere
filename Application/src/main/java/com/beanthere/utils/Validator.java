package com.beanthere.utils;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by staccie on 9/20/15.
 */
public class Validator {

    public static boolean isComplete(String... input) {

        if (input == null || input.length == 0) {
            return false;
        }

        for (int i = 0; i < input.length; i++) {
            if (input[i] == null || input[i].isEmpty()) {
                Logger.e("input", input[i] == null ? "null" : input[i]);
                return false;
            }
        }

        return true;
    }

    /**
     * Validate all required EditText inputs and show error message if not filled in
     * @param parentView
     * @param fieldIds
     * @param messageIds
     * @return
     */
    public static boolean validateRequired(View parentView, int[] fieldIds, int[] messageIds) {

        if (fieldIds == null || fieldIds.length == 0 || messageIds == null || messageIds.length == 0 || fieldIds.length != messageIds.length) {
            return false;
        }

        boolean isComplete = true;

        for (int i = 0; i < fieldIds.length; i++) {

            if (fieldIds[i] != 0) {
                String input = ((EditText) parentView.findViewById(fieldIds[i])).getText().toString().trim();
                if (input.isEmpty()) {
                    parentView.findViewById(messageIds[i]).setVisibility(View.VISIBLE);
                    isComplete = false;
                }
            }
        }

        return isComplete;
    }

    /**
     * Hide all error message for validating required fields
     *
     * @param parentView
     * @param messageIds
     */
    public static void clearValidationMessage(View parentView, int[] messageIds) {

        for (int i = 0; i < messageIds.length; i++) {
            parentView.findViewById(messageIds[i]).setVisibility(View.GONE);
        }

    }


}
