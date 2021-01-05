package com.evontech.passwordgridapp.custom.mcustom;

import java.util.Observable;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public abstract class LetterGridDataAdapter extends Observable {

    public abstract int getColCount();
    public abstract int getRowCount();
    public abstract char getLetter(int row, int col);
    public abstract String getLetter(int row, int col, String type);

}
