package com.michaelchaplin.spendometer.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SpendometerContract {

    // Prevent someone from initializing this class accidentally
    private SpendometerContract() {};

    // Content authority string for URIs
    public static final String CONTENT_AUTHORITY = "com.michaelchaplin.spendometer.data";

    // Base Content authority string for URIs
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // ************************** Categories ******************************

    // Table name for the path of the Categories Uri
    public static final String PATH_CATEGORIES = "categories";

    // Inner class that defines the constant values for the categories database table.
    // Each entry represents a single category
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

    }

}
