package com.niiadotei.storehouse.ui.supplychain

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.niiadotei.storehouse.R
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.FragmentSupplyChainBinding
import com.niiadotei.storehouse.ui.suppliers.SuppliersActivity
import com.niiadotei.storehouse.ui.supplies.SuppliesActivity

class SupplyChainFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var binding: FragmentSupplyChainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSupplyChainBinding.inflate(inflater, container, false)

        databaseHelper = DatabaseHelper(this.activity)

        val recyclerView = binding.supplyChainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val supplyChainViewModel = SupplyChainViewModel(this, databaseHelper.supplyChainArray)

        recyclerView.adapter = supplyChainViewModel

        val addSupplierFAB = binding.addSupplierFAB
        val addSupplyChainFAB = binding.addSupplyChainFAB

        val displays = databaseHelper.allSupplierDisplayNames

        val selectedDisplay = arrayOfNulls<String>(1)
        selectedDisplay[0] = ""

        val checkedDisplay = intArrayOf(-1)

        val productDisplays = databaseHelper.allProductDisplayNames
        val selectedProductDisplay = arrayOfNulls<String>(1)

        selectedProductDisplay[0] = ""
        val checkedProductDisplay = intArrayOf(-1)

        addSupplierFAB.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_supplier)
            dialog.show()

            val supplierName = dialog.findViewById<EditText>(R.id.supplierNameEditText)
            val supplierDisplay = dialog.findViewById<EditText>(R.id.displayNameEditText)
            val supplierPhone = dialog.findViewById<EditText>(R.id.supplierPhoneEditText)
            val supplierLocation = dialog.findViewById<EditText>(R.id.supplierLocationEditText)
            val addSupplier = dialog.findViewById<Button>(R.id.addSupplierButton)

            addSupplier.setOnClickListener SupplierButton@{
                val name = supplierName.text.toString().trim { it <= ' ' }
                val display = supplierDisplay.text.toString().trim { it <= ' ' }
                val phone = supplierPhone.text.toString().trim { it <= ' ' }
                val location = supplierLocation.text.toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, R.string.empty_supplier_name_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplierButton
                }
                if (TextUtils.isEmpty(display)) {
                    Toast.makeText(context, R.string.empty_display_name_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplierButton
                }
                if (display.length >= 15) {
                    Toast.makeText(context, R.string.short_display_name_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplierButton
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(context, R.string.empty_phone_number_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplierButton
                }
                if (TextUtils.isEmpty(location)) {
                    Toast.makeText(context, R.string.empty_location_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplierButton
                }
                databaseHelper.insertSupplier(name, display, phone, location)
                dialog.dismiss()
            }
        }

        addSupplyChainFAB.setOnClickListener { view: View ->
            val dialog = Dialog(requireContext())
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_supply_chain)
            dialog.show()

            val productNameUnderSupply = arrayOfNulls<String>(1)

            val chooseSupplier = dialog.findViewById<Button>(R.id.chooseSupplierButton)
            chooseSupplier.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.select_supplier_display_message)
                alertDialog.setSingleChoiceItems(displays, checkedDisplay[0]) { dialogInterface: DialogInterface, i: Int ->
                    checkedDisplay[0] = i
                    selectedDisplay[0] = displays[i]
                    chooseSupplier.text = selectedDisplay[0]

                    dialogInterface.dismiss()
                }

                alertDialog.setNegativeButton(R.string.negative_cancel_button) { _: DialogInterface?, _: Int -> }
                val displayAlertDialog = alertDialog.create()
                displayAlertDialog.show()
            }

            val chooseProduct = dialog.findViewById<Button>(R.id.chooseProductButton)
            chooseProduct.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.select_product_display_message)
                alertDialog.setSingleChoiceItems(productDisplays, checkedProductDisplay[0]) { dialogInterface: DialogInterface, i: Int ->
                    checkedProductDisplay[0] = i
                    selectedProductDisplay[0] = productDisplays[i]
                    chooseProduct.text = selectedProductDisplay[0]
                    productNameUnderSupply[0] = selectedProductDisplay[0]

                    dialogInterface.dismiss()
                }

                alertDialog.setNegativeButton(R.string.negative_cancel_button) { _: DialogInterface?, _: Int -> }
                val productDisplayAlertDialog = alertDialog.create()
                productDisplayAlertDialog.show()
            }

            val addSupply = dialog.findViewById<Button>(R.id.addSupplyButton)
            addSupply.setOnClickListener SupplyButton@{
                val id = databaseHelper.getSupplierID(selectedDisplay[0]!!)
                val productID = databaseHelper.getProductID(selectedProductDisplay[0]!!)

                if (TextUtils.isEmpty(selectedDisplay[0])) {
                    Toast.makeText(context, R.string.empty_display_supplier_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplyButton
                }
                if (TextUtils.isEmpty(selectedProductDisplay[0])) {
                    Toast.makeText(context, R.string.empty_display_product_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@SupplyButton
                }

                databaseHelper.insertSupply(id, productID)
                supplyChainViewModel.updateArray(databaseHelper.supplyChainArray)
                recyclerView.adapter = supplyChainViewModel

                val sharedPreferences = view.context.getSharedPreferences("supplyChain", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val productSupplyValue = productNameUnderSupply[0]
                val productQuantityValue = productNameUnderSupply[0].toString() + "Quantity"

                editor.putInt(productQuantityValue, 0)
                editor.putBoolean(productSupplyValue, false)
                editor.apply()

                dialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.supply_chain_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val suppliers = menu.findItem(R.id.suppliers_list)
        suppliers.setOnMenuItemClickListener {
            val intent = Intent(activity, SuppliersActivity::class.java)
            requireActivity().startActivity(intent)
            false
        }

        val supplies = menu.findItem(R.id.supply_history)
        supplies.setOnMenuItemClickListener {
            val intent = Intent(activity, SuppliesActivity::class.java)
            requireActivity().startActivity(intent)
            false
        }
    }

}