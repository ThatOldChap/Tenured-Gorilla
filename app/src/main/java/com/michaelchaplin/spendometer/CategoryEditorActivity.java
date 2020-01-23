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
import android.widget.ImageView;
import android.widget.Toast;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.ArrayList;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class CategoryEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewItemTouchListener {

    // Global variables to store the data for a new Category
    private EditText mCategoryNameEditText;
    private ImageView mCategoryImageIcon;
    public RecyclerView mCategoryIconRecyclerView;
    ArrayList<CategoryIconDataModel> arrayList = new ArrayList<>();
    private int mIconIDClicked = 0;
    private String mSavedCategoryName;
    private int mSavedIconID;

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new category)
    public static final int EXISTING_CATEGORY_LOADER = 1;

    // Content URI for storing the existing category. It will be null if it is a new category
    private Uri mCurrentCategoryUri;

    // Boolean flags to keep track of if fields were already edited/touched
    public boolean mNameHasChanged = false;
    public boolean mIconHasChanged = false;
    public boolean mExistingCategory = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_editor);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentCategoryUri = intent.getData();
        Log.d(LOG_TAG, "onCreate: mCurrentCategoryUri = " + mCurrentCategoryUri);

        // Set the relevant title of the app bar based on a new or existing category
        if (mCurrentCategoryUri == null) {
            setTitle("New Category");
            mExistingCategory = false;

            // Gets rid of the options menu so the "Delete" option can't be seen if there aren't any categories
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Category");
            mExistingCategory = true;
        }

        // Find all relevant views for input fields to see if they have been modified
        mCategoryNameEditText = findViewById(R.id.category_name_edit_text);
        mCategoryIconRecyclerView = findViewById(R.id.category_icon_recycler_view);
        mCategoryImageIcon = findViewById(R.id.category_icon_image);

        // Construct the arrayList of CategoryIconDataModels to populate the list of available icons
        for (int i = 0; i < CategoryIconData.iconArray.length; i++){
            arrayList.add(new CategoryIconDataModel(
               CategoryIconData.nameArray[i],
               CategoryIconData.iconArray[i],
               CategoryIconData.id_[i]
            ));
        }

        // Setup onTouchListener for the input fields to see if they have been modified
        View.OnTouchListener mTouchListener = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(LOG_TAG, "onTouch: Category name EditText was touched and mCategoryHasChanged is " + mNameHasChanged);
                mNameHasChanged = true;
                return false;
            }
        };

        // Setting a Touch Listener on the CategoryNameEditText to use as a "changes made" flag
        mCategoryNameEditText.setOnTouchListener(mTouchListener);

        // Defining the adapter for the RecyclerView
        CategoryIconAdapter mAdapter = new CategoryIconAdapter(this, arrayList, this);
        mCategoryIconRecyclerView.setAdapter(mAdapter);
        mCategoryIconRecyclerView.setHasFixedSize(true);

        // Specify a Grid Layout Manager for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3,GridLayoutManager.VERTICAL,false);
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
        // If a new category is being saved, save the category name to nameString to avoid saving a null value
        if(!mExistingCategory){
            mSavedCategoryName = nameString;
        }

        // If it is a new category with no name or icon chosen yet, return without saving
        Log.d(LOG_TAG, "saveCategory: nameString = " + nameString + " and mSavedCategoryName = " + mSavedCategoryName + " and mIconClicked = " + mIconIDClicked);
        if(!mExistingCategory && TextUtils.isEmpty(nameString) && mIconIDClicked == 0){return;}

        // If it is an existing category and nothing has changed, return without saving
        Log.d(LOG_TAG, "saveCategory: mIconHasChanged = " + mIconHasChanged + " and mNameHasChanged = " + mNameHasChanged);
        if(mExistingCategory && !mIconHasChanged && !mNameHasChanged && mSavedCategoryName.equals(nameString)){return;}

        if(!mSavedCategoryName.equals(nameString) && !mIconHasChanged){
            mNameHasChanged = true;
            mIconIDClicked = mSavedIconID;
        }

        // Creates a new ContentValues object to store the new data
        ContentValues values = new ContentValues();
        values.put(SpendometerContract.CategoryEntry.COL_NAME, nameString);
        values.put(SpendometerContract.CategoryEntry.COL_ICON_ID, mIconIDClicked);

        // Determine if this is a new or existing category
        if (mCurrentCategoryUri == null) {

            // Insert a new category into the Provider and return the URI for the Category table
            Uri newUri = getContentResolver().insert(SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI, values);

            Log.d(LOG_TAG, "Uri for new category is: " + newUri);

            if(newUri == null) {
                Toast.makeText(this, "Insertion failed, Uri is null",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insertion successful, Uri is valid",Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_editor_page, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hides the delete menu option if this is a new category
        if (mCurrentCategoryUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nameString = mCategoryNameEditText.getText().toString().trim();

        // User has clicked on a menu option
        switch (item.getItemId()) {

            case R.id.action_save_item:

                if(!mExistingCategory && (TextUtils.isEmpty(nameString) || !mIconHasChanged)){

                    Log.d(LOG_TAG, "onOptionsItemSelected: mNameHasChanged = " + mNameHasChanged + " and nameString = " + nameString);
                    Toast.makeText(this, "Please fill out remaining fields", Toast.LENGTH_SHORT).show();
                } else {
                    saveCategory();
                    finish(); // Exits the activity to return to CategoryActivity
                }
                return true;

            case R.id.action_delete_item:
                // Pop up a confirmation dialog for the deletion
                showDeletionConfirmationDialog();
                return true;

            case android.R.id.home:

                // If the category hasn't changed, continue with navigating up to the CategoriesActivity
                Log.d(LOG_TAG, "onOptionsItemSelected: mNameHasChanged = " + mNameHasChanged + " and mIconHasChanged = " + mIconHasChanged + " and nameString = " + nameString);
                if(mExistingCategory || (TextUtils.isEmpty(nameString) && !mIconHasChanged)) {
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

        String nameString = mCategoryNameEditText.getText().toString().trim();

        Log.d(LOG_TAG, "Back Button Pressed and mNameHasChanged is: " + mNameHasChanged + " and mIconHasChanged is: " + mIconHasChanged + " and nameString = " + nameString);
        // If it is an existing category or the Category name is blank and no icon has been selected, proceed
        if (mExistingCategory || (TextUtils.isEmpty(nameString) && !mIconHasChanged)) {
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
                SpendometerContract.CategoryEntry.COL_NAME,
                SpendometerContract.CategoryEntry.COL_ICON_ID
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
            int iconColumnIndex = data.getColumnIndex(SpendometerContract.CategoryEntry.COL_ICON_ID);

            // Extract the value from the cursor for a given column index
            String categoryName = data.getString(nameColumnIndex);
            int categoryIconID = data.getInt(iconColumnIndex);

            // Update the views on the screen with the new data from the database
            mCategoryNameEditText.setText(categoryName);
            mCategoryImageIcon.setImageResource(categoryIconID);

            // Setting up a flag to see if text is already in the field for the saveCategory() method
            mSavedCategoryName = categoryName;
            mSavedIconID = categoryIconID;
            Log.d(LOG_TAG, "onLoadFinished: categoryName = " + mSavedCategoryName + " and iconID = " + mSavedIconID);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mCategoryNameEditText.setText("");
        Log.d(LOG_TAG, "onLoaderReset: Loader is invalid");
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

    @Override
    public void onItemClick(int position) {

        // Finds the values of the CategoryIconData item at the position of the clicked item
        String name = arrayList.get(position).getIconName();
        int id_ = arrayList.get(position).getId();
        mIconIDClicked = arrayList.get(position).getIconDrawable();

        // Sets the preview image beside the EditText
        mCategoryImageIcon.setImageResource(mIconIDClicked);
        mIconHasChanged = true;

        Log.d(LOG_TAG, "OnIconClick: clicked position " + position + " and mIconHasChanged is: " + mIconHasChanged);
        Log.d(LOG_TAG, "OnIconClick: Name: " + name + ", Icon ID: " + mIconIDClicked + ", id_: " + id_);
    }
}
