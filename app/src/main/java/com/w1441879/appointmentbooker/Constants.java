package com.w1441879.appointmentbooker;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns{
    public static final String C_TABLE_NAME = "appointment";
    //COLUMNS
    public static final String C_TITLE = "title";
    public static final String C_TIME = "time";
    public static final String C_DESCRIP = "description";
    public static final String C_DATE = "date";
}
