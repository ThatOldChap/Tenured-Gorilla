package com.michaelchaplin.spendometer;

import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import java.util.ArrayList;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExpenseListAdapter.ExpenseTouchListener {

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new expense)
    public static final int EXPENSE_LOADER = 1;

    // Content URI for storing an existing expense. Will be null if it is a new expense
    private Uri mCurrentExpenseUri;

    // Creating variables for all views within the activity
    private RecyclerView mExpenseListRecyclerView;
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

        // Find all relevant views
        mExpenseListRecyclerView = findViewById(R.id.expense_list_recycler);

        // Defining the RecyclerView adapter characteristics
        ExpenseListAdapter mAdapter = new ExpenseListAdapter(this, expenseDataList, this);
        mExpenseListRecyclerView.setAdapter(mAdapter);

        // Prepare the cursor loader by either reconnecting with an existing one or starting a new one
        getSupportLoaderManager().initLoader(EXPENSE_LOADER, null, this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onExpenseClick(int position) {

    }
}
