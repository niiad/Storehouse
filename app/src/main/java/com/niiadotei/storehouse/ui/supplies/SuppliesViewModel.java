package com.niiadotei.storehouse.ui.supplies;

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

public class SuppliesViewModel extends RecyclerView.Adapter<SuppliesViewModel.ViewHolder> {
    AppCompatActivity appCompatActivity;

    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    public SuppliesViewModel(AppCompatActivity appCompatActivity, JSONArray jsonArray) {
        this.appCompatActivity = appCompatActivity;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public SuppliesViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supply_history, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliesViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            String productName = databaseHelper.getProductNameFromID(jsonObject.getInt("product"));
            holder.suppliedProductNameTextView.setText(productName);

            String supplierName = "From: " + databaseHelper.getSupplierNameFromID(jsonObject.getInt("supplier"));
            holder.suppliedSupplierNameTextView.setText(supplierName);

            String quantity = "Quantity: " + jsonObject.getInt("quantity");
            holder.suppliedQuantityTextView.setText(quantity);

            String request = "Date requested: " + jsonObject.getString("request");
            holder.suppliedRequestTextView.setText(request);

            String received = "Date received: " + jsonObject.getString("received");
            holder.suppliedReceivedTextView.setText(received);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView suppliedProductNameTextView;
        TextView suppliedSupplierNameTextView;
        TextView suppliedQuantityTextView;
        TextView suppliedRequestTextView;
        TextView suppliedReceivedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            suppliedProductNameTextView = itemView.findViewById(R.id.suppliedProductNameTextView);
            suppliedSupplierNameTextView = itemView.findViewById(R.id.suppliedSupplierNameTextView);
            suppliedQuantityTextView = itemView.findViewById(R.id.suppliedQuantityTextView);
            suppliedRequestTextView = itemView.findViewById(R.id.suppliedRequestTextView);
            suppliedReceivedTextView = itemView.findViewById(R.id.suppliedReceivedTextView);
        }
    }
}
