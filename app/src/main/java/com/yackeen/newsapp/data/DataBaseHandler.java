package com.yackeen.newsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohamed Yasser on 3/10/2016.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NewsAppDataBase";

    Context context;

    public DataBaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE "
                + NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME + " (" +
                NewsContract.NEWS_TABLE_ENTRY._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NewsContract.NEWS_TABLE_ENTRY.SECTION + " TEXT NOT NULL, " +
                NewsContract.NEWS_TABLE_ENTRY.TITLE + " TEXT NOT NULL, " +
                NewsContract.NEWS_TABLE_ENTRY.PUBLISHED_DATE + " TEXT NOT NULL, " +
                NewsContract.NEWS_TABLE_ENTRY.IURL + " TEXT NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME);
        onCreate(sqLiteDatabase);


    }
}
