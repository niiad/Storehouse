package com.niiadotei.storehouse.ui.stock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentStockBinding;


public class StockFragment extends Fragment {
    FloatingActionButton addProductFAB;

    StockViewModel stockViewModel;

    RecyclerView recyclerView;

    DatabaseHelper databaseHelper;

    private FragmentStockBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStockBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        recyclerView = binding.productRecyclerView;
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        stockViewModel = new StockViewModel(this, databaseHelper.getProductArray());

        recyclerView.setAdapter(stockViewModel);

        addProductFAB = binding.addProductFAB;

        addProductFAB.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_product);
            dialog.show();

            EditText productName = dialog.findViewById(R.id.productNameEditText);
            EditText productDisplay = dialog.findViewById(R.id.productDisplayEditText);
            EditText productCost = dialog.findViewById(R.id.productCostEditText);
            EditText productPrice = dialog.findViewById(R.id.productPriceEditText);
            EditText productQuantity = dialog.findViewById(R.id.productQuantityEditText);

            String[] displays = databaseHelper.getAllSupplierDisplayNames();
            final String[] selectedDisplay = new String[1];
            selectedDisplay[0] = "";
            int[] checkedDisplay = {-1};

            Button selectSupplier = dialog.findViewById(R.id.selectSupplierButton);
            selectSupplier.setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Select supplier display name");
                alertDialog.setSingleChoiceItems(displays, checkedDisplay[0], (dialogInterface, i) -> {
                    checkedDisplay[0] = i;
                    selectedDisplay[0] = displays[i];

                    selectSupplier.setText(selectedDisplay[0]);
                    dialogInterface.dismiss();
                });

                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

                });

                AlertDialog displayAlertDialog = alertDialog.create();
                displayAlertDialog.show();
            });

            Button addProduct = dialog.findViewById(R.id.addProductButton);
            addProduct.setOnClickListener(view1 -> {
                String name = productName.getText().toString().trim();
                String display = productDisplay.getText().toString().trim();
                String cost = productCost.getText().toString().trim();
                String price = productPrice.getText().toString().trim();
                String quantity = productQuantity.getText().toString().trim();
                String id = databaseHelper.getSupplierID(selectedDisplay[0]);

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Product name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(display)) {
                    Toast.makeText(getContext(), "Display name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(cost)) {
                    Toast.makeText(getContext(), "Cost cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(price)) {
                    Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(getContext(), "Quantity cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(selectedDisplay[0])) {
                    Toast.makeText(getContext(), "Supplier cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                databaseHelper.insertProduct(name, display, cost, price, quantity, id);

                stockViewModel.updateArray(databaseHelper.getProductArray());

                dialog.dismiss();

            });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}