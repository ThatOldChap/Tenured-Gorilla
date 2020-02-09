package com.michaelchaplin.spendometer.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_table")
public class Account {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String currency;

    private int iconID;

    // Public Constructor
    public Account(String name, String currency, int iconID) {
        this.name = name;
        this.currency = currency;
        this.iconID = iconID;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
    }

    // Getter methods
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCurrency() {
        return currency;
    }
    public int getIconID() {
        return iconID;
    }
}
