package com.niiadotei.storehouse.ui.customers

import android.annotation.SuppressLint

import android.app.AlertDialog
import android.app.Dialog

import android.content.DialogInterface
import android.content.Intent

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.text.InputType

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*

import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.RecyclerView

import com.niiadotei.storehouse.R
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.ui.customers.purchases.CustomerPurchasesActivity

import org.json.JSONArray
import org.json.JSONException

class CustomersViewModel(var fragment: Fragment, var jsonArray: JSONArray) : RecyclerView.Adapter<CustomersViewModel.ViewHolder>(), Filterable {
    private lateinit var databaseHelper: DatabaseHelper

    private var filteredJsonArray: JSONArray

    init {
        filteredJsonArray = JSONArray()
    }

    fun updateArray(jsonArray: JSONArray) {
        this.jsonArray = jsonArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        databaseHelper = DatabaseHelper(view.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            holder.customerNameTextView.text = jsonObject.getString("name")

            val contact = "Contact: " + jsonObject.getString("phone")
            holder.phoneNumberTextView.text = contact

            val location = "Location: " + jsonObject.getString("location")
            holder.locationTextView.text = location

            val date = "Date added: " + jsonObject.getString("date")
            holder.dateTextView.text = date
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener { view: View ->
            try {
                val jsonObject = jsonArray.getJSONObject(position)

                val customer = jsonObject.getString("name") + ", " + jsonObject.getString("phone")

                val intent = Intent(view.context, CustomerPurchasesActivity::class.java)
                intent.putExtra("customer", customer)

                view.context.startActivity(intent)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        holder.itemView.setOnLongClickListener {
            val getPosition = holder.adapterPosition

            try {
                val jsonObject = jsonArray.getJSONObject(getPosition)

                val stringID = jsonObject.getString("id")

                val builder = AlertDialog.Builder(fragment.activity)
                builder.setTitle(R.string.confirm_title)
                builder.setMessage(R.string.delete_customer_message)
                builder.setPositiveButton(R.string.positive_button) { _: DialogInterface?, _: Int ->
                    databaseHelper.deleteCustomer(stringID)
                    jsonArray.remove(getPosition)
                    notifyItemRemoved(getPosition)
                    notifyItemRangeChanged(getPosition, jsonArray.length())
                }
                builder.setNegativeButton(R.string.negative_button) { _: DialogInterface?, _: Int ->
                    try {
                        val idFromArray = jsonObject.getString("id")
                        val nameFromArray = jsonObject.getString("name")
                        val phoneNumberFromArray = jsonObject.getString("phone")
                        val locationFromArray = jsonObject.getString("location")

                        val dialog = Dialog(fragment.requireActivity())
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_customer)
                        dialog.show()

                        val nameEditText = dialog.findViewById<EditText>(R.id.customerNameEditText)
                        val phoneEditText = dialog.findViewById<EditText>(R.id.phoneNumberEditText)
                        val locationEditText = dialog.findViewById<EditText>(R.id.locationEditText)
                        val updateCustomerButton = dialog.findViewById<Button>(R.id.addCustomerButton)

                        nameEditText.setText(nameFromArray)
                        nameEditText.inputType = InputType.TYPE_NULL
                        nameEditText.setTextIsSelectable(false)

                        phoneEditText.setText(phoneNumberFromArray)

                        locationEditText.setText(locationFromArray)

                        updateCustomerButton.setText(R.string.update_button)
                        updateCustomerButton.setOnClickListener {
                            val updatedPhoneNumber = phoneEditText.text.toString().trim { it <= ' ' }
                            val updatedLocation = locationEditText.text.toString().trim { it <= ' ' }

                            databaseHelper.updateCustomer(idFromArray, updatedPhoneNumber, updatedLocation)

                            updateArray(databaseHelper.customerArray)
                            notifyItemChanged(holder.adapterPosition)
                            dialog.dismiss()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                builder.show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            true
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
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
        return databaseHelper.getFilteredCustomerArray(name!!)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var customerNameTextView: TextView
        var phoneNumberTextView: TextView
        var locationTextView: TextView
        var dateTextView: TextView

        init {
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView)
            phoneNumberTextView = itemView.findViewById(R.id.customerPhoneNumberTextView)
            locationTextView = itemView.findViewById(R.id.locationTextView)
            dateTextView = itemView.findViewById(R.id.customerDateAddedTextView)
        }
    }

}