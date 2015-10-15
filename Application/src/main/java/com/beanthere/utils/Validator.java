package com.beanthere.utils;

import android.util.Log;

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
                Log.e("input", input[i] == null ? "null" : input[i]);
                return false;
            }
        }

        return true;
    }
}
