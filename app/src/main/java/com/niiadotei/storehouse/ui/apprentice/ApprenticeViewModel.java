package com.niiadotei.storehouse.ui.apprentice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ApprenticeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ApprenticeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is apprentice fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
