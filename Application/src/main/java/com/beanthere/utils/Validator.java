package com.beanthere.utils;

/**
 * Created by staccie on 9/20/15.
 */
public class Validator {

    public static boolean isComplete(String... input) {

        for (int i= 0; i < input.length; i++) {
            if (input.length == 0 ) {
                return false;
            }
        }

        return true;
    }
}
