package com.michaelchaplin.spendometer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class SpendometerDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "spendometer.db";

    // Defines the database version number. Increments every time the database schema is changed
    private static final int DATABASE_VERSION = 1;

    // String for clearing a table
    public static final String CLEAR_TABLE = "DROP TABLE IF EXISTS ";

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

        // Clear the table if it already exists
        db.execSQL(CLEAR_TABLE + SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES);

        // Create the Categories table in the database
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);

        Log.d(LOG_TAG, "onCreate: Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // The database is still at version one
        /*switch (oldVersion) {
            case 2:
                sqLiteDatabase.execSQL(SpendometerContract.DATABASE_ALTER_CATEGORIES_V1_TO_V2);
            default:
                throw new IllegalStateException("OnUpgrade() with unknown oldVersion");
        }*/
    }
}
