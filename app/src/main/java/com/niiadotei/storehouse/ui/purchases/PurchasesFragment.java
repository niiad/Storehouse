package com.niiadotei.storehouse.ui.purchases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.niiadotei.storehouse.databinding.FragmentPurchasesBinding;

public class PurchasesFragment extends Fragment {
    private FragmentPurchasesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPurchasesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
