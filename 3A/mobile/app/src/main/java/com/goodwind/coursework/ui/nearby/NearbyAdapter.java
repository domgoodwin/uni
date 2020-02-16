package com.goodwind.coursework.ui.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodwind.coursework.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyViewHolder> {

    public static class NearbyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView icon;
        String fileName;
        public NearbyViewHolder(View v){
            super(v);

            textView = v.findViewById(R.id.txtNearbyName);
            icon = v.findViewById(R.id.imgIcon);
        }
    }

    public ArrayList<String> placeNames;
    public ArrayList<String> placeLinks;
    public ArrayList<String> iconLinks;
    private Context context;


    public NearbyAdapter(Context ctx){
        this.placeNames = new ArrayList<>();
        this.placeLinks = new ArrayList<>();
        this.context = ctx;
        this.iconLinks = new ArrayList<>();
    }


    public NearbyAdapter(ArrayList<String> placeNames, ArrayList<String> placeLinks, ArrayList<String> icons, Context ctx){
        this.placeLinks = placeNames;
        this.placeLinks = placeLinks;
        this.context = ctx;
        this.iconLinks = icons;
    }



    @NonNull
    @Override
    public NearbyAdapter.NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_element_view, parent, false);
        NearbyAdapter.NearbyViewHolder vh = new NearbyAdapter.NearbyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyAdapter.NearbyViewHolder holder, final int position) {
        String holidayName = placeNames.get(position);
        holder.textView.setText(holidayName);
        //holder.icon.setImageBitmap(iconLinks.get(position));
        new DownloadImageTask(holder.icon).execute(iconLinks.get(position));



        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! placeLinks.get(position).equals("placeholder")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeLinks.get(position)));
                    context.startActivity(browserIntent);
                } else {
                    Toast.makeText(context, "Page still loading, links might not work", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void getPlaceByPlaceID(String placeID, final int position){
        //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJrTLr-GyuEmsRBfy61i59si0&key=YOUR_API_KEY

        RequestQueue queue = Volley.newRequestQueue(context);
        String key = context.getString(R.string.google_maps_key);
        String url = String.format(Locale.getDefault(),
                "https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s",
                placeID,
                key
        );
        Log.d("nearby", "Getting place: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            placeLinks.set(position, res.getJSONObject("result").getString("url"));

                        } catch (JSONException e){
                            Log.e("nearby", e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("nearby", "Failed: "+error);
            }
        });
        queue.add(stringRequest);
    }

    private void openPlaceByPlaceID(String placeID){
        //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJrTLr-GyuEmsRBfy61i59si0&key=YOUR_API_KEY

        RequestQueue queue = Volley.newRequestQueue(context);
        String key = context.getString(R.string.google_maps_key);
        String url = String.format(Locale.getDefault(),
                "https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s",
                placeID,
                key
        );
        Log.d("nearby", "Getting place: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("nearby", response);
                            JSONObject res = new JSONObject(response);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(res.getJSONObject("result").getString("url")));
                            context.startActivity(browserIntent);
                        } catch (JSONException e){
                            Log.e("nearby", e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("nearby", "Failed: "+error);
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return placeNames.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
