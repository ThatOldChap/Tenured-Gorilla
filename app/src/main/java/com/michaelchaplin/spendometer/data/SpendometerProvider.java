package com.michaelchaplin.spendometer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// Defines the ContentProvider object that acts as the middle man for interacting with the database
public class SpendometerProvider extends ContentProvider {

    // Database helper object
    private SpendometerDbHelper mDbHelper;

    // Tag for any log messages
    public static final String LOG_TAB = SpendometerProvider.class.getSimpleName();

    // Creates a URI matcher object to match the Content URI to a corresponding code
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // URI Matcher code for the content URI for the Categories table
    public static final int CATEGORIES = 2;

    // URI Matcher code for the content URI for a single Category within the Categories table
    public static final int CATEGORIES_ID = 3;

    // Static initializer which runs the first time anything is called from this class
    static {

        // Maps the integer CATEGORIES to provide access to multiple rows of the Categories table
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_CATEGORIES, CATEGORIES);

        // Maps the integer CATEGORIES_ID to provide access to a single row of the Categories table
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_CATEGORIES, CATEGORIES_ID);
    }


    @Override
    public boolean onCreate() {

        // Initialize the SpendometerDbHelper object to create the database
        mDbHelper = new SpendometerDbHelper(getContext());
        return true;
    }

    // Performs a query for a given URI using the give projection, selection, selection args, and sort order.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Create and read the database using the SpendometerDbHelper
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Create a cursor to get an image of the database on the projection
        Cursor cursor = null;

        // Obtain an integer that represents the case of the URI
        int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given URI is
        switch (match) {
            case CATEGORIES:

                // Create a cursor of the whole Categories table in the database
                cursor = database.query(
                        SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CATEGORIES_ID:

                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.CategoryEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Create a cursor of a single Category from the Categories table in the database
                cursor = database.query(
                        SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query, unknown URI " + uri);
        }

        // Set a notification URI on the Cursor so that we know what content URI the Cursor was created for
        // If the data at this URI changes, then we know we need to update the cursor
        if(getContext() != null && cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
