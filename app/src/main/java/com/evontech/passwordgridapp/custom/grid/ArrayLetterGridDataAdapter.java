package com.evontech.passwordgridapp.custom.grid;


import android.util.Log;

import com.evontech.passwordgridapp.custom.mcustom.LetterGridDataAdapter;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class ArrayLetterGridDataAdapter extends LetterGridDataAdapter {

    private char mGrid[][];
    private String mLeftGrid[][];

    public ArrayLetterGridDataAdapter(String grid[][]) {
        mLeftGrid = grid;
        //Log.d("mLeftGrid ", mLeftGrid.toString());
    }
    public String[][] getLeftGrid() {
        return mLeftGrid;
    }
    public void setLeftGrid(String [][]grid) {
        if (grid != null && grid != mLeftGrid) {
            mLeftGrid = grid;
            setChanged();
            notifyObservers();
        }else {
            mLeftGrid =null;
            setChanged();
            notifyObservers();
        }
    }


    public ArrayLetterGridDataAdapter(char grid[][]) {
        mGrid = grid;
        //Log.d("mGrid ", mGrid.toString());
        /*for (int i=0;i<grid.length;i++){
            for (int j=0;j<grid[i].length;j++){
                Log.d("mGrid ", grid[i][j] +"");
            }
        }*/
    }
    public char[][] getGrid() {
        return mGrid;
    }
    public void setGrid(char[][] grid) {
        if (grid != null && grid != mGrid) {
           // Log.d("mGrid ", grid.toString());
            mGrid = grid;
            setChanged();
            notifyObservers();
        }else {
            mGrid =null;
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public int getColCount() {
        if(mGrid!=null)
        return mGrid[0].length;
        else if(mLeftGrid!=null)
        return mLeftGrid[0].length;
        else return 0;
    }

    @Override
    public int getRowCount() {
        if(mGrid!=null)
        return mGrid.length;
        else if(mLeftGrid!=null)
        return mLeftGrid.length;
        else return 0;
    }

    @Override
    public char getLetter(int row, int col) {
        if(mGrid!=null)
        return mGrid[row][col];
        else return 0;
    }

    @Override
    public String getLetter(int row, int col, String type) {
        if(mLeftGrid!=null)
            return mLeftGrid[row][col];
        else return "0";
    }


}
