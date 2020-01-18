package com.michaelchaplin.spendometer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseListActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewItemTouchListener {

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new expense)
    public static final int EXPENSE_LOADER = 1;

    // Creating variables for all views within the activity
    public RecyclerView mExpenseListRecyclerView;

    // Creating variables for the RecyclerView adapter
    public ExpenseListAdapter mAdapter;

    // Content URI for storing an existing expense. Will be null if it is a new expense
    public Uri mCurrentExpenseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentExpenseUri = intent.getData();
        Log.d(LOG_TAG, "onCreate: mCurrentExpenseUri = " + mCurrentExpenseUri);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Finding the RecyclerView and assigning its layout manager
        mExpenseListRecyclerView = findViewById(R.id.expense_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mExpenseListRecyclerView.setLayoutManager(layoutManager);
        mExpenseListRecyclerView.setHasFixedSize(true); // Improves performance

        // Defining the RecyclerView adapter characteristics
        mAdapter = new ExpenseListAdapter(this, null, this);
        mExpenseListRecyclerView.setAdapter(mAdapter);

        // Prepare the cursor loader by either reconnecting with an existing one or starting a new one
        getSupportLoaderManager().initLoader(EXPENSE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        Log.d(LOG_TAG, "onCreateLoader: mCurrentExpenseUri is = " + mCurrentExpenseUri + " and id = " + id);

        if (id == EXPENSE_LOADER) {
            // Defines a projection that contains all the columns from the Expenses table
            String[] projection = {
                    SpendometerContract.ExpenseEntry._ID,
                    SpendometerContract.ExpenseEntry.COL_CATEGORY,
                    SpendometerContract.ExpenseEntry.COL_COST,
                    SpendometerContract.ExpenseEntry.COL_DATE,
                    SpendometerContract.ExpenseEntry.COL_NOTES,
                    SpendometerContract.ExpenseEntry.COL_ICON_ID,
                    SpendometerContract.ExpenseEntry.COL_ACCOUNT
            };

            // This loader executes the ContentProvider's query method at the passed in Uri
            return new CursorLoader(this, SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, projection, null, null, null);
        }
        return null; // If the loader is not the expected one
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.d(LOG_TAG, "onLoadFinished: Preparing to swap cursor");
        // Swaps in the new cursor that was generated by the onCreateLoader
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // called when the loader is reset
        Log.d(LOG_TAG, "onLoaderReset: Loader is being invalidated");

        // Clears the cursor when it is being reset
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_expenses.xml file
        // This also adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // USer clicked on a menu item in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllExpenses();
                return true;

            case android.R.id.home:
                // Navigate up to the parent activity
                NavUtils.navigateUpFromSameTask(ExpenseListActivity.this);
                return true;

            default:
                // Invoke the super class if no button is clicked
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertDummyData() {
        // Create a ContentValues object and store the dummy data
        ContentValues values = new ContentValues();
        values.put(SpendometerContract.ExpenseEntry.COL_CATEGORY, "Work Lunches");
        values.put(SpendometerContract.ExpenseEntry.COL_ICON_ID, R.drawable.round_fiber_new_white_48dp);
        values.put(SpendometerContract.ExpenseEntry.COL_ACCOUNT, "AMEX");
        values.put(SpendometerContract.ExpenseEntry.COL_NOTES, "Stan's Diner");
        values.put(SpendometerContract.ExpenseEntry.COL_DATE, 9870055);
        values.put(SpendometerContract.ExpenseEntry.COL_COST, 15.47);

        // Inserts a new row into the provider via the ContentResolver and returns the Uri of the Expenses table row
        // Also notifies the adapter that there has been a change
        Uri uri = getContentResolver().insert(SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, values);
        int position = (int) ContentUris.parseId(uri);
        Log.d(LOG_TAG, "insertDummyData: Data was inserted into the database at position: " + position + " at the uri: " + uri);
    }

    private void deleteAllExpenses() {
        // Uses the ContentResolver to delete all expenses and return how many rows were deleted
        int rowsDeleted = getContentResolver().delete(SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, null, null);
        Log.d(LOG_TAG, "deleteAllExpenses: " + rowsDeleted + " rows deleted from the Expenses database");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(int position) {

        Log.d(LOG_TAG, "onItemClick: adapter position is: " + position);
        // Navigates to the ExpenseEditorActivity to edit the Expense at the position selected
        Intent intent = new Intent(ExpenseListActivity.this, ExpenseEditorActivity.class);
        Uri currentExpenseUri = ContentUris.withAppendedId(SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, position);
        intent.setData(currentExpenseUri);
        startActivity(intent);
    }
}
