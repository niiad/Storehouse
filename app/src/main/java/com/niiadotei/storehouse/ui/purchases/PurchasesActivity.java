package com.niiadotei.storehouse.ui.purchases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.ActivityPurchasesBinding;

public class PurchasesActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    PurchasesViewModel purchasesViewModel;

    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPurchasesBinding binding = ActivityPurchasesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Purchase History");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.purchasesRecyclerView;

        databaseHelper = new DatabaseHelper(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        purchasesViewModel = new PurchasesViewModel(this, databaseHelper.getPurchasesArray());
        recyclerView.setAdapter(purchasesViewModel);

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
