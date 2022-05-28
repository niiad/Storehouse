package com.niiadotei.storehouse.ui.customers.purchases

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

class CustomerPurchasesViewModel(var jsonArray: JSONArray) : RecyclerView.Adapter<CustomerPurchasesViewModel.ViewHolder>() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_purchases, parent, false)
        databaseHelper = DatabaseHelper(view.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            holder.customerPurchasedDateTextView.text = jsonObject.getString("date")

            holder.customerPurchasedProductTextView.text = jsonObject.getString("product")
            val quantity = "Quantity purchased: " + jsonObject.getInt("quantity")
            holder.customerPurchasedQuantityTextView.text = quantity

            val resources = holder.itemView.context.resources
            val locale = resources.configuration.locale
            val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

            holder.customerPurchasedAmountTextView.text = currencyInstance.format(jsonObject.getDouble("amount"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var customerPurchasedDateTextView: TextView
        var customerPurchasedProductTextView: TextView
        var customerPurchasedQuantityTextView: TextView
        var customerPurchasedAmountTextView: TextView

        init {
            customerPurchasedDateTextView = itemView.findViewById(R.id.customerPurchasedDateTextView)
            customerPurchasedProductTextView = itemView.findViewById(R.id.customerPurchasedProductTextView)
            customerPurchasedQuantityTextView = itemView.findViewById(R.id.customerPurchasedQuantityTextView)
            customerPurchasedAmountTextView = itemView.findViewById(R.id.customerPurchasedAmountTextView)
        }
    }
}