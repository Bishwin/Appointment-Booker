package com.w1441879.appointmentbooker;

import static android.provider.BaseColumns._ID;
import static com.w1441879.appointmentbooker.Constants.*;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppointmentData extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "appointment.db";
    private static final int DATABASE_VERSION = 1;

    public AppointmentData(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("CREATING DB");
        db.execSQL("CREATE TABLE " + C_TABLE_NAME + "(" +_ID + " INTEGER PRIMARY KEY, "
                + C_TITLE + " TEXT NOT NULL, " + C_TIME
                + " TEXT, " + C_DATE + " TEXT, " + C_DESCRIP + " TEXT, "
                + "UNIQUE(" + C_DATE + ", " + C_TITLE + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + C_TABLE_NAME);
        onCreate(db);
    }


}
