package com.michaelchaplin.spendometer.data.androidxprep;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("DELETE FROM account_table")
    void deleteAllAccounts();

    @Query("SELECT * FROM account_table ORDER BY name ASC")
    LiveData<List<Account>> getAllAccounts();
}
