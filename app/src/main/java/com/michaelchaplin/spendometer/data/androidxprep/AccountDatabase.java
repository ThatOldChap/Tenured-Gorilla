package com.michaelchaplin.spendometer.data.androidxprep;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Account.class, version = 1)
public abstract class AccountDatabase extends RoomDatabase {

    private static AccountDatabase instance;

    public abstract AccountDao accountDao();

    public static synchronized AccountDatabase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AccountDatabase.class, "account_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
