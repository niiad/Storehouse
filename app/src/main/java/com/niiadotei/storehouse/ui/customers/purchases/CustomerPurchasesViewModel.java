package com.niiadotei.storehouse.ui.customers.purchases;

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

public class CustomerPurchasesViewModel extends RecyclerView.Adapter<CustomerPurchasesViewModel.ViewHolder> {
    AppCompatActivity appCompatActivity;

    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    public CustomerPurchasesViewModel(AppCompatActivity appCompatActivity, JSONArray jsonArray) {
        this.appCompatActivity = appCompatActivity;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public CustomerPurchasesViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_purchases, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerPurchasesViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.customerPurchasedDateTextView.setText(jsonObject.getString("date"));
            holder.customerPurchasedProductTextView.setText(jsonObject.getString("product"));

            String quantity = "Quantity purchased: " + jsonObject.getInt("quantity");
            holder.customerPurchasedQuantityTextView.setText(quantity);

            Resources resources = holder.itemView.getContext().getResources();
            Locale locale = resources.getConfiguration().locale;
            NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

            holder.customerPurchasedAmountTextView.setText(currencyInstance.format(jsonObject.getDouble("amount")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerPurchasedDateTextView;
        TextView customerPurchasedProductTextView;
        TextView customerPurchasedQuantityTextView;
        TextView customerPurchasedAmountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerPurchasedDateTextView = itemView.findViewById(R.id.customerPurchasedDateTextView);
            customerPurchasedProductTextView = itemView.findViewById(R.id.customerPurchasedProductTextView);
            customerPurchasedQuantityTextView = itemView.findViewById(R.id.customerPurchasedQuantityTextView);
            customerPurchasedAmountTextView = itemView.findViewById(R.id.customerPurchasedAmountTextView);
        }
    }
}
