package com.michaelchaplin.spendometer;

public class ExpenseDataModel {

    // Defining the components of an Expense Item
    public String category,notes,account;
    public int icon;
    public long date;
    public double cost;

    // Constructor for the ExpenseDataModel class
    public ExpenseDataModel (String Category, String Notes, int Icon, long Date, double Cost, String Account){

        this.category = Category;
        this.notes = Notes;
        this.cost = Cost;
        this.icon = Icon;
        this.date = Date;
        this.account = Account;
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

    String getExpenseAccount() {
        return account;
    }
}
