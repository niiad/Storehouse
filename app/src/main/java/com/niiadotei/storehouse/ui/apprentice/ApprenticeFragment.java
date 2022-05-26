package com.niiadotei.storehouse.ui.apprentice;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApprenticeFragment extends Fragment{
    private FragmentApprenticeBinding binding;

    DatabaseHelper databaseHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentApprenticeBinding.inflate(inflater, container, false);

        Resources resources = this.getResources();
        Locale locale = resources.getConfiguration().locale;
        NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        TextView pastSalesMadeTextView = binding.pastSalesMadeTextView;
        TextView pastSalesQuantityTextView = binding.pastSalesQuantityTextView;
        TextView pastSalesNumberTextView = binding.pastSalesNumberTextView;
        TextView pastCustomerNumberTextView = binding.pastCustomerNumberTextView;
        TextView dateOfPastSalesTextView = binding.dateOfPastSales;

        TextView salesPerformanceValueTextView = binding.salesPerformanceValue;
        TextView salesPerformancePercentTextView = binding.salesPerformancePercent;
        TextView differenceInQuantityTextView = binding.differenceInQuantitySold;

        databaseHelper = new DatabaseHelper(this.getActivity());

        ApprenticeRetriever apprenticeRetriever = new ApprenticeRetriever(date, this.requireContext());

        AppCompatButton salesDateButton = binding.salesDateButton;

        TextView totalSalesMadeTextView = binding.salesMadeTextView;
        double totalSalesMade = apprenticeRetriever.getTotalSalesMadeToday();
        totalSalesMadeTextView.setText(currencyInstance.format(totalSalesMade));

        TextView salesQuantityTextView = binding.salesQuantityTextView;
        salesQuantityTextView.setText(String.valueOf(apprenticeRetriever.getTotalQuantitySoldToday()));

        TextView salesNumberTextView = binding.salesNumberTextView;
        salesNumberTextView.setText(String.valueOf(apprenticeRetriever.getTotalNumberOfSalesToday()));

        TextView customerNumberTextView = binding.customerNumberTextView;
        customerNumberTextView.setText(String.valueOf(apprenticeRetriever.getNumberOfNewCustomersToday()));

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            String pastDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());

            ApprenticeRetriever apprenticeRetrieverPast = new ApprenticeRetriever(pastDate, requireContext());

            String dateOfPastSales = "Date of Sales: " + pastDate;
            dateOfPastSalesTextView.setText(dateOfPastSales);

            double pastTotalSalesMade = apprenticeRetrieverPast.getTotalSalesMadeToday();
            pastSalesMadeTextView.setText(currencyInstance.format(pastTotalSalesMade));

            pastSalesQuantityTextView.setText(String.valueOf(apprenticeRetrieverPast.getTotalQuantitySoldToday()));

            pastSalesNumberTextView.setText(String.valueOf(apprenticeRetrieverPast.getTotalNumberOfSalesToday()));

            pastCustomerNumberTextView.setText(String.valueOf(apprenticeRetrieverPast.getNumberOfNewCustomersToday()));

            double salesPerformanceValue = totalSalesMade - pastTotalSalesMade;
            salesPerformanceValueTextView.setText(currencyInstance.format(salesPerformanceValue));

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double salesPerformancePercent = 0.0;

            if (totalSalesMade != 0.0) {
                salesPerformancePercent = Double.parseDouble(decimalFormat.format(((totalSalesMade - pastTotalSalesMade)  / totalSalesMade) * 100));
            }

            if (Double.isNaN(salesPerformancePercent)) {
                salesPerformancePercent = 0.0;
            }

            String salesPerformancePercentString = String.format(Locale.US, "%.0f%%", salesPerformancePercent);
            salesPerformancePercentTextView.setText(salesPerformancePercentString);

            int differenceInQuantity = apprenticeRetriever.getTotalQuantitySoldToday() - apprenticeRetrieverPast.getTotalQuantitySoldToday();
            differenceInQuantityTextView.setText(String.valueOf(differenceInQuantity));
        };

        salesDateButton.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            datePickerDialog.show();

        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
