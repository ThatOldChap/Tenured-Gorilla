package com.michaelchaplin.spendometer.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class SpendometerContract {

    // Prevent someone from initializing this class accidentally
    private SpendometerContract() {};

    // Content authority string for URIs
    private static final String CONTENT_AUTHORITY = "com.michaelchaplin.spendometer.data";

    // Base Content authority string for URIs
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // **********Defines the table that will store the names of categories in the database************

    // Table name for the path of the Categories Uri
    private static final String PATH_CATEGORIES = "categories";

    // Inner class that defines the constant values for the categories database table.
    // Each entry represents a single category
    public static final class CategoryEntry implements BaseColumns {

        // Complete string for the Categories Uri content
    public static final Uri CATEGORY_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);


    }

}
