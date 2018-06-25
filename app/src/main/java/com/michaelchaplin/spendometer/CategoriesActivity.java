package com.michaelchaplin.spendometer;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

public class CategoriesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the Category data loader
    public static final int CATEGORY_LOADER = 1;

    // Internal cursor adapter to be used with the Cursor Loader
    CategoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Find the ListView that populates the Category data
        ListView categoryListView = findViewById(R.id.list_view_categories);

        // Find and set the empty view for the CategoriesActivity
        View emptyView = findViewById(R.id.empty_view_categories);
        categoryListView.setEmptyView(emptyView);

        // Setup a CategoryCursorAdapter to create a list item for each category/row in the Cursor
        // FYI, there is no category data yet (until the load finished) so set teh passed in Cursor to null
        mCursorAdapter = new CategoryCursorAdapter(this, null);
        categoryListView.setAdapter(mCursorAdapter);

        // Prepare the loader by either reconnecting with an existing one or starting a new one
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Creates and returns a CursorLoader that will take care of creating a Cursor for the data being displayed
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {

        // Defines a projection that specifies the columns from the table that should be loaded
        String[] projection = {
                SpendometerContract.CategoryEntry._ID,
                SpendometerContract.CategoryEntry.COL_NAME
        };

        // Returns teh CursorLoader that will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,
                SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update the CategoryCursorAdapter with this new cursor containing the updated Category data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
