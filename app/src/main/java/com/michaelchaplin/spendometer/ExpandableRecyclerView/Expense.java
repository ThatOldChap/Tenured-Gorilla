package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import java.util.Calendar;
import java.util.Locale;

public class Expense {

    private String mCategory, mNotes, mAccount;
    private int mIconID;
    private long mDate;
    private double mCost;
    private Calendar mCalendar;

    // Standard Constructor to create an Expense
    public Expense(String category, String notes, String account, int iconID, long date, double cost) {

        mCategory = category;
        mNotes = notes;
        mAccount = account;
        mIconID = iconID;
        mDate = date;
        mCost = cost;

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(mDate);
    }

    // Getter methods for an expense
    public String getCategory() {
        return mCategory;
    }
    public String getNotes() {
        return mNotes;
    }
    public String getAccount() {
        return mAccount;
    }
    public int getIconID() {
        return mIconID;
    }
    public long getDate() {
        return mDate;
    }
    public double getCost() {
        return mCost;
    }
    public int getDayOfMonth() {
        return (mCalendar.get(Calendar.DAY_OF_MONTH));
    }
    public String getDayOfWeek() {
        return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }
    public String getMonth() {
        return mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
    public String getYear() {
        return String.valueOf(mCalendar.get(Calendar.YEAR));
    }

    // Setter methods for an expense
    public void setCategory(String category) {
        mCategory = category;
    }
    public void setNotes(String notes) {
        mNotes = notes;
    }
    public void setAccount(String account) {
        mAccount = account;
    }
    public void setIconID(int iconID) {
        mIconID = iconID;
    }
    public void setDate(long date) {
        mDate = date;
    }
    public void setCost(double cost) {
        mCost = cost;
    }
}
