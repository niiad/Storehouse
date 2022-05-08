package com.niiadotei.storehouse.ui.purchases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentPurchasesBinding;

public class PurchasesFragment extends DialogFragment {
    RecyclerView recyclerView;

    PurchasesViewModel purchasesViewModel;

    DatabaseHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.niiadotei.storehouse.databinding.FragmentPurchasesBinding binding = FragmentPurchasesBinding.inflate(inflater, container, false);

        recyclerView = binding.purchasesRecyclerView;

        databaseHelper = new DatabaseHelper(this.getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        purchasesViewModel = new PurchasesViewModel(this, databaseHelper.getPurchasesArray());
        recyclerView.setAdapter(purchasesViewModel);

        return binding.getRoot();
    }
}
