package com.michaelchaplin.spendometer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecyclerViewItemTouchListener {

    // Content URI for storing the existing expense. It will be null if it is a new expense
    private Uri mCurrentExpenseUri;

    // Create global variables for all the views on in the activity
    private EditText mAccountEditText, mNotesEditText, mCostEditText;
    private TextView mDateTextView;
    private RecyclerView mIconRecyclerView;
    CategoryIconListAdapter mAdapter;
    Calendar mCalendar = Calendar.getInstance(TimeZone.getDefault());

    // Selected category and icon data for the new expense
    int selectedIcon;
    String selectedCategory;

    // Boolean flag to check if any input fields have been modified
    public boolean mExpenseHasChanged = false;
    public boolean mIconHasChanged = false;
    public boolean mExistingExpense;

    // Identifier used to initialize the Loader if the content URI is not null (ie. is a new expense)
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

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Set the relevant title of the app bar based on a new or existing expense
        if (mCurrentExpenseUri == null) {
            setTitle("New Expense");
            mExistingExpense = false;

            // Gets rid of the options menu so the "Delete" option can't be seen if there aren't any categories
            invalidateOptionsMenu();

            // Sets the current date
            String simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(mCalendar.getTime());
            mDateTextView.setText(simpleDateFormat);

        } else {
            setTitle("Edit Expense");
            mExistingExpense = true;
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

        // Sets up the DatePickerDialog for choosing the date
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calls the method to open the DatePickerDialog
                pickDate();
            }
        });

        // Query the Categories Database to populate the RecyclerViewData of Category Icons
        Uri uri = SpendometerContract.CategoryEntry.CATEGORY_CONTENT_URI;
        String[] projection = {
                SpendometerContract.CategoryEntry._ID,
                SpendometerContract.CategoryEntry.COL_NAME,
                SpendometerContract.CategoryEntry.COL_ICON_ID
        };
        Cursor categoriesCursor = getContentResolver().query(uri, projection, null, null, "name COLLATE NOCASE ASC");

        // Setting up the RecyclerView and its adapter
        mAdapter = new CategoryIconListAdapter(this, categoriesCursor, this);
        mIconRecyclerView.setAdapter(mAdapter);
        mIconRecyclerView.setHasFixedSize(true);

        // Specifying a layout manager for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mIconRecyclerView.setLayoutManager(layoutManager);

        // Prepare the loader by either reusing an existing loader or creating a new one
        if(mCurrentExpenseUri != null) {
            getSupportLoaderManager().initLoader(EXISTING_EXPENSE_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        // Defines a projection that contains all the columns from the Expenses table
        String[] projection = {
                SpendometerContract.ExpenseEntry._ID,
                SpendometerContract.ExpenseEntry.COL_COST,
                SpendometerContract.ExpenseEntry.COL_ACCOUNT,
                SpendometerContract.ExpenseEntry.COL_NOTES,
                SpendometerContract.ExpenseEntry.COL_ICON_ID,
                SpendometerContract.ExpenseEntry.COL_DATE,
                SpendometerContract.ExpenseEntry.COL_CATEGORY
        };

        // This loader executes the ContentProvider's query method
        Log.d(LOG_TAG, "onCreateLoader: Loader is being created");
        return new CursorLoader(this, mCurrentExpenseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Exits this method if the cursor is null or if there is less than 1 row in the cursor
        if(cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if(cursor.moveToFirst()){

            Log.d(LOG_TAG, "onLoadFinished: Extracting data from cursor");
            // Find the names of the columns of the attributes that may be changing
            int costColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_COST);
            int accountColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ACCOUNT);
            int notesColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_NOTES);
            int dateColumnIndex = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_DATE);

            // Extract the value from the cursor at the given column index
            double cost = cursor.getDouble(costColumnIndex);
            String account = cursor.getString(accountColumnIndex);
            String notes = cursor.getString(notesColumnIndex);
            int date = cursor.getInt(dateColumnIndex);

            // Update the views on screen with the new data from the database
            mCostEditText.setText(String.valueOf(cost));
            mAccountEditText.setText(account);
            mNotesEditText.setText(notes);

            mCalendar.setTimeInMillis((long) date);
            mDateTextView.setText(new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault()).format(mCalendar.getTime()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.d(LOG_TAG, "onLoaderReset: Loader is invalidated");
        // If the loader is invalidated, clear out the data from the input fields
        mCostEditText.setText("");
        mAccountEditText.setText("");
        mNotesEditText.setText("");
        mDateTextView.setText("");
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
        // Hides the delete menu option if this is a new expense
        if (mCurrentExpenseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String costString = mCostEditText.getText().toString().trim();

        // Switch depending on which option item was selected
        switch (item.getItemId()) {

            case R.id.action_save_item:

                if (TextUtils.isEmpty(costString)) {
                    Log.d(LOG_TAG, "onOptionsItemSelected: Save selected");
                } else {
                    saveExpense();
                    finish(); // Exits the activity to return to the ExpenseActivity
                }
                return true;

            case R.id.action_delete_item:

                // Pop up a confirmation dialog for the deletion
                showDeletionConfirmationDialog();
                return true;

            case android.R.id.home:

                // If the expense hasn't changed, continue navigating up from the activity
                if(TextUtils.isEmpty(costString)) {
                    NavUtils.navigateUpFromSameTask(ExpenseEditorActivity.this);
                    return true;
                }
                // If there are unsaved changes, setup a dialog to warn the user
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(ExpenseEditorActivity.this);
                    }
                };

                // Show the dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        // Returns the selected item
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        String costString = mCostEditText.getText().toString().trim();

        // If it is an existing expense or <?>
        if (mExpenseHasChanged || TextUtils.isEmpty(costString)) {
            super.onBackPressed();
            return;
        }

        // Setup a dialog to warn the user if there are unsaved changes
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User selected the discard option so close the current activity
                finish();
            }
        };

        // Setup a dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void pickDate() {

        // Gets the current date
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Setup the datePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                    // Creates a format for the date that is being displayed
                    String selectedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calendar.getTime());
                    mDateTextView.setText(selectedDate);
                }
            },mYear, mMonth + 1, mDayOfMonth);

        // Show the datePickerDialog
        datePickerDialog.show();
    }



    private void showDeletionConfirmationDialog() {

        // Creates a builder to set the parameters of a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this expense?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteExpense();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Creates and shows the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // Creates a builder to set the parameters of a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep Editing" button so dismiss the dialog and continue editing
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        // Creates and shows the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveExpense() {

        double cost = Double.valueOf(mCostEditText.getText().toString().trim());
        String account = mAccountEditText.getText().toString().trim();
        String notes = mNotesEditText.getText().toString().trim();
        int iconID = selectedIcon;
        String category = selectedCategory;
        int date = Integer.valueOf(mDateTextView.getText().toString());

        // If it is an expense with no info entered yet, return without saving
        if(!mExpenseHasChanged && TextUtils.isEmpty(account) && TextUtils.isEmpty(notes)) {
            return;
        }

        // Creates a new ContentValues object to store the new data
        ContentValues values = new ContentValues();
        values.put(SpendometerContract.ExpenseEntry.COL_COST, cost);
        values.put(SpendometerContract.ExpenseEntry.COL_NOTES, notes);
        values.put(SpendometerContract.ExpenseEntry.COL_ACCOUNT, account);
        values.put(SpendometerContract.ExpenseEntry.COL_ICON_ID, iconID);
        values.put(SpendometerContract.ExpenseEntry.COL_CATEGORY, category);
        values.put(SpendometerContract.ExpenseEntry.COL_DATE, date);

        // Determine if this is a new or existing expense
        if (!mExistingExpense) {

            // Insert a new expense into the ContentResolver and return a Uri for the Expenses table
            Uri newUri = getContentResolver().insert(SpendometerContract.ExpenseEntry.EXPENSE_CONTENT_URI, values);
            Log.d(LOG_TAG, "saveExpense: Uri for the new expense is " + newUri);
        } else {

            // Update the existing expense and pass it into the ContentResolver
            int rowsAffected = getContentResolver().update(mCurrentExpenseUri, values, null, null);
            Log.d(LOG_TAG, "saveExpense: Updated expense at " + mCurrentExpenseUri + " with " + rowsAffected + " rowsAffected");
        }

        mAdapter.getCursor().close();
    }

    private void deleteExpense(){

        // Only performs this operation if it is an existing category
        if(mCurrentExpenseUri != null) {

            // Calls the ContentResolver to delete the Expense at the given Uri
            int rowsDeleted = getContentResolver().delete(mCurrentExpenseUri, null, null);

            if(rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting Expense", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); // Close the activity
    }

    @Override
    public void onItemClick(int position) {

        // Obtain the cursor present in the CategoryIconListAdapter and move to the position that was clicked
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        // Get the column IDs of the cursor at the selected position
        int categoryColumnIndex = cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_NAME);
        int iconIDColumnIndex = cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_ICON_ID);

        // Extract the data from the cursor to obtain the info from the selected category
        selectedCategory = cursor.getString(categoryColumnIndex);
        selectedIcon = cursor.getInt(iconIDColumnIndex);
        Log.d(LOG_TAG, "onItemClick: selectedCategory = " + selectedCategory + " and selectedIconID = " + selectedIcon);

        mIconHasChanged = true;
    }
}
