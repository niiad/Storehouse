package com.niiadotei.storehouse.ui.finance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

            holder.displayProductNameTextView.setText(jsonObject.getString("display"));
            holder.productNameTextView.setText(jsonObject.getString("name"));

            double cost = jsonObject.getDouble("cost");
            String costValue = String.valueOf(cost);
            holder.costPricePerUnitTextView.setText(costValue);

            int quantity = jsonObject.getInt("quantity");
            String quantityValue = String.valueOf(quantity);
            holder.quantityInStockTextView.setText(quantityValue);

            double totalCost = cost * quantity;
            String totalCostValue = String.valueOf(totalCost);
            holder.totalCostPriceTextView.setText(totalCostValue);

            double sellingPrice = jsonObject.getDouble("price");
            String sellingPriceValue = String.valueOf(sellingPrice);
            holder.sellingPricePerUnitTextView.setText(sellingPriceValue);

            double totalSellingPrice = sellingPrice * quantity;
            String totalSellingPriceValue = String.valueOf(totalSellingPrice);
            holder.totalSellingPriceTextView.setText(totalSellingPriceValue);

            double profitPerUnit = sellingPrice - cost;
            String profitPerUnitValue = String.valueOf(profitPerUnit);
            holder.profitPerUnitTextView.setText(profitPerUnitValue);

            double totalProfit = profitPerUnit * quantity;
            String totalProfitValue = String.valueOf(totalProfit);
            holder.totalProfitTextView.setText(totalProfitValue);

            holder.profitPercentTextView.setText("N/A");

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
