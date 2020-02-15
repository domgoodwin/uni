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
import android.widget.Toast;

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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageFragment extends Fragment {

    final String holidaySaveLocation = "holidays.json";
    GalleryViewModel galViewModel;
    ImageView mainImage;
    String curPhotoPath;
    FloatingActionButton fabDelete;
    FloatingActionButton fabShare;
    private int holidayIndex;
    private int placeIndex;
    final int REQUEST_IMAGE_CAPTURE = 2;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_img_view, container, false);
        curPhotoPath = getArguments().getString("filePath");
        holidayIndex = getArguments().getInt("holidayIndex");
        placeIndex = getArguments().getInt("placeIndex");

        if (curPhotoPath != null) {
            mainImage = root.findViewById(R.id.imgMain);
            mainImage.setImageBitmap(BitmapFactory.decodeFile(curPhotoPath));
        } else {
            Log.e("image", "How are you here with no image");
        }

        fabDelete = root.findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteImage(v);
            }
        });
        fabShare = root.findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shareImage(v);
            }
        });

        return root;
    }

    public void deleteImage(View v){
        if (holidayIndex == -1){
            Toast.makeText(getContext(), "You can't delete an image not attached to a place or holiday", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO
    }

    public void shareImage(View v){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        Uri imageToShare = FileProvider.getUriForFile(getContext(), "com.goodwind.coursework", getImageFile());
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageToShare);
        sendIntent.setType("image/jpeg");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private File getImageFile() {


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
        }


        File image = new File(curPhotoPath);

        return image;
    }

}
