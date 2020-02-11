package com.goodwind.coursework.ui.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewPlaceFragment extends Fragment {

    HolidayFile holidayFile;

    private ViewViewModel viewViewModel;
    EditText dateView;
    final String holidaySaveLocation = "holidays.json";
    private JSONObject holiday;
    private JSONObject place;
    private int holidayIndex;
    private int placeIndex;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    Location holLocation;

    View v;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewViewModel =
                ViewModelProviders.of(this).get(ViewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday_place_view, container, false);
        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        holidayIndex = getArguments().getInt("holidayIndex");
        placeIndex = getArguments().getInt("placeIndex");
        holiday = holidayFile.getHolidayByIndex(holidayIndex);
        place = holidayFile.getHolidayPlaceByIndex(holidayIndex, placeIndex);
        v = root;

        final FloatingActionButton fabEdit = root.findViewById(R.id.fab_add);
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                enableEdit(v);
            }
        });
        final FloatingActionButton fabDelete = root.findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deletePlace(v);
            }
        });
        final FloatingActionButton fabShare = root.findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shareHoliday(v);
            }
        });
        final Button viewOnMap = root.findViewById(R.id.btnViewMap);
        viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("holidayIndex", holidayIndex);
                bundle.putInt("placeIndex", placeIndex);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_map, bundle);
            }
        });
        final Button viewPhotos = root.findViewById(R.id.btnPhotos);
        viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("holidayIndex", holidayIndex);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_gallery_specific, bundle);
            }
        });

        // Allow empty edit, eg. add
        if (placeIndex != -1){
            Log.d("aaa", "place: " + place.toString());
            populateFields(place, root);
        } else {
            enableEdit(root);
        }

        return root;
    }

    private void enableEdit(View v){

        EditText name = v.findViewById(R.id.txtPlaceName);
        EditText date = v.findViewById(R.id.txtDate);
        EditText location = v.findViewById(R.id.txtPlaceLocation);
        EditText notes = v.findViewById(R.id.txtNotes);
        name.setEnabled(true);
        date.setEnabled(true);
        location.setEnabled(true);
        notes.setEnabled(true);

        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
        location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getLocation();
            }
        });

        FloatingActionButton fabEdit = v.findViewById(R.id.fab_add);
        FloatingActionButton fabDelete = v.findViewById(R.id.fab_delete);
        FloatingActionButton fabShare = v.findViewById(R.id.fab_share);
        fabDelete.hide();
        fabShare.hide();
        fabEdit.setImageResource(R.drawable.ic_menu_send);
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveChanges(v);
            }
        });

        Button btnMap = v.findViewById(R.id.btnViewMap);
        Button btnPhotos = v.findViewById(R.id.btnPhotos);
        btnMap.setVisibility(View.GONE);
        btnPhotos.setVisibility(View.GONE);

        // Set back button to cancel edit instead of going back fragments
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cancelEdit();
                setEnabled(false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);



    }

    private void cancelEdit(){
        EditText name = getView().findViewById(R.id.txtPlaceName);
        EditText date = getView().findViewById(R.id.txtDate);
        EditText location = getView().findViewById(R.id.txtPlaceLocation);
        EditText notes = getView().findViewById(R.id.txtNotes);
        name.setEnabled(false);
        date.setEnabled(false);
        location.setEnabled(false);
        notes.setEnabled(false);

        date.setOnClickListener(null);
        populateFields(place, getView());

        Button btnMap = getView().findViewById(R.id.btnViewMap);
        Button btnPhotos = getView().findViewById(R.id.btnPhotos);
        btnMap.setVisibility(View.VISIBLE);
        btnPhotos.setVisibility(View.VISIBLE);


        FloatingActionButton fabEdit = getView().findViewById(R.id.fab_add);
        FloatingActionButton fabDelete = getView().findViewById(R.id.fab_delete);
        FloatingActionButton fabShare = getView().findViewById(R.id.fab_share);
        fabDelete.show();
        fabShare.show();
        fabEdit.setImageResource(R.drawable.ic_menu_manage);
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                enableEdit(v);
            }
        });
    }

    private void saveChanges(View v){
        String name = ((TextView)getView().findViewById(R.id.txtPlaceName)).getText().toString();
        String date = ((TextView)getView().findViewById(R.id.txtDate)).getText().toString();
        String location = ((TextView)getView().findViewById(R.id.txtPlaceLocation)).getText().toString();
        String notes = ((TextView)getView().findViewById(R.id.txtNotes)).getText().toString();
        try {
            if (placeIndex == -1){
                place = new JSONObject();
                placeIndex = holiday.getJSONArray("places").length();
            }
            place.put("name", name);
            place.put("date", "20200101"); //todo proper formatting
            JSONObject coOrdJSON = new JSONObject();
            coOrdJSON.put("long", holLocation.getLongitude());
            coOrdJSON.put("lat", holLocation.getLatitude());
            coOrdJSON.put("display", getLocationDisplay(holLocation));
            place.put("location", coOrdJSON);
            place.put("notes", notes);
            holiday.getJSONArray("places").put(placeIndex, place);
            holidayFile.updateHoliday(holiday, holidayIndex, getActivity());
        } catch (JSONException e){
            Log.e("view", e.getMessage());
        }

        cancelEdit();
    }


    private void shareHoliday(View v){
        Intent sendIntent = new Intent();
        String shareText = "";
        try {
            // TODO: Format date properly
            shareText = String.format("I went to, %s, on %s and had a blast!",
                    holiday.getString("name"),
                    holiday.getString("date"));

        } catch (JSONException e) {
            Log.e("aaa", e.getMessage());
        }
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");



        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    private void deletePlace(View v){
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm")
                .setMessage("Do you really want to delete this Place from your holiday?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        holidayFile.deletePlace(holidayIndex, placeIndex, getActivity());
                        Toast.makeText(getActivity(), "Place deleted", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putInt("holidayIndex", holidayIndex);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_holiday_places_list, bundle);

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void populateFields(JSONObject place, View v){
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
            SimpleDateFormat store = new SimpleDateFormat("yyyymmdd");
            ((EditText)v.findViewById(R.id.txtPlaceName)).setText(place.getString("name"));
            Date date = store.parse(place.getString("date"));
            ((EditText)v.findViewById(R.id.txtDate)).setText(df.format(date));
            ((EditText)v.findViewById(R.id.txtNotes)).setText(place.getString("notes"));
            ((EditText)v.findViewById(R.id.txtPlaceLocation)).setText(place.getJSONObject("location").getString("display"));
        } catch (JSONException e){
            Log.e("view", "JSON parse exception: "+e.getMessage());

        } catch (ParseException e){
            Log.e("view", "Date parse exception: "+e.getMessage());

        }
    }

    public void onDateSelect(View view){
        dateView = (EditText) view;
        DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dpView, int year, int month, int dayOfMonth) {
                dateView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        } , 2020, 0, 01);
        picker.show();
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
                            holLocation = location;
                            ((TextView)getView().findViewById(R.id.txtPlaceLocation)).setText(getLocationDisplay(holLocation));
                        }
                    }
                }
        );
    }

    private String getLocationDisplay(Location loc){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        }catch (IOException e){
            Log.e("tag", e.getMessage());
        }
        if (addressList != null && !addressList.isEmpty()) {

            Address adr = addressList.get(0);
            String address = "";
            for (int i = 0; i <= adr.getMaxAddressLineIndex(); i++) {
                address = address + adr.getAddressLine(i);
            }
            return address;
        }
        return "";
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

}