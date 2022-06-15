package com.niiadotei.storehouse.ui.stock

import android.app.AlertDialog
import android.app.Dialog
import com.niiadotei.storehouse.data.DatabaseHelper
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.graphics.drawable.ColorDrawable
import com.niiadotei.storehouse.R
import android.widget.EditText
import android.content.DialogInterface
import android.widget.Toast
import android.text.TextUtils
import android.content.Intent
import android.graphics.Color
import android.view.*
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.niiadotei.storehouse.databinding.FragmentStockBinding
import com.niiadotei.storehouse.ui.purchases.PurchasesActivity
import java.lang.NumberFormatException
import java.text.DecimalFormat

class StockFragment : Fragment() {
    private lateinit var stockViewModel: StockViewModel
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var binding: FragmentStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStockBinding.inflate(inflater, container, false)



        databaseHelper = DatabaseHelper(this.activity)

        val recyclerView = binding.productRecyclerView
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        stockViewModel = StockViewModel(this, databaseHelper.productArray)
        recyclerView.adapter = stockViewModel

        val addProductFAB = binding.addProductFAB
        addProductFAB.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_product)
            dialog.show()

            val productName = dialog.findViewById<EditText>(R.id.productNameEditText)
            val productDisplay = dialog.findViewById<EditText>(R.id.productDisplayEditText)
            val productCost = dialog.findViewById<EditText>(R.id.productCostEditText)
            val productPrice = dialog.findViewById<EditText>(R.id.productPriceEditText)
            val productQuantity = dialog.findViewById<EditText>(R.id.productQuantityEditText)

            val displays = databaseHelper.allSupplierDisplayNames
            val selectedDisplay = arrayOfNulls<String>(1)
            selectedDisplay[0] = ""
            val checkedDisplay = intArrayOf(-1)

            val selectSupplier = dialog.findViewById<Button>(R.id.selectSupplierButton)
            selectSupplier.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.select_supplier_display_message)
                alertDialog.setSingleChoiceItems(displays, checkedDisplay[0]) { dialogInterface: DialogInterface, i: Int ->
                    checkedDisplay[0] = i
                    selectedDisplay[0] = displays[i]
                    selectSupplier.text = selectedDisplay[0]

                    dialogInterface.dismiss()
                }

                alertDialog.setNegativeButton(R.string.negative_cancel_button) { _: DialogInterface?, _: Int -> }
                val displayAlertDialog = alertDialog.create()
                displayAlertDialog.show()
            }

            val addProduct = dialog.findViewById<Button>(R.id.addProductButton)
            addProduct.setOnClickListener AddProduct@{ view1: View ->
                val decimalFormat = DecimalFormat("#.##")

                val name = productName.text.toString().trim { it <= ' ' }
                val display = productDisplay.text.toString().trim { it <= ' ' }

                val cost: Double
                val price: Double

                try {
                    cost = productCost.text.toString().trim { it <= ' ' }.toDouble()
                    decimalFormat.format(cost)
                } catch (e: NumberFormatException) {
                    Toast.makeText(view1.context, R.string.format_error_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }
                try {
                    price = productPrice.text.toString().trim { it <= ' ' }.toDouble()
                    decimalFormat.format(price)
                } catch (e: NumberFormatException) {
                    Toast.makeText(view1.context, R.string.format_error_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }

                val quantity: Int = try {
                    productQuantity.text.toString().trim { it <= ' ' }.toInt()
                } catch (e: NumberFormatException) {
                    Toast.makeText(view1.context, R.string.format_error_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }

                val id = databaseHelper.getSupplierID(selectedDisplay[0]!!)
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, R.string.empty_product_name_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }
                if (TextUtils.isEmpty(display)) {
                    Toast.makeText(context, R.string.empty_short_display_name_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }
                if (TextUtils.isEmpty(selectedDisplay[0])) {
                    Toast.makeText(context, R.string.empty_supplier_with_tip_message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@AddProduct
                }

                databaseHelper.insertProduct(name, display, cost, price, quantity, id)
                stockViewModel.updateArray(databaseHelper.productArray)

                dialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stock_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val search = menu.findItem(R.id.search_stock)
        val searchView = search.actionView as SearchView
        val purchases = menu.findItem(R.id.purchase_history)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                stockViewModel.filter.filter(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                stockViewModel.filter.filter(s)
                return false
            }
        })

        purchases.setOnMenuItemClickListener {
            val intent = Intent(activity, PurchasesActivity::class.java)
            requireActivity().startActivity(intent)
            false
        }
    }
}