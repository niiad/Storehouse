package com.niiadotei.storehouse.ui.supplychain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.niiadotei.storehouse.databinding.FragmentSupplyChainBinding;

public class SupplyChainFragment extends Fragment {

    private FragmentSupplyChainBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SupplyChainViewModel supplyChainViewModel =
                new ViewModelProvider(this).get(SupplyChainViewModel.class);

        binding = FragmentSupplyChainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        supplyChainViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}