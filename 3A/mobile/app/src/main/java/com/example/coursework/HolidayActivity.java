package com.example.coursework;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class HolidayActivity extends AppCompatActivity {

    EditText dateView;
    final String holidaySaveLocation = "holidays/";

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

        String fileName = holidayName.getText().toString().replace(" ", "_")+".json";
        boolean successCreate = false;
        try {
            FileOutputStream fileOut = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outStream = new OutputStreamWriter(fileOut);
            outStream.write(holidayName.getText().toString());
            outStream.flush();
            outStream.close();
            successCreate = true;
        } catch (java.io.IOException e) {
            System.out.println("Exception creating file: "+ e.getMessage());
        }

        if (successCreate) {
            Intent intent = new Intent(this, HolidaysActivity.class);
            startActivity(intent);
        } else {
            header.setText("Error, move me");
        }

    }
}
