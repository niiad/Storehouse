package com.niiadotei.storehouse.ui.purchases;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PurchasesViewModel extends RecyclerView.Adapter<PurchasesViewModel.ViewHolder> {
    AppCompatActivity appCompatActivity;

    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    public PurchasesViewModel(AppCompatActivity appCompatActivity, JSONArray jsonArray) {
        this.appCompatActivity = appCompatActivity;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public PurchasesViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchases, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasesViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.purchasedCustomerNameTextView.setText(jsonObject.getString("customer"));

            holder.purchasedProductTextView.setText(jsonObject.getString("product"));

            double amount = jsonObject.getDouble("amount");

            Resources resources = holder.itemView.getContext().getResources();
            Locale locale = resources.getConfiguration().locale;
            NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

            holder.purchasedPriceTextView.setText(currencyInstance.format(amount));

            int quantity = jsonObject.getInt("quantity");
            holder.purchasedQuantityTextView.setText(String.valueOf(quantity));

            holder.purchasedDateTextView.setText(jsonObject.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView purchasedCustomerNameTextView;
        TextView purchasedProductTextView;
        TextView purchasedPriceTextView;
        TextView purchasedQuantityTextView;
        TextView purchasedDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            purchasedCustomerNameTextView = itemView.findViewById(R.id.purchasedCustomerNameTextView);
            purchasedProductTextView = itemView.findViewById(R.id.purchasedProductTextView);
            purchasedPriceTextView = itemView.findViewById(R.id.purchasedPriceTextView);
            purchasedQuantityTextView = itemView.findViewById(R.id.purchasedQuantityTextView);
            purchasedDateTextView = itemView.findViewById(R.id.purchasedDateTextView);
        }
    }
}
