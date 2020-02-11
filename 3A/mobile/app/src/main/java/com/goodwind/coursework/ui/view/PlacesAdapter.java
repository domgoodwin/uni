package com.goodwind.coursework.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.R;

import java.util.List;
import java.util.Map;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    public static class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        String fileName;
        public PlacesViewHolder(View v){
            super(v);

            textView = v.findViewById(R.id.txtPlaceName);
        }
    }

    private Map<Integer, String> places;
    private Context context;


    public PlacesAdapter(Map<Integer, String> placeNames, Context ctx){
        this.places = placeNames;
        this.context = ctx;
    }



    @NonNull
    @Override
    public PlacesAdapter.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_text_view, parent, false);
        PlacesAdapter.PlacesViewHolder vh = new PlacesAdapter.PlacesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.PlacesViewHolder holder, int position) {
        String holidayName = places.get(position);
        holder.textView.setText(holidayName);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked place", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
