package com.w1441879.appointmentbooker;

import android.app.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;

import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CreateAppointment extends Activity{

    TextView timeField,dateField;
    String d = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        dateField = (TextView)findViewById(R.id.dateTitle);
        String date= getIntent().getStringExtra("date");
        dateField.setText(date);
        setTime();
    }

    public void setTime(){
        timeField = (TextView) findViewById(R.id.editTime);
            /*Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            timeField.setText(sdf.format(date).toString());*/

        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        timeField.setText(new StringBuilder().append(String.format("%02d",hour))
                .append(":").append(String.format("%02d",minute)));

        timeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(CreateAppointment.this, new TimePickerDialog.OnTimeSetListener() {

                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour=String.format("%02d",selectedHour);
                        String min=String.format("%02d",selectedMinute);

                        timeField.setText(hour + ":" + min);
                    }

                }, hour, minute, true);//Yes 24 hour time
                timePicker.setTitle("Select Time");
                timePicker.show();

            }
        });

    }






}
