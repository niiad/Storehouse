package com.niiadotei.storehouse.ui.suppliers;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.ActivitySuppliersBinding;

public class SuppliersActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    SuppliersViewModel suppliersViewModel;

    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySuppliersBinding binding = ActivitySuppliersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Suppliers");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.suppliersRecyclerView;

        databaseHelper = new DatabaseHelper(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        suppliersViewModel = new SuppliersViewModel(this, databaseHelper.getSuppliersArray());
        recyclerView.setAdapter(suppliersViewModel);
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
