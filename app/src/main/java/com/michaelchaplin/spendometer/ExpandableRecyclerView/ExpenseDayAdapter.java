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


}
