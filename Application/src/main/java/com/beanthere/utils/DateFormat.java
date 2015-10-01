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

}
