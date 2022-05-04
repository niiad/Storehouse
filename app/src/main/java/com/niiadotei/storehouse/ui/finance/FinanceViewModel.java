package com.niiadotei.storehouse.ui.finance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinanceViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public FinanceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is finance fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
