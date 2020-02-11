package com.goodwind.coursework.ui.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPlaceFragment extends Fragment {

    HolidayFile holidayFile;

    private ViewViewModel viewViewModel;
    EditText dateView;
    final String holidaySaveLocation = "holidays.json";
    private JSONObject holiday;
    private JSONObject place;
    private int holidayIndex;
    private int placeIndex;
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
        populateFields(place);

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
                deleteHoliday(v);
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

        final Button viewPlaces = root.findViewById(R.id.btnPlaces);
        viewPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("holidayIndex", holidayIndex);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_holiday_places_list, bundle);
            }
        });

        return root;
    }

    private void enableEdit(View v){
        EditText title = getView().findViewById(R.id.txtHolidayName);
        EditText start = getView().findViewById(R.id.txtStartDate);
        EditText end = getView().findViewById(R.id.txtEndDate);
        start.setEnabled(true);
        end.setEnabled(true);
        title.setEnabled(true);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
        end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onDateSelect(v);
            }
        });
        FloatingActionButton fabEdit = getView().findViewById(R.id.fab_add);
        FloatingActionButton fabDelete = getView().findViewById(R.id.fab_delete);
        fabDelete.hide();
        fabEdit.setImageResource(R.drawable.ic_menu_send);
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveChanges(v);
            }
        });

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
        EditText title = getView().findViewById(R.id.txtHolidayName);
        EditText start = getView().findViewById(R.id.txtStartDate);
        EditText end = getView().findViewById(R.id.txtEndDate);
        start.setEnabled(false);
        end.setEnabled(false);
        title.setEnabled(false);

        start.setOnClickListener(null);
        end.setOnClickListener(null);
        populateFields(holiday);


        FloatingActionButton fabEdit = getView().findViewById(R.id.fab_add);
        FloatingActionButton fabDelete = getView().findViewById(R.id.fab_delete);
        fabDelete.show();
        fabEdit.setImageResource(R.drawable.ic_menu_manage);
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                enableEdit(v);
            }
        });
    }

    private void saveChanges(View v){
        String holidayName = ((TextView)getView().findViewById(R.id.txtHolidayName)).getText().toString();
        String startDate = ((TextView)getView().findViewById(R.id.txtStartDate)).getText().toString();
        String endDate = ((TextView)getView().findViewById(R.id.txtEndDate)).getText().toString();
        try {
            holiday.put("name", holidayName);
            holiday.put("startDate", startDate);
            holiday.put("endDate", endDate);
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
            shareText = String.format("I went on holiday, %s, between %s and %s and has a blast!",
                    holiday.getString("name"),
                    holiday.getString("startDate"),
                    holiday.getString("endDate"));

        } catch (JSONException e) {
            Log.e("aaa", e.getMessage());
        }
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");



        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    private void deleteHoliday(View v){
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm")
                .setMessage("Do you really want to delete this Holiday?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        holidayFile.deleteHoliday(holidayIndex, getActivity());
                        Toast.makeText(getActivity(), "Holiday deleted", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home);

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void populateFields(JSONObject place){
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
            SimpleDateFormat store = new SimpleDateFormat("yyyymmdd");
            ((EditText)v.findViewById(R.id.txtPlaceName)).setText(place.getString("name"));
            Date date = store.parse(place.getString("date"));
            ((EditText)v.findViewById(R.id.txtDate)).setText(df.format(date));
            ((EditText)v.findViewById(R.id.txtPlaceLocation)).setText(place.getString("name"));
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
}