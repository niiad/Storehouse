package com.niiadotei.storehouse.ui.stock;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockViewModel extends RecyclerView.Adapter<StockViewModel.ViewHolder> implements Filterable {
    Fragment fragment;

    JSONArray jsonArray;
    JSONArray filteredJsonArray;

    DatabaseHelper databaseHelper;

    public FilteredViewHolder filteredViewHolder;

    public StockViewModel(Fragment fragment, JSONArray jsonArray) {
        this.fragment = fragment;
        this.jsonArray = jsonArray;

        filteredJsonArray = new JSONArray();
    }

    public void updateArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public StockViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.displayNameTextView.setText(jsonObject.getString("display"));
            holder.nameTextView.setText(jsonObject.getString("name"));

            String price = "GHC " + jsonObject.getString("price");
            holder.priceTextView.setText(price);

            holder.supplierTextView.setText(databaseHelper.getSupplierNameFromID(jsonObject.getString("supplier")));

            String quantity = "Qty: " + jsonObject.getString("quantity");
            holder.quantityTextView.setText(quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // holder click listeners
    }

    @Override
    public Filter getFilter() {
        if (filteredViewHolder == null) {
            filteredJsonArray = jsonArray;

            filteredViewHolder = new StockViewModel.FilteredViewHolder(this, filteredJsonArray);
        }
        return filteredViewHolder;
    }

    public JSONArray getFilteredJsonArray(String name) {
        return databaseHelper.getFilteredProductArray(name);
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayNameTextView;
        TextView nameTextView;
        TextView priceTextView;
        TextView supplierTextView;
        TextView quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            displayNameTextView = itemView.findViewById(R.id.displayProductName);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            supplierTextView = itemView.findViewById(R.id.productSupplier);
            quantityTextView = itemView.findViewById(R.id.productQuantity);
        }
    }

    private static class FilteredViewHolder extends Filter {
        private final StockViewModel stockViewModel;

        private final JSONArray jsonArray;
        private JSONArray filteredJsonArray;

        private FilteredViewHolder(StockViewModel stockViewModel, JSONArray jsonArray) {
            this.stockViewModel = stockViewModel;

            this.jsonArray = jsonArray;
            this.filteredJsonArray = new JSONArray();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults results = new FilterResults();

            if (charSequence.length() == 0) {
                filteredJsonArray = jsonArray;
            } else {
                final String filterPattern = charSequence.toString().trim();

                filteredJsonArray = stockViewModel.getFilteredJsonArray(filterPattern);
            }

            results.values = filteredJsonArray;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            stockViewModel.updateArray((JSONArray) filterResults.values);
            stockViewModel.notifyDataSetChanged();
        }
    }
}