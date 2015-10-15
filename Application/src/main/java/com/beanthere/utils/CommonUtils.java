package com.beanthere.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.beanthere.objects.AppObject;

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

    public static void setScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        AppObject.screenWidth = size.x;
    }
}
