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
import android.util.Log;

// Defines the ContentProvider object that acts as the middle man for interacting with the database
public class SpendometerProvider extends ContentProvider {

    // Database helper object
    private SpendometerDbHelper mDbHelper;

    // Tag for any log messages
    public static final String LOG_TAG = SpendometerProvider.class.getSimpleName();

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
        if (getContext() != null && cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        // Obtain the integer that represents the case of the URI from the UriMatcher
        final int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:
                // Add a new category into the Categories database table
                return insertCategory(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Function to insert a new Category into the database
    private Uri insertCategory(Uri uri, ContentValues values) {

        // Check the values of the ContentValues to make sure they aren't empty and won't create errors
        if (values.containsKey(SpendometerContract.CategoryEntry.COL_NAME)) {

            String name = values.getAsString(SpendometerContract.CategoryEntry.COL_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Category name cannot be blank");
            }
        }

        // Get a writeable version of the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new Category into the database with the given values
        long id = database.insert(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, null, values);

        // Check to make sure the inserted Category is valid
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the CATEGORY_CONTENT_URI
        // Desired URI: content://com.michaelchaplin.spendometer/categories
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Obtain an integer that represents the case of the URI from the UriMatcher
        final int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:

                // Update the entire Categories table
                return updateCategory(uri, contentValues, selection, selectionArgs);

            case CATEGORIES_ID:

                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.CategoryEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Update the selected rows and columns in the Categories table
                return updateCategory(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Updates the categories in the database with the given ContentValues. Returns the number of rows updated
    private int updateCategory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check the values of the ContentValues to make sure they are desirable
        if (values.containsKey(SpendometerContract.CategoryEntry.COL_NAME)) {
            String name = values.getAsString(SpendometerContract.CategoryEntry.COL_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Category cannot be blank");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Create/open the writeable database so it can be updated with the new data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Return the number of database rows affected by the update statement
        int rowsUpdated = database.update(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, values, selection, selectionArgs);

        // Check to see whether rows were updated, then notify all listeners that the given URI has changed
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // Delete the data in the table at a given selection and selectionArgs
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // GEt a writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Store the number of rows deleted from the table
        int rowsDeleted;

        // Obtain an integer that represents the case of the URI from the UriMatcher
        final int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:

                // Delete all the rows that match the selection and selectionArgs
                rowsDeleted = database.delete(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, selection, selectionArgs);

                // Check if there have been any rows deleted from the table, and notify all listeners
                if (rowsDeleted != 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case CATEGORIES_ID:

                // Select which row(s) should be deleted
                selection = SpendometerContract.CategoryEntry._ID + "=?";

                // Select which columns in the table should be deleted
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Delete the specified row(s) give by the ID in the URI
                rowsDeleted = database.delete(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, selection, selectionArgs);

                // Check if there have been any rows deleted from the table, and notify all listeners
                if (rowsDeleted != 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        // Obtain an integer that represents the case of the URI from the UriMatcher
        final int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:
                // Return the URI type as a list of Categories
                return SpendometerContract.CategoryEntry.CONTENT_LIST_TYPE;

            case CATEGORIES_ID:
                // Return the URI type as a single Category
                return SpendometerContract.CategoryEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
