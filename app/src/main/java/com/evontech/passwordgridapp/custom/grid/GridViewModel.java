package com.evontech.passwordgridapp.custom.grid;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.evontech.passwordgridapp.custom.common.SingleLiveEvent;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataMapper;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;
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
    static class Loaded extends GridState {
        GridData mGridData;
        private Loaded(GridData gridData) {
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
    private UserAccount userAccount;

    private GridState mCurrentState = null;
    private MutableLiveData<GridState> mOnGridState;
    private SingleLiveEvent<AnswerResult> mOnAnswerResult;

    private String chosenOption;
    private String selectedTypedWord;
    private boolean isUpperCase;
    private boolean isLowerCase;
    private boolean isNumbers;
    private boolean isSpecialCharacters;

    public GridViewModel(GridDataSource gridDataSource) {
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
            setGridState(new Loaded(mCurrentGridData));
        }
    }

    public void loadGridRound(int gid) {
        if (!(mCurrentState instanceof Generating)) {
            setGridState(new Loading(gid));

            mGridDataSource.getGridData(gid, gridRound -> {
                mCurrentGridData = new GridDataMapper().map(gridRound);
                Log.d("SelectionCriteria ", mCurrentGridData.getmSelectionCriteria());
                Log.d("ChosenOption ", mCurrentGridData.getmChosenOption());
                //..............Set generation criteria in db according to grid generation criteria and chosen option........
                restoreGenerationCriteria(mCurrentGridData);
                GridDataCreator.setGridGenerationCriteria(isUpperCase, isLowerCase, isNumbers, isSpecialCharacters);
                List<Word> leftWordList = new ArrayList<Word>();
                List<Word> topWordList = new ArrayList<Word>();
                mCurrentLeftData = mGridDataCreator.newGridData(leftWordList, mCurrentGridData.getGrid().getRowCount(), 1, "Left Borders");
                mCurrentTopData = mGridDataCreator.newGridData(topWordList, 1, mCurrentGridData.getGrid().getColCount(), "Top Borders");

                setGridState(new Loaded(mCurrentGridData));
            });
        }
    }


    public void setGridGenerationCriteria(boolean isUpperCase, boolean isLowerCase, boolean isNumbers, boolean isSpecialCharacters){
            this.isUpperCase = isUpperCase;
            this.isLowerCase = isLowerCase;
            this.isNumbers = isNumbers;
            this.isSpecialCharacters = isSpecialCharacters;
    }

    private void restoreGenerationCriteria(GridData gridData){
        String generationCriteria = gridData.getmSelectionCriteria();
        if(generationCriteria.contains("isUpperCase")) isUpperCase = true;
        else isUpperCase = false;
        if(generationCriteria.contains("isLowerCase")) isLowerCase = true;
        else isLowerCase = false;
        if(generationCriteria.contains("isNumbers")) isNumbers = true;
        else isNumbers = false;
        if(generationCriteria.contains("isSpecialCharacters")) isSpecialCharacters = true;
        else isSpecialCharacters = false;
    }

    public void setGridChosenOption(String chosenOption){
        this.chosenOption = chosenOption;
    }

    public void setSelectedTypedWord(String selectedTypedWord) {
        this.selectedTypedWord = selectedTypedWord;
        Log.d("selectedTypedWord ", selectedTypedWord);
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
                if(userAccount!=null)
                Log.d("AccountGridId Here...", userAccount.getAccountGridId()+"");
                if(userAccount!=null && userAccount.getAccountGridId()>0) gr.setId(userAccount.getAccountGridId());  //great...
                gr.setmSelectionCriteria(getSelectionCriteria());
                gr.setmChosenOption(chosenOption);
                gr.setmSelectedTypedWord(selectedTypedWord);
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
                        setGridState(new Loaded(mCurrentGridData));
                    });
        }
    }

    public void updateGridData(){
        mCurrentGridData.setmSelectionCriteria(getSelectionCriteria());
        mCurrentGridData.setmChosenOption(chosenOption);
        if(!TextUtils.isEmpty(selectedTypedWord)) mCurrentGridData.setmSelectedTypedWord(selectedTypedWord);
        mGridDataSource.saveGridData(new GridDataMapper().revMap(mCurrentGridData));
    }

    private String getSelectionCriteria(){
        String selectionCriteria = "";
        if(isUpperCase) selectionCriteria = selectionCriteria.concat("isUpperCase");
        if(isLowerCase) selectionCriteria = selectionCriteria.concat("isLowerCase");
        if(isNumbers) selectionCriteria = selectionCriteria.concat("isNumbers");
        if(isSpecialCharacters) selectionCriteria = selectionCriteria.concat("isSpecialCharacters");
        return selectionCriteria;
    }

    public void removeAllStreakLines(){
        mGridDataSource.deleteAllLines(mCurrentGridData.getId());
    }

    public void answerWord(int index, String answerStr, UsedWord.AnswerLine answerLine, boolean reverseMatching) {   //helpful while saving password after selection
        UsedWord correctWord = mCurrentGridData.markWordAsAnswered(answerStr, answerLine, reverseMatching); //for loading same grid with password in future
        correctWord.setId(mCurrentGridData.getId());

        //boolean correct = correctWord != null;
        //mOnAnswerResult.setValue(new AnswerResult(correct, correctWord != null ? correctWord.getId() : -1));
        //if (correct) {
            mGridDataSource.markWordAsAnswered(index, correctWord);
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

    public void updateAccountInfo(UserAccount userAccount){
        mGridDataSource.updateAccountInfo(userAccount);
        this.userAccount = userAccount;
    }
}
