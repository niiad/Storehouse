package com.niiadotei.storehouse.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {
    final static String databaseName = "backstore_db";

    String customerTableName = "customers";
    String productTableName = "products";
    String supplierTableName = "suppliers";

    String supplyChainTable = "supply";

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createCustomerTableQuery = "create table " + customerTableName +
                "(id INTEGER primary key autoincrement, name TEXT, phone TEXT, location TEXT, date TEXT)";

        String createProductTableQuery = "create table " + productTableName +
                "(id INTEGER primary key autoincrement, name TEXT, display TEXT, cost INTEGER, price INTEGER, quantity INTEGER, supplier INTEGER)";

        String createSupplierTableQuery = "create table " + supplierTableName +
                "(id INTEGER primary key autoincrement, name TEXT, display TEXT, phone TEXT, location TEXT)";

        String createSupplyTableQuery = "create table " + supplyChainTable +
                "(id INTEGER primary key autoincrement, supplier INTEGER, product INTEGER, quantity INTEGER, supplied INTEGER)";

        sqLiteDatabase.execSQL(createCustomerTableQuery);
        sqLiteDatabase.execSQL(createSupplierTableQuery);
        sqLiteDatabase.execSQL(createProductTableQuery);
        sqLiteDatabase.execSQL(createSupplyTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropCustomerTableQuery = "drop table if exists " + customerTableName;
        String dropSupplierTableQuery = "drop table if exists " + supplierTableName;
        String dropProductTableQuery = "drop table if exists " + productTableName;
        String dropSupplyChainTableQuery = "drop table if exists " + supplyChainTable;

        sqLiteDatabase.execSQL(dropCustomerTableQuery);
        sqLiteDatabase.execSQL(dropSupplierTableQuery);
        sqLiteDatabase.execSQL(dropProductTableQuery);
        sqLiteDatabase.execSQL(dropSupplyChainTableQuery);

        onCreate(sqLiteDatabase);
    }

    public void insertCustomer(String name, String phone, String location, String date) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("location", location);
        contentValues.put("date", date);

        sqLiteDatabase.insertWithOnConflict(customerTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
    }

    public void insertSupplier(String supplierName, String supplierDisplayName, String supplierPhone, String supplierLocation) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", supplierName);
        contentValues.put("display", supplierDisplayName);
        contentValues.put("phone", supplierPhone);
        contentValues.put("location", supplierLocation);

        sqLiteDatabase.insertWithOnConflict(supplierTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
    }

    public void insertProduct(String productName, String productDisplayName, String cost, String price, String quantity, String supplier) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", productName);
        contentValues.put("display", productDisplayName);
        contentValues.put("cost", cost);
        contentValues.put("price", price);
        contentValues.put("quantity", quantity);
        contentValues.put("supplier", supplier);

        sqLiteDatabase.insertWithOnConflict(productTableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
    }

    public void insertSupply(String supplier, String product, String quantity, String supplied) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("supplier", supplier);
        contentValues.put("product", product);
        contentValues.put("quantity", quantity);
        contentValues.put("supplied", supplied);

        sqLiteDatabase.insertWithOnConflict(supplyChainTable, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
    }

    public void updateCustomer(String id, String phone, String location) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "update " + customerTableName + " set phone='" + phone + "', location='" + location + "' where id='" + id + "'";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void updateSupplier(String id, String name, String display, String phone, String location) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "update " + supplierTableName + " set name='" + name + "', display='" + display + "', phone='" + phone + "', location'" + location + "' where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void updateProduct(String id, String cost, String price) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "update " + productTableName + " set cost='" + cost + "', price='" + price + "' where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void updateSupply(String id, String supplied) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "update " + supplyChainTable + " set supplied='" + supplied + "' where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void deleteCustomer(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "delete from " + customerTableName + " where id='" + id + "'";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void deleteProduct(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "delete from " + productTableName + " where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void deleteSupplier(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "delete from " + supplierTableName + " where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void deleteSupplyChain(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "delete from " + supplyChainTable + " where id='" + id + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void truncateCustomerTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query0 = "delete from " + customerTableName;
        String query1 = "delete from sqlite_sequence where name='" + customerTableName + "'";

        sqLiteDatabase.execSQL(query0);
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.close();
    }

    public void truncateProductTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query0 = "delete from " + productTableName;
        String query1 = "delete from sqlite_sequence where name='" + productTableName + "'";

        sqLiteDatabase.execSQL(query0);
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.close();
    }

    public void truncateSupplierTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query0 = "delete from " + supplierTableName;
        String query1 = "delete from sqlite_sequence where name='" + supplierTableName + "'";

        sqLiteDatabase.execSQL(query0);
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.close();
    }

    public void truncateSupplyChainTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query0 = "delete from " + supplyChainTable;
        String query1 = "delete from sqlite_sequence where name='" + supplyChainTable + "'";

        sqLiteDatabase.execSQL(query0);
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.close();
    }

    public JSONArray getCustomerArray() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + customerTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("name", cursor.getString(1));
                    jsonObject.put("phone", cursor.getString(2));
                    jsonObject.put("location", cursor.getString(3));
                    jsonObject.put("date", cursor.getString(4));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }

    public JSONArray getProductArray() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + productTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("name", cursor.getString(1));
                    jsonObject.put("display", cursor.getString(2));
                    jsonObject.put("cost", cursor.getString(3));
                    jsonObject.put("price", cursor.getString(4));
                    jsonObject.put("quantity", cursor.getString(5));
                    jsonObject.put("supplier", cursor.getString(6));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }

    public JSONArray getSupplyChainArray() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + supplyChainTable;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("supplier", cursor.getString(1));
                    jsonObject.put("product", cursor.getString(2));
                    jsonObject.put("quantity", cursor.getString(3));
                    jsonObject.put("supplied", cursor.getString(4));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }

    public JSONArray getSupplierArray() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + supplierTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("name", cursor.getString(1));
                    jsonObject.put("display", cursor.getString(2));
                    jsonObject.put("phone", cursor.getString(3));
                    jsonObject.put("location", cursor.getString(4));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }

    public String[] getAllSupplierDisplayNames() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<String> displays = new ArrayList<>();
        String[] result;

        String query = "select * from " + supplierTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                displays.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        result = new String[displays.size()];
        result = displays.toArray(result);

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public String[] getAllProductDisplayNames() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<String> displays = new ArrayList<>();
        String[] result;

        String query = "select * from " + productTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                displays.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        result = new String[displays.size()];
        result = displays.toArray(result);

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public String getSupplierID(String display) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "select * from " + supplierTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        String id = "";

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(display)) {
                    id = cursor.getString(0);

                    cursor.close();
                    sqLiteDatabase.close();

                    return id;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return id;
    }

    public String getProductID(String display) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "select * from " + productTableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        String id = "";

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(display)) {
                    id = cursor.getString(0);

                    cursor.close();
                    sqLiteDatabase.close();

                    return id;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return id;
    }

    public JSONArray getFilteredCustomerArray(String name) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + customerTableName + " where name like'%" + name + "%'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("name", cursor.getString(1));
                    jsonObject.put("phone", cursor.getString(2));
                    jsonObject.put("location", cursor.getString(3));
                    jsonObject.put("date", cursor.getString(4));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }

    public JSONArray getFilteredProductArray(String name) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        String query = "select * from " + productTableName + " where name like'%" + name + "%'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("name", cursor.getString(1));
                    jsonObject.put("display", cursor.getString(2));
                    jsonObject.put("cost", cursor.getString(3));
                    jsonObject.put("price", cursor.getString(4));
                    jsonObject.put("quantity", cursor.getString(5));
                    jsonObject.put("supplier", cursor.getString(6));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return jsonArray;
    }
}
