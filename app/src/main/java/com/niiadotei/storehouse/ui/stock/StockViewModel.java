package com.niiadotei.storehouse.ui.stock;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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
        double price = 0.0;
        int id = 0;

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            id = jsonObject.getInt("id");

            holder.displayNameTextView.setText(jsonObject.getString("display"));
            holder.nameTextView.setText(jsonObject.getString("name"));

            Resources resources = holder.itemView.getContext().getResources();
            Locale locale = resources.getConfiguration().locale;
            NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

            price = jsonObject.getDouble("price");
            holder.priceTextView.setText(currencyInstance.format(price));

            holder.supplierTextView.setText(databaseHelper.getSupplierNameFromID(jsonObject.getString("supplier")));

            String quantity = "Qty: " + jsonObject.getString("quantity");
            holder.quantityTextView.setText(quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double finalPrice = price;
        int finalId = id;
        holder.itemView.setOnClickListener(view -> {
            String[] customers = databaseHelper.getAllCustomersWithPhone();
            Arrays.sort(customers);
            final String[] selectedCustomer = new String[1];
            selectedCustomer[0] = "";
            int[] checkedCustomer = {-1};

            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_purchase);
            dialog.show();

            EditText editText = dialog.findViewById(R.id.purchase_quantity);

            Button selectCustomer = dialog.findViewById(R.id.selectCustomerButton);
            selectCustomer.setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                alertDialog.setTitle("Select the customer buying");
                alertDialog.setSingleChoiceItems(customers, checkedCustomer[0], (dialogInterface, i) -> {
                    checkedCustomer[0] = i;
                    selectedCustomer[0] = customers[i];

                    selectCustomer.setText(selectedCustomer[0].substring(0, 10));
                    dialogInterface.dismiss();
                });

                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

                });

                AlertDialog customerAlertDialog = alertDialog.create();
                customerAlertDialog.show();
            });

            Button makePurchase = dialog.findViewById(R.id.makePurchaseButton);
            makePurchase.setOnClickListener(view1 -> {
                try {
                    int quantity = Integer.parseInt(editText.getText().toString().trim());
                    int stockQuantity = databaseHelper.getProductQuantityFromID(finalId);

                    if (quantity == 0) {
                        Toast.makeText(holder.itemView.getContext(), "Quantity cannot be 0", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }

                    if (quantity > stockQuantity) {
                        Toast.makeText(holder.itemView.getContext(), "Stock quantity not enough to make purchase", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
                    builder.setTitle("Confirm");

                    double totalPurchaseCost = finalPrice * quantity;

                    Resources resources = holder.itemView.getContext().getResources();
                    Locale locale = resources.getConfiguration().locale;
                    NumberFormat currencyInstance = DecimalFormat.getCurrencyInstance(locale);

                    builder.setMessage("Collect from the customer, a total cost of " + currencyInstance.format(totalPurchaseCost) + " before confirming.");

                    builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());

                            int productID = jsonObject.getInt("id");

                            databaseHelper.updateProductQuantity(productID, -quantity);

                            databaseHelper.insertPurchases(selectedCustomer[0], holder.nameTextView.getText().toString(), totalPurchaseCost, quantity, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

                            Toast.makeText(holder.itemView.getContext(), "Purchase successfully made", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                        } catch (JSONException e) {
                            Toast.makeText(holder.itemView.getContext(), "Error making the purchase", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }));

                    builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));
                    builder.show();
                } catch (NumberFormatException e) {
                    Toast.makeText(holder.itemView.getContext(), "Quantity format not supported", Toast.LENGTH_LONG).show();
                }
            });

        });

        holder.itemView.setOnLongClickListener(view -> {
            int getPosition = holder.getAdapterPosition();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(getPosition);

                String stringID = jsonObject.getString("id");

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this product?");

                builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
                    databaseHelper.deleteProduct(stringID);
                    jsonArray.remove(getPosition);
                    notifyItemRemoved(getPosition);
                    notifyItemRangeRemoved(getPosition, jsonArray.length());
                }));

                builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));
                builder.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        });
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