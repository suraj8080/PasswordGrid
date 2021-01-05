package com.evontech.passwordgridapp.custom.data.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.evontech.passwordgridapp.custom.data.WordDataSource;
import com.evontech.passwordgridapp.custom.models.Word;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class WordSQLiteDataSource implements WordDataSource {

    private DbHelper mHelper;

    @Inject
    public WordSQLiteDataSource(DbHelper helper) {
        mHelper = helper;
    }

    @Override
    public List<Word> getWords() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.WordBank._ID,
                DbContract.WordBank.COL_STRING
        };

        Cursor c = db.query(DbContract.WordBank.TABLE_NAME, cols, null, null, null, null, null);

        List<Word> wordList = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                wordList.add(new Word(c.getInt(0), c.getString(1)));

                c.moveToNext();
            }
        }

        c.close();
        return wordList;
    }
}
