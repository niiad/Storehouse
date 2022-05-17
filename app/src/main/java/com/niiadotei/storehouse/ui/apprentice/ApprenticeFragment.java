package com.niiadotei.storehouse.ui.apprentice;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApprenticeFragment extends Fragment {
    private FragmentApprenticeBinding binding;

    DatabaseHelper databaseHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentApprenticeBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        Resources resources = this.getResources();
        Locale locale = resources.getConfiguration().locale;
        NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

        TextView totalSalesMadeTextView = binding.salesMadeTextView;
        double totalSalesMade = getTotalSalesMadeToday();
        totalSalesMadeTextView.setText(currencyInstance.format(totalSalesMade));

        TextView salesQuantityTextView = binding.salesQuantityTextView;
        salesQuantityTextView.setText(String.valueOf(getTotalQuantitySoldToday()));

        TextView salesNumberTextView = binding.salesNumberTextView;
        salesNumberTextView.setText(String.valueOf(getTotalNumberOfSalesToday()));

        TextView customerNumberTextView = binding.customerNumberTextView;
        customerNumberTextView.setText(String.valueOf(getNumberOfNewCustomersToday()));

        return binding.getRoot();
    }

    private double getTotalSalesMadeToday() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        JSONArray jsonArray = databaseHelper.getPurchasesArrayFromDate(date);

        double totalSales = 0.0;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                totalSales += jsonObject.getDouble("amount");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    private int getTotalQuantitySoldToday() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        JSONArray jsonArray = databaseHelper.getPurchasesArrayFromDate(date);

        int quantity = 0;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                quantity += jsonObject.getInt("quantity");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return quantity;
    }

    private int getTotalNumberOfSalesToday() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        JSONArray jsonArray = databaseHelper.getPurchasesArrayFromDate(date);

        return jsonArray.length();
    }

    private int getNumberOfNewCustomersToday() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        JSONArray jsonArray = databaseHelper.getCustomerArrayFromDate(date);

        return jsonArray.length();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
