package com.goodwind.coursework.ui.gallery;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class ImageEditFragment extends Fragment {

    private static final String TAG = "ImageEditFragment";

    HolidayFile holidayFile;
    final String holidaySaveLocation = HolidayFile.holidaySaveLocation;
    GalleryViewModel galViewModel;
    String curPhotoPath;

    FloatingActionButton fabSave;
    private int holidayIndex;
    private int placeIndex;
    private int imageIndex;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    Location curLocation;
    LatLng selectedLocation;
    Place selectedPlace;

    ImageView imgPreview;
    EditText txtTags;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_img_edit, container, false);

        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        curPhotoPath = getArguments().getString("filePath");
        holidayIndex = getArguments().getInt("holidayIndex");
        placeIndex = getArguments().getInt("placeIndex");
        imageIndex = getArguments().getInt("imageIndex");

        if (curPhotoPath != null) {
            imgPreview = root.findViewById(R.id.imgPreview);
            imgPreview.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(curPhotoPath), 200, 200));
        } else {
            Log.e(TAG, "How are you here with no image");
        }

        txtTags = root.findViewById(R.id.txtTags);
        populateFields(root, holidayFile.getImage(holidayIndex, placeIndex, imageIndex));
        setupLocationAutocomplete(true);

        fabSave = root.findViewById(R.id.fab_add);
        fabSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveImage();
            }
        });

        return root;
    }

    private void processImageLocation(){

        try {
            ExifInterface exif = new ExifInterface(curPhotoPath);
            float[] latLng = new float[]{0, 0};
            if (exif.getLatLong(latLng)){
                selectedLocation = new LatLng(latLng[0], latLng[1]);
            } else {
                getLocation();
            }
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
        }

    }

    private void populateFields(View v, JSONObject image) {
        try {
            ((EditText) v.findViewById(R.id.txtTags)).setText(image.getString("tags"));
            JSONObject location = image.getJSONObject("location");
            selectedLocation = new LatLng(location.getDouble("lat"), location.getDouble("long"));
        } catch (JSONException e) {
            Log.e(TAG, "JSON parse exception: " + e.getMessage());
            processImageLocation();
        }
    }

    public void saveImage(){
        String tags = txtTags.getText().toString();
        JSONObject holiday = holidayFile.getHolidayByIndex(holidayIndex);
        JSONObject location = new JSONObject();
        try {
            location.put("lat", selectedLocation.latitude);
            location.put("long", selectedLocation.longitude);
            location.put("display", getLocationDisplay(selectedLocation.latitude, selectedLocation.longitude));
            JSONObject image = holidayFile.getImage(holidayIndex, placeIndex, imageIndex);
            image.put("location", location);
            image.put("tags", tags);
            image.put("file", curPhotoPath);

            // If image exists in just holiday
            if (placeIndex == -1){
                holiday.getJSONArray("images").put(imageIndex, image);
            } else {
                holiday.getJSONArray("places").getJSONObject(placeIndex).getJSONArray("images").put(imageIndex, image);
            }
        } catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }
        holidayFile.updateHoliday(holiday, holidayIndex, getActivity());
        getActivity().onBackPressed();
    }


    private void getLocation() {
        Log.d(TAG, "getLocation");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
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
                            selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragLocationComplete);
                            autocompleteFragment.setText(getLocationDisplay(location.getLatitude(), location.getLongitude()));
                        }
                    }
                }
        );
    }

    private String getLocationDisplay(double lat, double lng){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);
        }catch (IOException e){
            Log.e(TAG, e.getMessage());
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
                    //getLocation();
                } else {
                    Toast.makeText(getContext(),
                            "Cannot get location permissions",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void setupLocationAutocomplete(boolean enable){
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragLocationComplete);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                selectedPlace = place;
                selectedLocation = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        AppCompatEditText originEditText = (AppCompatEditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        AppCompatImageButton originSearchBtn = (AppCompatImageButton) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button);
        AppCompatImageButton originDelBtn = (AppCompatImageButton) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
        originEditText.setEnabled(enable);
        if (selectedLocation != null){
            originEditText.setHint(getLocationDisplay(selectedLocation.latitude, selectedLocation.longitude));
        }
        originSearchBtn.setVisibility(enable ? View.VISIBLE : View.GONE);
        originDelBtn.setVisibility(enable ? View.VISIBLE : View.GONE);
//        originEditText.setText(getLocationDisplay(selectedLocation.latitude, selectedLocation.longitude));
    }

}
