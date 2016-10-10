package com.time_em.utils;

import android.widget.EditText;
import android.widget.TextView;


public class ValueSetUtils {



    public static void setValueTextView(TextView tv, String value)
    {
        if(value!=null && !value.equalsIgnoreCase("null")) {
            tv.setText(value);
        }else{
            tv.setText("");
            }

        }
    public static void setValueEditText(EditText ed, String value)
    {
        if(value!=null && !value.equalsIgnoreCase("null")) {
            ed.setText(value);
        }else{
            ed.setText("");
            }

        }
    public static void setEnableEditText(EditText ed)
    {
        ed.setEnabled(true);

        }
    public static void setDisableEditText(EditText ed)
    {
        ed.setEnabled(false);
        }
}
