package com.michaelchaplin.spendometer.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Expense.class, version = 1)
public abstract class ExpenseDatabase extends RoomDatabase {

    private static ExpenseDatabase instance;

    public abstract ExpenseDao expenseDao();

    public static synchronized ExpenseDatabase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ExpenseDatabase.class, "expense_database")
                    .fallbackToDestructiveMigration() // Deletes the database and recreates it from scratch
                    .build();
        }
        return instance;
    }
}
