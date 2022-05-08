package com.niiadotei.storehouse.ui.apprentice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ApprenticeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ApprenticeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("The apprentice is an artificial intelligence that will help you better manage your stock and supply and understand your customers.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
