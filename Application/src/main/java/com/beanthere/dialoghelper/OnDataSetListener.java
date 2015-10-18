package com.beanthere.dialoghelper;

/**
 * Created by staccie on 9/26/15.
 */
public interface OnDataSetListener {

    /** Listen when data is selected from checkbox, dropdown, radio button */
    // void onListItemSet(long fieldId, String ids, String values);

    void onDateSet(long fieldId, String date);

    void onTimeSet(long fieldId, String time);

    // void onTemplateChosen(long coreId, String title);

}
