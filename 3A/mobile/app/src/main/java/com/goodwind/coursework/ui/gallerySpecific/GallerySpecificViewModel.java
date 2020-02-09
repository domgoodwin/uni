package com.goodwind.coursework.ui.gallerySpecific;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GallerySpecificViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GallerySpecificViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is send fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}