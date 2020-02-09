package com.michaelchaplin.spendometer.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.michaelchaplin.spendometer.R;

@Database(entities = Category.class, version = 1)
public abstract class CategoryDatabase extends RoomDatabase {

    private static CategoryDatabase instance;

    public abstract CategoryDao categoryDao();

    public static synchronized CategoryDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CategoryDatabase.class, "category_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }

    };


    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private CategoryDao categoryDao;

        private PopulateDbAsyncTask(CategoryDatabase db) {
            categoryDao = db.categoryDao();
        }

        // Add all necessary entries into database to pre-populate it
        @Override
        protected Void doInBackground(Void... voids) {
            categoryDao.insert(new Category("Credit Card", R.drawable.round_credit_card_white_48));
            return null;
        }
    }

}