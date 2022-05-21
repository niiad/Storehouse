package com.niiadotei.storehouse.ui.apprentice;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApprenticeFragment extends Fragment{
    private FragmentApprenticeBinding binding;

    DatabaseHelper databaseHelper;

    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentApprenticeBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        Apprentice apprentice = new Apprentice(date, this.requireContext());

        Resources resources = this.getResources();
        Locale locale = resources.getConfiguration().locale;
        NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

        Button salesDateButton = binding.salesDateButton;
        salesDateButton.setOnClickListener(view -> {
            DialogFragment datePicker = new DatePicker();
            datePicker.show(getChildFragmentManager(), "Pick a date");
        });

        TextView totalSalesMadeTextView = binding.salesMadeTextView;
        double totalSalesMade = apprentice.getTotalSalesMadeToday();
        totalSalesMadeTextView.setText(currencyInstance.format(totalSalesMade));

        TextView salesQuantityTextView = binding.salesQuantityTextView;
        salesQuantityTextView.setText(String.valueOf(apprentice.getTotalQuantitySoldToday()));

        TextView salesNumberTextView = binding.salesNumberTextView;
        salesNumberTextView.setText(String.valueOf(apprentice.getTotalNumberOfSalesToday()));

        TextView customerNumberTextView = binding.customerNumberTextView;
        customerNumberTextView.setText(String.valueOf(apprentice.getNumberOfNewCustomersToday()));

        return binding.getRoot();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
