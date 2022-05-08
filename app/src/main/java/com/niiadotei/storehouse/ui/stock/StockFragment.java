package com.niiadotei.storehouse.ui.stock;

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentStockBinding;
import com.niiadotei.storehouse.ui.purchases.PurchasesViewModel;

import java.text.DecimalFormat;


public class StockFragment extends Fragment {
    FloatingActionButton addProductFAB;

    StockViewModel stockViewModel;

    RecyclerView recyclerView;

    DatabaseHelper databaseHelper;

    private FragmentStockBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

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
                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                String name = productName.getText().toString().trim();
                String display = productDisplay.getText().toString().trim();

                double cost;
                double price;
                int quantity;

                try {
                    cost = Double.parseDouble(productCost.getText().toString().trim());
                    decimalFormat.format(cost);
                } catch (NumberFormatException e) {
                    Toast.makeText(view1.getContext(), "Format not supported", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                try {
                    price = Double.parseDouble(productPrice.getText().toString().trim());
                    decimalFormat.format(price);
                } catch (NumberFormatException e) {
                    Toast.makeText(view1.getContext(), "Format not supported", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                try {
                    quantity = Integer.parseInt(productQuantity.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(view1.getContext(), "Format not supported", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                int id = databaseHelper.getSupplierID(selectedDisplay[0]);

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Product name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(display)) {
                    Toast.makeText(getContext(), "Display name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(selectedDisplay[0])) {
                    Toast.makeText(getContext(), "Supplier cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                databaseHelper.insertProduct(name, display, cost, price, quantity, id);

                stockViewModel.updateArray(databaseHelper.getProductArray());

                dialog.dismiss();

            });
        });

        return binding.getRoot();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stock_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem search = menu.findItem(R.id.search_stock);
        SearchView searchView = (SearchView) search.getActionView();

        MenuItem purchases = menu.findItem(R.id.purchase_history);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                stockViewModel.getFilter().filter(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                stockViewModel.getFilter().filter(s);

                return false;
            }
        });

        purchases.setOnMenuItemClickListener(menuItem -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.fragment_purchases, null);

            RecyclerView recyclerView = view.findViewById(R.id.purchasesRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setHasFixedSize(true);

            PurchasesViewModel purchasesViewModel = new PurchasesViewModel(this, databaseHelper.getPurchasesArray());
            recyclerView.setAdapter(purchasesViewModel);

            builder.setTitle("Purchase History");
            builder.setView(view).setPositiveButton("Ok", null).setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}