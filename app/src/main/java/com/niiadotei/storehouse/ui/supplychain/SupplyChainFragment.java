package com.niiadotei.storehouse.ui.supplychain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.data.DatabaseHelper;
import com.niiadotei.storehouse.databinding.FragmentSupplyChainBinding;
import com.niiadotei.storehouse.ui.suppliers.SuppliersActivity;
import com.niiadotei.storehouse.ui.supplies.SuppliesActivity;

public class SupplyChainFragment extends Fragment {
    FloatingActionButton addSupplierFAB;
    FloatingActionButton addSupplyChainFAB;

    DatabaseHelper databaseHelper;

    SupplyChainViewModel supplyChainViewModel;

    RecyclerView recyclerView;

    private FragmentSupplyChainBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSupplyChainBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        recyclerView = binding.supplyChainRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        supplyChainViewModel = new SupplyChainViewModel(this, databaseHelper.getSupplyChainArray());

        recyclerView.setAdapter(supplyChainViewModel);

        addSupplierFAB = binding.addSupplierFAB;
        addSupplyChainFAB = binding.addSupplyChainFAB;

        String[] displays = databaseHelper.getAllSupplierDisplayNames();
        final String[] selectedDisplay = new String[1];
        selectedDisplay[0] = "";
        int[] checkedDisplay = {-1};

        String[] productDisplays = databaseHelper.getAllProductDisplayNames();
        final String[] selectedProductDisplay = new String[1];
        selectedProductDisplay[0] = "";
        int[] checkedProductDisplay = {-1};

        addSupplierFAB.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_supplier);
            dialog.show();

            EditText supplierName = dialog.findViewById(R.id.supplierNameEditText);
            EditText supplierDisplay = dialog.findViewById(R.id.displayNameEditText);
            EditText supplierPhone = dialog.findViewById(R.id.supplierPhoneEditText);
            EditText supplierLocation = dialog.findViewById(R.id.supplierLocationEditText);

            Button addSupplier = dialog.findViewById(R.id.addSupplierButton);
            addSupplier.setOnClickListener(view1 -> {
                String name = supplierName.getText().toString().trim();
                String display = supplierDisplay.getText().toString().trim();
                String phone = supplierPhone.getText().toString().trim();
                String location = supplierLocation.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Supplier name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(display)) {
                    Toast.makeText(getContext(), "Display name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (display.length() >= 15) {
                    Toast.makeText(getContext(), "Display name should be short", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "Phone number is empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(location)) {
                    Toast.makeText(getContext(), "Location cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                databaseHelper.insertSupplier(name, display, phone, location);

                dialog.dismiss();

            });

        });

        addSupplyChainFAB.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_supply_chain);
            dialog.show();

            final String[] productNameUnderSupply = new String[1];


            Button chooseSupplier = dialog.findViewById(R.id.chooseSupplierButton);
            chooseSupplier.setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Select supplier display name");
                alertDialog.setSingleChoiceItems(displays, checkedDisplay[0], (dialogInterface, i) -> {
                    checkedDisplay[0] = i;
                    selectedDisplay[0] = displays[i];

                    chooseSupplier.setText(selectedDisplay[0]);
                    dialogInterface.dismiss();
                });

                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

                });

                AlertDialog displayAlertDialog = alertDialog.create();
                displayAlertDialog.show();
            });

            Button chooseProduct = dialog.findViewById(R.id.chooseProductButton);
            chooseProduct.setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Select product display name");
                alertDialog.setSingleChoiceItems(productDisplays, checkedProductDisplay[0], (dialogInterface, i) -> {
                    checkedProductDisplay[0] = i;
                    selectedProductDisplay[0] = productDisplays[i];

                    chooseProduct.setText(selectedProductDisplay[0]);

                    productNameUnderSupply[0] = selectedProductDisplay[0];
                    dialogInterface.dismiss();
                });

                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

                });

                AlertDialog productDisplayAlertDialog = alertDialog.create();
                productDisplayAlertDialog.show();
            });


            Button addSupply = dialog.findViewById(R.id.addSupplyButton);
            addSupply.setOnClickListener(view1 -> {
                int id = databaseHelper.getSupplierID(selectedDisplay[0]);
                String productID = databaseHelper.getProductID(selectedProductDisplay[0]);

                if (TextUtils.isEmpty(selectedDisplay[0])) {
                    Toast.makeText(getContext(), "Supplier display name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(selectedProductDisplay[0])) {
                    Toast.makeText(getContext(), "Product display name cannot be empty", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                databaseHelper.insertSupply(id, productID);

                supplyChainViewModel.updateArray(databaseHelper.getSupplyChainArray());

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("supplyChain", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String productSupplyValue = productNameUnderSupply[0];
                String productQuantityValue = productNameUnderSupply[0] + "Quantity";
                editor.putInt(productQuantityValue, 0);
                editor.putBoolean(productSupplyValue, false);

                editor.apply();

                dialog.dismiss();
            });

        });


        return binding.getRoot();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.supply_chain_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem suppliers = menu.findItem(R.id.suppliers_list);
        suppliers.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getActivity(), SuppliersActivity.class);
            requireActivity().startActivity(intent);

            return false;
        });

        MenuItem supplies = menu.findItem(R.id.supply_history);
        supplies.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getActivity(), SuppliesActivity.class);
            requireActivity().startActivity(intent);

            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}