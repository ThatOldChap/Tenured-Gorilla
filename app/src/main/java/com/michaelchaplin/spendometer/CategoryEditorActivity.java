package com.michaelchaplin.spendometer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.ArrayList;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class CategoryEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Global variables to store the data for a new Category
    private EditText mCategoryNameEditText;
    public RecyclerView mCategoryIconRecyclerView;
    ArrayList<CategoryIconData> arrayList = new ArrayList<>();

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new category)
    public static final int EXISTING_CATEGORY_LOADER = 1;

    // Content URI for storing the existing category. It will be null if it is a new category
    private Uri mCurrentCategoryUri;

    // Boolean flag that keeps track of if a category has already been edited
    public boolean mCategoryHasChanged = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_editor);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentCategoryUri = intent.getData();

        // Set the relevant title of the app bar based on a new or existing category
        if (mCurrentCategoryUri == null) {
            setTitle("New Category");

            // Gets rid of the options menu so the "Delete" option can't be seen if there aren't any categories
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Category");
        }

        // Find all relevant views for input fields to see if they have been modified
        mCategoryNameEditText = findViewById(R.id.category_name_edit_text);
        mCategoryIconRecyclerView = findViewById(R.id.category_icon_recycler_view);
        arrayList.add(new CategoryIconData("Icon 1", R.drawable.category_icon_1));
        arrayList.add(new CategoryIconData("Icon 2", R.drawable.ic_launcher_background));
        arrayList.add(new CategoryIconData("Icon 3", R.drawable.ic_launcher_foreground));
        arrayList.add(new CategoryIconData("Icon 4", R.drawable.category_icon_1));
        arrayList.add(new CategoryIconData("Icon 5", R.drawable.ic_launcher_background));
        arrayList.add(new CategoryIconData("Icon 6", R.drawable.ic_launcher_foreground));

        // Setup onTouchListener for the input fields to see if they have been modified
        View.OnTouchListener mTouchListener = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mCategoryHasChanged = true;
                return false;
            }
        };

        mCategoryNameEditText.setOnTouchListener(mTouchListener);

        CategoryIconAdapter mAdapter = new CategoryIconAdapter(this, arrayList);
        mCategoryIconRecyclerView.setAdapter(mAdapter);

        // Fixes the size of the RecyclerView to improve performance
        // mCategoryIconRecyclerView.setHasFixedSize(true);

        // Specify a Grid Layout Manager for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2,GridLayoutManager.VERTICAL,false);
        mCategoryIconRecyclerView.setLayoutManager(layoutManager);

        // Prepare the loader be either reusing an existing loader or creating a new one
        if(mCurrentCategoryUri != null){
            getSupportLoaderManager().initLoader(EXISTING_CATEGORY_LOADER, null, this);
        }
    }

    // Get user input from the category editor and save the category into the database
    private void saveCategory() {

        // Read from the user edit field and trim the whitespace on the end
        String nameString = mCategoryNameEditText.getText().toString().trim();

        // Check if the mCategoryNameEditText field is blank and return to the CategoryActivity with no changes
        if(mCurrentCategoryUri == null && TextUtils.isEmpty(nameString)){return;}

        // Creates a new ContentValues object to store the new column name
        ContentValues values = new ContentValues();
        values.put(SpendometerContract.CategoryEntry.COL_NAME, nameString);

        // Determine if this is a new or existing category
        if (mCurrentCategoryUri == null) {

            // Insert a new category into the Provider and return the URI for the Category table
            Uri newUri = getContentResolver().insert(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, values);

            Log.d(LOG_TAG, "Uri for new category is: " + newUri);

            if(newUri == null) {
                Toast.makeText(this, "Insertion failed",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insertion successful",Toast.LENGTH_SHORT).show();
            }
        } else {

            // This case means there is an existing category, so update the category with the content Uri mCurrentCategoryUri
            // and pass into the new ContentValues
            int rowsAffected = getContentResolver().update(mCurrentCategoryUri, values, null, null);

            if(rowsAffected == 0) {
                Toast.makeText(this, "Insertion failed",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insertion successful",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_category_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hides the delete menu option if this is a new category
        if (mCurrentCategoryUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete_category);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User has clicked on a menu option
        switch (item.getItemId()) {

            case R.id.action_save_category:

                String nameString = mCategoryNameEditText.getText().toString().trim();

                if(TextUtils.isEmpty(nameString)){
                    Toast.makeText(this, "Please fill in a name for the Category", Toast.LENGTH_SHORT).show();
                } else {
                    saveCategory();
                    finish(); // Exits the activity to return to CategoryActivity
                }
                return true;

            case R.id.action_delete_category:
                // Pop up a confirmation dialog for the deletion
                showDeletionConfirmationDialog();
                return true;

            case android.R.id.home:
                // If the category hasn't changed, continue with navigating up to the CategoriesActivity
                Log.d(LOG_TAG, "Home Button Pressed and change flag is: " + mCategoryHasChanged);

                if(!mCategoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(CategoryEditorActivity.this);
                    return true;
                }
                // If there are unsaved changes, setup a dialog to warn the user
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(CategoryEditorActivity.this);
                    }
                };

                // Show the dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        // Returns the selected item
        return super.onOptionsItemSelected(item);
    }

    // This method is called when the back button is pressed
    @Override
    public void onBackPressed() {

        Log.d(LOG_TAG, "Back Button Pressed and change flag is: " + mCategoryHasChanged);
        // If the category hasn't changed, continue with handling back button press
        if (!mCategoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Setup a dialog to warn the user if there are unsaved changes
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Discard" button, close the current activity
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Creates and returns a CursorLoader that will take care of creating a Cursor for the data being displayed
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Defines a projection that contains all the columns from the Categories table
        String[] projection = {
                SpendometerContract.CategoryEntry._ID,
                SpendometerContract.CategoryEntry.COL_NAME
        };

        // This loader executes the ContentProvider's query method
        return new CursorLoader(this, mCurrentCategoryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Exits this method if the cursor is null or if there is less than 1 row in the cursor
        if(data == null || data.getCount() < 1){
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if(data.moveToFirst()){

            // Find the names of the columns of the attributes that may be changing
            int nameColumnIndex = data.getColumnIndex(SpendometerContract.CategoryEntry.COL_NAME);

            // Extract the value from the cursor for a given column index
            String categoryName = data.getString(nameColumnIndex);

            // Update the views on the screen with the new data from the database
            mCategoryNameEditText.setText(categoryName);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mCategoryNameEditText.setText("");
    }

    // Prompt for the user to confirm that they want to delete the category
    private void showDeletionConfirmationDialog() {

        // Creates a builder to set the parameters of a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this category?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCategory();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Creates and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Prompt for user to confirm that they want to proceed with unsaved changes
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){

        // Creates a builder to set the parameters of a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep Editing" button so dismiss the dialog and continue editing
                if(dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Creates and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // Performs the deletion of a category in the database
    private void deleteCategory(){

        // Only performs this operation of it is an existing category
        if (mCurrentCategoryUri != null){
            // Calls the ContentResolver to delete the Category at the given Uri
            int rowsDeleted = getContentResolver().delete(mCurrentCategoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting Category", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); // Close the activity
    }


}
