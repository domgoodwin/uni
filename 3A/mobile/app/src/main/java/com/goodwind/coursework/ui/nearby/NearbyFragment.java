package com.goodwind.coursework.ui.nearby;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.goodwind.coursework.ui.view.PlacesAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class NearbyFragment extends Fragment  {

    private NearbyViewModel nearbyViewModel;
    Button btnSearch;
    Spinner spnType;
    Location curLocation;
    RecyclerView lvPlaces;
    private final int REQUEST_LOCATION_PERMISSION = 1;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getLocation();
        nearbyViewModel =
                ViewModelProviders.of(this).get(NearbyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);
        btnSearch = root.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNearby(v);
            }
        });

        spnType = root.findViewById(R.id.spnType);

        lvPlaces = root.findViewById(R.id.lvPlaces);
        lvPlaces.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        lvPlaces.setAdapter(new NearbyAdapter(getContext()));


        return root;
    }

    private void searchNearby(View v){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String key = getString(R.string.google_maps_key);
        String latLong = "52.47819,-1.89984";
        latLong = curLocation.getLatitude() + "," + curLocation.getLongitude();
        String type = spnType.getSelectedItem().toString();
        type = type.toLowerCase().replace(" ", "_");
        int radius = 1500;
        String url = String.format(Locale.getDefault(),
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%d&type=%s&key=%s",
                latLong,
                radius,
                type,
                key
                );
        Log.d("nearby", "Getting nearby: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("nearby", "Failed: "+error);
            }
        });
        queue.add(stringRequest);
    }



    private void getLocation() {
        Log.d("add fragment", "getLocation");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d("add fragment", "getLocation: permissions granted");
            getLastLocation();
        }
    }

    private void getLastLocation(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            curLocation = location;
                        }
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getContext(),
                            "Cannot get locaton permissions",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void processResponse(String response){
        NearbyAdapter adapter = (NearbyAdapter)lvPlaces.getAdapter();
        try {
            JSONArray results = new JSONObject(response).getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                adapter.placeNames.add(place.getString("name"));
                adapter.iconLinks.add(place.getString("icon"));
                adapter.placeLinks.add("placeholder");
                adapter.getPlaceByPlaceID(place.getString("place_id"), adapter.placeNames.size()-1);
            }
        } catch (JSONException e){
            Log.e("nearby", e.getMessage());
        }
        adapter.notifyItemInserted(adapter.placeLinks.size() - 1);
    }


}
