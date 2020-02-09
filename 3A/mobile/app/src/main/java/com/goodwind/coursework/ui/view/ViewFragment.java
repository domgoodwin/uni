package com.goodwind.coursework.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.goodwind.coursework.ui.add.AddViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewFragment extends Fragment {

    HolidayFile holidayFile;

    private ViewViewModel viewViewModel;
    EditText dateView;
    final String holidaySaveLocation = "holidays.json";
    private JSONObject holiday;
    private int holidayIndex;
    View v;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewViewModel =
                ViewModelProviders.of(this).get(ViewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday_view, container, false);
        final TextView textView = root.findViewById(R.id.txtHeader);
        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        holidayIndex = getArguments().getInt("holidayIndex");
        holiday = holidayFile.getHolidayByIndex(holidayIndex);
        v = root;
        populateFields(holiday);

        final FloatingActionButton fabEdit = root.findViewById(R.id.fab_edit);
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
        final Button viewOnMap = root.findViewById(R.id.btnViewMap);
        viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("holidayIndex", holidayIndex);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_map, bundle);
            }
        });
        return root;
    }

    private void enableEdit(View v){
        EditText start = getView().findViewById(R.id.txtStartDate);
        EditText end = getView().findViewById(R.id.txtEndDate);
        start.setEnabled(true);
        end.setEnabled(true);
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
        FloatingActionButton fabEdit = getView().findViewById(R.id.fab_edit);
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
        EditText start = getView().findViewById(R.id.txtStartDate);
        EditText end = getView().findViewById(R.id.txtEndDate);
        start.setEnabled(false);
        end.setEnabled(false);
        start.setOnClickListener(null);
        end.setOnClickListener(null);
        populateFields(holiday);


        FloatingActionButton fabEdit = getView().findViewById(R.id.fab_edit);
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
        // TODO: Implement
    }

    private void deleteHoliday(View v){
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm")
                .setMessage("Do you really want to delete this Holiday?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getActivity(), "Yaay", Toast.LENGTH_SHORT).show();
                        // TODO: Delete and save holidays
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void populateFields(JSONObject holiday){
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
            SimpleDateFormat store = new SimpleDateFormat("yyyymmdd");
            ((TextView)v.findViewById(R.id.txtHeader)).setText(holiday.getString("name"));
            Date start = store.parse(holiday.getString("startDate"));
            Date end = store.parse(holiday.getString("endDate"));
            ((EditText)v.findViewById(R.id.txtStartDate)).setText(df.format(start));
            ((EditText)v.findViewById(R.id.txtEndDate)).setText(df.format(end));
            ((EditText)v.findViewById(R.id.txtDebug)).setText(holiday.toString());
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