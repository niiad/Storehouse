package com.niiadotei.storehouse.ui.customers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class CustomersViewModel extends RecyclerView.Adapter<CustomersViewModel.ViewHolder> implements Filterable {
    Fragment fragment;
    JSONArray jsonArray;

    DatabaseHelper databaseHelper;

    JSONArray filteredJsonArray;

    public FilteredViewHolder filteredViewHolder;

    public CustomersViewModel(Fragment fragment, JSONArray jsonArray) {
        this.fragment = fragment;
        this.jsonArray = jsonArray;

        filteredJsonArray = new JSONArray();
    }

    public void updateArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public CustomersViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);

        databaseHelper = new DatabaseHelper(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewModel.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.customerNameTextView.setText(jsonObject.getString("name"));

            String contact = "Contact: " + jsonObject.getString("phone");
            holder.phoneNumberTextView.setText(contact);

            String location = "Location: " + jsonObject.getString("location");
            holder.locationTextView.setText(location);

            String date = "Date added: " + jsonObject.getString("date");
            holder.dateTextView.setText(date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(view -> {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());

                String idFromArray = jsonObject.getString("id");
                String nameFromArray = jsonObject.getString("name");
                String phoneNumberFromArray = jsonObject.getString("phone");
                String locationFromArray = jsonObject.getString("location");

                Dialog dialog = new Dialog(fragment.getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_customer);
                dialog.show();

                EditText nameEditText = dialog.findViewById(R.id.customerNameEditText);
                EditText phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                EditText locationEditText = dialog.findViewById(R.id.locationEditText);
                Button updateCustomerButton = dialog.findViewById(R.id.addCustomerButton);

                nameEditText.setText(nameFromArray);
                nameEditText.setInputType(InputType.TYPE_NULL);
                nameEditText.setTextIsSelectable(false);

                phoneEditText.setText(phoneNumberFromArray);
                locationEditText.setText(locationFromArray);
                updateCustomerButton.setText(R.string.update_button);

                updateCustomerButton.setOnClickListener(view1 -> {
                    String updatedPhoneNumber = phoneEditText.getText().toString().trim();
                    String updatedLocation = locationEditText.getText().toString().trim();

                    databaseHelper.updateCustomer(idFromArray, updatedPhoneNumber, updatedLocation);
                    updateArray(databaseHelper.getCustomerArray());
                    notifyItemChanged(holder.getAdapterPosition());
                    dialog.dismiss();
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            int getPosition = holder.getAdapterPosition();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(getPosition);

                String stringID = jsonObject.getString("id");

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this customer?");

                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    databaseHelper.deleteCustomer(stringID);
                    jsonArray.remove(getPosition);
                    notifyItemRemoved(getPosition);
                    notifyItemRangeChanged(getPosition, jsonArray.length());
                });

                builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));

                builder.show();
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public Filter getFilter() {
        if (filteredViewHolder == null) {
            filteredJsonArray = jsonArray;

            filteredViewHolder = new CustomersViewModel.FilteredViewHolder(this, filteredJsonArray);
        }

        return filteredViewHolder;
    }

    public JSONArray getFilteredJsonArray(String name) {
        return databaseHelper.getFilteredCustomerArray(name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView phoneNumberTextView;
        TextView locationTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            phoneNumberTextView = itemView.findViewById(R.id.customerPhoneNumberTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            dateTextView = itemView.findViewById(R.id.customerDateAddedTextView);
        }
    }

    private static class FilteredViewHolder extends Filter {
        private final CustomersViewModel customersViewModel;
        private final JSONArray jsonArray;
        private JSONArray filteredJsonArray;

        private FilteredViewHolder(CustomersViewModel customersViewModel, JSONArray jsonArray) {
            this.customersViewModel = customersViewModel;
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

                filteredJsonArray = customersViewModel.getFilteredJsonArray(filterPattern);
            }

            results.values = filteredJsonArray;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            customersViewModel.updateArray((JSONArray) filterResults.values);
            customersViewModel.notifyDataSetChanged();
        }
    }
}