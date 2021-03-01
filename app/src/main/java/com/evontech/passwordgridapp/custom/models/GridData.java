package com.evontech.passwordgridapp.custom.models;

import com.evontech.passwordgridapp.custom.common.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridData {

    private int mId;
    private String mName;
    private int mDuration;
    private Grid mGrid;
    private String mSelectionCriteria;
    private String mChosenOption;
    private String mSelectedTypedWord;
    private List<UsedWord> mUsedWords;

    public GridData() {
        this(0, "", 0, null, new ArrayList<>(), "", "", "");
    }

    public GridData(int id, String name, int duration, Grid grid, List<UsedWord> usedWords, String selectionCriteria, String chosenOption, String selectedTypedWord) {
        mId = id;
        mName = name;
        mDuration = duration;
        mGrid = grid;
        mUsedWords = usedWords;
        mSelectionCriteria = selectionCriteria;
        mChosenOption = chosenOption;
        mSelectedTypedWord = selectedTypedWord;
    }

    public String getmSelectedTypedWord() {
        return mSelectedTypedWord;
    }

    public void setmSelectedTypedWord(String mSelectedTypedWord) {
        this.mSelectedTypedWord = mSelectedTypedWord;
    }

    public String getmSelectionCriteria() {
        return mSelectionCriteria;
    }

    public void setmSelectionCriteria(String mSelectionCriteria) {
        this.mSelectionCriteria = mSelectionCriteria;
    }

    public String getmChosenOption() {
        return mChosenOption;
    }

    public void setmChosenOption(String mChosenOption) {
        this.mChosenOption = mChosenOption;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public Grid getGrid() {
        return mGrid;
    }

    public void setGrid(Grid grid) {
        mGrid = grid;
    }

    public List<UsedWord> getUsedWords() {
        return mUsedWords;
    }

    public UsedWord markWordAsAnswered(String word, UsedWord.AnswerLine answerLine, boolean enableReverse) {
        //String answerStrRev = Util.getReverseString(word);
       // for (UsedWord usedWord : mUsedWords) {

       //     if (usedWord.isAnswered()) continue;

        //    String currUsedWord = usedWord.getString();
       //     if (currUsedWord.equalsIgnoreCase(word) ||
       //             (currUsedWord.equalsIgnoreCase( answerStrRev ) && enableReverse)) {
                UsedWord usedWord = new UsedWord();
                usedWord.setString(word);
                usedWord.setAnswered(true);
                usedWord.setAnswerLine(answerLine);
                return usedWord;
            //}
      //  }
        //return null;
    }

    public int getAnsweredWordsCount() {
        int count = 0;
        for (UsedWord uw : mUsedWords) {
            if (uw.isAnswered()) count++;
        }
        return count;
    }

    public boolean isFinished() {
        return getAnsweredWordsCount() == mUsedWords.size();
    }

    public void addUsedWord(UsedWord usedWord) {
        mUsedWords.add(usedWord);
    }

    public void addUsedWords(List<UsedWord> usedWords) {
        mUsedWords.addAll(usedWords);
    }
}
