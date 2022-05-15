package com.niiadotei.storehouse.ui.supplies;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.ActivitySuppliesBinding;

public class SuppliesActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    SuppliesViewModel suppliesViewModel;

    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySuppliesBinding binding = ActivitySuppliesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Supplies History");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.suppliesRecyclerView;

        databaseHelper = new DatabaseHelper(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        suppliesViewModel = new SuppliesViewModel(this, databaseHelper.getSupplyChainArray());
        recyclerView.setAdapter(suppliesViewModel);
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
