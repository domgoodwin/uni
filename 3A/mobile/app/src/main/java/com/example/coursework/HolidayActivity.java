package com.example.coursework;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class HolidayActivity extends AppCompatActivity {

    EditText dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday);

    }


    public void onDateSelect(View view){
        dateView = (EditText) view;
        DatePickerDialog picker = new DatePickerDialog(HolidayActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dpView, int year, int month, int dayOfMonth) {
                dateView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        } , 2020, 0, 01);
        picker.show();
    }

    public void onHolidayAdd(View view){
        TextView holidayName = findViewById(R.id.txtName);
        TextView header = findViewById(R.id.txtHeader);
        header.setText(holidayName.getText());
    }
}
