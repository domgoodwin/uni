package com.goodwind.coursework.ui.gallerySpecific;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.HolidayAdapter;
import com.goodwind.coursework.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        String fileName;
        public GalleryViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.imgPreview);
        }
    }

    private List<Bitmap> images;
    private Context context;


    public GalleryAdapter(List<Bitmap> imgs, Context ctx){
        this.images = imgs;
        this.context = ctx;
    }

    public void addBm(Bitmap b){
        images.add(b);
    }


    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_img_view, parent, false);
        GalleryAdapter.GalleryViewHolder vh = new GalleryAdapter.GalleryViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryViewHolder holder, int position) {
        Bitmap image = images.get(position);
        holder.imageView.setImageBitmap(image);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
