package com.example.coursework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Gallery;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void showGallery(View view) {
        TextView welcome = findViewById(R.id.mainMessage);
        welcome.setText("Going to gallery");
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    public void showHolidays(View view) {
        TextView welcome = findViewById(R.id.mainMessage);
        welcome.setText("Going to holidays");
        Intent intent = new Intent(this, HolidayActivity.class);
        startActivity(intent);
    }
}
