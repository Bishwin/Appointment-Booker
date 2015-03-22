package com.w1441879.appointmentbooker;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static android.provider.BaseColumns._ID;
import static com.w1441879.appointmentbooker.Constants.ALL_KEYS;
import static com.w1441879.appointmentbooker.Constants.COL_TITLE;
import static com.w1441879.appointmentbooker.Constants.C_TABLE_NAME;
import static com.w1441879.appointmentbooker.Constants.C_TIME;
import static com.w1441879.appointmentbooker.Constants.C_TITLE;
import static com.w1441879.appointmentbooker.Constants.ORDER_BY_TIME;
import static com.w1441879.appointmentbooker.Constants.WHERE;
import static com.w1441879.appointmentbooker.Constants.projection;


public class DeleteScreen extends Activity{

    AppointmentData appointData;
    ListView list;
    SimpleCursorAdapter mAdapter;
    TextView dateTitle;
    String date;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        dateTitle = (TextView)findViewById(R.id.dateTitle);
        date= getIntent().getStringExtra("date");
        dateTitle.setText(date);

        appointData = new AppointmentData(this);
        //Cursor cursor = getAppointments();
        addToListView();
        registerListClicker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appointData.close();
    }

    private void addToListView(){

        String[] selectionArgs = {date};

        db = appointData.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(C_TABLE_NAME, projection , WHERE,
                selectionArgs, null, null,
                ORDER_BY_TIME);

        String[] fromColumns = {C_TIME, C_TITLE};
        int[] toViews = {R.id.resultTime, R.id.resultTitle};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_layout,
                cursor, fromColumns, toViews);

        list=(ListView)findViewById(R.id.listView);
        list.setAdapter(mAdapter);
    }

    private void registerListClicker(){
        list=(ListView)findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String WHERE = _ID + "=" + id;
                Cursor cursor = db.query(C_TABLE_NAME, ALL_KEYS,WHERE, null, null, null, null);
                if(cursor.moveToFirst()){
                    String title = cursor.getString(COL_TITLE);
                    long DBID = cursor.getLong(0);
                    deleteDialog(DBID, title);
                }
                cursor.close();
            }
        });

    }

    private void deleteDialog(final long id, String title){

        final String selection = _ID + "=?";
        final String[] selectionArgs = { String.valueOf(id) };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete '" + title + "' ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.delete(C_TABLE_NAME, selection , selectionArgs);
                addToListView();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }


}
