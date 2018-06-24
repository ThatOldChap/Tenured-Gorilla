package com.michaelchaplin.spendometer.data;

import android.content.UriMatcher;

// Defines the ContentProvider object that acts as the middle man for interacting with the database
public class SpendometerProvider {

    // Database helper object
    private SpendometerDbHelper mDbHelper;

    // Tag for any log messages
    public static final String LOG_TAB = SpendometerProvider.class.getSimpleName();

    // Creates a URI matcher object to match the Content URI to a corresponding code
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


}
