package com.beanthere.dialoghelper;

/**
 * Created by staccie on 9/26/15.
 */
public interface OnDataSetListener {

    /** Listen when data is selected from checkbox, dropdown, radio button */
    public void onListItemSet(long fieldId, String ids, String values);

    public void onDateSet(long fieldId, String date);

    public void onTimeSet(long fieldId, String time);

    public void onTemplateChosen(long coreId, String title);

}
