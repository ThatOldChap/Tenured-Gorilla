package com.michaelchaplin.spendometer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

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

        // Find the TextView that corresponds to the Add New Category button
        TextView newCategoryButton = findViewById(R.id.add_new_category);

        // Setup an onClickListener to find when the button has been pressed
        newCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add a new category to this list when the button is pressed
                // insertCategory();

                Intent newCategoryIntent = new Intent(CategoriesActivity.this,CategoryEditorActivity.class);
                startActivity(newCategoryIntent);
            }
        });

        // Find the ListView that populates the Category data
        ListView categoryListView = findViewById(R.id.list_view_categories);

        // Find and set the empty view for the CategoriesActivity
        View emptyView = findViewById(R.id.empty_view_categories);
        categoryListView.setEmptyView(emptyView);

        Log.d(LOG_TAG, "Setting emptyView for the ListView");

        // Setup a CategoryCursorAdapter to create a list item for each category/row in the Cursor
        // FYI, there is no category data yet (until the load finished) so set the passed in Cursor to null
        mCursorAdapter = new CategoryCursorAdapter(this, null);
        categoryListView.setAdapter(mCursorAdapter);

        Log.d(LOG_TAG, "CursorAdapter set to ListView");

        // Setup an item click listener for each individual category in the ListView
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Create a new intent to go the CategoryEditorActivity
                Intent intent = new Intent(CategoriesActivity.this, CategoryEditorActivity.class);

                // Create a Uri that represents the category that was click on, identified by the "id"
                Uri currentCategoryUri = ContentUris.withAppendedId(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, id);

                // Pass the Uri to the CategoryEditorActivity as a part of the Intent
                intent.setData(currentCategoryUri);

                // Launch the CategoryEditorActivity to display the current data for the chosen Category
                startActivity(intent);
            }
        });

        // Prepare the loader by either reconnecting with an existing one or starting a new one
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {

        // Inflate the menu options from the res/menu/menu_categories.xml file
        // This also adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertCategory();
                Log.d(LOG_TAG, "ran insertCategory()");
                return true;

            // Respond to a click on the "Delete All Entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllCategories();
                Log.d(LOG_TAG, "ran deleteAllCategories()");
                return true;

            case android.R.id.home:

                // Navigate up to parent activity which is the Main Activity
                NavUtils.navigateUpFromSameTask(CategoriesActivity.this);
                return true;

            default:

                // Invoke the super class if no button is clicked
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to add a dummy category in the database
    private void insertCategory() {

        // Create a ContentValues object where the column names are the keys and the attributes are the values
        ContentValues values = new ContentValues();
        values.put(SpendometerContract.CategoryEntry.COL_NAME, "Big Rig Eatery");
        values.put(SpendometerContract.CategoryEntry.COL_ICON_ID, R.drawable.category_icon_1);

        // TODO: Add fake data with category names and icon in using CategoryIconData

        // Insert a new row into the Provider via the ContentResolver
        // Use the CATEGORY_CONTENT_URI to indicate that we want to insert a category into the table
        // This also receives the new content URI that will allow us to access the Categories table data in the future
        getContentResolver().insert(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, values);
    }

    // Helper method to delete all the categories in the database
    private void deleteAllCategories() {

        // Uses the ContentResolver to delete return how many rows were deleted in the database
        int rowsDeleted = getContentResolver().delete(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, null, null);
        Log.d(LOG_TAG, rowsDeleted + " rows deleted from the Categories database");
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
                SpendometerContract.CategoryEntry.COL_NAME,
                SpendometerContract.CategoryEntry.COL_ICON_ID
        };

        Log.d(LOG_TAG, "About to run cursor loader");

        // Returns the CursorLoader that will execute the ContentProvider's query method on a background thread
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
        Log.d(LOG_TAG, "Finished swapping cursor in onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
