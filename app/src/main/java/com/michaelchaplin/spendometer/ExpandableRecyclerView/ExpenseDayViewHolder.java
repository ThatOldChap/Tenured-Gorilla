package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.view.View;
import android.widget.TextView;

import com.michaelchaplin.spendometer.R;

public class ExpenseDayViewHolder extends ParentViewHolder {

    private TextView mTotalCost, mYear, mMonth, mDayOfWeek, mDayOfMonth;

    public ExpenseDayViewHolder(View itemView) {
        super(itemView);

        mTotalCost = itemView.findViewById(R.id.expense_group_total_cost);
        mYear = itemView.findViewById(R.id.expense_group_year);
        mMonth = itemView.findViewById(R.id.expense_group_month);
        mDayOfWeek = itemView.findViewById(R.id.expense_group_day_of_week);
        mDayOfMonth = itemView.findViewById(R.id.expense_group_day_of_month);
    }

    public void setData(ExpenseDay expenseDay) {

        mTotalCost.setText(String.valueOf(expenseDay.getTotalCost()));
        mYear.setText(expenseDay.getYear());
        mMonth.setText(expenseDay.getMonth());
        mDayOfWeek.setText(expenseDay.getDayOfWeek());
        mDayOfMonth.setText(expenseDay.getDayOfMonth());
    }

}
