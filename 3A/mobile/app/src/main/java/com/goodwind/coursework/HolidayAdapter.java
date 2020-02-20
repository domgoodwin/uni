package com.goodwind.coursework;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {
    private JSONArray holidays;
    private static final String TAG = "HolidayAdapter";

    public static class HolidayViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public HolidayViewHolder(View v){
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element clicked: "+getAdapterPosition());
                    Bundle bundle = new Bundle();
                    bundle.putInt("holidayIndex", getAdapterPosition());
                    Navigation.findNavController((Activity)v.getContext(), R.id.nav_host_fragment).navigate(R.id.nav_view, bundle);
                }
            });

            textView = (TextView) v.findViewById(R.id.txtHolidayView);
        }
    }

    public HolidayAdapter(JSONArray hols){
        holidays = hols;
    }

    @NonNull
    @Override
    public HolidayAdapter.HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holiday_text_view, parent, false);
        HolidayViewHolder vh = new HolidayViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayAdapter.HolidayViewHolder holder, int position) {
        try{
            JSONObject holiday =(JSONObject) holidays.get(position);
            holder.textView.setText(holiday.getString("name"));
        }
        catch (JSONException e){
            Log.e(TAG, "JSON Array Parse error: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return holidays.length();
    }
}
