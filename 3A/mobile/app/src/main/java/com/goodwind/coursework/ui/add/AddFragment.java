package com.goodwind.coursework.ui.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddFragment extends Fragment {

    HolidayFile holidayFile;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private AddViewModel addViewModel;
    Location curLocation;
    Place selectedPlace;
    LatLng selectedLocation;
    EditText dateView;
    final String holidaySaveLocation = HolidayFile.holidaySaveLocation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        final TextView textStartDate = root.findViewById(R.id.txtStartDate);
        textStartDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
        final TextView textEndDate = root.findViewById(R.id.txtEndDate);
        textEndDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
//        final TextView textPlaceDate = root.findViewById(R.id.txtPlaceDate);
//        textPlaceDate.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                onDateSelect(v);
//            }
//        });
        final Button btnSubmit = (Button) root.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onHolidayAdd(v);
            }
        });
//        final Button btnLocation = (Button) root.findViewById(R.id.btnGetLocation);
//        btnLocation.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                getLocation();
//            }
//        });





        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        return root;
    }



    public void onDateSelect(View view){
        dateView = (EditText) view;
        DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dpView, int year, int month, int dayOfMonth) {
//                dateView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                DateFormat df = DateFormat.getDateInstance();
                dateView.setText(df.format(c.getTime()));
            }
        } , 2020, 0, 01);
        picker.show();
    }


    public void onHolidayAdd(View view){
        // Hide keyboard on submit
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        TextView txtMessage = getView().findViewById(R.id.txtMessage);
        TextView holidayName = getView().findViewById(R.id.txtName);
        TextView holidayStart = getView().findViewById(R.id.txtStartDate);
        TextView holidayEnd = getView().findViewById(R.id.txtEndDate);
        TextView header = getView().findViewById(R.id.txtHeader);
        TextView notes = getView().findViewById(R.id.txtNotes);
        TextView companions = getView().findViewById(R.id.txtCompanions);

        if(holidayName.getText().toString().equals("") || holidayStart.getText().toString().equals("") || holidayEnd.getText().toString().equals("")){
            txtMessage.setText("Mandatory fields not completed: Name, startdate and enddate");
            return;
        }

        JSONObject holidaysJSON = holidayFile.getHolidays();
        JSONArray holidaysArrJSON = null;
        try {
            holidaysArrJSON = holidaysJSON.getJSONArray("holidays");
        } catch (JSONException e) {
            Log.e("add fragment", "JSON get array exception: " + e.getMessage());
        }

        String errorMessage;
        for (int i=0; i < holidaysArrJSON.length(); i++) {
            try {
                JSONObject jObj = holidaysArrJSON.getJSONObject(i);
                // Check if holiday already exists by same name
                if (jObj.has("name") && jObj.getString("name").equals(holidayName.getText().toString())){
                    errorMessage = "Holiday with name: "+holidayName.getText()+" already exists. Please delete existing holiday or rename this holiday.";
                }
            } catch (JSONException e){
                Log.e("add fragment", "JSON Array get error: " + e.getMessage());
            }
        }

        try{
            JSONObject holidayJSON = new JSONObject();
            holidayJSON.put("name", holidayName.getText());
            // TODO: Nicer date formatting based on local settings
            DateFormat df = DateFormat.getDateInstance();
            DateFormat storeDf = new SimpleDateFormat("yyyymmdd");
            holidayJSON.put("startDate", storeDf.format(df.parse(holidayStart.getText().toString())));
            holidayJSON.put("endDate",  storeDf.format(df.parse(holidayEnd.getText().toString())));
            holidayJSON.put("images", new JSONArray());
            holidayJSON.put("notes", notes.getText());
            holidayJSON.put("companions", companions.getText());
            JSONArray placesJSON = new JSONArray();
            holidayJSON.put("places", placesJSON);
            holidaysArrJSON.put(holidayJSON);
        } catch (JSONException e){
            Log.e("add fragment", "JSON exception: " + e.getMessage());
        } catch (ParseException e) {
            Log.e("add fragment", "Date parse exception: " + e.getMessage());
        }

        Log.i("add fragment", "Writing holidays back: " + holidaysJSON.toString());

        if (holidayFile.saveHolidays(holidaysJSON, getActivity())) {
            // TODO
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home);
        } else {
            header.setText("Error, move me");
        }

    }

}
