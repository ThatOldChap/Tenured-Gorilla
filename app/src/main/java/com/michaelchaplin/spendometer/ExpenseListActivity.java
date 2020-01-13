package com.michaelchaplin.spendometer;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.ArrayList;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExpenseListAdapter.ExpenseTouchListener {

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new expense)
    public static final int EXPENSE_LOADER = 1;

    // Content URI for storing an existing expense. Will be null if it is a new expense
    private Uri mCurrentExpenseUri;

    // Creating variables for all views within the activity
    public TextView mCategoryName;
    public TextView mNotes;
    public TextView mAccount;
    public TextView mDate;
    public TextView mCost;
    public ImageView mIcon;
    public RecyclerView mExpenseListRecyclerView;

    ArrayList<ExpenseDataModel> expenseDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentExpenseUri = intent.getData();

        // Set the relevant title of the app bar based on a new or existing expense
        if(mCurrentExpenseUri == null){
            setTitle("New Expense");
            // TODO: add an invalidateOptionsMenu() method call
        } else {
            setTitle("Edit Expense");
        }

        // Get a support ActionBar corresponding to this toolbar
                ActionBar ab = getSupportActionBar();
                // Enable the Up button
                if(ab != null){
                    ab.setDisplayHomeAsUpEnabled(true);
                    ab.setHomeButtonEnabled(true);
                }

        // Construct an expenseDataList of ExpenseDataModels to populate the RecyclerView
        /*for (int i = 0; i < expenseDataList.size(); i++){
            expenseDataList.add(new ExpenseDataModel(
                ExpenseData.categoryArray[i],
                ExpenseData.costArray[i],
                ExpenseData.dateArray[i],
                ExpenseData.notesArray[i],
                ExpenseData.iconArray[i],
                ExpenseData.accountArray[i]
            ));
        }*/

        // Find all relevant views
        mExpenseListRecyclerView = findViewById(R.id.expense_list_recycler);

        // Defining the RecyclerView adapter characteristics
        ExpenseListAdapter mAdapter = new ExpenseListAdapter(this, this);
        mExpenseListRecyclerView.setAdapter(mAdapter);

        // Prepare the cursor loader by either reconnecting with an existing one or starting a new one
        getSupportLoaderManager().initLoader(EXPENSE_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

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

        // This loader executes the ContentProvider's query method
        return new CursorLoader(this, mCurrentExpenseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Exits this method if the cursor is null or if there is less than 1 row in the cursor
        if(cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if(cursor.moveToFirst()) {

            // Find the names of the columns of the attributes that may be changing
            int categoryColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_CATEGORY);
            int costColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_COST);
            int dateColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_DATE);
            int notesColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_NOTES);
            int iconColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ICON_ID);
            int accountColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ACCOUNT);

            // Extract the values from the cursor for a given column index
            String category = cursor.getString(categoryColumnIndex);
            int cost = cursor.getInt(costColumnIndex);
            String date = Double.toString(cursor.getDouble(dateColumnIndex));
            String notes = cursor.getString(notesColumnIndex);
            int icon = cursor.getInt(iconColumnIndex);
            String account = cursor.getString(accountColumnIndex);

            // Update the views with the new data from the database
            mCategoryName.setText(category);
            mCost.setText(cost);
            mDate.setText(date);
            mNotes.setText(notes);
            mIcon.setImageResource(icon);
            mAccount.setText(account);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // called when the loader is reset
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
        values.put(SpendometerContract.ExpenseEntry.COL_DATE, "Jan 12, 2020");
        values.put(SpendometerContract.ExpenseEntry.COL_COST, 15.47);

        // Inserts a new row into the provider via the ContentResolver, the Uri of the Expenses table
        getContentResolver().insert(SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, values);
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
    public void onExpenseClick(int position) {

    }
}
