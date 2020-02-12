package com.michaelchaplin.spendometer.data.androidxprep;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense_table")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String category;

    private String notes;

    private String account;

    private int iconID;

    private long date;

    private double cost;

    // Public constructor
    public Expense(String category, String notes, String account, int iconID, long date, double cost) {
        this.category = category;
        this.notes = notes;
        this.account = account;
        this.iconID = iconID;
        this.date = date;
        this.cost = cost;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
    }

    // Getter methods
    public int getId() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public String getNotes() {
        return notes;
    }
    public String getAccount() {
        return account;
    }
    public int getIconID() {
        return iconID;
    }
    public long getDate() {
        return date;
    }
    public double getCost() {
        return cost;
    }
}
