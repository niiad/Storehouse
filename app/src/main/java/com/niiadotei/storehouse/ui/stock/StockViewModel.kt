package com.niiadotei.storehouse.ui.stock

import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import com.niiadotei.storehouse.data.DatabaseHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import com.niiadotei.storehouse.R
import org.json.JSONException
import android.graphics.drawable.ColorDrawable
import android.content.DialogInterface
import android.text.TextUtils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class StockViewModel(var fragment: Fragment, var jsonArray: JSONArray) : RecyclerView.Adapter<StockViewModel.ViewHolder>(), Filterable {
    private var filteredJsonArray: JSONArray
    private lateinit var databaseHelper: DatabaseHelper

    init {
        filteredJsonArray = JSONArray()
    }

    fun updateArray(jsonArray: JSONArray) {
        this.jsonArray = jsonArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        databaseHelper = DatabaseHelper(view.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var price = 0.0
        var id = 0

        try {
            val jsonObject = jsonArray.getJSONObject(position)

            id = jsonObject.getInt("id")

            holder.displayNameTextView.text = jsonObject.getString("display")
            holder.nameTextView.text = jsonObject.getString("name")

            val resources = holder.itemView.context.resources
            val locale = resources.configuration.locale
            val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

            price = jsonObject.getDouble("price")
            holder.priceTextView.text = currencyInstance.format(price)

            holder.supplierTextView.text = databaseHelper.getSupplierNameFromID(jsonObject.getInt("supplier"))

            val quantity = "Qty: " + jsonObject.getString("quantity")
            holder.quantityTextView.text = quantity
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val finalPrice = price
        val finalId = id

        holder.itemView.setOnClickListener {
            val customers = databaseHelper.allCustomersWithPhone
            Arrays.sort(customers)

            val selectedCustomer = arrayOfNulls<String>(1)
            selectedCustomer[0] = ""

            val checkedCustomer = intArrayOf(-1)

            val dialog = Dialog(holder.itemView.context)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_purchase)
            dialog.show()

            val editText = dialog.findViewById<EditText>(R.id.purchase_quantity)
            val selectCustomer = dialog.findViewById<Button>(R.id.selectCustomerButton)

            selectCustomer.setOnClickListener {
                val alertDialog = AlertDialog.Builder(holder.itemView.context)
                alertDialog.setTitle(R.string.select_customer_message)
                alertDialog.setSingleChoiceItems(customers, checkedCustomer[0]) { dialogInterface: DialogInterface, i: Int ->
                    checkedCustomer[0] = i
                    selectedCustomer[0] = customers[i]
                    selectCustomer.text = selectedCustomer[0]!!.substring(0, 10)
                    dialogInterface.dismiss()
                }

                alertDialog.setNegativeButton(R.string.negative_cancel_button) { _: DialogInterface?, _: Int -> }
                val customerAlertDialog = alertDialog.create()
                customerAlertDialog.show()
            }

            val makePurchase = dialog.findViewById<Button>(R.id.makePurchaseButton)
            makePurchase.setOnClickListener MakePurchase@{
                try {
                    val quantity = editText.text.toString().trim { it <= ' ' }.toInt()
                    val stockQuantity = databaseHelper.getProductQuantityFromID(finalId)
                    if (quantity == 0) {
                        Toast.makeText(holder.itemView.context, R.string.zero_quantity_message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()

                        return@MakePurchase
                    }
                    if (quantity > stockQuantity) {
                        Toast.makeText(holder.itemView.context, R.string.quantity_not_enough_message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()

                        return@MakePurchase
                    }
                    if (TextUtils.isEmpty(selectedCustomer[0])) {
                        Toast.makeText(holder.itemView.context, R.string.empty_customer_message, Toast.LENGTH_LONG).show()

                        return@MakePurchase
                    }
                    val builder = AlertDialog.Builder(fragment.activity)
                    builder.setTitle(R.string.confirm_title)
                    val totalPurchaseCost = finalPrice * quantity

                    val resources = holder.itemView.context.resources
                    val locale = resources.configuration.locale
                    val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

                    builder.setMessage("Collect from the customer, a total cost of " + currencyInstance.format(totalPurchaseCost) + " before confirming.")
                    builder.setPositiveButton(R.string.positive_button) { _: DialogInterface?, _: Int ->
                        try {
                            val jsonObject = jsonArray.getJSONObject(holder.adapterPosition)

                            val productID = jsonObject.getInt("id")

                            databaseHelper.updateProductQuantity(productID, -quantity)

                            databaseHelper.insertPurchases(selectedCustomer[0], holder.nameTextView.text.toString(), totalPurchaseCost, quantity, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()))

                            Toast.makeText(holder.itemView.context, R.string.purchase_success_message, Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        } catch (e: JSONException) {
                            Toast.makeText(holder.itemView.context, R.string.purchase_error_message, Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                    }

                    builder.setNegativeButton(R.string.negative_button) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                    builder.show()
                } catch (e: NumberFormatException) {

                }
            }
        }

        holder.itemView.setOnLongClickListener {
            val getPosition = holder.adapterPosition
            try {
                val jsonObject = jsonArray.getJSONObject(getPosition)

                val stringID = jsonObject.getString("id")

                val builder = AlertDialog.Builder(fragment.activity)
                builder.setTitle(R.string.confirm_title)
                builder.setMessage(R.string.delete_product_message)
                builder.setPositiveButton(R.string.positive_button) { _: DialogInterface?, _: Int ->
                    databaseHelper.deleteProduct(stringID)

                    jsonArray.remove(getPosition)

                    notifyItemRemoved(getPosition)
                    notifyItemRangeRemoved(getPosition, jsonArray.length())
                }
                builder.setNegativeButton(R.string.negative_button) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                builder.show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            true
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()

                filteredJsonArray = if (charSequence.isEmpty()) {
                    jsonArray
                } else {
                    val filterPattern = charSequence.toString().trim { it <= ' ' }
                    getFilteredJsonArray(filterPattern)
                }

                results.values = filteredJsonArray
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                if (charSequence.isEmpty()) {
                    updateArray(jsonArray)
                } else {
                    updateArray(filterResults.values as JSONArray)

                }

                notifyDataSetChanged()
            }
        }
    }

    fun getFilteredJsonArray(name: String?): JSONArray {
        return databaseHelper.getFilteredProductArray(name!!)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayNameTextView: TextView
        var nameTextView: TextView
        var priceTextView: TextView
        var supplierTextView: TextView
        var quantityTextView: TextView

        init {
            displayNameTextView = itemView.findViewById(R.id.displayProductName)
            nameTextView = itemView.findViewById(R.id.productName)
            priceTextView = itemView.findViewById(R.id.productPrice)
            supplierTextView = itemView.findViewById(R.id.productSupplier)
            quantityTextView = itemView.findViewById(R.id.productQuantity)
        }
    }


}