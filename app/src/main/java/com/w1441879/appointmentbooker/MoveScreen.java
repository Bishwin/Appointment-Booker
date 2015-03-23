package com.w1441879.appointmentbooker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.provider.BaseColumns._ID;
import static com.w1441879.appointmentbooker.Constants.*;

public class MoveScreen extends Activity {

    AppointmentData appointData;
    ListView list;
    SimpleCursorAdapter mAdapter;
    TextView dateTitle;
    String date, newDate, title;
    SQLiteDatabase db;
    long DBID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        dateTitle = (TextView)findViewById(R.id.dateTitle);
        date= getIntent().getStringExtra("date");
        dateTitle.setText(date);

        appointData = new AppointmentData(this);
        addToListView();
        registerListClicker();
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
                Cursor cursor = db.query(C_TABLE_NAME, ALL_KEYS, WHERE, null, null, null, null);
                if (cursor.moveToFirst()) {
                    DBID = cursor.getLong(0);
                    title = cursor.getString(COL_TITLE);
                    setDate();
                    //updateDate(DBID);
                    cursor.close();
                }

            }
        });
    }

    public void setDate(){

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(MoveScreen.this, new DatePickerDialog.OnDateSetListener(){

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear+1)+ "/" + String.format("%02d", year);
                System.out.println("NEW DATE: " + newDate);
                boolean successful = false;
                try {
                    updateDate(DBID);
                    successful=true;
                } catch (SQLiteConstraintException e) {
                    showDialog(title);

                    //System.out.println("ERROR");
                }
                if(successful) {
                    Toast.makeText(getApplicationContext(), "EVENT MOVED", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        },year,month ,day);
        datePicker.setTitle("Select Time");
        datePicker.show();
    }

    private void updateDate(long id){
        System.out.println("UPDATE");
        SQLiteDatabase db = appointData.getWritableDatabase();

        final String selection = _ID + "=?";
        final String[] selectionArgs = { String.valueOf(id) };

        ContentValues values = new ContentValues();
            values.put(C_DATE, newDate);
        db.update(C_TABLE_NAME,values,selection,selectionArgs);
        addToListView();
    }

    private void showDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        if(title.matches("")){
            builder.setMessage("Please enter a Title");
        } else {
            builder.setMessage("Appointment '" + title + "' already exists, please enter a different title");
        }
        AlertDialog alert = builder.create();
        alert.show();
    }



}
