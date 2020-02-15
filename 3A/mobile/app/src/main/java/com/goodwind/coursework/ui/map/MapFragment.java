package com.goodwind.coursework.ui.map;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class MapFragment extends Fragment {

    private MapViewModel mapViewModel;
    final String holidaySaveLocation = "holidays.json";
    private HolidayFile holidayFile;
    private int holidayIndex;
    private int placeIndex;
    private JSONObject holiday;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);


        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        holidayIndex = getArguments().getInt("holidayIndex");
        placeIndex = getArguments().getInt("placeIndex");
        if (holidayIndex != -1){
            holiday = holidayFile.getHolidayByIndex(holidayIndex);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setCompassEnabled(true);
                addHolidayMapMarkers(mMap);
            }
        });

        return root;
    }

    private void addHolidayMapMarkers(GoogleMap map) {
        map.clear(); //clear old markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (holidayIndex == -1) {
            HashMap<LatLng, String> places = holidayFile.getAllPlaces();
            for (HashMap.Entry<LatLng, String> place : places.entrySet()) {
                LatLng loc = place.getKey();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(loc.latitude, loc.longitude))
                        .title(place.getValue());
                map.addMarker(marker);
                builder.include(marker.getPosition());
            }
        } else {
            try {
                // No holiday so view all places
                 if (placeIndex == -1) {
                    JSONArray places = holiday.getJSONArray("places");
                    for (int j = 0; j < places.length(); j++) {
                        JSONObject place = places.getJSONObject(j);
                        JSONObject placeLocation = place.getJSONObject("location");
                        Log.d("aaa", "Added point to map: " + placeLocation.getDouble("lat") + placeLocation.getDouble("long"));
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(placeLocation.getDouble("lat"), placeLocation.getDouble("long")))
                                .title(place.getString("name"));
                        map.addMarker(marker);
                        // Pan camera to include all points
                        builder.include(marker.getPosition());

                    }
                } else {
                    JSONArray places = holiday.getJSONArray("places");
                    JSONObject place = places.getJSONObject(placeIndex);
                    JSONObject placeLocation = place.getJSONObject("location");
                    Log.d("aaa", "Added point to map: " + placeLocation.getDouble("lat") + placeLocation.getDouble("long"));
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(placeLocation.getDouble("lat"), placeLocation.getDouble("long")))
                            .title(place.getString("name"));
                    map.addMarker(marker);
                    // Pan camera to include all points
                    builder.include(marker.getPosition());
                }
            } catch (JSONException e){
                Log.e("aaa", e.getMessage());
            }
        }
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 0);
        map.moveCamera(cu);
    }



}
