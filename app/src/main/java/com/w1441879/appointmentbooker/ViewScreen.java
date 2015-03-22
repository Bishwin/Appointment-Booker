package com.w1441879.appointmentbooker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static android.provider.BaseColumns._ID;
import static com.w1441879.appointmentbooker.Constants.C_DATE;
import static com.w1441879.appointmentbooker.Constants.C_TABLE_NAME;
import static com.w1441879.appointmentbooker.Constants.C_TIME;
import static com.w1441879.appointmentbooker.Constants.C_TITLE;


public class ViewScreen extends Activity {

    AppointmentData appointData;
    ListView list;
    SimpleCursorAdapter mAdapter;
    TextView dateTitle;
    String date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        dateTitle = (TextView)findViewById(R.id.dateTitle);
        date= getIntent().getStringExtra("date");
        dateTitle.setText(date);



        appointData = new AppointmentData(this);

        try{
            Cursor cursor = getAppointments();
            addToListView(cursor);
        }finally {
            appointData.close();
        }
    }

    private Cursor getAppointments() {
        /* Perform a managed query. The Activity will
        handle closing and re-querying the cursor
        when needed. */

        String[] projection = {
          _ID, C_TIME, C_TITLE, C_DATE
        };
        String ORDER_BY = C_TIME + " ASC";
        System.out.println(date);
        String selection = C_DATE + "=?";

        String[] selectionArgs = {date};



        SQLiteDatabase db = appointData.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(C_TABLE_NAME, projection , selection,
                selectionArgs, null, null,
                ORDER_BY);

        return cursor;
    }

    private void addToListView(Cursor cursor){

        String[] fromColumns = {C_TIME, C_TITLE};
        int[] toViews = {R.id.resultTime, R.id.resultTitle};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_layout,
                cursor, fromColumns, toViews);

        list=(ListView)findViewById(R.id.listView);
        list.setAdapter(mAdapter);
    }

}
