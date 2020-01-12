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

    // URI Matcher codes for the content URIs for the database tables
    public static final int CATEGORIES = 2;
    public static final int CATEGORIES_ID = 3;
    public static final int EXPENSES = 4;
    public static final int EXPENSES_ID = 5;

    // Static initializer which runs the first time anything is called from this class
    static {

        // Maps the integer CATEGORIES to provide access to multiple rows of the Categories table
        // ex. if the uri is "com.michaelchaplin.spendometer.data/categories", return "CATEGORIES" (ie. an int = 2)
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_CATEGORIES, CATEGORIES);
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_EXPENSES, EXPENSES);

        // Maps the integer CATEGORIES_ID to provide access to a single row of the Categories table
        // if the uri is "com.michaelchaplin.spendometer.data/categories/#", return "CATEGORIES_ID" (ie. an int = 3)
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_CATEGORIES + "/#", CATEGORIES_ID);
        sUriMatcher.addURI(SpendometerContract.CONTENT_AUTHORITY, SpendometerContract.PATH_EXPENSES + "/#", EXPENSES_ID);

    }

    @Override
    public boolean onCreate() {

        // Initialize the SpendometerDbHelper object to create the database
        mDbHelper = new SpendometerDbHelper(getContext());
        return true;
    }

    // Performs a query for a given Uri using the given projection, selection, selection args, and sort order.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Create and read the database using the SpendometerDbHelper
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Create a cursor to get an image of the database on the projection
        Cursor cursor;
        // Obtain an integer that represents the case of the Uri
        int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:

                // Create a cursor of the whole Categories table
                cursor = database.query(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CATEGORIES_ID:

                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.CategoryEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Create a cursor of a single Category from the Categories table
                cursor = database.query(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPENSES:

                // Creates a cursor of the whole Expenses table
                cursor = database.query(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPENSES_ID:

                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.ExpenseEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Create a cursor of a single Expense from the Expenses table
                cursor = database.query(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES,
                        projection, selection, selectionArgs, null, null, sortOrder);
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
                // Add a new category into the Categories  table
                return insertCategory(uri, contentValues);

            case EXPENSES:
                // Add a new expense into the Expenses table
                return insertExpense(uri, contentValues);

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
        if (values.containsKey(SpendometerContract.CategoryEntry.COL_ICON_ID)) {
            int icon_id = values.getAsInteger(SpendometerContract.CategoryEntry.COL_ICON_ID);
            if (icon_id == 0) {
                throw new IllegalArgumentException("Icon ID cannot be 0 (default)");
            }
        }

        // Get a writeable version of the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new Category into the database with the given values
        long id = database.insert(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, null, values);

        // Check to make sure the inserted Category is valid
        if (id == -1) {
            Log.d(LOG_TAG, "insertCategory: Failed to insert row for " + uri);
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

    // Function to insert a new Expense into the database
    private Uri insertExpense(Uri uri, ContentValues values){

        // Checks to see if the values of the ContentValues aren't empty to cause any errors
        if(values.containsKey((SpendometerContract.ExpenseEntry.COL_CATEGORY))){
            String category = values.getAsString(SpendometerContract.ExpenseEntry.COL_CATEGORY);
            if(category == null){
                throw new IllegalArgumentException("Please choose a category");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_COST)){
            double cost = values.getAsDouble(SpendometerContract.ExpenseEntry.COL_COST);
            if (cost <= 0) {
                throw new IllegalArgumentException("Please enter a cost");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_DATE)){
            int date = values.getAsInteger(SpendometerContract.ExpenseEntry.COL_DATE);
             if (date <= 0) {
                 throw new IllegalArgumentException("Please enter a date");
             }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_NOTES)){
            String notes = values.getAsString(SpendometerContract.ExpenseEntry.COL_NOTES);
            if(notes == null) {
                throw new IllegalArgumentException("Please enter some notes");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_ICON_ID)){
            int icon_id = values.getAsInteger(SpendometerContract.ExpenseEntry.COL_ICON_ID);
            if (icon_id <= 0) {
                throw new IllegalArgumentException("Icon ID cannot be 0 (default)");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_ACCOUNT)){
            String account = values.getAsString(SpendometerContract.ExpenseEntry.COL_ACCOUNT);
            if (account == null) {
                throw new IllegalArgumentException("Please choose an account");
            }
        }

        // Get a writeable version of the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new Expense into the database with the given values
        long id = database.insert(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES, null, values);

        // Check to make sure the insertion is valid
        if (id == -1) {
            Log.d(LOG_TAG, "insertExpense: Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Expenses content Uri
        // Desired Uri: content://com.michaelchaplin.spendometer/expenses
        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the new Uri with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Obtain an integer that represents the case of the URI from the UriMatcher
        final int match = sUriMatcher.match(uri);

        // Find out which UriMatcher case the given Uri is
        switch (match) {
            case CATEGORIES:
                // Update the whole Categories table
                return updateCategory(uri, contentValues, selection, selectionArgs);

            case CATEGORIES_ID:
                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.CategoryEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Update the selected rows and columns in the Categories table
                return updateCategory(uri, contentValues, selection, selectionArgs);

            case EXPENSES:
                // Update the whole Expenses table
                return updateExpense(uri, contentValues, selection, selectionArgs);

            case EXPENSES_ID:
                // Create a string that forces the selection to be an integer to choose a row in the table
                selection = SpendometerContract.ExpenseEntry._ID + "=?";

                // Take the selected table columns that the user wants to query
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Update the selected rows and columns in the Expenses table
                return updateExpense(uri, contentValues, selection, selectionArgs);

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
                throw new IllegalArgumentException("Category name cannot be blank");
            }
        }

        int icon_id = values.getAsInteger(SpendometerContract.CategoryEntry.COL_ICON_ID);
        if (icon_id == 0) {
            throw new IllegalArgumentException("Category icon cannot be blank");
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

    // Updates the expenses in the database with a given ContentValues and returns the number of rows updates
    private int updateExpense(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check the values of the ContentValues to make sure they are not empty
        if (values.containsKey(SpendometerContract.ExpenseEntry.COL_CATEGORY)) {
            String category = values.getAsString(SpendometerContract.ExpenseEntry.COL_CATEGORY);
            if(category == null){
                throw new IllegalArgumentException("Please choose a category");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_COST)){
            double cost = values.getAsDouble(SpendometerContract.ExpenseEntry.COL_COST);
            if (cost <= 0) {
                throw new IllegalArgumentException("Please enter a cost");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_DATE)){
            int date = values.getAsInteger(SpendometerContract.ExpenseEntry.COL_DATE);
            if (date <= 0) {
                throw new IllegalArgumentException("Please enter a date");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_NOTES)){
            String notes = values.getAsString(SpendometerContract.ExpenseEntry.COL_NOTES);
            if(notes == null) {
                throw new IllegalArgumentException("Please enter some notes");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_ICON_ID)){
            int icon_id = values.getAsInteger(SpendometerContract.ExpenseEntry.COL_ICON_ID);
            if (icon_id <= 0) {
                throw new IllegalArgumentException("Icon ID cannot be 0 (default)");
            }
        }

        if(values.containsKey(SpendometerContract.ExpenseEntry.COL_ACCOUNT)){
            String account = values.getAsString(SpendometerContract.ExpenseEntry.COL_ACCOUNT);
            if (account == null) {
                throw new IllegalArgumentException("Please choose an account");
            }
        }

        // Create/open the writeable database so it can be updated with the modified data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Return the number of the database rows affected by the update statement
        int rowsUpdated = database.update(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES, values, selection, selectionArgs);

        // Check to see whether rows were updated, then notify all listeners that the given Uri has changed
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

                // Delete the specified row(s) given by the ID in the Uri
                rowsDeleted = database.delete(SpendometerContract.CategoryEntry.TABLE_NAME_CATEGORIES, selection, selectionArgs);

                // Check if there have been any rows deleted from the table, and notify all listeners
                if (rowsDeleted != 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case EXPENSES:
                // Delete all the rows that match the selection and selectionArgs
                rowsDeleted = database.delete(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES, selection, selectionArgs);

                // Check if there have been any rows deleted from the table and notify all listeners
                if (rowsDeleted != 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case EXPENSES_ID:
                // Select which row(s) should be deleted
                selection = SpendometerContract.ExpenseEntry._ID + "=?";

                // Select which columns in the table should be deleted
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Delete the specified row(s) given by the ID in the Uri
                rowsDeleted = database.delete(SpendometerContract.ExpenseEntry.TABLE_NAME_EXPENSES, selection, selectionArgs);

                // Check if there have been any rows deleted from the table, and notify all listeners
                if(rowsDeleted != 0 && getContext() != null) {
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
                // Return the Uri type as a list of Categories
                return SpendometerContract.CategoryEntry.CONTENT_LIST_TYPE;

            case CATEGORIES_ID:
                // Return the Uri type as a single Category
                return SpendometerContract.CategoryEntry.CONTENT_ITEM_TYPE;

            case EXPENSES:
                // Return the Uri type as a list of Expenses
                return SpendometerContract.ExpenseEntry.CONTENT_LIST_TYPE;

            case EXPENSES_ID:
                // Return the Uri type as a single Expense
                return SpendometerContract.ExpenseEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
