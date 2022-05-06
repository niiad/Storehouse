package com.niiadotei.storehouse.ui.finance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FinanceViewModel extends RecyclerView.Adapter<FinanceViewModel.ViewHolder> {
    Fragment fragment;

    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    public FinanceViewModel(Fragment fragment, JSONArray jsonArray) {
        this.fragment = fragment;
        this.jsonArray = jsonArray;
    }

    public void updateArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public FinanceViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finance_product, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(Locale.US);


            holder.displayProductNameTextView.setText(jsonObject.getString("display"));
            holder.productNameTextView.setText(jsonObject.getString("name"));

            double cost = jsonObject.getDouble("cost");
            String costValue = currencyInstance.format(cost);
            holder.costPricePerUnitTextView.setText(costValue);

            int quantity = jsonObject.getInt("quantity");
            String quantityValue = String.valueOf(quantity);
            holder.quantityInStockTextView.setText(quantityValue);

            double totalCost = Double.parseDouble(decimalFormat.format(cost)) * quantity;
            String totalCostValue = currencyInstance.format(totalCost);
            holder.totalCostPriceTextView.setText(totalCostValue);

            double sellingPrice = jsonObject.getDouble("price");
            String sellingPriceValue = currencyInstance.format(sellingPrice);
            holder.sellingPricePerUnitTextView.setText(sellingPriceValue);

            double totalSellingPrice = Double.parseDouble(decimalFormat.format(sellingPrice)) * quantity;
            String totalSellingPriceValue = currencyInstance.format(totalSellingPrice);
            holder.totalSellingPriceTextView.setText(totalSellingPriceValue);

            double profitPerUnit = Double.parseDouble(decimalFormat.format(sellingPrice - cost));
            String profitPerUnitValue = currencyInstance.format(profitPerUnit);
            holder.profitPerUnitTextView.setText(profitPerUnitValue);

            double totalProfit = Double.parseDouble(decimalFormat.format(profitPerUnit * quantity));
            String totalProfitValue = currencyInstance.format(totalProfit);
            holder.totalProfitTextView.setText(totalProfitValue);

            holder.profitPercentTextView.setText("N/A");

            if (sellingPrice > cost) {
                holder.totalProfitTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.secondary_emerald));
            } else if (sellingPrice < cost) {
                holder.totalProfitTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.secondary_red));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayProductNameTextView;
        TextView productNameTextView;
        TextView costPricePerUnitTextView;
        TextView totalCostPriceTextView;
        TextView sellingPricePerUnitTextView;
        TextView totalSellingPriceTextView;
        TextView profitPerUnitTextView;
        TextView totalProfitTextView;
        TextView quantityInStockTextView;
        TextView profitPercentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            displayProductNameTextView = itemView.findViewById(R.id.displayProductName);
            productNameTextView = itemView.findViewById(R.id.productName);
            costPricePerUnitTextView = itemView.findViewById(R.id.costPricePerUnit);
            totalCostPriceTextView = itemView.findViewById(R.id.totalCostPrice);
            sellingPricePerUnitTextView = itemView.findViewById(R.id.sellingPricePerUnit);
            totalSellingPriceTextView = itemView.findViewById(R.id.totalSellingPrice);
            profitPerUnitTextView = itemView.findViewById(R.id.profitPerUnit);
            totalProfitTextView = itemView.findViewById(R.id.totalProfit);
            quantityInStockTextView = itemView.findViewById(R.id.quantityInStock);
            profitPercentTextView = itemView.findViewById(R.id.profitPercent);
        }
    }

}
