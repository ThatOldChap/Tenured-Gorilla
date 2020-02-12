package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelchaplin.spendometer.R;
import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDayAdapter extends ExpandableRecyclerAdapter<ExpenseDay, Expense, ExpenseDayViewHolder, ExpenseChildViewHolder> {

    private String TAG = ExpenseDayAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    public ExpenseDayAdapter(Context context, List<ExpenseDay> parentList) {
        super(parentList);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ExpenseDayViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType) {

        View expenseDayView = mInflater.inflate(R.layout.expense_group, parentViewGroup, false);
        return new ExpenseDayViewHolder(expenseDayView);
    }

    @Override
    public ExpenseChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {

        View expenseView = mInflater.inflate(R.layout.expense_child, childViewGroup, false);
        return new ExpenseChildViewHolder(expenseView);
    }

    @Override
    public void onBindParentViewHolder(ExpenseDayViewHolder expenseDayViewHolder, int parentPosition, ExpenseDay expenseDay) {
        expenseDayViewHolder.setData(expenseDay);
    }

    @Override
    public void onBindChildViewHolder(ExpenseChildViewHolder expenseViewHolder, int parentPosition, int childPosition, Expense expense) {
        expenseViewHolder.setData(expense);
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
