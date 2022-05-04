package com.niiadotei.storehouse.ui.supplychain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupplyChainViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SupplyChainViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is supply chain fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}