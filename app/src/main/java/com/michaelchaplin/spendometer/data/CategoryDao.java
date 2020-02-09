package com.michaelchaplin.spendometer.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM category_table")
    void deleteAllCategories();

    @Query("SELECT * FROM category_table ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
}
