package com.michaelchaplin.spendometer;

import android.graphics.drawable.Icon;

public class ExpenseDataModel {

    // Defining the components of an Expense Item
    public String category,notes;
    public int icon;
    public long date;
    public double cost;

    // Constructor for the ExpenseDataModel class
    public ExpenseDataModel (String Category, String Notes, int Icon, long Date, double Cost){

        this.category = Category;
        this.notes = Notes;
        this.cost = Cost;
        this.icon = Icon;
        this.date = Date;
    }

    String getExpenseCategory(){
        return category;
    }

    String getExpenseNotes(){
        return notes;
    }

    int getExpenseIcon(){
        return icon;
    }

    long getExpenseDate(){
        return date;
    }

    double getExpenseCost(){
        return cost;
    }

}
