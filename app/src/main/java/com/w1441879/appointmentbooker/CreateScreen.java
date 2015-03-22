package com.w1441879.appointmentbooker;

import android.app.Activity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.w1441879.appointmentbooker.Constants.C_DATE;
import static com.w1441879.appointmentbooker.Constants.C_TABLE_NAME;
import static com.w1441879.appointmentbooker.Constants.C_TIME;
import static com.w1441879.appointmentbooker.Constants.C_TITLE;


import java.util.Calendar;

public class CreateScreen extends Activity{

    TextView timeField,dateField;
    Button save;
    AppointmentData appointData;
    String time, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        appointData = new AppointmentData(this);

        dateField = (TextView)findViewById(R.id.dateTitle);
        date= getIntent().getStringExtra("date");
        dateField.setText(date);

        setTime();

        save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    addAppointment();
                    Toast.makeText(getApplicationContext(), "EVENT CREATED", Toast.LENGTH_SHORT).show();
                } finally{
                    appointData.close();
                    finish();
                }
            }
        });
    }

    private void addAppointment() {
        /* Insert a new record into the Events data
        source. You would do something similar
        for delete and update. */
        EditText titleInput = (EditText) findViewById(R.id.editTitle);
        String title = titleInput.getText().toString();

        SQLiteDatabase db = appointData.getWritableDatabase();

        ContentValues values = new ContentValues();
            values.put(C_TIME, time);
            values.put(C_TITLE, title);
            values.put(C_DATE, date);
        db.insertOrThrow(C_TABLE_NAME, null, values);
    }


    public void setTime(){
        timeField = (TextView) findViewById(R.id.editTime);

        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        time = String.format("%02d", hour) + ":" + String.format("%02d", minute);
        timeField.setText(time);

        timeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(CreateScreen.this, new TimePickerDialog.OnTimeSetListener() {

                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        timeField.setText(time);
                    }

                }, hour, minute, true);//Yes 24 hour time
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }






}
