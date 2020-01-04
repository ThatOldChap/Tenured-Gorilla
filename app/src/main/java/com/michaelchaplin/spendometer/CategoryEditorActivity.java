package com.michaelchaplin.spendometer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class CategoryEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Global variables to store the data for a new Category
    private EditText mCategoryNameEditText;
    public RecyclerView mCategoryIconRecyclerView;
    public ArrayList<CategoryIconData> arrayList;

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new category)
    public static final int EXISTING_CATEGORY_LOADER = 1;

    // Content URI for storing the existing category. It will be null if it is a new category
    private Uri mCurrentCategoryUri;

    // Boolean flag that keeps track of if a category has already been edited
    private boolean mCategoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCategoryHasChanged = true;
            return false;
        }
    };

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
        //mCategoryNameEditText.setOnTouchListener(mTouchListener);

        CategoryIconAdapter mAdapter = new CategoryIconAdapter(this, arrayList);
        mCategoryIconRecyclerView.setAdapter(mAdapter);

        // Fixes the size of the RecyclerView to improve performance
        mCategoryIconRecyclerView.setHasFixedSize(true);

        // Specify a Grid Layout Manager for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mCategoryIconRecyclerView.setLayoutManager(layoutManager);

        // Prepare the loader be either reusing an existing loader or creating a new one
        if(mCurrentCategoryUri != null){
            getSupportLoaderManager().initLoader(EXISTING_CATEGORY_LOADER, null, null);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mCategoryNameEditText.setText("");
    }
}
