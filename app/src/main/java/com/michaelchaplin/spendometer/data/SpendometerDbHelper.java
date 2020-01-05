package com.michaelchaplin.spendometer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpendometerDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "spendometer.db";

    // Defines the database version number. Increments every time the database schema is changed
    private static final int DATABASE_VERSION = 1;

    // Constructs a new instance of SpendometerDbHelper
    public SpendometerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // This section is called when the database is created for the first time

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ****************************** Categories ************************************
        // Create a String that contains the SQL ststement to create the Categories table
        String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES + " ("
                + SpendometerContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, "
                + SpendometerContract.CategoryEntry.COL_NAME + " TEXT NOT NULL, "
                + SpendometerContract.CategoryEntry.COL_ICON_ID + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // The database is still at version one

    }
}
