package com.niiadotei.storehouse.ui.suppliers

import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import com.niiadotei.storehouse.data.DatabaseHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.niiadotei.storehouse.R
import org.json.JSONException
import android.widget.TextView

class SuppliersViewModel(var jsonArray: JSONArray) : RecyclerView.Adapter<SuppliersViewModel.ViewHolder>() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suppliers, parent, false)
        databaseHelper = DatabaseHelper(view.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            holder.supplierNameTextView.text = jsonObject.getString("name")

            val phone = "Contact: " + jsonObject.getString("phone")
            holder.supplierPhoneNumberTextView.text = phone

            val location = "Location: " + jsonObject.getString("location")
            holder.supplierLocationTextView.text = location

            holder.supplierDisplayNameTextView.text = jsonObject.getString("display")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var supplierNameTextView: TextView
        var supplierPhoneNumberTextView: TextView
        var supplierLocationTextView: TextView
        var supplierDisplayNameTextView: TextView

        init {
            supplierNameTextView = itemView.findViewById(R.id.supplierNameTextView)
            supplierPhoneNumberTextView = itemView.findViewById(R.id.supplierPhoneNumberTextView)
            supplierLocationTextView = itemView.findViewById(R.id.supplierLocationTextView)
            supplierDisplayNameTextView = itemView.findViewById(R.id.supplierDisplayNameTextView)
        }
    }
}