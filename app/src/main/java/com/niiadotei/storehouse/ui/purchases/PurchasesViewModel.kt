package com.niiadotei.storehouse.ui.purchases

import org.json.JSONArray

import androidx.recyclerview.widget.RecyclerView

import com.niiadotei.storehouse.data.DatabaseHelper

import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View

import com.niiadotei.storehouse.R

import org.json.JSONException

import android.widget.TextView

import java.text.DecimalFormat

class PurchasesViewModel(var jsonArray: JSONArray) : RecyclerView.Adapter<PurchasesViewModel.ViewHolder>() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchases, parent, false)
        databaseHelper = DatabaseHelper(view.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            holder.purchasedCustomerNameTextView.text = jsonObject.getString("customer")

            holder.purchasedProductTextView.text = jsonObject.getString("product")

            val amount = jsonObject.getDouble("amount")

            val resources = holder.itemView.context.resources
            val locale = resources.configuration.locale
            val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

            val cost = "Amount paid: " + currencyInstance.format(amount)
            holder.purchasedPriceTextView.text = cost

            val quantity = "Quantity purchased: " + jsonObject.getInt("quantity")
            holder.purchasedQuantityTextView.text = quantity

            val date = "Date of purchase: " + jsonObject.getString("date")
            holder.purchasedDateTextView.text = date
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var purchasedCustomerNameTextView: TextView
        var purchasedProductTextView: TextView
        var purchasedPriceTextView: TextView
        var purchasedQuantityTextView: TextView
        var purchasedDateTextView: TextView

        init {
            purchasedCustomerNameTextView = itemView.findViewById(R.id.purchasedCustomerNameTextView)
            purchasedProductTextView = itemView.findViewById(R.id.purchasedProductTextView)
            purchasedPriceTextView = itemView.findViewById(R.id.purchasedPriceTextView)
            purchasedQuantityTextView = itemView.findViewById(R.id.purchasedQuantityTextView)
            purchasedDateTextView = itemView.findViewById(R.id.purchasedDateTextView)
        }
    }
}