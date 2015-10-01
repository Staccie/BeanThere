package com.beanthere.dialoghelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.beanthere.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by staccie on 9/26/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static DatePickerFragment newInstance(String title, String defaultDate) {

        DatePickerFragment dialog = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("defaultDate", defaultDate);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String defaultDate = bundle.getString("defaultDate");

        Calendar c = Calendar.getInstance();

        if (!(defaultDate == null || defaultDate.isEmpty())) {
            SimpleDateFormat sdf_parse = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf_parse.parse(defaultDate);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Use the current date as the default date in the picker
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
//        dialog.setTitle(getArguments().getString("title"));
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        ((OnDataSetListener) getActivity()).onDateSet(-1, year + "-" + CommonUtils.zerofy(month + 1, 2) + "-" + CommonUtils.zerofy(day, 2));
    }

}
