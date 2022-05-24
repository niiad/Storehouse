package com.niiadotei.storehouse.ui.finance

import android.content.Context

import com.niiadotei.storehouse.data.DatabaseHelper

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.DecimalFormat

class Accountant(context: Context) {
    private val databaseHelper: DatabaseHelper

    init {
        databaseHelper = DatabaseHelper(context)
    }

    fun getCurrentStockValue(): Double {
        val jsonArray: JSONArray = databaseHelper.productArray

        var stockValue: Double = 0.0

        try {
            for (i: Int in 0..jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                stockValue += (jsonObject.getInt("quantity") * jsonObject.getDouble("price"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return stockValue
    }

    fun getMostPricedProductName(): String {
        val jsonArray: JSONArray = databaseHelper.productArray

        var name: String = ""
        var mostPriced: Double = 0.0

        try {
            for (i: Int in 0..jsonArray.length()) {
                var jsonObject: JSONObject = jsonArray.getJSONObject(i)

                var price = jsonObject.getDouble("price")

                if (price > mostPriced) {
                    mostPriced = price
                    name = jsonObject.getString("display")
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return name
    }

    fun getLeastPricedProductName(): String {
        val jsonArray: JSONArray = databaseHelper.productArray

        var name: String = ""
        var leastPriced: Double = 0.0

        try {
            val firstJSONObject: JSONObject = jsonArray.getJSONObject(1)
            leastPriced = firstJSONObject.getDouble("price")

            for (i: Int in 0..jsonArray.length()) {
                var jsonObject: JSONObject = jsonArray.getJSONObject(i)

                var price = jsonObject.getDouble("price")

                if (price <= leastPriced) {
                    leastPriced = price
                    name = jsonObject.getString("display")
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return name
    }

    fun getMostProfitableProductName(): String {
        val jsonArray: JSONArray = databaseHelper.productArray
        val decimalFormat: DecimalFormat = DecimalFormat("#.##")

        var name: String = ""
        var mostProfitable: Double = 0.0

        try {
            for (i: Int in 0..jsonArray.length()) {
                var jsonObject: JSONObject = jsonArray.getJSONObject(i)

                var cost: Double = jsonObject.getDouble("cost")
                var price: Double = jsonObject.getDouble("price")

                var profit: Double = decimalFormat.format(price - cost).toDouble()
                var profitPercent: Double = decimalFormat.format((profit * 100) / cost).toDouble()

                if (profitPercent > mostProfitable) {
                    mostProfitable = profitPercent

                    name = jsonObject.getString("display")
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return name
    }
}