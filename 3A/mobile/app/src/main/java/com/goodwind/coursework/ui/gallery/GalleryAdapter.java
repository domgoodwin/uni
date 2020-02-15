package com.goodwind.coursework.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.R;

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
    private List<String> filePaths;
    private Context context;
    private int holidayIndex = -1;
    private int placeIndex = -1;


    public GalleryAdapter(List<String> imgFiles, List<Bitmap> imgs, Context ctx){
        this.images = imgs;
        this.context = ctx;
        this.filePaths = imgFiles;
    }

    public GalleryAdapter(List<String> imgFiles, List<Bitmap> imgs, Context ctx, int holIndex, int placeIndex){
        this(imgFiles, imgs, ctx);
        this.holidayIndex = holIndex;
        this.placeIndex = placeIndex;
    }

    public void addBm(String filePath, Bitmap b){
        images.add(b);
        filePaths.add(filePath);
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
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryViewHolder holder, final int position) {
        Bitmap image = images.get(position);
        holder.imageView.setImageBitmap(image);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked image", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", filePaths.get(position));
                bundle.putInt("holidayIndex", holidayIndex);
                bundle.putInt("placeIndex", placeIndex);
                bundle.putInt("imageIndex", position);
                Navigation.findNavController((Activity)v.getContext(), R.id.nav_host_fragment).navigate(R.id.nav_img_view, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
