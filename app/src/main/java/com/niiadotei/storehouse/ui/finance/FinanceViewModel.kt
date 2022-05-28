package com.niiadotei.storehouse.ui.finance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView

import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.RecyclerView

import com.niiadotei.storehouse.R
import com.niiadotei.storehouse.data.DatabaseHelper

import org.json.JSONArray
import org.json.JSONException

import java.text.DecimalFormat

class FinanceViewModel(var fragment: Fragment, var jsonArray: JSONArray) : RecyclerView.Adapter<FinanceViewModel.ViewHolder>() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finance_product, parent, false)
        databaseHelper = DatabaseHelper(view.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = jsonArray.getJSONObject(position)

            val decimalFormat = DecimalFormat("#.##")

            val resources = holder.itemView.context.resources
            val locale = resources.configuration.locale
            val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

            holder.displayProductNameTextView.text = jsonObject.getString("display")

            holder.productNameTextView.text = jsonObject.getString("name")

            val cost = jsonObject.getDouble("cost")
            val costValue = currencyInstance.format(cost)
            holder.costPricePerUnitTextView.text = costValue

            val quantity = jsonObject.getInt("quantity")
            val quantityValue = quantity.toString()
            holder.quantityInStockTextView.text = quantityValue

            val totalCost = decimalFormat.format(cost).toDouble() * quantity
            val totalCostValue = currencyInstance.format(totalCost)
            holder.totalCostPriceTextView.text = totalCostValue

            val sellingPrice = jsonObject.getDouble("price")
            val sellingPriceValue = currencyInstance.format(sellingPrice)
            holder.sellingPricePerUnitTextView.text = sellingPriceValue

            val totalSellingPrice = decimalFormat.format(sellingPrice).toDouble() * quantity
            val totalSellingPriceValue = currencyInstance.format(totalSellingPrice)
            holder.totalSellingPriceTextView.text = totalSellingPriceValue

            val profitPerUnit = decimalFormat.format(sellingPrice - cost).toDouble()
            val profitPerUnitValue = currencyInstance.format(profitPerUnit)
            holder.profitPerUnitTextView.text = profitPerUnitValue

            val totalProfit = decimalFormat.format(profitPerUnit * quantity).toDouble()
            val totalProfitValue = currencyInstance.format(totalProfit)
            holder.totalProfitTextView.text = totalProfitValue

            var profitPercent = decimalFormat.format(profitPerUnit * 100).toDouble() / cost
            if (java.lang.Double.isInfinite(profitPercent)) {
                profitPercent = 100.00
            }

            val profitPercentValue = String.format(locale, "%.0f%%", profitPercent)
            holder.profitPercentTextView.text = profitPercentValue
            holder.profitPercentTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.secondary_orange))
            if (sellingPrice > cost) {
                holder.totalProfitTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.secondary_emerald))
            } else if (sellingPrice < cost) {
                holder.totalProfitTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.secondary_red))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayProductNameTextView: TextView
        var productNameTextView: TextView
        var costPricePerUnitTextView: TextView
        var totalCostPriceTextView: TextView
        var sellingPricePerUnitTextView: TextView
        var totalSellingPriceTextView: TextView
        var profitPerUnitTextView: TextView
        var totalProfitTextView: TextView
        var quantityInStockTextView: TextView
        var profitPercentTextView: TextView

        init {
            displayProductNameTextView = itemView.findViewById(R.id.displayProductName)
            productNameTextView = itemView.findViewById(R.id.productName)
            costPricePerUnitTextView = itemView.findViewById(R.id.costPricePerUnit)
            totalCostPriceTextView = itemView.findViewById(R.id.totalCostPrice)
            sellingPricePerUnitTextView = itemView.findViewById(R.id.sellingPricePerUnit)
            totalSellingPriceTextView = itemView.findViewById(R.id.totalSellingPrice)
            profitPerUnitTextView = itemView.findViewById(R.id.profitPerUnit)
            totalProfitTextView = itemView.findViewById(R.id.totalProfit)
            quantityInStockTextView = itemView.findViewById(R.id.quantityInStock)
            profitPercentTextView = itemView.findViewById(R.id.profitPercent)
        }
    }
}