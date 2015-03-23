package com.w1441879.appointmentbooker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.w1441879.appointmentbooker.Constants.*;


public class EditDialog extends Activity {

    EditText eventTitle, eventDescrip;
    TextView eventDate, eventTime;
    Button amendBtn;
    String title, time, descrip, date;
    long ID;
    AppointmentData appointData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        getData();

        appointData = new AppointmentData(this);

        amendBtn = (Button) findViewById(R.id.saveBtn);
        amendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean successful = false;
                title = eventTitle.getText().toString();

                descrip = eventDescrip.getText().toString();

                if(title.matches("")){
                    showDialog(title);
                    //System.out.println("STRING IS EMPTY");
                }else try {
                    updateData();
                    successful=true;
                } catch (SQLiteConstraintException e) {
                    showDialog(title);

                    //System.out.println("ERROR");
                }
                if(successful) {
                    Toast.makeText(getApplicationContext(), "EVENT AMENDED", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void getData(){
        ID = getIntent().getLongExtra("ID", 0);
        title = getIntent().getStringExtra("title");
        time = getIntent().getStringExtra("time");
        descrip = getIntent().getStringExtra("description");
        date= getIntent().getStringExtra("date");

        eventDate = (TextView)findViewById(R.id.dateTitle);
        eventTitle = (EditText)findViewById(R.id.createTitle);
        eventTime = (TextView)findViewById(R.id.createTime);
        eventDescrip = (EditText)findViewById(R.id.createDescrip);

        eventDate.setText(date);
        eventTitle.setText(title);
        eventTime.setText(time);
        eventDescrip.setText(descrip);

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(EditDialog.this, new TimePickerDialog.OnTimeSetListener() {

                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        eventTime.setText(time);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

    }

    private void updateData(){
        SQLiteDatabase db = appointData.getWritableDatabase();

        final String selection = _ID + "=?";
        final String[] selectionArgs = { String.valueOf(ID) };

        ContentValues values = new ContentValues();
            values.put(C_TITLE, title);
            values.put(C_TIME, time);
            values.put(C_DATE, date);
            values.put(C_DESCRIP, descrip);
        db.update(C_TABLE_NAME,values,selection,selectionArgs);
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
