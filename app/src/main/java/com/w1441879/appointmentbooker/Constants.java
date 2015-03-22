package com.w1441879.appointmentbooker;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns{
    public static final String C_TABLE_NAME = "appointment";
    //COLUMNS
    public static final String C_TITLE = "title";
    public static final String C_TIME = "time";
    public static final String C_DESCRIP = "description";
    public static final String C_DATE = "date";

    public static final int COL_TITLE = 1;
    public static final int COL_TIME = 2;
    public static final int COL_DESCRIP = 3;
    public static final int COL_DATE = 4;

    public static final String[] ALL_KEYS = {_ID, C_TITLE, C_TIME, C_DESCRIP, C_DATE};
    public static final String[] projection = {_ID, C_TIME, C_TITLE, C_DATE};
    public static final String WHERE = C_DATE + "=?";
    public static final String ORDER_BY_TIME = C_TIME + " ASC";


}
