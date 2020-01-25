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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class CategoriesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewItemTouchListener {

    // Identifier for the Category data loader
    public static final int CATEGORY_LOADER = 1;

    // Creating variables for all views within the activity
    public RecyclerView mCategoryRecyclerView;

    public CategoryListAdapter mAdapter;
    public Uri mCurrentCategoryUri;

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

                Intent newCategoryIntent = new Intent(CategoriesActivity.this,CategoryEditorActivity.class);
                startActivity(newCategoryIntent);
            }
        });

        // Finding the RecyclerView and assigning its layout manager
        mCategoryRecyclerView = findViewById(R.id.category_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mCategoryRecyclerView.setLayoutManager(layoutManager);
        mCategoryRecyclerView.setHasFixedSize(true); // Improves performance

        // Defining the RecyclerView adapter characteristics
        mAdapter = new CategoryListAdapter(this, null, this);
        mCategoryRecyclerView.setAdapter(mAdapter);

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
                insertDummyData();
                return true;

            // Respond to a click on the "Delete All Entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllCategories();
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
    private void insertDummyData() {

        // Create a ContentValues object where the column names are the keys and the attributes are the values
        ContentValues values = new ContentValues();

        // Read and insert the data from CategoryIconData and put into a ContentValues
        for (int i = 0; i < CategoryIconData.iconArray.length; i++){
            values.put(SpendometerContract.CategoryEntry.COL_NAME, CategoryIconData.nameArray[i]);
            values.put(SpendometerContract.CategoryEntry.COL_ICON_ID, CategoryIconData.iconArray[i]);

            // Inserts a new row into the provider via the ContentResolver, the Uri of the Category table
            getContentResolver().insert(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, values);
        }
    }

    // Helper method to delete all the categories in the database
    private void deleteAllCategories() {

        // Uses the ContentResolver to delete all categories and return how many rows were deleted in the database
        int rowsDeleted = getContentResolver().delete(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, null, null);
        Log.d(LOG_TAG, rowsDeleted + " rows deleted from the Categories database");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Creates and returns a CursorLoader that will take care of creating a Cursor for the data being displayed
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Defines a projection that specifies the columns from the table that should be loaded
        String[] projection = {
                SpendometerContract.CategoryEntry._ID,
                SpendometerContract.CategoryEntry.COL_NAME,
                SpendometerContract.CategoryEntry.COL_ICON_ID
        };

        Log.d(LOG_TAG, "onCreateLoader: mCurrentCategoryUri is = " + mCurrentCategoryUri + " and id = " + id);
        // Returns the CursorLoader that will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, projection,null,null,"name COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.d(LOG_TAG, "onLoadFinished: Preparing to swap cursor");
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.d(LOG_TAG, "onLoaderReset: Loader is being reset");
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(int position) {

        long id = mAdapter.getItemId(position);
        Log.d(LOG_TAG, "onItemClick: adapter position is " + position);
        Log.d(LOG_TAG, "onItemClick: item id (ie. Uri /# number) is = " + id);

        // Navigates to the CategoryEditorActivity to edit the Category at the position selected
        Intent intent = new Intent(CategoriesActivity.this, CategoryEditorActivity.class);
        Uri currentCategoryUri = ContentUris.withAppendedId(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, id);
        intent.setData(currentCategoryUri);
        startActivity(intent);
    }
}
