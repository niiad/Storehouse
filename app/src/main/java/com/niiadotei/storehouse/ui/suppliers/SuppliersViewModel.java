package com.niiadotei.storehouse.ui.suppliers;

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

public class SuppliersViewModel extends RecyclerView.Adapter<SuppliersViewModel.ViewHolder> {
    AppCompatActivity appCompatActivity;

    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    public SuppliersViewModel(AppCompatActivity appCompatActivity, JSONArray jsonArray) {
        this.appCompatActivity = appCompatActivity;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public SuppliersViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suppliers, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.supplierNameTextView.setText(jsonObject.getString("name"));
            holder.supplierPhoneNumberTextView.setText(jsonObject.getString("phone"));
            holder.supplierLocationTextView.setText(jsonObject.getString("location"));
            holder.supplierDisplayNameTextView.setText(jsonObject.getString("display"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView supplierNameTextView;
        TextView supplierPhoneNumberTextView;
        TextView supplierLocationTextView;
        TextView supplierDisplayNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            supplierNameTextView = itemView.findViewById(R.id.supplierNameTextView);
            supplierPhoneNumberTextView = itemView.findViewById(R.id.supplierPhoneNumberTextView);
            supplierLocationTextView = itemView.findViewById(R.id.supplierLocationTextView);
            supplierDisplayNameTextView = itemView.findViewById(R.id.supplierDisplayNameTextView);
        }
    }
}
