package com.niiadotei.storehouse.ui.supplies

import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import com.niiadotei.storehouse.data.DatabaseHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.niiadotei.storehouse.R
import org.json.JSONException
import android.widget.TextView

class SuppliesViewModel(var jsonArray: JSONArray) : RecyclerView.Adapter<SuppliesViewModel.ViewHolder>() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_supply_history, parent, false)
        databaseHelper = DatabaseHelper(view.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            val productName = databaseHelper.getProductNameFromID(jsonObject.getInt("product"))

            holder.suppliedProductNameTextView.text = productName
            val supplierName = "From: " + databaseHelper.getSupplierNameFromID(jsonObject.getInt("supplier"))
            holder.suppliedSupplierNameTextView.text = supplierName

            val quantity = "Quantity: " + jsonObject.getInt("quantity")
            holder.suppliedQuantityTextView.text = quantity

            val request = "Date requested: " + jsonObject.getString("request")
            holder.suppliedRequestTextView.text = request

            val received = "Date received: " + jsonObject.getString("received")
            holder.suppliedReceivedTextView.text = received
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var suppliedProductNameTextView: TextView
        var suppliedSupplierNameTextView: TextView
        var suppliedQuantityTextView: TextView
        var suppliedRequestTextView: TextView
        var suppliedReceivedTextView: TextView

        init {
            suppliedProductNameTextView = itemView.findViewById(R.id.suppliedProductNameTextView)
            suppliedSupplierNameTextView = itemView.findViewById(R.id.suppliedSupplierNameTextView)
            suppliedQuantityTextView = itemView.findViewById(R.id.suppliedQuantityTextView)
            suppliedRequestTextView = itemView.findViewById(R.id.suppliedRequestTextView)
            suppliedReceivedTextView = itemView.findViewById(R.id.suppliedReceivedTextView)
        }
    }
}