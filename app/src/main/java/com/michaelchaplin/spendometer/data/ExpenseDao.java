package com.michaelchaplin.spendometer.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("DELETE FROM expense_table")
    void deleteAllExpenses();

    @Query("SELECT * FROM expense_table ORDER BY date ASC")
    LiveData<List<Expense>> getAllExpenses();
}
