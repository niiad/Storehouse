package com.niiadotei.storehouse.ui.customers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentCustomersBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomersFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton addCustomerFAB;

    CustomersViewModel customersViewModel;

    DatabaseHelper databaseHelper;

    private FragmentCustomersBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        recyclerView = binding.customerRecyclerView;
        addCustomerFAB = binding.addCustomerFAB;

        databaseHelper = new DatabaseHelper(this.getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customersViewModel = new CustomersViewModel(this, databaseHelper.getCustomerArray());
        recyclerView.setAdapter(customersViewModel);


        addCustomerFAB.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_customer);
            dialog.show();

            EditText customerName = dialog.findViewById(R.id.customerNameEditText);
            EditText customerPhone = dialog.findViewById(R.id.phoneNumberEditText);
            EditText customerLocation = dialog.findViewById(R.id.locationEditText);

            Button addCustomer = dialog.findViewById(R.id.addCustomerButton);
            addCustomer.setOnClickListener(view1 -> {
                String name = customerName.getText().toString().trim();
                String phone = customerPhone.getText().toString().trim();
                String location = customerLocation.getText().toString().trim();
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "The name of the customer cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "The customer should provide a phone number", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(location)) {
                    Toast.makeText(getContext(), "The location of the customer should be provided", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                databaseHelper.insertCustomer(name, phone, location, date);

                customersViewModel.updateArray(databaseHelper.getCustomerArray());

                dialog.dismiss();
            });
        });

        addCustomerFAB.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to delete all customers?");

            builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
                databaseHelper.truncateCustomerTable();

                customersViewModel.updateArray(databaseHelper.getCustomerArray());

                recyclerView.setAdapter(customersViewModel);
            }));

            builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));

            builder.show();
            return true;
        });

        return binding.getRoot();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.customer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem search = menu.findItem(R.id.search_customer);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                customersViewModel.getFilter().filter(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                customersViewModel.getFilter().filter(s);

                return false;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}