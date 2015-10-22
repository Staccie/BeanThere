package com.beanthere.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by staccie on 9/14/15.
 */
public class DateFormat {

    public static String inputToString(String input) {

        SimpleDateFormat parser = new SimpleDateFormat();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = "";

        try {

            Date date = parser.parse(input);
            str = formatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formatDateJoin(String strDate) {

        Logger.e("formatDateJoin", strDate);

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String str = "";

        try {

            Date date = parser.parse(strDate);
            str = formatter.format(date);

            Logger.e("formatDateJoin", str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}
