package com.evontech.passwordgridapp.custom.grid;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.evontech.passwordgridapp.custom.common.SingleLiveEvent;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.WordDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataMapper;
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

public class GridViewModel extends ViewModel {

    static abstract class GameState {}
    static class Generating extends GameState {
        int rowCount;
        int colCount;
        String name;
        private Generating(int rowCount, int colCount, String name) {
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.name = name;
        }
    }
    static class Loading extends GameState {
        int gid;
        private Loading(int gid) {
            this.gid = gid;
        }
    }
    static class Finished extends GameState {
        GridData mGridData;
        private Finished(GridData gridData) {
            this.mGridData = gridData;
        }
    }
    static class Paused extends GameState {
        private Paused() {}
    }
    static class Playing extends GameState {
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
    private WordDataSource mWordDataSource;
    private GridDataCreator mGridDataCreator;
    private GridData mCurrentGridData;
    public GridData mCurrentLeftData;
    public GridData mCurrentTopData;

    private GameState mCurrentState = null;
    private MutableLiveData<GameState> mOnGameState;
    private SingleLiveEvent<AnswerResult> mOnAnswerResult;

    private boolean isUpperCase;
    private boolean isLowerCase;
    private boolean isNumbers;
    private boolean isSpecialCharacters;

   /* public GridPlayViewModel(){
        Log.d("GridPlayActivity ", "default constructor");
    }*/

    public GridViewModel(GridDataSource gridDataSource, WordDataSource wordDataSource) {
        mGridDataSource = gridDataSource;
        mWordDataSource = wordDataSource;
        mGridDataCreator = new GridDataCreator();
        resetLiveData();
    }

    private void resetLiveData() {
        mOnGameState = new MutableLiveData<>();
        mOnAnswerResult = new SingleLiveEvent<>();
    }

    public void stopGame() {
        setGameState(new Finished(null));
        mCurrentGridData = null;
        //resetLiveData();
    }

    public void pauseGame() {
        setGameState(new Paused());
    }

    public void resumeGame() {
        if (mCurrentState instanceof Paused) {
            setGameState(new Playing(mCurrentGridData));
        }
    }

    public void loadGameRound(int gid) {
        if (!(mCurrentState instanceof Generating)) {
            setGameState(new Loading(gid));

            mGridDataSource.getGameData(gid, gameRound -> {
                mCurrentGridData = new GridDataMapper().map(gameRound);
                setGameState(new Playing(mCurrentGridData));
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
    public void generateNewGameRound(int rowCount, int colCount) {
        if (!(mCurrentState instanceof Generating)) {
            setGameState(new Generating(rowCount, colCount, "Play me"));

            Observable.create((ObservableOnSubscribe<GridData>) emitter -> {
                List<Word> wordList = mWordDataSource.getWords();
                Log.d("wordList ", wordList.size()+"");
                GridDataCreator.setGridGenerationCriteria(isUpperCase, isLowerCase, isNumbers, isSpecialCharacters);
                GridData gr = mGridDataCreator.newGridData(wordList, rowCount, colCount, "Play me");
                List<Word> leftWordList = new ArrayList<Word>();
                List<Word> topWordList = new ArrayList<Word>();
                mCurrentLeftData = mGridDataCreator.newGridData(leftWordList, rowCount, 1, "Left Borders");
                mCurrentTopData = mGridDataCreator.newGridData(topWordList, 1, colCount, "Top Borders");
                long gid = mGridDataSource.saveGameData(new GridDataMapper().revMap(gr));
                gr.setId((int) gid);
                emitter.onNext(gr);
                emitter.onComplete();
            }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gameRound -> {
                        mCurrentGridData = gameRound;
                        Log.d("gridId ", mCurrentGridData.getId()+"");
                        setGameState(new Playing(mCurrentGridData));
                    });
        }
    }

    public void answerWord(String answerStr, UsedWord.AnswerLine answerLine, boolean reverseMatching) {   //helpful while saving password after selection
        UsedWord correctWord = mCurrentGridData.markWordAsAnswered(answerStr, answerLine, reverseMatching); //for loading same grid with password in future

        boolean correct = correctWord != null;
        mOnAnswerResult.setValue(new AnswerResult(correct, correctWord != null ? correctWord.getId() : -1));
        if (correct) {
            mGridDataSource.markWordAsAnswered(correctWord);
            if (mCurrentGridData.isFinished()) {
                setGameState(new Finished(mCurrentGridData));
            }
        }
    }

    public LiveData<GameState> getOnGameState() {
        return mOnGameState;
    }

    public LiveData<AnswerResult> getOnAnswerResult() {
        return mOnAnswerResult;
    }

    private void setGameState(GameState state) {
        mCurrentState = state;
        mOnGameState.setValue(mCurrentState);
    }
}
