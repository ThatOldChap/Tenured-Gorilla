package com.michaelchaplin.spendometer;

import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import com.michaelchaplin.spendometer.ExpandableRecyclerView.Expense;
import com.michaelchaplin.spendometer.ExpandableRecyclerView.ExpenseDay;
import com.michaelchaplin.spendometer.ExpandableRecyclerView.ExpenseDayAdapter;
import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.ArrayList;
import java.util.List;

public class RecentSpendingActivity extends AppCompatActivity {

    public RecyclerView mExpenseListRecyclerView;
    public ExpenseDayAdapter mExpenseDayAdapter;
    List<Expense> expenseData1 = new ArrayList<>();
    List<Expense> expenseData2 = new ArrayList<>();
    List<Expense> expenseData3 = new ArrayList<>();
    ExpenseDay expenseDay1, expenseDay2, expenseDay3;
    List<ExpenseDay> mExpenseDayData = new ArrayList<>();
    private String TAG = RecentSpendingActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Setting up the dummy data to populate the RecyclerView
        expenseData1.add(new Expense("Food", "Stan's Diner", "AMEX", R.drawable.round_fastfood_white_48, 1581310800005L, 15.48));
        expenseData1.add(new Expense("Groceries", "Independent", "PCMC", R.drawable.round_restaurant_white_48, 1581310805000L, 72.38));
        expenseData1.add(new Expense("Gas", "Ultramar", "PCMC", R.drawable.round_local_gas_station_white_48, 1581310800050L, 125.00));
        
        expenseData2.add(new Expense("Food", "Pho May", "PCMC", R.drawable.round_fastfood_white_48, 1581397200005L, 16.20));
        expenseData2.add(new Expense("Alcohol", "Overflow Brewery", "AMEX", R.drawable.round_local_bar_white_48, 1581397205000L, 10.00));
                       
        expenseData3.add(new Expense("Movies", "Harley Quinn", "AMEX", R.drawable.round_local_movies_white_48, 1581483605000L, 26.87));
        expenseData3.add(new Expense("Work Dues", "PEO Renewal", "AMEX", R.drawable.round_work_white_48, 1581483600050L, 156.65));
        expenseData3.add(new Expense("Sports", "Hockey Intramural", "Cash", R.drawable.round_sports_tennis_white_48, 1581483650000L, 20.00));
                         
        expenseDay1 = new ExpenseDay(expenseData1);
        expenseDay2 = new ExpenseDay(expenseData2);
        expenseDay3 = new ExpenseDay(expenseData3);

        mExpenseDayData.add(expenseDay1);
        mExpenseDayData.add(expenseDay2);
        mExpenseDayData.add(expenseDay3);

        // 1581310800000L is 2020/02/10, 1581397200000L is 2020/02/11 and 1581483600000 is 2020/02/12
        
        // Find the RecyclerView and assign its layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mExpenseListRecyclerView = findViewById(R.id.expense_list_recycler);
        mExpenseListRecyclerView.setLayoutManager(layoutManager);
        mExpenseListRecyclerView.setHasFixedSize(true); // Improves the performance
        
        // Defines the adapter and its characteristics
        mExpenseDayAdapter = new ExpenseDayAdapter(this, mExpenseDayData);
        mExpenseListRecyclerView.setAdapter(mExpenseDayAdapter);
        
    }


    public List<ExpenseDay> CursorToExpenseConverter(Cursor cursor) {

        List<Expense> expenseList = new ArrayList<>();
        List<ExpenseDay> parentList = new ArrayList<>();
        boolean mDataValid;
        int COL_CATEGORY, COL_ACCOUNT, COL_COST, COL_NOTES, COL_ICON_ID, COL_DATE;
        int prevDayOfMonth = 1;

        // Determines whether the cursor is null or has data in it
        mDataValid = cursor != null && cursor.moveToFirst();

        if(mDataValid) {

            COL_CATEGORY = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_CATEGORY);
            COL_ACCOUNT = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ACCOUNT);
            COL_COST = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_COST);
            COL_NOTES = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ICON_ID);
            COL_DATE = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_DATE);
            COL_ICON_ID = cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ICON_ID);

            // Creates the parentList
            for (int i = 0; i < cursor.getCount(); i++) {

                // Create an expense out of each cursor row
                Expense mExpense = new Expense(
                        cursor.getString(COL_CATEGORY),
                        cursor.getString(COL_NOTES),
                        cursor.getString(COL_ACCOUNT),
                        cursor.getInt(COL_ICON_ID),
                        cursor.getLong(COL_DATE),
                        cursor.getDouble(COL_COST)
                );

                if(mExpense.getDayOfMonth() == prevDayOfMonth){
                    expenseList.add(mExpense);
                    prevDayOfMonth = mExpense.getDayOfMonth();
                } else {
                    parentList.add(new ExpenseDay(expenseList));
                }
                Log.d(TAG, "CursorToExpenseConverter: New expense processed, i = " + i);
            }
        }
        return parentList;
    }

}
