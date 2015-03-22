package com.w1441879.appointmentbooker;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.w1441879.appointmentbooker.Constants.*;
import java.util.Calendar;

public class CreateScreen extends Activity{

    TextView timeField,dateField;
    Button save;
    AppointmentData appointData;
    String time, date, title;
    EditText titleInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        appointData = new AppointmentData(this);

        dateField = (TextView)findViewById(R.id.dateTitle);
        date= getIntent().getStringExtra("date");
        dateField.setText(date);

        titleInput = (EditText) findViewById(R.id.editTitle);

        setTime();

        save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean successful = false;
                title = titleInput.getText().toString();

                if(title.matches("")){
                    showDialog(title);
                }else try {
                        addAppointment();
                        successful=true;
                    } catch (SQLiteConstraintException e) {
                        showDialog(title);
                    }
                if(successful) {
                    Toast.makeText(getApplicationContext(), "EVENT CREATED", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appointData.close();
    }

    private void addAppointment() {
        SQLiteDatabase db = appointData.getWritableDatabase();

        ContentValues values = new ContentValues();
            values.put(C_TITLE, title);
            values.put(C_TIME, time);
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


    private void showDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        if(title.matches("")){
            builder.setMessage("Please enter a Title");
        } else {
            builder.setMessage("Appointment " + title + " already exists, please enter a different title");
        }
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();



    }

}
