package com.uxm.embeddedproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "MapLocation.db";
	private static final String createQuery = "CREATE TABLE location ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title text, "
			+ "latitude DOUBLE, longitude DOUBLE" + ");";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS location");

		onCreate(db);
	}

}
