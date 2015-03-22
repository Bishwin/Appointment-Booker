package com.w1441879.appointmentbooker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.view.View.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeScreen extends Activity implements OnClickListener {

    CalendarView calendar;
    View createBtn, editBtn, searchBtn, deleteBtn, moveBtn, translateBtn;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //Buttons
        createBtn=findViewById(R.id.createBtn);
        editBtn=findViewById(R.id.viewEditBtn);
        searchBtn=findViewById(R.id.searchBtn);
        deleteBtn=findViewById(R.id.deleteBtn);
        moveBtn=findViewById(R.id.moveBtn);
        translateBtn=findViewById(R.id.translateBtn);
        //Listeners
        createBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);


        InitCal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void InitCal(){
        calendar = (CalendarView)findViewById(R.id.calendarView);
        long today = calendar.getDate();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        date = (sdf.format(today));

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view,
                                            int year, int month, int dayOfMonth) {
                month++;
                date = dayOfMonth + "/" + (String.format("%02d",month)) + "/" + year;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.createBtn:
                Intent createEvent = new Intent(HomeScreen.this, CreateScreen.class);
                createEvent.putExtra("date", date);
                startActivity(createEvent);
                break;
            case R.id.viewEditBtn:
                Intent editEvent = new Intent(HomeScreen.this, ViewScreen.class);
                editEvent.putExtra("date", date);
                startActivity(editEvent);
                break;
            case R.id.deleteBtn:

                break;
        }
    }
}
