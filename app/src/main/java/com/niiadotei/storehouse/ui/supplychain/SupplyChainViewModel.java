package com.niiadotei.storehouse.ui.supplychain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SupplyChainViewModel extends RecyclerView.Adapter<SupplyChainViewModel.ViewHolder> implements Filterable {
    Fragment fragment;

    JSONArray jsonArray;
    JSONArray filteredJsonArray;

    DatabaseHelper databaseHelper;

    public FilteredViewHolder filteredViewHolder;


    public SupplyChainViewModel(Fragment fragment, JSONArray jsonArray) {
        this.fragment = fragment;
        this.jsonArray = jsonArray;

        filteredJsonArray = new JSONArray();
    }

    public void updateArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public SupplyChainViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supply_chain, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplyChainViewModel.ViewHolder holder, int position) {
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("supplyChain", Context.MODE_PRIVATE);

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            int product = jsonObject.getInt("product");

            holder.suppliedProductTextView.setText(databaseHelper.getProductNameFromID(jsonObject.getInt("product")));
            holder.supplierTextView.setText(databaseHelper.getSupplierNameFromID(jsonObject.getString("supplier")));

            String request = holder.suppliedProductTextView.getText().toString() + "Request";
            holder.requestTextView.setText(sharedPreferences.getString(request, ""));

            boolean supplyAlreadyProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.getText().toString(), false);

            if (supplyAlreadyProgress) {
                holder.supplyButton.setText(R.string.supply_progress_hint);
                holder.supplyButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary_emerald));
                holder.supplyButton.setEnabled(false);

                String supplyQuantityView = holder.suppliedProductTextView.getText().toString() + "Quantity";
                int quantity = sharedPreferences.getInt(supplyQuantityView, 0);

                holder.supplyQuantityEditText.setText(String.valueOf(quantity));
                holder.supplyQuantityEditText.setEnabled(false);

                holder.receivedButton.setEnabled(true);
                holder.receivedButton.setText(R.string.received_button);
            } else {
                holder.receivedButton.setText(R.string.received_progress_hint);
            }


            final int[] quantity = {0};

            holder.supplyButton.setOnClickListener(view -> {
                String quantityFromUser = holder.supplyQuantityEditText.getText().toString();

                try {
                    quantity[0] = Integer.parseInt(quantityFromUser);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String supplyQuantityView = holder.suppliedProductTextView.getText().toString() + "Quantity";

                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    editor.putString(request, date);
                    editor.putInt(supplyQuantityView, quantity[0]);
                    editor.apply();

                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Format not supported", Toast.LENGTH_LONG).show();
                }

                if (TextUtils.isEmpty(quantityFromUser)) {
                    Toast.makeText(view.getContext(), "Quantity not given", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(holder.suppliedProductTextView.getText().toString(), true);
                editor.apply();

                boolean inProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.getText().toString(), false);

                if (inProgress) {
                    holder.supplyButton.setText(R.string.supply_progress_hint);
                    holder.supplyButton.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.secondary_emerald));
                    holder.supplyButton.setEnabled(false);

                    holder.supplyQuantityEditText.setEnabled(false);

                    holder.receivedButton.setEnabled(true);
                    holder.receivedButton.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primary_dark));
                    holder.receivedButton.setText(R.string.received_button);
                }
            });

            holder.receivedButton.setOnClickListener(view -> {
                boolean inProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.getText().toString(), false);

                if (inProgress) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String supplyQuantityView = holder.suppliedProductTextView.getText().toString() + "Quantity";

                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    databaseHelper.updateProductQuantity(product, sharedPreferences.getInt(supplyQuantityView, 0));
                    databaseHelper.updateSupply(product, sharedPreferences.getInt(supplyQuantityView, 0), sharedPreferences.getString(request, ""), date);

                    Toast.makeText(view.getContext(), "Supply successful!", Toast.LENGTH_LONG).show();

                    holder.supplyButton.setEnabled(true);
                    holder.supplyButton.setText(R.string.supply_button);
                    holder.supplyButton.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.secondary_red));

                    holder.supplyQuantityEditText.setText("");
                    holder.supplyQuantityEditText.setEnabled(true);

                    holder.receivedButton.setEnabled(false);
                    holder.receivedButton.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primary_light));
                    holder.receivedButton.setText(R.string.received_progress_hint);

                    holder.requestTextView.setText("");

                    editor.putBoolean(holder.suppliedProductTextView.getText().toString(), false);
                    editor.apply();
                } else {
                    holder.receivedButton.setText(R.string.received_progress_hint);
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnLongClickListener(view -> {
            int getPosition = holder.getAdapterPosition();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(getPosition);

                String stringID = jsonObject.getString("id");

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this supply?");

                builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
                    databaseHelper.deleteSupplyChain(stringID);
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

            filteredViewHolder = new SupplyChainViewModel.FilteredViewHolder(this, filteredJsonArray);
        }

        return filteredViewHolder;
    }

    public JSONArray getFilteredJsonArray(String string) {
        // needs work
        return jsonArray;
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView suppliedProductTextView;
        TextView supplierTextView;
        TextView requestTextView;
        EditText supplyQuantityEditText;
        Button supplyButton;
        Button receivedButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            suppliedProductTextView = itemView.findViewById(R.id.displaySuppliedProductName);
            supplierTextView = itemView.findViewById(R.id.displaySupplierName);
            requestTextView = itemView.findViewById(R.id.requestTextView);
            supplyQuantityEditText = itemView.findViewById(R.id.supply_quantity);
            supplyButton = itemView.findViewById(R.id.supply_button);
            receivedButton = itemView.findViewById(R.id.received_button);

        }
    }

    private static class FilteredViewHolder extends Filter {
        private final SupplyChainViewModel supplyChainViewModel;

        private final JSONArray jsonArray;
        private JSONArray filteredJsonArray;

        private FilteredViewHolder(SupplyChainViewModel supplyChainViewModel, JSONArray jsonArray) {
            this.supplyChainViewModel = supplyChainViewModel;

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

                filteredJsonArray = supplyChainViewModel.getFilteredJsonArray(filterPattern);
            }

            results.values = filteredJsonArray;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            supplyChainViewModel.updateArray((JSONArray) filterResults.values);
            supplyChainViewModel.notifyDataSetChanged();
        }
    }
}