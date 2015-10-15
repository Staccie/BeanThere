package com.beanthere.utils;

/**
 * Created by staccie
 */
public class CommonUtils {

    public static String getDistance(String distance) {

        String result = "";

        if (distance == null || distance.trim() == "") {
            return "";
        } else {

            int index = distance.indexOf(".");
            if (index != -1) {
                index = ((index + 2) > distance.length()) ? distance.length() : index + 2;
                result = distance.substring(0, index);
            }
        }
        return result;
    }

    /** Only positive number */
    public static String zerofy(int number, int digit) {
        String result = String.valueOf(number);

        while (result.length() < digit) {
            result = "0".concat(result);
        }
        return result;
    }
}
