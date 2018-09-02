package com.example.romatupkalenko.wikiresearcherapp.data.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

@Database(entities = {ArticleEntry.class}, version = 10)
public abstract class WikiResearchDatabase extends RoomDatabase{
    private static final String LOG_TAG = WikiResearchDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "wikidb";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static  WikiResearchDatabase sInstance;

    public static  WikiResearchDatabase getInstance(Context context) {
        Log.i(LOG_TAG, "Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WikiResearchDatabase.class,  WikiResearchDatabase.DATABASE_NAME).
                         fallbackToDestructiveMigration().build();
                Log.i(LOG_TAG, "Made new database");
            }
        }
        return sInstance;
    }


    // The associated DAOs for the database
    public abstract ArticleDao articleDao();
}
