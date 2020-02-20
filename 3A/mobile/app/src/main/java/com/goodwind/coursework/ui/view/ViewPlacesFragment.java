package com.goodwind.coursework.ui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class ViewPlacesFragment extends Fragment {

    HolidayFile holidayFile;

    private ViewViewModel viewViewModel;
    EditText dateView;
    final String holidaySaveLocation = HolidayFile.holidaySaveLocation;
    private JSONObject holiday;
    private int holidayIndex;
    View v;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewViewModel =
                ViewModelProviders.of(this).get(ViewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday_places_list, container, false);
        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        holidayIndex = getArguments().getInt("holidayIndex");
        holiday = holidayFile.getHolidayByIndex(holidayIndex);


        RecyclerView lvPlaces = root.findViewById(R.id.lvPlaces);
        lvPlaces.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        PlacesAdapter adapter = new PlacesAdapter(holidayFile.getPlaces(holidayIndex), holidayIndex, getContext());
        lvPlaces.setAdapter(adapter);

        final FloatingActionButton fabShare = root.findViewById(R.id.fab_add);
        fabShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addPlace();
            }
        });

        if (adapter.getItemCount() == 0){
            ((TextView)root.findViewById(R.id.text_home2)).setText("No saved holidays\nClick add to create one!");
        }

        return root;
    }

    public void addPlace(){
        Toast.makeText(getContext(), "Adding place", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putInt("holidayIndex", holidayIndex);
        Navigation.findNavController((Activity)getContext(), R.id.nav_host_fragment).navigate(R.id.nav_holiday_place_view, bundle);
    }


}