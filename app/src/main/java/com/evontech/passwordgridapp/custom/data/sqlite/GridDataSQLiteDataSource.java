package com.evontech.passwordgridapp.custom.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.evontech.passwordgridapp.custom.UserLoginDataSource;
import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.models.AppUser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridDataSQLiteDataSource implements GridDataSource, AccountDataSource, UserLoginDataSource {

    private com.evontech.passwordgridapp.custom.data.sqlite.DbHelper mHelper;

    @Inject
    public GridDataSQLiteDataSource(com.evontech.passwordgridapp.custom.data.sqlite.DbHelper helper) {
        mHelper = helper;
    }

    @Override
    public void getGridData(int gid, int userId, GridRoundCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.GRID._ID,
                DbContract.GRID.COL_NAME,
                DbContract.GRID.COL_GRID_ROW_COUNT,
                DbContract.GRID.COL_GRID_COL_COUNT,
                DbContract.GRID.COL_GRID_DATA,
                DbContract.GRID.COL_SELECTION_CRITERIA,
                DbContract.GRID.COL_CHOSEN_OPTION,
                DbContract.GRID.COL_SELECTED_TYPED_WORD,
                DbContract.GRID.COL_GRID_UPDATED_PASSWORD
                //DbContract.GRID.COL_GRID_PASSWORD_LENGTH
        };
        String sel = DbContract.GRID._ID + "=? AND " + DbContract.GRID.COL_USER_ID + " = ?";
        String[] selArgs = {String.valueOf(gid), String.valueOf(userId)};

        Cursor c = db.query(DbContract.GRID.TABLE_NAME, cols, sel, selArgs, null, null, null);
        GridDataEntity ent = null;
        if (c.moveToFirst()) {
            ent = new GridDataEntity();
            ent.setId(c.getInt(0));
            ent.setName(c.getString(1));
            ent.setGridRowCount(c.getInt(2));
            ent.setGridColCount(c.getInt(3));
            ent.setGridData(c.getString(4));
            ent.setmSelectionCriteria(c.getString(5));
            ent.setmChosenOption(c.getString(6));
            ent.setmSelectedTypedWord(c.getString(7));
            ent.setUpdatedPassword(c.getString(8));
           // ent.setmGridPasswordLength(c.getInt(9));
            Log.d("Getting GridData ", c.getString(4));
            Log.d("Getting SelectedTypedWord ", c.getString(7));
            Log.d("Getting userId ", userId+"");
            ent.setUsedWords(getUsedWords(gid, userId));
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
    public long saveGridData(GridDataEntity gameRound, int userId) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GRID.COL_USER_ID, userId);
        values.put(DbContract.GRID.COL_NAME, gameRound.getName());
        values.put(DbContract.GRID.COL_GRID_ROW_COUNT, gameRound.getGridRowCount());
        values.put(DbContract.GRID.COL_GRID_COL_COUNT, gameRound.getGridColCount());
        //values.put(DbContract.GRID.COL_GRID_PASSWORD_LENGTH, gameRound.getmGridPasswordLength());
        values.put(DbContract.GRID.COL_SELECTION_CRITERIA, gameRound.getmSelectionCriteria());
        values.put(DbContract.GRID.COL_CHOSEN_OPTION, gameRound.getmChosenOption());
        values.put(DbContract.GRID.COL_SELECTED_TYPED_WORD, gameRound.getmSelectedTypedWord());
        Log.d("Saving SELECTION_CRITERIA ", gameRound.getmSelectionCriteria());
        Log.d("Saving CHOSEN_OPTION ", gameRound.getmChosenOption());
        Log.d("Saving SELECTED_TYPED_WORD ", gameRound.getmSelectedTypedWord());
        Log.d("Saving GridData ", gameRound.getGridData());
        Log.d("Saving userId ", userId+"");
        values.put(DbContract.GRID.COL_GRID_DATA, gameRound.getGridData());
        values.put(DbContract.GRID.COL_GRID_UPDATED_PASSWORD, gameRound.getUpdatedPassword());

        long gid;
        if(gameRound.getId()>0){
            gid = gameRound.getId();
            String where = DbContract.GRID._ID + "=? AND " + DbContract.GRID.COL_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(gid), String.valueOf(userId)};
            int updateStatus = db.update(DbContract.GRID.TABLE_NAME, values,where, whereArgs);
            Log.d("saveGridData updateStatus ", ""+updateStatus);
        }else {
             gid = db.insert(DbContract.GRID.TABLE_NAME, "null", values);
             Log.d("saveGridData insertStatus ", ""+gid);
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
        values.put(DbContract.UserAccounts.COL_USER_ID, userAccount.getUserId());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_NAME, userAccount.getAccountName());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_USER_NAME, userAccount.getUserName());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_CATEGORY, userAccount.getAccountCategory());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_URL, userAccount.getAccountUrl());
        Log.d("Saving AccountData ", ""+userAccount.getId());
        Log.d("UserAccounts userId ", ""+userAccount.getUserId());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_GRID_ID, userAccount.getAccountGridId());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_PASSWORD, userAccount.getAccountPwd());

        long acId;
        if(userAccount.getId()>0){
            acId = userAccount.getId();
            String where = DbContract.UserAccounts._ID + "=?  AND " + DbContract.UserAccounts.COL_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(acId), String.valueOf(userAccount.getUserId())};
            int updateStatus = db.update(DbContract.UserAccounts.TABLE_NAME, values,where, whereArgs);
            Log.d("saveAccountData updateStatus ", ""+updateStatus);
        }else {
            acId = db.insert(DbContract.UserAccounts.TABLE_NAME, "null", values);
            Log.d("saveAccountData insertStatus ", ""+acId);
        }
        userAccount.setId((int) acId);
        return acId;
    }

    @Override
    public int updateAccountInfo(UserAccount account) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.UserAccounts.COL_ACCOUNT_GRID_ID, account.getAccountGridId());
        values.put(DbContract.UserAccounts.COL_ACCOUNT_PASSWORD, account.getAccountPwd());
        String where = DbContract.UserAccounts._ID + "=?  AND " + DbContract.UserAccounts.COL_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(account.getId()), String.valueOf(account.getUserId())};
        int updateStatus = db.update(DbContract.UserAccounts.TABLE_NAME, values,where, whereArgs);
        Log.d("UserAccounts userId ", ""+account.getUserId());
        Log.d("updateAccountInfo gridId ", ""+account.getAccountGridId());
        Log.d("updateAccountInfo pwd ", ""+account.getAccountPwd());
        Log.d("updateAccountInfo updateStatus ", ""+updateStatus);
        return updateStatus;
    }

    @Override
    public List<UserAccount> getAllAccountData(int userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String[] cols = {
                DbContract.UserAccounts._ID,
                DbContract.UserAccounts.COL_USER_ID,
                DbContract.UserAccounts.COL_ACCOUNT_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_USER_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_URL,
                DbContract.UserAccounts.COL_ACCOUNT_CATEGORY,
                DbContract.UserAccounts.COL_ACCOUNT_GRID_ID,
                DbContract.UserAccounts.COL_ACCOUNT_PASSWORD
        };
        String sel = DbContract.UserAccounts.COL_USER_ID + "=?";
        String[] selArgs = {String.valueOf(userId)};

        List<UserAccount> allAccounts = new ArrayList<>();
        try {
            Cursor c = db.query(DbContract.UserAccounts.TABLE_NAME, cols, sel, selArgs, null, null, null);
            UserAccount userAccount = null;
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    userAccount = new UserAccount();
                    userAccount.setId(c.getInt(0));
                    userAccount.setUserId(c.getInt(1));
                    userAccount.setAccountName(c.getString(2));
                    userAccount.setUserName(c.getString(3));
                    userAccount.setAccountUrl(c.getString(4));
                    userAccount.setAccountCategory(c.getString(5));
                    userAccount.setAccountGridId(c.getInt(6));
                    userAccount.setAccountPwd(c.getString(7));
                  //  Log.d("Getting accountData ", c.getString(2));
                    allAccounts.add(userAccount);
                    c.moveToNext();
                  //  Log.d("UserAccounts userId ", ""+userAccount.getUserId());
                }
            }
            c.close();
        }catch (Exception e){}
        return allAccounts;
    }

    @Override
    public UserAccount getAccountData(int accountId, int userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String[] cols = {
                DbContract.UserAccounts._ID,
                DbContract.UserAccounts.COL_ACCOUNT_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_USER_NAME,
                DbContract.UserAccounts.COL_ACCOUNT_URL,
                DbContract.UserAccounts.COL_ACCOUNT_CATEGORY,
                DbContract.UserAccounts.COL_ACCOUNT_GRID_ID,
                DbContract.UserAccounts.COL_ACCOUNT_PASSWORD
        };
        String where = DbContract.UserAccounts._ID + "=?  AND " + DbContract.UserAccounts.COL_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(accountId), String.valueOf(userId)};

        Cursor c = db.query(DbContract.UserAccounts.TABLE_NAME, cols, where, whereArgs, null, null, null);
        UserAccount userAccount = null;
        if (c.moveToFirst()) {
            userAccount = new UserAccount();
            userAccount.setId(c.getInt(0));
            userAccount.setAccountName(c.getString(1));
            userAccount.setUserName(c.getString(2));
            userAccount.setAccountUrl(c.getString(3));
            userAccount.setAccountCategory(c.getString(4));
            userAccount.setAccountGridId(c.getInt(5));
            userAccount.setAccountPwd(c.getString(6));
            Log.d("Getting accountData ", c.getString(1));
        }
        c.close();
        return userAccount;
    }

    @Override
    public long registerUserLogin(AppUser user) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.UserLogin.COL_NAME, user.getName());
        values.put(DbContract.UserLogin.COL_MOBILE, user.getMobile());
        values.put(DbContract.UserLogin.COL_LOGIN_USER_NAME, user.getUserName());
        values.put(DbContract.UserLogin.COL_LOGIN_USER_PASSWORD, user.getUserPassword());
        Log.d("Register Useer ", ""+user.getId());

        long acId;
        if(user.getId()>0){
            acId = user.getId();
            String where = DbContract.UserLogin._ID + "=?";
            String whereArgs[] = {String.valueOf(acId)};
            int updateStatus = db.update(DbContract.UserLogin.TABLE_NAME, values,where, whereArgs);
            Log.d(" user login updateStatus ", ""+updateStatus);
        }else {
            acId = db.insert(DbContract.UserLogin.TABLE_NAME, "null", values);
            Log.d("user registration status ", ""+acId);
        }
        user.setId((int) acId);
        return acId;
    }

    @Override
    public AppUser userLogin(AppUser user) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.UserLogin._ID,
                DbContract.UserLogin.COL_NAME,
                DbContract.UserLogin.COL_MOBILE,
                DbContract.UserLogin.COL_LOGIN_USER_NAME,
                DbContract.UserLogin.COL_LOGIN_USER_PASSWORD
        };
        String sel = DbContract.UserLogin.COL_LOGIN_USER_NAME + "=? AND " + DbContract.UserLogin.COL_LOGIN_USER_PASSWORD + " = ?";
        String selArgs[] = {user.getUserName(), user.getUserPassword()};

        Cursor c = db.query(DbContract.UserLogin.TABLE_NAME, cols, sel, selArgs, null, null, null);
        AppUser appUser = null;
        if (c.moveToFirst()) {
            appUser = new AppUser();
            appUser.setId(c.getInt(0));
            appUser.setName(c.getString(1));
            appUser.setMobile(c.getString(2));
            appUser.setUserName(c.getString(3));
            appUser.setUserPassword(c.getString(4));
            Log.d("Getting UserLogin Data ", c.getString(2));
        }
        c.close();
        return appUser;
    }

    @Override
    public void deleteGridData(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = DbContract.GRID._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        db.delete(DbContract.GRID.TABLE_NAME, sel, selArgs);

        sel = DbContract.UsedLine.COL_GRID_ID + "=?";
        db.delete(DbContract.UsedLine.TABLE_NAME, sel, selArgs);
    }

    @Override
    public void deleteAllLines(int gid, int userId) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = DbContract.UsedLine.COL_GRID_ID + "=? AND " + DbContract.GRID.COL_USER_ID + " = ?";
        String[] selArgs = {String.valueOf(gid), String.valueOf(userId)};
        db.delete(DbContract.UsedLine.TABLE_NAME, sel, selArgs);
    }


    @Override
    public void deleteGridDatas() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DbContract.GRID.TABLE_NAME, null, null);
        db.delete(DbContract.UsedLine.TABLE_NAME, null, null);
    }

    @Override
    public void saveGridDataDuration(int gid, int newDuration) {
        /*SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GRID.COL_DURATION, newDuration);

        String where = DbContract.GRID._ID + "=?";
        String whereArgs[] = {String.valueOf(gid)};

        db.update(DbContract.GRID.TABLE_NAME, values, where, whereArgs);*/
    }

    @Override
    public void markWordAsAnswered(int index, int userId, UsedWord usedWord) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(index==0) {
            String sel = DbContract.UsedLine.COL_GRID_ID + "=?  AND " + DbContract.UsedLine.COL_USER_ID + " = ?";
            String[] selArgs = {String.valueOf(usedWord.getId()), String.valueOf(userId)};
            int status = db.delete(DbContract.UsedLine.TABLE_NAME, sel, selArgs);
            Log.d("markWordAsAnswered deleteStatus ", "" + status);
        }
        ContentValues values = new ContentValues();
        values.put(DbContract.UsedLine.COL_USER_ID, userId);
        values.put(DbContract.UsedLine.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
        values.put(DbContract.UsedLine.COL_LINE_COLOR, usedWord.getAnswerLine().color);
        values.put(DbContract.UsedLine.COL_GRID_ID, usedWord.getId());
        values.put(DbContract.UsedLine.COL_WORD_STRING, usedWord.getString());
        long insertedId = db.insert(DbContract.UsedLine.TABLE_NAME, "null", values);
        usedWord.setId((int) insertedId);
        Log.d("markWordAsAnswered updateStatus ", ""+insertedId);
        Log.d("Saving userId ", userId+"");

        /*String where = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord._ID + "=?";
        String whereArgs[] = {String.valueOf(usedWord.getId())};
        Log.d("Anser_Line_Data "+usedWord.getAnswerLine().toString(), "COL_LINE_COLOR "+usedWord.getAnswerLine().color);
        Log.d("GridId ", ""+usedWord.getId());

        int updateStatus = db.update(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, values, where, whereArgs);
        Log.d("Line updateStatus ", ""+updateStatus);*/
    }

    private List<UsedWord> getUsedWords(int gid, int userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String[] cols = {
                DbContract.UsedLine._ID,
                DbContract.UsedLine.COL_WORD_STRING,
                DbContract.UsedLine.COL_ANSWER_LINE_DATA,
                DbContract.UsedLine.COL_LINE_COLOR
        };
        String sel = DbContract.UsedLine.COL_GRID_ID + "=? AND " + DbContract.UsedLine.COL_USER_ID + " = ?";
        String[] selArgs = {String.valueOf(gid), String.valueOf(userId)};
        Cursor c = db.query(DbContract.UsedLine.TABLE_NAME, cols, sel, selArgs, null, null, null);
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
                usedWordList.add(usedWord);
                c.moveToNext();
            }
        }
        c.close();
        return usedWordList;
    }

    private String getGameDataInfoQuery(int gid) {
        String subQ = "(SELECT COUNT(*) FROM " + DbContract.UsedLine.TABLE_NAME + " WHERE " +
                DbContract.UsedLine.COL_GRID_ID + "=" + DbContract.GRID.TABLE_NAME + "." + DbContract.GRID._ID + ")";
        String order = " ORDER BY " + DbContract.GRID._ID + " DESC";
        if (gid > 0) {
            subQ = "(SELECT COUNT(*) FROM " + DbContract.UsedLine.TABLE_NAME + " WHERE " +
                    DbContract.UsedLine.COL_GRID_ID + "=" + gid + ")";
            order = " WHERE " + DbContract.UsedLine._ID + "=" + gid;
        }

        return "SELECT " +
                DbContract.GRID._ID + "," +
                DbContract.GRID.COL_NAME + "," +
                DbContract.GRID.COL_GRID_ROW_COUNT + "," +
                DbContract.GRID.COL_GRID_COL_COUNT + "," +
                subQ +
                " FROM " + DbContract.GRID.TABLE_NAME + order;
    }

    private GridDataInfo getGameDataInfoFromCursor(Cursor c) {
        GridDataInfo gdi = new GridDataInfo();
        gdi.setId(c.getInt(0));
        gdi.setName(c.getString(1));
        gdi.setGridRowCount(c.getInt(3));
        gdi.setGridColCount(c.getInt(4));
        gdi.setUsedWordsCount(c.getInt(5));
        return gdi;
    }

}
