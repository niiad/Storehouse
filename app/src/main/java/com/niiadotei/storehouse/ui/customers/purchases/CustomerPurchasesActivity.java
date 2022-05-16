package com.niiadotei.storehouse.ui.customers.purchases;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.ActivityCustomerPurchasesBinding;

public class CustomerPurchasesActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    CustomerPurchasesViewModel customerPurchasesViewModel;

    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCustomerPurchasesBinding binding = ActivityCustomerPurchasesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Customer Purchases");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.customerPurchasesRecyclerView;

        databaseHelper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        String customer = intent.getStringExtra("customer");

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        customerPurchasesViewModel = new CustomerPurchasesViewModel(this, databaseHelper.getPurchasesArray(customer));

        recyclerView.setAdapter(customerPurchasesViewModel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
