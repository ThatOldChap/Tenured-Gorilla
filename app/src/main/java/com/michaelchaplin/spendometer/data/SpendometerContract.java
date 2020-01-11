package com.michaelchaplin.spendometer.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SpendometerContract {

    // Prevent someone from initializing this class accidentally
    private SpendometerContract() {}

    // Content authority string for URIs
    public static final String CONTENT_AUTHORITY = "com.michaelchaplin.spendometer.data";

    // Base Content authority string for URIs
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // String for Upgrading the database from version 1 to 2
    public static final String DATABASE_ALTER_CATEGORIES_V1_TO_V2 = "ALTER TABLE " +  CategoryEntry.TABLE_NAME_CATEGORIES + " ADD COLUMN " + CategoryEntry.COL_ICON_ID + " string;";

    // ************************** Categories ******************************

    // Table name for the path of the Categories Uri
    public static final String PATH_CATEGORIES = "categories";

    // Inner class that defines the constant values for the categories database table
    public static final class CategoryEntry implements BaseColumns {

        // Complete string for the Categories Uri content
        public static final Uri CATEGORY_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);

        // The MIME type of the CATEGORY_CONTENT_URI for a list of Categories
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;

        // The MIME type of the CATEGORY_CONTENT_URI for a single Category
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;

        // Name of the database table for the categories
        public static final String TABLE_NAME_CATEGORIES = "categories";

        // Unique ID number for each category in the database table
        public static final String _ID = BaseColumns._ID;

        // Name of the category
        // TYPE = TEXT
        public static final String COL_NAME = "name";

        // Drawable ID of the icon for the category
        // TYPE = INT
        public static final String COL_ICON_ID = "icon_id";

    }

    // ************************** Expenses ******************************

    // Table name for the path of the Expenses Uri
    public static final String PATH_EXPENSES = "expenses";

    // Inner class that defines the constant values for the expenses database table
    public static final class ExpenseEntry implements BaseColumns {

        // String for the Expenses Uri content
        // ex. content://com.michaelchaplin.spendometer.data/expenses
        public static final Uri EXPENSE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSES);

        // The MIME type of the EXPENSE_CONTENT_URI for a list of expenses
        // ex. vnd.android.cursor.dir/com.michaelchaplin.spendometer.data/expenses
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        // The MIME type of the EXPENSE_CONTENT_URI for a single expense
        // ex. vnd.android.cursor.item/com.michaelchaplin.spendometer.data/expenses
        public static final  String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        // Name of the database table for the expenses
        public static final String TABLE_NAME_EXPENSES = "expenses";

        // Unique ID number for each expense in the database table
        public static final String _ID = BaseColumns._ID;

        // Name of the category the expense belongs to
        // Type: TEXT
        public static final String COL_CATEGORY = "category";

        // Cost of the expense
        // Type: REAL
        public static final String COL_COST = "cost";

        // Date the expense was paid (in UTC time)
        // Type: INT
        public static final String COL_DATE = "date";

        // Notes related to the expense
        // Type: TEXT
        public static final String COL_NOTES = "notes";

        // Drawable ID of the category icon for the selected expense category
        // Type: INT
        public static final String COL_ICON_ID = "icon_id";

        // Type of payment method used for the expense
        // Type: INT
        public static final  String COL_TYPE = "type";

            public static final int TYPE_CASH = 0;
            public static final int TYPE_CREDIT = 1;
            public static final int TYPE_DEBIT = 2;
            public static final int TYPE_OTHER = 3;
    }

}
