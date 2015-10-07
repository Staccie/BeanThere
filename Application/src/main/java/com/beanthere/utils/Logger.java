package com.beanthere.utils;

import android.util.Log;

/**
 * Created by staccie on 10/5/15.
 */
public class Logger {

    public static boolean DEBUG_MODE = true;

    public static void d(String tag, String msg) {
        if (DEBUG_MODE) Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG_MODE) Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG_MODE) Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG_MODE) Log.v(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG_MODE) Log.w(tag, msg);
    }

    public static void logfile(String tag, String msg) {
        if (DEBUG_MODE) Log.v(tag, msg);


    }
}
