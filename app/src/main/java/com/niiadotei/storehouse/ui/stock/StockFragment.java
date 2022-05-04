package com.niiadotei.storehouse.ui.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.niiadotei.storehouse.databinding.FragmentStockBinding;

public class StockFragment extends Fragment {

    private FragmentStockBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StockViewModel stockViewModel =
                new ViewModelProvider(this).get(StockViewModel.class);

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textStock;
        stockViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}