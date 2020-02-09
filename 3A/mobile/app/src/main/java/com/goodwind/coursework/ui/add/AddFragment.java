package com.goodwind.coursework.ui.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class AddFragment extends Fragment {

    HolidayFile holidayFile;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private AddViewModel addViewModel;
    Location holLocation;
    EditText dateView;
    final String holidaySaveLocation = "holidays.json";


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
        final TextView textPlaceDate = root.findViewById(R.id.txtPlaceDate);
        textPlaceDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
        final Button btnSubmit = (Button) root.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onHolidayAdd(v);
            }
        });
        final TextView textLocation = root.findViewById(R.id.txtPlaceLocation);
        textLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getLocation();
            }
        });


        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        return root;
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

    public void onHolidayAdd(View view){
        // Hide keyboard on submit
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        TextView holidayName = getView().findViewById(R.id.txtName);
        TextView holidayStart = getView().findViewById(R.id.txtStartDate);
        TextView holidayEnd = getView().findViewById(R.id.txtEndDate);
        TextView header = getView().findViewById(R.id.txtHeader);

        TextView placeDate = getView().findViewById(R.id.txtPlaceDate);
        TextView placeName = getView().findViewById(R.id.txtPlaceName);

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
            DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
            DateFormat storeDf = new SimpleDateFormat("yyyymmdd");
            holidayJSON.put("startDate", storeDf.format(df.parse(holidayStart.getText().toString())));
            holidayJSON.put("endDate",  storeDf.format(df.parse(holidayEnd.getText().toString())));
            holidayJSON.put("images", new JSONArray());
            JSONObject placeJSON = new JSONObject();
            placeJSON.put("name", placeName.getText());
            placeJSON.put("images", new JSONArray());
            holidayJSON.put("date",  storeDf.format(df.parse(placeDate.getText().toString())));
            JSONObject coOrdJSON = new JSONObject();
            coOrdJSON.put("long", holLocation.getLongitude());
            coOrdJSON.put("lat", holLocation.getLatitude());
            coOrdJSON.put("display", getLocationDisplay(holLocation));
            placeJSON.put("location", coOrdJSON);
            JSONArray placesJSON = new JSONArray();
            placesJSON.put(placeJSON);
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
    private class FetchAddressTask extends AsyncTask<Location, Void, String> {
        private final String TAG = FetchAddressTask.class.getSimpleName();
        private Context mContext;

        FetchAddressTask(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        protected String doInBackground(Location... locations) {
            return null;
        }
        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
        }
    }
}
