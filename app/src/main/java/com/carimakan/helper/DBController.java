package com.carimakan.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBController  extends SQLiteOpenHelper {

	public DBController(Context applicationcontext) {
        super(applicationcontext, "restaurant.db", null, 1);
    }
	//Creates Table
	@Override
	public void onCreate(SQLiteDatabase database) {
		String query;
		query = "CREATE TABLE restaurant( Id INTEGER PRIMARY KEY, Nama TEXT, Alamat TEXT, Lat REAL, Long REAL, Kategori TEXT, Link_gambar TEXT)";
        database.execSQL(query);
	}
	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query;
		query = "DROP TABLE IF EXISTS restaurant";
		database.execSQL(query);
        onCreate(database);
	}
	
	
	/**
	 * Inserts User into SQLite DB
	 * @param queryValues
	 */
	public void insertUser(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Id", queryValues.get("id"));
		values.put("Nama", queryValues.get("nama"));
        values.put("Alamat", queryValues.get("alamat"));
        values.put("Lat", queryValues.get("lat"));
        values.put("Long", queryValues.get("long"));
		values.put("Kategori", queryValues.get("kategori"));
		values.put("Link_gambar", queryValues.get("link_gambar"));
		database.insert("restaurant", null, values);
		database.close();
	}
	
	/**
	 * Get list of Users from SQLite DB as Array List
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getAllUser() {
		ArrayList<HashMap<String, String>> usersList;
		usersList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM restaurant";
	    SQLiteDatabase database = this.getWritableDatabase();
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	HashMap<String, String> map = new HashMap<String, String>();
	        	map.put("Id", cursor.getString(0));
	        	map.put("Nama", cursor.getString(1));
                map.put("Alamat", cursor.getString(2));
                map.put("Lat", cursor.getString(3));
                map.put("Long", cursor.getString(4));
                map.put("Kategori", cursor.getString(5));
                map.put("Link_gambar", cursor.getString(6));
                usersList.add(map);

	        } while (cursor.moveToNext());
	    }
	    database.close();
	    return usersList;
	}

}
