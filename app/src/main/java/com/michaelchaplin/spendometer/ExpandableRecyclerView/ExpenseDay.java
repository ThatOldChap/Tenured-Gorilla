package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import java.util.List;

public class ExpenseDay implements Parent<Expense>{

    private List<Expense> mExpenseList;
    private double totalCost = 0;

    public ExpenseDay(List<Expense> expenseList) {
        mExpenseList = expenseList;
    }

    public double getTotalCost() {
        return totalCost;
    }
    public String getDayOfMonth() {
        return mExpenseList.get(0).getDayOfMonth();
    }
    public String getDayOfWeek() {
        return mExpenseList.get(0).getDayOfWeek();
    }
    public String getMonth() {
        return mExpenseList.get(0).getMonth();
    }
    public String getYear() {
        return mExpenseList.get(0).getYear();
    }

    @Override
    public List<Expense> getChildList() {

        if(mExpenseList != null){

            for (int i = 0; i < mExpenseList.size(); i++){
                totalCost += mExpenseList.get(i).getCost();
            }
        } else {
            throw new IllegalStateException("Child List cannot be null");
        }
        return mExpenseList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return (mExpenseList != null && mExpenseList.size() >= 1);
    }

}
