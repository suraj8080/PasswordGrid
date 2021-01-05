package com.evontech.passwordgridapp.custom.grid;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;
import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.common.Direction;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.mcustom.LetterBoard;
import com.evontech.passwordgridapp.custom.mcustom.StreakView;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridActivity extends FullscreenActivity {

    public static final String EXTRA_GAME_ROUND_ID =
            "com.evontech.passwordgridapp.custom.gridplay.GridActivity.ID";
    public static final String EXTRA_ROW_COUNT =
            "com.evontech.passwordgridapp.custom.gridplay.GridActivity.ROW";
    public static final String EXTRA_COL_COUNT =
            "com.evontech.passwordgridapp.custom.gridplay.GridActivity.COL";

    private static final StreakLineMapper STREAK_LINE_MAPPER = new StreakLineMapper();


    @Inject ViewModelFactory mViewModelFactory;
    private GridViewModel mViewModel;

    @BindView(R.id.btnReset)
    AppCompatButton mButtonReset;

    @BindView(R.id.top_letter_board) LetterBoard mLetterBoardTop;
    @BindView(R.id.letter_board) LetterBoard mLetterBoard;
    @BindView(R.id.left_letter_board) LetterBoard mLetterBoardLeft;
    @BindView(R.id.verticalscoll_left_center)
    CustomScrollView myScrollView;
    @BindView(R.id.text_sel_layout)
    View mTextSelLayout;
    @BindView(R.id.text_selection)
    TextView mTextSelection;
    @BindView(R.id.text_chooseFromBorder)
    TextView mTextFromBorder;

    @BindView(R.id.loading)
    View mLoading;
    @BindView(R.id.loadingText)
    TextView mLoadingText;
    @BindView(R.id.content_layout)
    View mContentLayout;

    @BindColor(R.color.gray) int mGrayColor;

    private int rowCount;
    private int colCount;
    private ArrayLetterGridDataAdapter mLetterAdapter;
    private ArrayLetterGridDataAdapter mLetterLeftAdapter;
    private ArrayLetterGridDataAdapter mLetterTopAdapter;

    private int topBorderStartRow;
    private int topBorderStartCol;
    private int topBorderEndRow;
    private int topBorderEndtCol;

    private int leftBorderStartRow;
    private int leftBorderStartCol;
    private int leftBorderEndRow;
    private int leftBorderEndCol;

    private int mainBoardStartRow;
    private int mainBoardStartCol;
    private int mainBoardEndRow;
    private int mainBoardEndCol;
    private boolean isSingleCellSelected;
    private StringBuilder wordFromBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        ButterKnife.bind(this);
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);

        if(getPreferences().showWordFromBorder()){
            wordFromBorder = new StringBuilder();
            mTextFromBorder.setVisibility(View.VISIBLE);
        }

        //mLetterBoard.getStreakView().setEnableOverrideStreakLineColor(getPreferences().grayscale());
        mLetterBoard.getStreakView().setOverrideStreakLineColor(mGrayColor);
        mLetterBoard.getStreakView().setInteractive(true);
        mLetterBoard.getStreakView().setRememberStreakLine(true);
        mLetterBoard.getGridLineBackground().setVisibility(View.VISIBLE);
        mLetterBoard.setGridLineVisibility(true);
        mLetterBoard.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                streakLine.setColor(Util.getRandomColorWithAlpha(170));
                mTextSelLayout.setVisibility(View.VISIBLE);
                mLetterBoardLeft.removeAllStreakLine();
                mLetterBoardTop.removeAllStreakLine();

                int row = streakLine.getStartIndex().row;
                int col = streakLine.getStartIndex().col;
                Log.d("GridActivity ", "start row: "+row +" start col: "+col);
                mTextSelection.setText(str);
            }
            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {
                myScrollView.setEnableScrolling(false);
                if (str.isEmpty()) {
                    mTextSelection.setText("...");
                } else {
                    mTextSelection.setText(str);
                }
            }
            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                int row = streakLine.getEndIndex().row;
                int col = streakLine.getEndIndex().col;
                Log.d("GridActivity ", "end row: "+row +" end col: "+col);
                mViewModel.answerWord(str, STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                if (str.isEmpty()) {
                    mTextSelection.setText("");
                    mLetterBoard.popStreakLine();
                }else {
                    mTextSelection.setText(str);
                    Log.d("passwordEntropyBits ", calculateEntropyBits(str.length(),getUsedSymbolLength()) +"");
                    isSingleCellSelected = false;
                }

                if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col && !isSingleCellSelected) {
                    mainBoardStartRow = row;
                    mainBoardStartCol = col;
                    isSingleCellSelected = true;
                    mLetterBoard.addStreakLine(streakLine);
                }else if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col && isSingleCellSelected){
                    mainBoardEndRow = row;
                    mainBoardEndCol = col;
                    isSingleCellSelected = false;
                    StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                    newStreakLine.getStartIndex().set(mainBoardStartRow,mainBoardStartCol);
                    newStreakLine.getEndIndex().set(mainBoardEndRow,mainBoardEndCol);
                    mLetterBoard.removeAllStreakLine();
                    if(Direction.fromLine(newStreakLine.getStartIndex(), newStreakLine.getEndIndex()) != Direction.NONE){
                        String selectedPwd = Util.getStringInRange(mLetterAdapter, newStreakLine.getStartIndex(), newStreakLine.getEndIndex());
                        mLetterBoard.addStreakLine(newStreakLine);
                        mTextSelection.setText(selectedPwd);
                    }
                }
                myScrollView.setEnableScrolling(true);
            }
        });


        mLetterBoardTop.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                topBorderStartRow = streakLine.getStartIndex().row;
                topBorderStartCol = streakLine.getStartIndex().col;
                isSingleCellSelected = false;

                Log.d("GridActivity topBorder ", "start row: "+topBorderStartRow +" start col: "+topBorderStartCol);
            }
            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {

            }
            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                int row = streakLine.getEndIndex().row;
                int col = streakLine.getEndIndex().col;
                if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col) {
                    topBorderEndRow = row;
                    topBorderEndtCol = col;
                    mLetterBoardLeft.removeAllStreakLine();
                    Log.d("GridActivity topBorder ", "end row: "+topBorderEndRow +" end col: "+topBorderEndtCol);
                    Log.d("wordFromBorder ", str);
                    if(getPreferences().showWordFromBorder()){
                        char[][] topborder = mLetterTopAdapter.getGrid();
                        char [][] mainboard = mLetterAdapter.getGrid();
                        wordFromBorder = wordFromBorder.append(topborder[row][col]);
                        mTextFromBorder.setText(wordFromBorder.toString());

                        StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                        int occurrence = (int) wordFromBorder.chars().filter(ch -> ch == topborder[row][col]).count()-1;
                        newStreakLine.getStartIndex().set(occurrence, topBorderStartCol);
                        newStreakLine.getEndIndex().set(occurrence, col);
                        mLetterBoard.addStreakLine(newStreakLine);
                        StringBuilder strPwd = new StringBuilder(mTextSelection.getText().toString());
                        mTextSelection.setText(strPwd.append(mainboard[occurrence][col]).toString());
                    }else {
                        StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                        newStreakLine.getStartIndex().set(topBorderStartRow, topBorderStartCol);
                        newStreakLine.getEndIndex().set(getPreferences().getGridCol(), col);
                        mLetterBoard.removeAllStreakLine();
                        // mLetterBoard.addStreakLine(newStreakLine);
                    }

                }
                else {
                    mLetterBoardTop.removeAllStreakLine();
                    topBorderEndRow = 0;
                    topBorderEndtCol = 0;
                }
            }
        });

        mLetterBoardLeft.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                leftBorderStartRow = streakLine.getStartIndex().row;
                leftBorderStartCol = streakLine.getStartIndex().col;
                isSingleCellSelected = false;
                Log.d("GridActivity leftBorder ", "start row: "+leftBorderStartRow +" start col: "+leftBorderStartCol);
            }
            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {

            }
            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                int row = streakLine.getEndIndex().row;
                int col = streakLine.getEndIndex().col;
                if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col) {
                    leftBorderEndRow = row;
                    leftBorderEndCol = col;
                    mLetterBoardTop.removeAllStreakLine();
                    Log.d("GridActivity leftBorder ", "end row: "+leftBorderEndRow +" end col: "+leftBorderEndCol);
                    StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                    newStreakLine.getStartIndex().set(leftBorderStartRow,leftBorderStartCol);
                    newStreakLine.getEndIndex().set(row,getPreferences().getGridCol());
                    mLetterBoard.removeAllStreakLine();
                    //mLetterBoard.addStreakLine(newStreakLine);
                }
                else {
                    mLetterBoardLeft.removeAllStreakLine();
                    leftBorderEndRow = 0;
                    leftBorderEndCol = 0;
                }

            }
        });


        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GridViewModel.class);
        mViewModel.getOnGameState().observe(this, this::onGameStateChanged);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_GAME_ROUND_ID)) {
                int gid = extras.getInt(EXTRA_GAME_ROUND_ID);
                mViewModel.loadGameRound(gid);
            } else {
                rowCount = extras.getInt(EXTRA_ROW_COUNT);
                colCount = extras.getInt(EXTRA_COL_COUNT);
                Preferences preferences = getPreferences();
                mViewModel.setGridGenerationCriteria(preferences.showUpperCharacters(), preferences.showLowerCharacters(),preferences.showNumberCharacters(), preferences.showSpecialCharacters());
                mViewModel.generateNewGameRound(rowCount, colCount);
            }
        }

        /*if (!getPreferences().showGridLine()) {
            mLetterBoard.getGridLineBackground().setVisibility(View.INVISIBLE);
        } else {
            mLetterBoard.getGridLineBackground().setVisibility(View.VISIBLE);
        }*/

        //mLetterBoard.getStreakView().setSnapToGrid(getPreferences().getSnapToGrid());

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mViewModel.stopGame();
                mTextSelection.setText("");
                //mLetterAdapter.setGrid(null);
                mLetterBoard.removeAllStreakLine();
                mLetterBoardLeft.removeAllStreakLine();
                mLetterBoardTop.removeAllStreakLine();
                isSingleCellSelected = false;
                mTextFromBorder.setText("");
                wordFromBorder = new StringBuilder();
                //mViewModel.generateNewGameRound(rowCount, colCount);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.resumeGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.pauseGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopGame();
    }

    private void onGameStateChanged(GridViewModel.GameState gameState) {
        showLoading(false, null);
        if (gameState instanceof GridViewModel.Generating) {
            GridViewModel.Generating state = (GridViewModel.Generating) gameState;
            String text = "Generating " + state.rowCount + "x" + state.colCount + " grid";
            showLoading(true, text);
        } else if (gameState instanceof GridViewModel.Finished) {
          //  showFinishGame(((GridPlayViewModel.Finished) gameState).mGameData.getId());
        } else if (gameState instanceof GridViewModel.Paused) {

        } else if (gameState instanceof GridViewModel.Playing) {
            onGameRoundLoaded(((GridViewModel.Playing) gameState).mGridData);
        }
    }

    private void onGameRoundLoaded(GridData gridData) {
        if (gridData.isFinished()) {
            setGameAsAlreadyFinished();
        }

        doneLoadingContent();  //call it accordingly

        showLetterGrid(gridData.getGrid().getArray());
        mLetterBoard.setVisibility(View.VISIBLE);
        mLetterBoardTop.setVisibility(View.VISIBLE);
        mLetterBoardLeft.setVisibility(View.VISIBLE);

        //doneLoadingContent();
    }

    private boolean isInitialized;
    private void tryScale() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int boardWidth = mLetterBoard.getWidth();
        int screenWidth = metrics.widthPixels - (boardWidth/mLetterBoard.getGridColCount()) - (int) Util.convertDpToPx(this, 20);
        int topBorderLeftMargin = (boardWidth/mLetterBoard.getGridColCount()) + (int) Util.convertDpToPx(this, 10);
        int dynamicStreakWidth = (boardWidth/mLetterBoard.getGridColCount());
        mLetterBoard.setStreakWidth(dynamicStreakWidth);
        mLetterBoardTop.setStreakWidth(dynamicStreakWidth);
        mLetterBoardLeft.setStreakWidth(dynamicStreakWidth);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(topBorderLeftMargin, 0, 0, 0);
        //Log.d("topBorderLeftMargin ", ""+topBorderLeftMargin);
        mLetterBoardTop.setLayoutParams(params);

        if (boardWidth > screenWidth) {
            float scale = (float)screenWidth / (float)boardWidth;
            mLetterBoardLeft.scale(scale, scale);
            mLetterBoard.scale(scale, scale);
            mLetterBoardTop.scale(scale, scale);

            /*mLetterBoard.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(100)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();*/
        }
         if(!isInitialized) {
            isInitialized = true;
             new Handler().postDelayed(this::tryScale, 100);
        }
    }

    private void doneLoadingContent() {
        // call tryScale() on the next render frame
        new Handler().postDelayed(this::tryScale, 0);
    }

    private void showLoading(boolean enable, String text) {
        if (enable) {
            mLoading.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.VISIBLE);
            mContentLayout.setVisibility(View.GONE);
            mLoadingText.setText(text);
        } else {
            mLoading.setVisibility(View.GONE);
            mLoadingText.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showLetterGrid(char[][] grid) {
        if (mLetterAdapter == null) {
            mLetterAdapter = new ArrayLetterGridDataAdapter(grid);
            mLetterBoard.setDataAdapter(mLetterAdapter, "");
        } else {
            mLetterAdapter.setGrid(grid);
        }

       char[][] leftGrid = mViewModel.mCurrentLeftData.getGrid().getArray();
        String[][] leftStringGrid = new String[leftGrid.length][leftGrid[0].length];
        ArrayList<String> numValue = new ArrayList<String>();
        int numIndex = 0;
        for (int i =0;i<=rowCount;i++){
            numValue.add(String.valueOf(i));
        }

        //char[][] leftStringGrid = new char[][]{"10", "11"};
        for (int i=0;i<leftGrid.length;i++){
            for (int j =0;j<leftGrid[i].length;j++){
                leftStringGrid[i][j] = numValue.get(numIndex);
                //Log.d("leftGrid ", numValue.get(numIndex));
                numIndex ++;
            }
        }
        if (mLetterLeftAdapter == null) {
            //mLetterLeftAdapter = new ArrayLetterGridDataAdapter(leftGrid);
            mLetterLeftAdapter = new ArrayLetterGridDataAdapter(leftStringGrid);
            mLetterBoardLeft.setDataAdapter(mLetterLeftAdapter, "leftGrid");
        } else {
            mLetterLeftAdapter.setLeftGrid(leftStringGrid);
        }

        ArrayList<Character> alphabetValValue = new ArrayList<Character>();
        int alphabetIndex = 0;
        for(char c = 'A'; c <= 'Z'; ++c)
            alphabetValValue.add(c);
        char[][] topGrid = mViewModel.mCurrentTopData.getGrid().getArray();
        for (int i=0;i<topGrid.length;i++){
            for (int j=0;j<topGrid[i].length;j++){
                topGrid[i][j] = alphabetValValue.get(alphabetIndex);
                alphabetIndex++;
            }
        }
        if (mLetterTopAdapter == null) {
            mLetterTopAdapter = new ArrayLetterGridDataAdapter(topGrid);
            mLetterBoardTop.setDataAdapter(mLetterTopAdapter, "");
        } else {
            mLetterTopAdapter.setGrid(topGrid);
        }
    }

    private void showFinishGame(int gameId) {
        /*Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(GameOverActivity.EXTRA_GAME_ROUND_ID, gameId);
        startActivity(intent);
        finish();*/
    }

    private void setGameAsAlreadyFinished() {
        mLetterBoard.getStreakView().setInteractive(false);
    }

    //
    private TextView createUsedWordTextView(UsedWord uw) {
        TextView tv = new TextView(this);
        tv.setPadding(10, 5, 10, 5);
        if (uw.isAnswered()) {
         //   if (getPreferences().grayscale()) {
                uw.getAnswerLine().color = mGrayColor;
        //    }
            tv.setBackgroundColor(uw.getAnswerLine().color);
            tv.setText(uw.getString());
            tv.setTextColor(Color.WHITE);
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mLetterBoard.addStreakLine(STREAK_LINE_MAPPER.map(uw.getAnswerLine()));
        }
        else {
            String str = uw.getString();
            if (uw.isMystery()) {
                int revealCount = uw.getRevealCount();
                String uwString = uw.getString();
                str = "";
                for (int i = 0; i < uwString.length(); i++) {
                    if (revealCount > 0) {
                        str += uwString.charAt(i);
                        revealCount--;
                    }
                    else {
                        str += " ?";
                    }
                }
            }
            tv.setText(str);
        }

        tv.setTag(uw);
        return tv;
    }

    //method to calculate the entropy of a random password:

    private double calculateEntropyBits(int passwordLength, int usedSymbolLength){
        Log.d("passwordLength "+passwordLength, "usedSymbolLength "+usedSymbolLength);
        return (double) (Math.log(Math.pow(usedSymbolLength, passwordLength)) / Math.log(2));
    }

    private int getUsedSymbolLength(){
        int length = 0;
        if(getPreferences().showUpperCharacters())
            length  = length + 26;
        if(getPreferences().showLowerCharacters())
            length  = length + 26;
        if(getPreferences().showNumberCharacters())
            length  = length + 10;
        if(getPreferences().showSpecialCharacters())
            length  = length + 32;

        return length;
    }
}
