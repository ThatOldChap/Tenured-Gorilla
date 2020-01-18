package com.michaelchaplin.spendometer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class ExpenseEditorActivity extends AppCompatActivity {

    // Content URI for storing the existing expense. It will be null if it is a new expense
    private Uri mCurrentExpenseUri;


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


    }
}
