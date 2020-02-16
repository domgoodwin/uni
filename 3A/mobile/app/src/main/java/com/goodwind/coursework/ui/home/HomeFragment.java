package com.goodwind.coursework.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.HolidayAdapter;
import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HolidayFile holidayFile;
    final String holidaySaveLocation = HolidayFile.holidaySaveLocation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());

        RecyclerView rView = root.findViewById(R.id.lvPlaces);
        rView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        HolidayAdapter adapter = new HolidayAdapter(holidayFile.getHolidaysArray());
        rView.setLayoutManager(layoutManager);
        rView.setAdapter(adapter);

        final FloatingActionButton fabAdd = root.findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onClickAdd(v);
            }
        });

        if (adapter.getItemCount() == 0){
            ((TextView)root.findViewById(R.id.text_home)).setText("No saved holidays\nClick add to create one!");
        }

        return root;
    }

    public void onClickAdd(View view){
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_add);
    }
}