package com.niiadotei.storehouse.ui.supplychain

import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import com.niiadotei.storehouse.data.DatabaseHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import com.niiadotei.storehouse.R
import androidx.core.content.ContextCompat
import android.text.TextUtils
import org.json.JSONException
import android.content.DialogInterface
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

class SupplyChainViewModel(var fragment: Fragment, var jsonArray: JSONArray) : RecyclerView.Adapter<SupplyChainViewModel.ViewHolder>(), Filterable {
    private var filteredJsonArray: JSONArray
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var filteredViewHolder: FilteredViewHolder


    init {
        filteredJsonArray = JSONArray()
    }

    fun updateArray(jsonArray: JSONArray) {
        this.jsonArray = jsonArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_supply_chain, parent, false)
        databaseHelper = DatabaseHelper(view.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("supplyChain", Context.MODE_PRIVATE)
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            val product = jsonObject.getInt("product")
            holder.suppliedProductTextView.text = databaseHelper.getProductNameFromID(jsonObject.getInt("product"))

            holder.supplierTextView.text = databaseHelper.getSupplierNameFromID(jsonObject.getInt("supplier"))

            val request = holder.suppliedProductTextView.text.toString() + "Request"
            holder.requestTextView.text = sharedPreferences.getString(request, "")

            val supplyAlreadyProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.text.toString(), false)
            if (supplyAlreadyProgress) {
                holder.supplyButton.setText(R.string.supply_progress_hint)
                holder.supplyButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.secondary_emerald))
                holder.supplyButton.isEnabled = false

                val supplyQuantityView = holder.suppliedProductTextView.text.toString() + "Quantity"
                val quantity = sharedPreferences.getInt(supplyQuantityView, 0)

                holder.supplyQuantityEditText.setText(quantity.toString())
                holder.supplyQuantityEditText.isEnabled = false
                holder.receivedButton.isEnabled = true
                holder.receivedButton.setText(R.string.received_button)
            } else {
                holder.receivedButton.setText(R.string.received_progress_hint)
            }

            val quantity = intArrayOf(0)
            holder.supplyButton.setOnClickListener SupplyButton@{ view: View ->
                val quantityFromUser = holder.supplyQuantityEditText.text.toString()

                try {
                    quantity[0] = quantityFromUser.toInt()
                    val editor = sharedPreferences.edit()
                    val supplyQuantityView = holder.suppliedProductTextView.text.toString() + "Quantity"
                    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                    editor.putString(request, date)
                    editor.putInt(supplyQuantityView, quantity[0])
                    editor.apply()
                } catch (e: NumberFormatException) {

                }
                if (TextUtils.isEmpty(quantityFromUser)) {
                    Toast.makeText(view.context, R.string.quantity_not_given_message, Toast.LENGTH_LONG).show()
                    return@SupplyButton
                }

                val editor = sharedPreferences.edit()
                editor.putBoolean(holder.suppliedProductTextView.text.toString(), true)
                editor.apply()

                val inProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.text.toString(), false)
                if (inProgress) {
                    holder.supplyButton.setText(R.string.supply_progress_hint)
                    holder.supplyButton.setBackgroundColor(ContextCompat.getColor(view.context, R.color.secondary_emerald))
                    holder.supplyButton.isEnabled = false

                    holder.supplyQuantityEditText.isEnabled = false

                    holder.receivedButton.isEnabled = true
                    holder.receivedButton.setBackgroundColor(ContextCompat.getColor(view.context, R.color.primary_dark))
                    holder.receivedButton.setText(R.string.received_button)
                }
            }

            holder.receivedButton.setOnClickListener { view: View ->
                val inProgress = sharedPreferences.getBoolean(holder.suppliedProductTextView.text.toString(), false)
                if (inProgress) {
                    val editor = sharedPreferences.edit()

                    val supplyQuantityView = holder.suppliedProductTextView.text.toString() + "Quantity"

                    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                    databaseHelper.updateProductQuantity(product, sharedPreferences.getInt(supplyQuantityView, 0))

                    databaseHelper.updateSupply(product, sharedPreferences.getInt(supplyQuantityView, 0), sharedPreferences.getString(request, "")!!, date)

                    Toast.makeText(view.context, R.string.supply_successful_message, Toast.LENGTH_LONG).show()

                    holder.supplyButton.isEnabled = true
                    holder.supplyButton.setText(R.string.supply_button)
                    holder.supplyButton.setBackgroundColor(ContextCompat.getColor(view.context, R.color.secondary_red))

                    holder.supplyQuantityEditText.setText("")
                    holder.supplyQuantityEditText.isEnabled = true

                    holder.receivedButton.isEnabled = false
                    holder.receivedButton.setBackgroundColor(ContextCompat.getColor(view.context, R.color.primary_light))
                    holder.receivedButton.setText(R.string.received_progress_hint)

                    holder.requestTextView.text = ""

                    editor.putBoolean(holder.suppliedProductTextView.text.toString(), false)
                    editor.apply()
                } else {
                    holder.receivedButton.setText(R.string.received_progress_hint)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        holder.itemView.setOnLongClickListener {
            val getPosition = holder.adapterPosition

            try {
                val jsonObject = jsonArray.getJSONObject(getPosition)

                val stringID = jsonObject.getString("id")

                val builder = AlertDialog.Builder(fragment.activity)
                builder.setTitle(R.string.confirm_title)
                builder.setMessage(R.string.supply_delete_message)

                builder.setPositiveButton(R.string.positive_button) { _: DialogInterface?, _: Int ->
                    databaseHelper.deleteSupplyChain(stringID)

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
        return filteredViewHolder
    }

    fun getFilteredJsonArray(name: String?): JSONArray {
        return jsonArray
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var suppliedProductTextView: TextView
        var supplierTextView: TextView
        var requestTextView: TextView
        var supplyQuantityEditText: EditText
        var supplyButton: Button
        var receivedButton: Button

        init {
            suppliedProductTextView = itemView.findViewById(R.id.displaySuppliedProductName)
            supplierTextView = itemView.findViewById(R.id.displaySupplierName)
            requestTextView = itemView.findViewById(R.id.requestTextView)
            supplyQuantityEditText = itemView.findViewById(R.id.supply_quantity)
            supplyButton = itemView.findViewById(R.id.supply_button)
            receivedButton = itemView.findViewById(R.id.received_button)
        }
    }

    class FilteredViewHolder(private val supplyChainViewModel: SupplyChainViewModel, private val jsonArray: JSONArray) : Filter() {
        private var filteredJsonArray: JSONArray

        init {
            filteredJsonArray = JSONArray()
        }

        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val results = FilterResults()

            filteredJsonArray = if (charSequence.isEmpty()) {
                jsonArray
            } else {
                val filterPattern = charSequence.toString().trim { it <= ' ' }
                supplyChainViewModel.getFilteredJsonArray(filterPattern)
            }

            results.values = filteredJsonArray
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            supplyChainViewModel.updateArray(filterResults.values as JSONArray)
            supplyChainViewModel.notifyDataSetChanged()
        }

    }

}