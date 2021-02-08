package com.evontech.passwordgridapp.custom.accounts;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evontech.passwordgridapp.custom.common.SingleLiveEvent;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataMapper;
import com.evontech.passwordgridapp.custom.grid.GridDataCreator;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.Word;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class AccountsViewModel extends ViewModel {

    static abstract class GridState {}
    static class Generating extends GridState {
        int rowCount;
        int colCount;
        String name;
        private Generating(int rowCount, int colCount, String name) {
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.name = name;
        }
    }
    static class Loading extends GridState {
        int gid;
        private Loading(int gid) {
            this.gid = gid;
        }
    }
    static class Finished extends GridState {
        GridData mGridData;
        private Finished(GridData gridData) {
            this.mGridData = gridData;
        }
    }
    static class Paused extends GridState {
        private Paused() {}
    }
    static class Playing extends GridState {
        GridData mGridData;
        private Playing(GridData gridData) {
            this.mGridData = gridData;
        }
    }

    static class AnswerResult {
        public boolean correct;
        public int usedWordId;
        AnswerResult(boolean correct, int usedWordId) {
            this.correct = correct;
            this.usedWordId = usedWordId;
        }
    }

    private GridDataSource mGridDataSource;
    private GridDataCreator mGridDataCreator;
    private GridData mCurrentGridData;
    public GridData mCurrentLeftData;
    public GridData mCurrentTopData;

    private GridState mCurrentState = null;
    private MutableLiveData<GridState> mOnGridState;
    private SingleLiveEvent<AnswerResult> mOnAnswerResult;

    private boolean isUpperCase;
    private boolean isLowerCase;
    private boolean isNumbers;
    private boolean isSpecialCharacters;

    public AccountsViewModel(GridDataSource gridDataSource) {
        mGridDataSource = gridDataSource;
        mGridDataCreator = new GridDataCreator();
        resetLiveData();
    }

    private void resetLiveData() {
        mOnGridState = new MutableLiveData<>();
        mOnAnswerResult = new SingleLiveEvent<>();
    }

    public void stopGrid() {
        setGridState(new Finished(null));
        mCurrentGridData = null;
        //resetLiveData();
    }

    public void pauseGrid() {
        setGridState(new Paused());
    }

    public void resumeGrid() {
        if (mCurrentState instanceof Paused) {
            setGridState(new Playing(mCurrentGridData));
        }
    }

    public void loadGridRound(int gid) {
        if (!(mCurrentState instanceof Generating)) {
            setGridState(new Loading(gid));

            mGridDataSource.getGridData(gid, gridRound -> { // %3t%X$80ZR
                mCurrentGridData = new GridDataMapper().map(gridRound);

                GridDataCreator.setGridGenerationCriteria(isUpperCase, isLowerCase, isNumbers, isSpecialCharacters);
                List<Word> leftWordList = new ArrayList<Word>();
                List<Word> topWordList = new ArrayList<Word>();
                mCurrentLeftData = mGridDataCreator.newGridData(leftWordList, mCurrentGridData.getGrid().getRowCount(), 1, "Left Borders");
                mCurrentTopData = mGridDataCreator.newGridData(topWordList, 1, mCurrentGridData.getGrid().getRowCount(), "Top Borders");

                setGridState(new Playing(mCurrentGridData));
            });
        }
    }


    public void setGridGenerationCriteria(boolean isUpperCase, boolean isLowerCase, boolean isNumbers, boolean isSpecialCharacters){
            this.isUpperCase = isUpperCase;
            this.isLowerCase = isLowerCase;
            this.isNumbers = isNumbers;
            this.isSpecialCharacters = isSpecialCharacters;
    }

    @SuppressLint("CheckResult")
    public void generateNewGridRound(int rowCount, int colCount) {
        if (!(mCurrentState instanceof Generating)) {
            setGridState(new Generating(rowCount, colCount, "Play me"));

            Observable.create((ObservableOnSubscribe<GridData>) emitter -> {
                List<Word> wordList = new ArrayList<>(); //mWordDataSource.getWords();
                Log.d("wordList ", wordList.size()+"");
                GridDataCreator.setGridGenerationCriteria(isUpperCase, isLowerCase, isNumbers, isSpecialCharacters);
                GridData gr = mGridDataCreator.newGridData(wordList, rowCount, colCount, "Play me");
                List<Word> leftWordList = new ArrayList<Word>();
                List<Word> topWordList = new ArrayList<Word>();
                long gid = mGridDataSource.saveGridData(new GridDataMapper().revMap(gr));
                mCurrentLeftData = mGridDataCreator.newGridData(leftWordList, rowCount, 1, "Left Borders");
                mCurrentTopData = mGridDataCreator.newGridData(topWordList, 1, colCount, "Top Borders");
                gr.setId((int) gid);
                emitter.onNext(gr);
                emitter.onComplete();
            }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gridRound -> {
                        mCurrentGridData = gridRound;
                        Log.d("gridId ", mCurrentGridData.getId()+"");
                        setGridState(new Playing(mCurrentGridData));
                    });
        }
    }

    public void updateGridData(){
        mGridDataSource.saveGridData(new GridDataMapper().revMap(mCurrentGridData));
    }

    public void removeAllStreakLines(){
        mGridDataSource.deleteAllLines(mCurrentGridData.getId());
    }

    public void answerWord(String answerStr, UsedWord.AnswerLine answerLine, boolean reverseMatching) {   //helpful while saving password after selection
        UsedWord correctWord = mCurrentGridData.markWordAsAnswered(answerStr, answerLine, reverseMatching); //for loading same grid with password in future
        correctWord.setId(mCurrentGridData.getId());

        //boolean correct = correctWord != null;
        //mOnAnswerResult.setValue(new AnswerResult(correct, correctWord != null ? correctWord.getId() : -1));
        //if (correct) {
            mGridDataSource.markWordAsAnswered(correctWord);
           /* if (mCurrentGridData.isFinished()) {
                setGridState(new Finished(mCurrentGridData));
            }*/
        //}
    }

    public LiveData<GridState> getOnGridState() {
        return mOnGridState;
    }

    public LiveData<AnswerResult> getOnAnswerResult() {
        return mOnAnswerResult;
    }

    private void setGridState(GridState state) {
        mCurrentState = state;
        mOnGridState.setValue(mCurrentState);
    }
}
