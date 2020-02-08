package com.example.coursework.ui.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.coursework.HolidayActivity;
import com.example.coursework.HolidaysActivity;
import com.example.coursework.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
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
        final Button btnSubmit = (Button) root.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onHolidayAdd(v);
            }
        });
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

    public void onHolidayAdd(View view){
        // Hide keyboard on submit
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        TextView holidayName = getView().findViewById(R.id.txtName);
        TextView holidayStart = getView().findViewById(R.id.txtStartDate);
        TextView holidayEnd = getView().findViewById(R.id.txtEndDate);
        TextView header = getView().findViewById(R.id.txtHeader);

        JSONObject holidaysJSON = getHolidays();
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
            holidayJSON.put("startDate", df.parse(holidayStart.getText().toString()));
            holidayJSON.put("endDate", df.parse(holidayEnd.getText().toString()));
            JSONObject coOrdJSON = new JSONObject();
            coOrdJSON.put("long", 0);
            coOrdJSON.put("lat", 0);
            coOrdJSON.put("display", "TODO nice name");
            holidayJSON.put("location", coOrdJSON);
            holidaysArrJSON.put(holidayJSON);
        } catch (JSONException e){
            Log.e("add fragment", "JSON exception: " + e.getMessage());
        } catch (ParseException e) {
            Log.e("add fragment", "Date parse exception: " + e.getMessage());
        }

        Log.i("add fragment", "Writing holidays back: " + holidaysJSON.toString());

        String fileName = holidaySaveLocation;

        boolean successCreate = false;
        try {

            FileOutputStream fileOut = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outStream = new OutputStreamWriter(fileOut);
            outStream.write(holidaysJSON.toString());
            outStream.flush();
            outStream.close();
            successCreate = true;
        } catch (java.io.IOException e) {
            System.out.println("Exception creating file: "+ e.getMessage());
        }

        if (successCreate) {
            // TODO
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home);
        } else {
            header.setText("Error, move me");
        }

    }

    public JSONObject getHolidays(){
        String holidaysRaw = readHolidaysFile();
        Log.i("add fragment", "Holidays file input: " + holidaysRaw);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(holidaysRaw);
        } catch (JSONException e ){
            Log.e("add fragment", "JSON Parse error: " + e.getMessage());
        }
        try{
            if (! jObj.has("holidays")){
                jObj.put("holidays", new JSONArray());
            }
        } catch (JSONException e){
            Log.e("add fragment", "JSON Array Parse error: " + e.getMessage());
        }


        return jObj;
    }

    public String readHolidaysFile(){
        File file = new File(getActivity().getFilesDir(), holidaySaveLocation);
        String out = "";
        if (!file.exists()){
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                out = "{}";
                bw.write(out);
                bw.close();
            } catch (IOException e){
                Log.e("add fragment", "Initial file creation error: "+ e.getMessage());
            }
        } else {
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(getContext().openFileInput(holidaySaveLocation)));
                StringBuffer output = new StringBuffer();
                String line;
                while ((line = bReader.readLine()) != null){
                    output.append(line + "\n");
                }
                out = output.toString();
                bReader.close();


            }catch (FileNotFoundException e) {
                Log.e("add fragment", "File not found: " + e.getMessage());
            } catch ( IOException e){
                Log.e("add fragment", "File read exception: " + e.getMessage());
            }
        }
        if (out.equals("")|| out == null){
            out = "{}";
        }

        return out;
    }
}