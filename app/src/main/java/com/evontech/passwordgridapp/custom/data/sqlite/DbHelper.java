package com.evontech.passwordgridapp.custom.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;


    private static final String SQL_CREATE_TABLE_USED_WORD =
            "CREATE TABLE " + DbContract.UsedWord.TABLE_NAME + " (" +
                    DbContract.UsedWord._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.UsedWord.COL_GRID_ID + " INTEGER," +
                    DbContract.UsedWord.COL_WORD_STRING + " TEXT," +
                    DbContract.UsedWord.COL_ANSWER_LINE_DATA + " TEXT," +
                    DbContract.UsedWord.COL_LINE_COLOR + " INTEGER," +
                    DbContract.UsedWord.COL_IS_MYSTERY + " TEXT," +
                    DbContract.UsedWord.COL_REVEAL_COUNT + " INTEGER)";

    private static final String SQL_CREATE_TABLE_GRID =
            "CREATE TABLE " + DbContract.GRID.TABLE_NAME + " (" +
                    DbContract.GRID._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.GRID.COL_NAME + " TEXT," +
                    DbContract.GRID.COL_DURATION + " INTEGER," +
                    DbContract.GRID.COL_GRID_ROW_COUNT + " INTEGER," +
                    DbContract.GRID.COL_GRID_COL_COUNT + " INTEGER," +
                    DbContract.GRID.COL_SELECTION_CRITERIA + " TEXT," +
                    DbContract.GRID.COL_CHOSEN_OPTION + " TEXT," +
                    DbContract.GRID.COL_GRID_DATA + " TEXT)";

    private static final String SQL_CREATE_TABLE_USER_ACCOUNT =
            "CREATE TABLE " + DbContract.UserAccounts.TABLE_NAME + " (" +
                    DbContract.UserAccounts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.UserAccounts.COL_ACCOUNT_NAME + " TEXT," +
                    DbContract.UserAccounts.COL_ACCOUNT_USER_NAME + " TEXT," +
                    DbContract.UserAccounts.COL_ACCOUNT_URL + " TEXT," +
                    DbContract.UserAccounts.COL_ACCOUNT_GRID_ID + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USED_WORD);
        db.execSQL(SQL_CREATE_TABLE_GRID);
        db.execSQL(SQL_CREATE_TABLE_USER_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

