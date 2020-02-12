package com.michaelchaplin.spendometer.data.androidxprep;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_table")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private int iconID;

    // Public Constructor
    public Category(String name, int iconID) {
        this.name = name;
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
    public int getIconID() {
        return iconID;
    }
}
