package com.goodwind.coursework.ui.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodwind.coursework.HolidayFile;
import com.goodwind.coursework.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GalleryFragment extends Fragment {

    HolidayFile holidayFile;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private GalleryViewModel galViewModel;
    int holidayIndex;
    int placeIndex;
    JSONObject holiday;
    JSONObject place;
    EditText dateView;
    final String holidaySaveLocation = "holidays.json";
    final int REQUEST_IMAGE_CAPTURE = 2;
    ImageView lastImage;
    String curPhotoPath;
    GalleryAdapter imgPreviews;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView imgPreviewsView = root.findViewById(R.id.lvPlaces);
        imgPreviewsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        holidayIndex = getArguments().getInt("holidayIndex");
        placeIndex = getArguments().getInt("placeIndex");
        if (holidayIndex != -1){
            holiday = holidayFile.getHolidayByIndex(holidayIndex);
            if (placeIndex != -1) {
                place = holidayFile.getHolidayPlaceByIndex(holidayIndex, placeIndex);
            }
            imgPreviews = new GalleryAdapter(holidayFile.getImageFilePaths(holiday, placeIndex), holidayFile.getImages(holiday, placeIndex), getContext(), holidayIndex, placeIndex);
        } else {
            imgPreviews = new GalleryAdapter(holidayFile.getImageFilePaths(), holidayFile.getImages(), getContext());
        }


        imgPreviewsView.setAdapter(imgPreviews);


        final FloatingActionButton fabAdd = root.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addImage();
            }
        });

        holidayFile = new HolidayFile(holidaySaveLocation, getContext(), getActivity());
        return root;
    }

    private void addImage(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e){
                Log.e("aaa", e.getMessage());
                e.printStackTrace();
            }
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.goodwind.coursework", photoFile);
                Log.d("addImage", "Saving photo: "+photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data", false);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_IMAGE_CAPTURE:
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                lastImage.setImageBitmap(imageBitmap);
//                break;
//        }
        Bitmap b = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(curPhotoPath), 200, 200);

        imgPreviews.addBm(curPhotoPath, b);
        try {
            JSONObject imgJSON = new JSONObject();
            JSONObject locationJSON = new JSONObject();
            imgJSON.put("location", locationJSON);
            imgJSON.put("tags", "");
            imgJSON.put("file", curPhotoPath);
            if (placeIndex == -1){
                JSONArray images = holiday.getJSONArray("images");
                images.put(imgJSON);
                holiday.put("images", images);
            } else {

                JSONArray images = holiday.getJSONArray("places").getJSONObject(placeIndex).getJSONArray("images");
                images.put(imgJSON);
                holiday.getJSONArray("places").getJSONObject(placeIndex).put("images", images);
            }
            holidayFile.updateHoliday(holiday, holidayIndex, getActivity());
        } catch (JSONException e) {
            Log.e("save img", e.getMessage());
        }

    }


    private File createImageFile() throws IOException {


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
        }
        Log.d("create img file", "extStorage: permissions granted");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("aaa", "File create location: "+storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        curPhotoPath = image.getAbsolutePath();
        return image;
    }
}
