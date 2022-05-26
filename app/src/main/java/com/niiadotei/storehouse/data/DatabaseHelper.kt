package com.niiadotei.storehouse.data

import android.content.ContentValues
import android.content.Context

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, databaseName, null, 1) {
    private var customerTableName = "customers"
    private var productTableName = "products"
    private var supplierTableName = "suppliers"
    private var purchasesTableName = "purchases"
    private var supplyChainTable = "supply"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val createCustomerTableQuery = "create table " + customerTableName +
                "(id INTEGER primary key autoincrement, name TEXT, phone TEXT, location TEXT, date TEXT)"
        val createProductTableQuery = "create table " + productTableName +
                "(id INTEGER primary key autoincrement, name TEXT, display TEXT, cost REAL, price REAL, quantity INTEGER, supplier INTEGER)"
        val createSupplierTableQuery = "create table " + supplierTableName +
                "(id INTEGER primary key autoincrement, name TEXT, display TEXT, phone TEXT, location TEXT)"
        val createSupplyTableQuery = "create table " + supplyChainTable +
                "(id INTEGER primary key autoincrement, supplier INTEGER, product INTEGER, quantity INTEGER, request TEXT, received TEXT)"
        val createPurchasesTableQuery = "create table " + purchasesTableName +
                "(id INTEGER primary key autoincrement, customer TEXT, product TEXT, amount REAL, quantity INTEGER, date TEXT)"

        sqLiteDatabase.execSQL(createCustomerTableQuery)
        sqLiteDatabase.execSQL(createSupplierTableQuery)
        sqLiteDatabase.execSQL(createProductTableQuery)
        sqLiteDatabase.execSQL(createPurchasesTableQuery)
        sqLiteDatabase.execSQL(createSupplyTableQuery)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        val dropCustomerTableQuery = "drop table if exists $customerTableName"
        val dropSupplierTableQuery = "drop table if exists $supplierTableName"
        val dropProductTableQuery = "drop table if exists $productTableName"
        val dropPurchasesTableQuery = "drop table if exists $purchasesTableName"
        val dropSupplyChainTableQuery = "drop table if exists $supplyChainTable"

        sqLiteDatabase.execSQL(dropCustomerTableQuery)
        sqLiteDatabase.execSQL(dropSupplierTableQuery)
        sqLiteDatabase.execSQL(dropProductTableQuery)
        sqLiteDatabase.execSQL(dropPurchasesTableQuery)
        sqLiteDatabase.execSQL(dropSupplyChainTableQuery)

        onCreate(sqLiteDatabase)
    }

    fun insertCustomer(name: String?, phone: String?, location: String?, date: String?) {
        val sqLiteDatabase = writableDatabase

        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("phone", phone)
        contentValues.put("location", location)
        contentValues.put("date", date)

        sqLiteDatabase.insertWithOnConflict(customerTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)

        sqLiteDatabase.close()
    }

    fun insertSupplier(supplierName: String?, supplierDisplayName: String?, supplierPhone: String?, supplierLocation: String?) {
        val sqLiteDatabase = writableDatabase

        val contentValues = ContentValues()

        contentValues.put("name", supplierName)
        contentValues.put("display", supplierDisplayName)
        contentValues.put("phone", supplierPhone)
        contentValues.put("location", supplierLocation)

        sqLiteDatabase.insertWithOnConflict(supplierTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)

        sqLiteDatabase.close()
    }

    fun insertProduct(productName: String?, productDisplayName: String?, cost: Double, price: Double, quantity: Int, supplier: Int) {
        val sqLiteDatabase = writableDatabase

        val contentValues = ContentValues()

        contentValues.put("name", productName)
        contentValues.put("display", productDisplayName)
        contentValues.put("cost", cost)
        contentValues.put("price", price)
        contentValues.put("quantity", quantity)
        contentValues.put("supplier", supplier)

        sqLiteDatabase.insertWithOnConflict(productTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)

        sqLiteDatabase.close()
    }

    fun insertPurchases(customer: String?, product: String?, amount: Double, quantity: Int, date: String?) {
        val sqLiteDatabase = writableDatabase

        val contentValues = ContentValues()

        contentValues.put("customer", customer)
        contentValues.put("product", product)
        contentValues.put("amount", amount)
        contentValues.put("quantity", quantity)
        contentValues.put("date", date)

        sqLiteDatabase.insertWithOnConflict(purchasesTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)

        sqLiteDatabase.close()
    }

    fun insertSupply(supplier: Int, product: String?) {
        val sqLiteDatabase = writableDatabase

        val contentValues = ContentValues()

        contentValues.put("supplier", supplier)
        contentValues.put("product", product)

        sqLiteDatabase.insertWithOnConflict(supplyChainTable, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)

        sqLiteDatabase.close()
    }

    fun updateCustomer(id: String, phone: String, location: String) {
        val sqLiteDatabase = writableDatabase

        val query = "update $customerTableName set phone='$phone', location='$location' where id='$id'"

        sqLiteDatabase.execSQL(query)

        sqLiteDatabase.close()
    }

    fun updateProductQuantity(id: Int, quantity: Int) {
        val sqLiteDatabase = writableDatabase

        val newQuantity: Int

        val query0 = "select * from $productTableName"

        val cursor = sqLiteDatabase.rawQuery(query0, null)

        var oldQuantity = 0

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(0) == id) {
                    oldQuantity = cursor.getInt(5)
                }
            } while (cursor.moveToNext())
        }

        newQuantity = oldQuantity + quantity

        val query1 = "update $productTableName set quantity='$newQuantity' where id='$id'"

        sqLiteDatabase.execSQL(query1)

        cursor.close()
        sqLiteDatabase.close()
    }

    fun updateSupply(product: Int, quantity: Int, request: String, received: String) {
        val sqLiteDatabase = writableDatabase

        val query = "update $supplyChainTable set quantity='$quantity', request='$request', received='$received' where product='$product'"

        sqLiteDatabase.execSQL(query)

        sqLiteDatabase.close()
    }

    fun deleteCustomer(id: String) {
        val sqLiteDatabase = writableDatabase

        val query = "delete from $customerTableName where id='$id'"

        sqLiteDatabase.execSQL(query)

        sqLiteDatabase.close()
    }

    fun deleteProduct(id: String) {
        val sqLiteDatabase = writableDatabase

        val query = "delete from $productTableName where id='$id'"

        sqLiteDatabase.execSQL(query)

        sqLiteDatabase.close()
    }

    fun deleteSupplyChain(id: String) {
        val sqLiteDatabase = writableDatabase

        val query = "delete from $supplyChainTable where id='$id'"

        sqLiteDatabase.execSQL(query)

        sqLiteDatabase.close()
    }

    fun truncateCustomerTable() {
        val sqLiteDatabase = writableDatabase

        val query0 = "delete from $customerTableName"
        val query1 = "delete from sqlite_sequence where name='$customerTableName'"

        sqLiteDatabase.execSQL(query0)
        sqLiteDatabase.execSQL(query1)

        sqLiteDatabase.close()
    }

    val customerArray: JSONArray
        get() {
            val sqLiteDatabase = readableDatabase

            val jsonArray = JSONArray()

            val query = "select * from $customerTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("id", cursor.getString(0))
                        jsonObject.put("name", cursor.getString(1))
                        jsonObject.put("phone", cursor.getString(2))
                        jsonObject.put("location", cursor.getString(3))
                        jsonObject.put("date", cursor.getString(4))
                        jsonArray.put(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            sqLiteDatabase.close()

            return jsonArray
        }

    fun getCustomerArrayFromDate(date: String): JSONArray {
        val sqLiteDatabase = readableDatabase

        val jsonArray = JSONArray()

        val query = "select * from $customerTableName where date='$date'"

        val cursor = sqLiteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("id", cursor.getString(0))
                    jsonObject.put("name", cursor.getString(1))
                    jsonObject.put("phone", cursor.getString(2))
                    jsonObject.put("location", cursor.getString(3))
                    jsonObject.put("date", cursor.getString(4))
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        sqLiteDatabase.close()

        return jsonArray
    }

    val suppliersArray: JSONArray
        get() {
            val sqLiteDatabase = readableDatabase

            val jsonArray = JSONArray()

            val query = "select * from $supplierTableName"
            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("id", cursor.getInt(0))
                        jsonObject.put("name", cursor.getString(1))
                        jsonObject.put("display", cursor.getString(2))
                        jsonObject.put("phone", cursor.getString(3))
                        jsonObject.put("location", cursor.getString(4))
                        jsonArray.put(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            sqLiteDatabase.close()

            return jsonArray
        }

    val productArray: JSONArray
        get() {
            val sqLiteDatabase = readableDatabase

            val jsonArray = JSONArray()

            val query = "select * from $productTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("id", cursor.getString(0))
                        jsonObject.put("name", cursor.getString(1))
                        jsonObject.put("display", cursor.getString(2))
                        jsonObject.put("cost", cursor.getString(3))
                        jsonObject.put("price", cursor.getString(4))
                        jsonObject.put("quantity", cursor.getString(5))
                        jsonObject.put("supplier", cursor.getString(6))
                        jsonArray.put(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            sqLiteDatabase.close()

            return jsonArray
        }

    val purchasesArray: JSONArray
        get() {
            val sqLiteDatabase = readableDatabase

            val jsonArray = JSONArray()

            val query = "select * from $purchasesTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("id", cursor.getInt(0))
                        jsonObject.put("customer", cursor.getString(1))
                        jsonObject.put("product", cursor.getString(2))
                        jsonObject.put("amount", cursor.getDouble(3))
                        jsonObject.put("quantity", cursor.getInt(4))
                        jsonObject.put("date", cursor.getString(5))
                        jsonArray.put(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            sqLiteDatabase.close()

            return jsonArray
        }

    fun getPurchasesArray(customer: String): JSONArray {
        val sqLiteDatabase = readableDatabase

        val jsonArray = JSONArray()

        val query = "select * from $purchasesTableName where customer='$customer'"

        val cursor = sqLiteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("id", cursor.getInt(0))
                    jsonObject.put("customer", cursor.getString(1))
                    jsonObject.put("product", cursor.getString(2))
                    jsonObject.put("amount", cursor.getDouble(3))
                    jsonObject.put("quantity", cursor.getInt(4))
                    jsonObject.put("date", cursor.getString(5))
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        sqLiteDatabase.close()

        return jsonArray
    }

    fun getPurchasesArrayFromDate(date: String): JSONArray {
        val sqLiteDatabase = readableDatabase

        val jsonArray = JSONArray()

        val query = "select * from $purchasesTableName where date='$date'"

        val cursor = sqLiteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("id", cursor.getInt(0))
                    jsonObject.put("customer", cursor.getString(1))
                    jsonObject.put("product", cursor.getString(2))
                    jsonObject.put("amount", cursor.getDouble(3))
                    jsonObject.put("quantity", cursor.getInt(4))
                    jsonObject.put("date", cursor.getString(5))
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        sqLiteDatabase.close()

        return jsonArray
    }

    val supplyChainArray: JSONArray
        get() {
            val sqLiteDatabase = readableDatabase

            val jsonArray = JSONArray()

            val query = "select * from $supplyChainTable"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("id", cursor.getString(0))
                        jsonObject.put("supplier", cursor.getString(1))
                        jsonObject.put("product", cursor.getString(2))
                        jsonObject.put("quantity", cursor.getString(3))
                        jsonObject.put("request", cursor.getString(4))
                        jsonObject.put("received", cursor.getString(5))
                        jsonArray.put(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            sqLiteDatabase.close()

            return jsonArray
        }

    val allSupplierDisplayNames: Array<String?>
        get() {
            val sqLiteDatabase = readableDatabase

            val displays = ArrayList<String>()

            val query = "select * from $supplierTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    displays.add(cursor.getString(2))
                } while (cursor.moveToNext())
            }

            var result: Array<String?> = arrayOfNulls(displays.size)
            result = displays.toArray(result)

            cursor.close()
            sqLiteDatabase.close()

            return result
        }

    val allCustomersWithPhone: Array<String?>
        get() {
            val sqLiteDatabase = readableDatabase

            val customers = ArrayList<String>()

            val query = "select * from $customerTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    customers.add(cursor.getString(1) + ", " + cursor.getString(2))
                } while (cursor.moveToNext())
            }

            var result: Array<String?> = arrayOfNulls(customers.size)
            result = customers.toArray(result)

            cursor.close()
            sqLiteDatabase.close()

            return result
        }

    val allProductDisplayNames: Array<String?>
        get() {
            val sqLiteDatabase = readableDatabase

            val displays = ArrayList<String>()

            val query = "select * from $productTableName"

            val cursor = sqLiteDatabase.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    displays.add(cursor.getString(2))
                } while (cursor.moveToNext())
            }

            var result: Array<String?> = arrayOfNulls(displays.size)
            result = displays.toArray(result)

            cursor.close()
            sqLiteDatabase.close()

            return result
        }

    fun getSupplierID(display: String): Int {
        val sqLiteDatabase = readableDatabase

        val query = "select * from $supplierTableName"

        val cursor = sqLiteDatabase.rawQuery(query, null)

        var id = 0

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2) == display) {
                    id = cursor.getInt(0)
                    cursor.close()
                    sqLiteDatabase.close()
                    return id
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return id
    }

    fun getProductID(display: String): String {
        val sqLiteDatabase = readableDatabase

        val query = "select * from $productTableName"

        val cursor = sqLiteDatabase.rawQuery(query, null)

        var id = ""

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2) == display) {
                    id = cursor.getString(0)
                    cursor.close()
                    sqLiteDatabase.close()
                    return id
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return id
    }

    fun getSupplierNameFromID(id: Int): String {
        val sqLiteDatabase = readableDatabase

        val query = "select * from $supplierTableName"

        val cursor = sqLiteDatabase.rawQuery(query, null)

        var name = ""

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(0) == id) {
                    name = cursor.getString(2)
                    cursor.close()
                    sqLiteDatabase.close()
                    return name
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return name
    }

    fun getProductNameFromID(id: Int): String {
        val sqLiteDatabase = readableDatabase

        val query = "select * from $productTableName"

        val cursor = sqLiteDatabase.rawQuery(query, null)

        var name = ""

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(0) == id) {
                    name = cursor.getString(2)
                    cursor.close()
                    sqLiteDatabase.close()
                    return name
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return name
    }

    fun getProductQuantityFromID(id: Int): Int {
        val sqLiteDatabase = readableDatabase

        val query = "select * from $productTableName"

        val cursor = sqLiteDatabase.rawQuery(query, null)

        var quantity = 0

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(0) == id) {
                    quantity = cursor.getInt(5)
                    cursor.close()
                    sqLiteDatabase.close()
                    return quantity
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return quantity
    }

    fun getFilteredCustomerArray(name: String): JSONArray {
        val sqLiteDatabase = readableDatabase

        val jsonArray = JSONArray()

        val query = "select * from $customerTableName where name like'%$name%'"

        val cursor = sqLiteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("id", cursor.getString(0))
                    jsonObject.put("name", cursor.getString(1))
                    jsonObject.put("phone", cursor.getString(2))
                    jsonObject.put("location", cursor.getString(3))
                    jsonObject.put("date", cursor.getString(4))
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return jsonArray
    }

    fun getFilteredProductArray(name: String): JSONArray {
        val sqLiteDatabase = readableDatabase

        val jsonArray = JSONArray()

        val query = "select * from $productTableName where name like'%$name%'"

        val cursor = sqLiteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("id", cursor.getString(0))
                    jsonObject.put("name", cursor.getString(1))
                    jsonObject.put("display", cursor.getString(2))
                    jsonObject.put("cost", cursor.getString(3))
                    jsonObject.put("price", cursor.getString(4))
                    jsonObject.put("quantity", cursor.getString(5))
                    jsonObject.put("supplier", cursor.getString(6))
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        sqLiteDatabase.close()

        return jsonArray
    }

    companion object {
        const val databaseName = "backstore_db"
    }
}