package com.niiadotei.storehouse.ui.finance;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentFinanceBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FinanceFragment extends Fragment {
    DatabaseHelper databaseHelper;

    RecyclerView recyclerView;

    FinanceViewModel financeViewModel;

    private FragmentFinanceBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinanceBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        Accountant accountant = new Accountant(requireContext());

        Resources resources = requireContext().getResources();
        Locale locale = resources.getConfiguration().locale;
        NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

        recyclerView = binding.financeProductRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

        financeViewModel = new FinanceViewModel(this, databaseHelper.getProductArray());

        recyclerView.setAdapter(financeViewModel);

        TextView currentStockValueTextView = binding.currentStockValueTextView;
        currentStockValueTextView.setText(currencyInstance.format(accountant.getCurrentStockValue()));

        TextView mostPricedProductTextView = binding.mostPricedProductTextView;
        mostPricedProductTextView.setText(accountant.getMostPricedProductName());

        TextView leastPricedProductTextView = binding.leastPricedProductTextView;
        leastPricedProductTextView.setText(accountant.getLeastPricedProductName());

        TextView mostProfitableProductTextView = binding.mostProfitableProductTextView;
        mostProfitableProductTextView.setText(accountant.getMostProfitableProductName());

        return binding.getRoot();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.finance_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
