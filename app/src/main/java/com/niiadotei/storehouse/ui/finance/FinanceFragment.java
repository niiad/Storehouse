package com.niiadotei.storehouse.ui.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentFinanceBinding;

public class FinanceFragment extends Fragment {
    DatabaseHelper databaseHelper;

    RecyclerView recyclerView;

    FinanceViewModel financeViewModel;

    private FragmentFinanceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinanceBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        recyclerView = binding.financeProductRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

        financeViewModel = new FinanceViewModel(this, databaseHelper.getProductArray());

        recyclerView.setAdapter(financeViewModel);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
