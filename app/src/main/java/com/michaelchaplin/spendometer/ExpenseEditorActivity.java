package com.michaelchaplin.spendometer;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecyclerViewItemTouchListener {

    // Content URI for storing the existing expense. It will be null if it is a new expense
    private Uri mCurrentExpenseUri;

    // Create global variables for all the views on in the activity
    private TextView mAccountEditText, mNotesEditText, mCostEditText;
    private TextView mDateTextView;
    private RecyclerView mIconRecyclerView;

    // Boolean flag to check if any input fields have been modified
    public boolean mExpenseHasChanged = false;

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new category)
    public static final int EXISTING_EXPENSE_LOADER = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_editor);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentExpenseUri = intent.getData();
        Log.d(LOG_TAG, "onCreate: mCurrentCategoryUri = " + mCurrentExpenseUri);

        // Set the relevant title of the app bar based on a new or existing category
        if (mCurrentExpenseUri == null) {
            setTitle("New Expense");

            // Gets rid of the options menu so the "Delete" option can't be seen if there aren't any categories
            //invalidateOptionsMenu();
        } else {
            setTitle("Edit Expense");
        }

        // Find all relevant views for input fields to see if they have been modified
        mAccountEditText = findViewById(R.id.expense_edit_account);
        mNotesEditText = findViewById(R.id.expense_edit_notes);
        mCostEditText = findViewById(R.id.expense_edit_cost);
        mDateTextView = findViewById(R.id.expense_edit_date);
        mIconRecyclerView = findViewById(R.id.expense_edit_category_recycler);

        // Setup an onTouchListener for the input fields to see if they have been modified
        View.OnTouchListener mTouchListener = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mExpenseHasChanged = true;
                return false;
            }
        };

        mAccountEditText.setOnTouchListener(mTouchListener);
        mNotesEditText.setOnTouchListener(mTouchListener);
        mCostEditText.setOnTouchListener(mTouchListener);
        mDateTextView.setOnTouchListener(mTouchListener);

        // Query the Categories Database to populate the RecyclerViewData of Category Icons
        Uri uri = Uri.parse(SpendometerContract.CONTENT_AUTHORITY + SpendometerContract.PATH_CATEGORIES);
        String[] projection = {
                SpendometerContract.CategoryEntry._ID,
                SpendometerContract.CategoryEntry.COL_NAME,
                SpendometerContract.CategoryEntry.COL_ICON_ID
        };
        Cursor categoriesCursor = getContentResolver().query(uri, projection, null, null, null);

        // Setting up the RecyclerView and its adapter
        CategoryIconAdapter mAdapter = new CategoryIconAdapter(this, null, this) {};


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(int position) {

    }
}
