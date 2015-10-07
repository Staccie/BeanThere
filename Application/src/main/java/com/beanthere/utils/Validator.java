package com.beanthere.utils;

import android.util.Log;

/**
 * Created by staccie on 9/20/15.
 */
public class Validator {

    public static boolean isComplete(String... input) {

        for (int i= 0; i < input.length; i++) {
            if (input.length == 0 ) {
                Log.e("input", input[i] == null ? "null" : input[i]);
                return false;
            }
        }

        return true;
    }
}
