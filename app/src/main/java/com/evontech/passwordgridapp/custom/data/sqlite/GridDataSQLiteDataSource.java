package com.evontech.passwordgridapp.custom.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridDataSQLiteDataSource implements GridDataSource, AccountDataSource {

    private com.evontech.passwordgridapp.custom.data.sqlite.DbHelper mHelper;

    @Inject
    public GridDataSQLiteDataSource(com.evontech.passwordgridapp.custom.data.sqlite.DbHelper helper) {
        mHelper = helper;
    }

    @Override
    public void getGridData(int gid, GridRoundCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.GRID._ID,
                DbContract.GRID.COL_NAME,
                DbContract.GRID.COL_DURATION,
                DbContract.GRID.COL_GRID_ROW_COUNT,
                DbContract.GRID.COL_GRID_COL_COUNT,
                DbContract.GRID.COL_GRID_DATA
        };
        String sel = DbContract.GRID._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        Cursor c = db.query(DbContract.GRID.TABLE_NAME, cols, sel, selArgs, null, null, null);
        GridDataEntity ent = null;
        if (c.moveToFirst()) {
            ent = new GridDataEntity();
            ent.setId(c.getInt(0));
            ent.setName(c.getString(1));
            ent.setDuration(c.getInt(2));
            ent.setGridRowCount(c.getInt(3));
            ent.setGridColCount(c.getInt(4));
            ent.setGridData(c.getString(5));
            Log.d("Getting GridData ", c.getString(5));
            ent.setUsedWords(getUsedWords(gid));
        }
        c.close();

        callback.onLoaded(ent);
    }

    @Override
    public void getGridDataInfos(InfosCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<GridDataInfo> infoList = new ArrayList<>();
        Cursor c = db.rawQuery(getGameDataInfoQuery(-1), null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                infoList.add(getGameDataInfoFromCursor(c));
                c.moveToNext();
            }
        }
        c.close();

        callback.onLoaded(infoList);
    }

    @Override
    public void getGridDataInfo(int gid, StatCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.rawQuery(getGameDataInfoQuery(gid), null);
        if (c.moveToFirst()) {
            GridDataInfo gameData = getGameDataInfoFromCursor(c);
            callback.onLoaded(gameData);
        }
        c.close();
    }

    @Override
    public long saveGridData(GridDataEntity gameRound) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GRID.COL_NAME, gameRound.getName());
        values.put(DbContract.GRID.COL_DURATION, gameRound.getDuration());
        values.put(DbContract.GRID.COL_GRID_ROW_COUNT, gameRound.getGridRowCount());
        values.put(DbContract.GRID.COL_GRID_COL_COUNT, gameRound.getGridColCount());
        Log.d("Saving GridData ", gameRound.getGridData());
        values.put(DbContract.GRID.COL_GRID_DATA, gameRound.getGridData());

        long gid;
        if(gameRound.getId()>0){
            gid = gameRound.getId();
            String where = DbContract.GRID._ID + "=?";
            String whereArgs[] = {String.valueOf(gid)};
            int updateStatus = db.update(DbContract.GRID.TABLE_NAME, values,where, whereArgs);
            Log.d("updateStatus ", ""+updateStatus);
        }else {
             gid = db.insert(DbContract.GRID.TABLE_NAME, "null", values);
            Log.d("insertStatus ", ""+gid);
        }
        gameRound.setId((int) gid);

        /*if(gameRound.getUsedWords()!=null) {
            for (UsedWord usedWord : gameRound.getUsedWords()) {
                //if(!getUsedWords((int) gid).contains(usedWord)){
                values.clear();
                //if(usedWord.isAnswered()) {
                values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID, gid);
                values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_WORD_STRING, usedWord.getString());
                values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_IS_MYSTERY, usedWord.isMystery() ? "true" : "false");
                values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_REVEAL_COUNT, usedWord.getRevealCount());
                if (usedWord.getAnswerLine() != null) {
                    values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
                    values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_LINE_COLOR, usedWord.getAnswerLine().color);
                }

                long insertedId = db.insert(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, "null", values);
                usedWord.setId((int) insertedId);
              //  }
            }
        }*/

        return gid;
    }

    @Override
    public long saveAccountData(UserAccount userAccount) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.UserAccounts.COL_ACCOUNT_NAME, userAccount.getAccountName());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_USER_NAME, userAccount.getUserName());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_URL, userAccount.getAccountUrl());
        Log.d("Saving AccountData ", ""+userAccount.getId());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_GRID_ID, userAccount.getAccountGridId());

        long acId;
        if(userAccount.getId()>0){
            acId = userAccount.getId();
            String where = DbContract.UserAccounts._ID + "=?";
            String whereArgs[] = {String.valueOf(acId)};
            int updateStatus = db.update(DbContract.UserAccounts.TABLE_NAME, values,where, whereArgs);
            Log.d("updateStatus ", ""+updateStatus);
        }else {
            acId = db.insert(DbContract.UserAccounts.TABLE_NAME, "null", values);
            Log.d("insertStatus ", ""+acId);
        }
        userAccount.setId((int) acId);
        return acId;
    }

    @Override
    public List<UserAccount> getAllAccountData() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.UserAccounts._ID,
                DbContract.UserAccounts.COL_ACCOUNT_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_USER_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_URL,
                DbContract.UserAccounts.COL_ACCOUNT_GRID_ID
        };
        //String sel = DbContract.UserAccounts._ID + "=?";
        //String selArgs[] = {String.valueOf(accountId)};

        Cursor c = db.query(DbContract.UserAccounts.TABLE_NAME, cols, null, null, null, null, null);
        List<UserAccount> allAccounts = new ArrayList<>();
        UserAccount userAccount = null;
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                userAccount = new UserAccount();
                userAccount.setId(c.getInt(0));
                userAccount.setAccountName(c.getString(1));
                userAccount.setUserName(c.getString(2));
                userAccount.setAccountUrl(c.getString(3));
                userAccount.setAccountGridId(c.getInt(4));
                Log.d("Getting accountData ", c.getString(1));
                allAccounts.add(userAccount);
                c.moveToNext();
            }
        }
        c.close();
        return allAccounts;
    }

    @Override
    public UserAccount getAccountData(int accountId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.UserAccounts._ID,
                DbContract.UserAccounts.COL_ACCOUNT_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_USER_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_URL,
                DbContract.UserAccounts.COL_ACCOUNT_GRID_ID
        };
        String sel = DbContract.UserAccounts._ID + "=?";
        String selArgs[] = {String.valueOf(accountId)};

        Cursor c = db.query(DbContract.UserAccounts.TABLE_NAME, cols, sel, selArgs, null, null, null);
        UserAccount userAccount = null;
        if (c.moveToFirst()) {
            userAccount = new UserAccount();
            userAccount.setId(c.getInt(0));
            userAccount.setAccountName(c.getString(1));
            userAccount.setUserName(c.getString(2));
            userAccount.setAccountUrl(c.getString(3));
            userAccount.setAccountGridId(c.getInt(4));
            Log.d("Getting accountData ", c.getString(1));
        }
        c.close();
        return userAccount;
    }

    @Override
    public void deleteGridData(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = DbContract.GRID._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        db.delete(DbContract.GRID.TABLE_NAME, sel, selArgs);

        sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID + "=?";
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, sel, selArgs);
    }

    @Override
    public void deleteAllLines(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String selArgs[] = {String.valueOf(gid)};
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID + "=?";
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, sel, selArgs);
    }

    @Override
    public void deleteGridDatas() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DbContract.GRID.TABLE_NAME, null, null);
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, null, null);
    }

    @Override
    public void saveGridDataDuration(int gid, int newDuration) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GRID.COL_DURATION, newDuration);

        String where = DbContract.GRID._ID + "=?";
        String whereArgs[] = {String.valueOf(gid)};

        db.update(DbContract.GRID.TABLE_NAME, values, where, whereArgs);
    }

    @Override
    public void markWordAsAnswered(UsedWord usedWord) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_LINE_COLOR, usedWord.getAnswerLine().color);

        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID, usedWord.getId());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_WORD_STRING, usedWord.getString());
        long insertedId = db.insert(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, "null", values);
        usedWord.setId((int) insertedId);
        Log.d("Line updateStatus ", ""+insertedId);

        /*String where = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord._ID + "=?";
        String whereArgs[] = {String.valueOf(usedWord.getId())};
        Log.d("Anser_Line_Data "+usedWord.getAnswerLine().toString(), "COL_LINE_COLOR "+usedWord.getAnswerLine().color);
        Log.d("GridId ", ""+usedWord.getId());

        int updateStatus = db.update(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, values, where, whereArgs);
        Log.d("Line updateStatus ", ""+updateStatus);*/
    }

    private String getGameDataInfoQuery(int gid) {
        String subQ = "(SELECT COUNT(*) FROM " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME + " WHERE " +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID + "=" + DbContract.GRID.TABLE_NAME + "." + DbContract.GRID._ID + ")";
        String order = " ORDER BY " + DbContract.GRID._ID + " DESC";
        if (gid > 0) {
            subQ = "(SELECT COUNT(*) FROM " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME + " WHERE " +
                    com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID + "=" + gid + ")";
            order = " WHERE " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord._ID + "=" + gid;
        }

        return "SELECT " +
                DbContract.GRID._ID + "," +
                DbContract.GRID.COL_NAME + "," +
                DbContract.GRID.COL_DURATION + "," +
                DbContract.GRID.COL_GRID_ROW_COUNT + "," +
                DbContract.GRID.COL_GRID_COL_COUNT + "," +
                subQ +
                " FROM " + DbContract.GRID.TABLE_NAME + order;
    }

    private GridDataInfo getGameDataInfoFromCursor(Cursor c) {
        GridDataInfo gdi = new GridDataInfo();
        gdi.setId(c.getInt(0));
        gdi.setName(c.getString(1));
        gdi.setDuration(c.getInt(2));
        gdi.setGridRowCount(c.getInt(3));
        gdi.setGridColCount(c.getInt(4));
        gdi.setUsedWordsCount(c.getInt(5));
        return gdi;
    }

    private List<UsedWord> getUsedWords(int gid) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord._ID,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_WORD_STRING,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_ANSWER_LINE_DATA,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_LINE_COLOR,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_IS_MYSTERY,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_REVEAL_COUNT
        };
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GRID_ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        Cursor c = db.query(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, cols, sel, selArgs, null, null, null);

        List<UsedWord> usedWordList = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                int id = c.getInt(0);
                String str = c.getString(1);
                String lineData = c.getString(2);
                int col = c.getInt(3);

                UsedWord.AnswerLine answerLine = null;
                if (lineData != null) {
                    answerLine = new UsedWord.AnswerLine();
                    answerLine.fromString(lineData);
                    answerLine.color = col;
                }

                UsedWord usedWord = new UsedWord();
                usedWord.setId(id);
                usedWord.setString(str);
                usedWord.setAnswered(lineData != null);
                usedWord.setAnswerLine(answerLine);
                usedWord.setIsMystery(Boolean.valueOf(c.getString(4)));
                usedWord.setRevealCount(c.getInt(5));

                usedWordList.add(usedWord);

                c.moveToNext();
            }
        }
        c.close();

        return usedWordList;
    }
}
