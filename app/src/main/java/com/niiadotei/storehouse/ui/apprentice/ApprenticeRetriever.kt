package com.niiadotei.storehouse.ui.apprentice

import android.content.Context

import com.niiadotei.storehouse.data.DatabaseHelper

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApprenticeRetriever(date: String, context: Context) {
    private val date: String
    private val databaseHelper: DatabaseHelper

    init {
        this.date = date
        this.databaseHelper = DatabaseHelper(context)
    }

    fun getTotalSalesMadeToday(): Double {
        val jsonArray: JSONArray = databaseHelper.getPurchasesArrayFromDate(date)

        var totalSales: Double = 0.0

        try {
            for (i: Int in 0..jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                totalSales += jsonObject.getDouble("amount")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return totalSales
    }

    fun getTotalQuantitySoldToday(): Int {
        val jsonArray: JSONArray = databaseHelper.getPurchasesArrayFromDate(date)

        var quantity: Int = 0

        try {
            for (i: Int in 0..jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                quantity += jsonObject.getInt("quantity")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return quantity
    }

    fun getTotalNumberOfSalesToday(): Int {
        val jsonArray: JSONArray = databaseHelper.getPurchasesArrayFromDate(date)

        return jsonArray.length()
    }

    fun getNumberOfNewCustomersToday(): Int {
        val jsonArray: JSONArray = databaseHelper.getCustomerArrayFromDate(date)

        return jsonArray.length()
    }
}