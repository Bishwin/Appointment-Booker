package com.w1441879.appointmentbooker;

import android.app.Activity;
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
import static com.w1441879.appointmentbooker.Constants.*;


public class ViewScreen extends Activity {

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
            addToListView();
            registerListClicker();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appointData.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addToListView();
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
                        long DBID = cursor.getLong(0);
                        String title = cursor.getString(COL_TITLE);
                        String time = cursor.getString(COL_TIME);
                        String descrip = cursor.getString(COL_DESCRIP);

                        Intent updateEvent = new Intent(ViewScreen.this, EditDialog.class);
                        updateEvent.putExtra("ID", DBID);
                        updateEvent.putExtra("date", date);
                        updateEvent.putExtra("title", title);
                        updateEvent.putExtra("time", time);
                        updateEvent.putExtra("description", descrip);
                        startActivity(updateEvent);

                        System.out.println(title);

                    cursor.close();
                }

            }
        });
    }



}
