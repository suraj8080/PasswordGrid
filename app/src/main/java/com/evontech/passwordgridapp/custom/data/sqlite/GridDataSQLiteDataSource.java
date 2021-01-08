package com.evontech.passwordgridapp.custom.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridDataSQLiteDataSource implements GridDataSource {

    private com.evontech.passwordgridapp.custom.data.sqlite.DbHelper mHelper;

    @Inject
    public GridDataSQLiteDataSource(com.evontech.passwordgridapp.custom.data.sqlite.DbHelper helper) {
        mHelper = helper;
    }

    @Override
    public void getGameData(int gid, GameRoundCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_NAME,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_DURATION,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_ROW_COUNT,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_COL_COUNT,
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_DATA
        };
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        Cursor c = db.query(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, cols, sel, selArgs, null, null, null);
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
    public void getGameDataInfos(InfosCallback callback) {
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
    public void getGameDataInfo(int gid, StatCallback callback) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.rawQuery(getGameDataInfoQuery(gid), null);
        if (c.moveToFirst()) {
            GridDataInfo gameData = getGameDataInfoFromCursor(c);
            callback.onLoaded(gameData);
        }
        c.close();
    }

    @Override
    public long saveGameData(GridDataEntity gameRound) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_NAME, gameRound.getName());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_DURATION, gameRound.getDuration());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_ROW_COUNT, gameRound.getGridRowCount());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_COL_COUNT, gameRound.getGridColCount());
        Log.d("Saving GridData ", gameRound.getGridData());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_DATA, gameRound.getGridData());

        long gid;
        if(gameRound.getId()>0){
            gid = gameRound.getId();
            String where = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + "=?";
            String whereArgs[] = {String.valueOf(gid)};
            int updateStatus = db.update(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, values,where, whereArgs);
            Log.d("updateStatus ", ""+updateStatus);
        }else {
             gid = db.insert(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, "null", values);
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
    public void deleteGameData(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, sel, selArgs);

        sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID + "=?";
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, sel, selArgs);
    }

    @Override
    public void deleteAllLines(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String selArgs[] = {String.valueOf(gid)};
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID + "=?";
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, sel, selArgs);
    }

    @Override
    public void deleteGameDatas() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, null, null);
        db.delete(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME, null, null);
    }

    @Override
    public void saveGameDataDuration(int gid, int newDuration) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_DURATION, newDuration);

        String where = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + "=?";
        String whereArgs[] = {String.valueOf(gid)};

        db.update(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME, values, where, whereArgs);
    }

    @Override
    public void markWordAsAnswered(UsedWord usedWord) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_LINE_COLOR, usedWord.getAnswerLine().color);

        values.put(com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID, usedWord.getId());
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
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID + "=" + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME + "." + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + ")";
        String order = " ORDER BY " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + " DESC";
        if (gid > 0) {
            subQ = "(SELECT COUNT(*) FROM " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.TABLE_NAME + " WHERE " +
                    com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID + "=" + gid + ")";
            order = " WHERE " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord._ID + "=" + gid;
        }

        return "SELECT " +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound._ID + "," +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_NAME + "," +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_DURATION + "," +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_ROW_COUNT + "," +
                com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.COL_GRID_COL_COUNT + "," +
                subQ +
                " FROM " + com.evontech.passwordgridapp.custom.data.sqlite.DbContract.GameRound.TABLE_NAME + order;
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
        String sel = com.evontech.passwordgridapp.custom.data.sqlite.DbContract.UsedWord.COL_GAME_ROUND_ID + "=?";
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
