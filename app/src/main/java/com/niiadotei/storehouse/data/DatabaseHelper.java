package com.niiadotei.storehouse.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {
    final static String databaseName = "backstore_db";
    String customerTableName = "customers";

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createCustomerTableQuery = "create table " + customerTableName +
                "(id INTEGER primary key autoincrement, name TEXT, phone TEXT, location TEXT, date TEXT)";


        sqLiteDatabase.execSQL(createCustomerTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropCustomerTableQuery = "drop table if exists " + customerTableName;

        sqLiteDatabase.execSQL(dropCustomerTableQuery);

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

    public void updateCustomer(String id, String phone, String location) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "update " + customerTableName + " set phone='" + phone + "', location='" + location + "' where id='" + id + "'";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void deleteCustomer(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "delete from " + customerTableName + " where id='" + id + "'";
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
}
