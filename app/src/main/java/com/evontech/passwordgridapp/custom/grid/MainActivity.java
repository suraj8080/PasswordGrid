package com.evontech.passwordgridapp.custom.grid;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.common.GridIndex;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.mcustom.LetterBoard;
import com.evontech.passwordgridapp.custom.mcustom.StreakView;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.Word;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FullscreenActivity {

    private TableLayout table;
    private static int TABLE_WIDTH = 6;
    private static int TABLE_HEIGHT = 6;

    @BindView(R.id.letter_board_border_top) LetterBoard mLetterBoardBorderTop;
    @BindView(R.id.letter_board_border_left) LetterBoard mLetterBoardBorderLeft;

    @BindView(R.id.letter_board) LetterBoard mLetterBoard;
    @BindView(R.id.text_sel_layout)
    View mTextSelLayout;
    @BindView(R.id.text_selection)
    TextView mTextSelection;
    @BindColor(R.color.gray) int mGrayColor;

    private ArrayLetterGridDataAdapter mLetterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);


        //table = (TableLayout) findViewById(R.id.table_layout);
        Button btnGenerateGrid =(Button) findViewById(R.id.btnGenerateGrid);
        AppCompatEditText etPassword = (AppCompatEditText) findViewById(R.id.etPassword);
        btnGenerateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, GridCriteriaActivity.class));
                }
                else{
                   // generateGrid(Integer.parseInt(etPassword.getText().toString()));
                    resetGrid();
                    setupCustomGrid(Integer.parseInt(etPassword.getText().toString()));
                }
            }
        });

        //mLetterBoard.getStreakView().setEnableOverrideStreakLineColor(getPreferences().grayscale());
        mLetterBoard.getStreakView().setOverrideStreakLineColor(mGrayColor);
        mLetterBoard.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                streakLine.setColor(Util.getRandomColorWithAlpha(170));
                mTextSelLayout.setVisibility(View.VISIBLE);
                mTextSelection.setText(str);
                GridIndex idx = streakLine.getStartIndex();
                Log.d("onTouchBegin ", "str " +str + " row "+idx.row +" col "+idx.col);
            }

            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {
                if (str.isEmpty()) {
                    mTextSelection.setText("...");
                } else {
                    mTextSelection.setText(str);
                }
            }

            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                //mViewModel.answerWord(str, STREAK_LINE_MAPPER.revMap(streakLine), getPreferences().reverseMatching());
               // mTextSelLayout.setVisibility(View.GONE);
                mTextSelection.setText(str);
            }
        });

        mLetterBoard.getGridLineBackground().setVisibility(View.VISIBLE);
       // mLetterBoard.getStreakView().setSnapToGrid(getPreferences().getSnapToGrid());


        /*table.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TableRow row = (TableRow)table.getChildAt(0);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float startX = motionEvent.getX();
                        float startY = motionEvent.getY();
                        Log.d("DOWN", "DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float endX = motionEvent.getX();
                        float endY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                         endX = motionEvent.getX();
                         endY = motionEvent.getY();
                        Log.d("UP", "UP " + table.getChildCount());
                        break;
                }
                return true;

            }
        });*/
    }

    /*private void generateGrid(int length){
        table.removeAllViews();
// Populate the table with stuff
        TABLE_HEIGHT = length;
        TABLE_WIDTH = length;

        for (int y = 0; y < TABLE_HEIGHT; y++) {
            final int row = y;
            TableRow r = new TableRow(this);
            table.addView(r);
            for (int x = 0; x < TABLE_WIDTH; x++) {
                final int col = x;

                TableRow.LayoutParams tr = new TableRow.LayoutParams(130, 130);
                //table.setWeightSum(12.0f);
                //tr.weight = 0;
                Button b = new Button(this);
                b.setLayoutParams(tr);
                b.setText(row+" "+col);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),
                                "You clicked (" + row + "," + col + ")",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                r.addView(b);
            }
        }
    }*/



    private GridData gridData;
    private void setupCustomGrid(int gridLength){
        mLetterBoard.setVisibility(View.VISIBLE);
        List<Word> wordList = new ArrayList<>();
        for(int i=0;i<gridLength*10;i++) {
            wordList.add(new Word(i, String.valueOf(i)));
        }

        gridData =new GridDataCreator().newGridData(wordList, gridLength, gridLength, "Play me");
        if (mLetterAdapter == null) {
            mLetterAdapter = new ArrayLetterGridDataAdapter(gridData.getGrid().getArray());
            mLetterBoard.setDataAdapter(mLetterAdapter, "");
        }else {
            mLetterAdapter.setGrid(gridData.getGrid().getArray());
        }
        //tryScale("letterBoard");
        new Handler().postDelayed(() -> tryScale("letterBoard"), 300);

        setBorderGrids(gridLength);
    }

    private ArrayLetterGridDataAdapter mLetterAdapterTop;
    private ArrayLetterGridDataAdapter mLetterAdapterLeft;
    private GridData topBorderData;
    private GridData leftBorderData;
    private void setBorderGrids(int gridLength){
        mLetterBoardBorderTop.setVisibility(View.VISIBLE);
        mLetterBoardBorderLeft.setVisibility(View.VISIBLE);
        mLetterBoardBorderTop.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                GridIndex idx = streakLine.getStartIndex();
                Log.d("onTouchBegin TopBorder: ", "str " +str + " row "+idx.row +" col "+idx.col);
            }

            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {

            }

            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {

            }
        });


        //mLetterBoardBorderTop.getStreakView().setEnableOverrideStreakLineColor(getPreferences().getSnapToGrid());
        mLetterBoardBorderTop.getStreakView().setOverrideStreakLineColor(mGrayColor);
        //mLetterBoardBorderLeft.getStreakView().setEnableOverrideStreakLineColor(getPreferences().getSnapToGrid());
        mLetterBoardBorderLeft.getStreakView().setOverrideStreakLineColor(mGrayColor);

        //for top borders...
        List<Word> topList = new ArrayList<>();
        for(int i=0;i<gridLength+1;i++) {
            //if(i==0) topList.add(new Word(i, " "));
            topList.add(new Word(i, String.valueOf(i)));
        }
        topBorderData = new GridDataCreator().newGridData(topList, 1, gridLength, "Top Borders");
        if (mLetterAdapterTop == null) {
            mLetterAdapterTop = new ArrayLetterGridDataAdapter(topBorderData.getGrid().getArray());
            mLetterBoardBorderTop.setDataAdapter(mLetterAdapterTop,"");
        }else {
            mLetterAdapterTop.setGrid(topBorderData.getGrid().getArray());
        }
        new Handler().postDelayed(() -> tryScale("topBoard"), 300);
        //tryScale("topBoard");
        //for left borders...
        List<Word> leftList = new ArrayList<>();
        for(int i=0;i<gridLength;i++) {
            leftList.add(new Word(i, String.valueOf(i)));
        }
        leftBorderData = new GridDataCreator().newGridData(leftList, gridLength, 1, "Left Borders");
        if (mLetterAdapterLeft == null) {
            mLetterAdapterLeft = new ArrayLetterGridDataAdapter(leftBorderData.getGrid().getArray());
            mLetterBoardBorderLeft.setDataAdapter(mLetterAdapterLeft,"");
        }else {
            mLetterAdapterLeft.setGrid(leftBorderData.getGrid().getArray());
        }
        //tryScale("leftBoard");
        new Handler().postDelayed(() -> tryScale("leftBoard"), 300);
    }

    private void resetGrid(){
        if(mLetterAdapter!=null){ //reset
            gridData = null;
            mLetterAdapter.setGrid(null);
            mLetterBoard.removeAllStreakLine();
            mTextSelection.setText("");
            mTextSelLayout.setVisibility(View.GONE);

            topBorderData = null;
            mLetterAdapterTop.setGrid(null);
            mLetterBoardBorderTop.removeAllStreakLine();

            leftBorderData = null;
            mLetterAdapterLeft.setGrid(null);
            mLetterBoardBorderLeft.removeAllStreakLine();
        }
    }


    private void tryScale(String type) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int boardWidth = 0;
        int screenWidth = 0;

        if(type.equals("letterBoard")) {
            boardWidth = mLetterBoard.getWidth();
            screenWidth = metrics.widthPixels - (boardWidth/mLetterBoard.getGridColCount()) - 50;
            Log.d("boardWidth", boardWidth+"");
        }else if(type.equals("topBoard")){
            boardWidth = mLetterBoardBorderTop.getWidth();
            screenWidth = metrics.widthPixels  - (boardWidth/mLetterBoardBorderTop.getGridColCount()) - 50;
            Log.d("TopboardWidth", boardWidth+"");
        }else { //left
            boardWidth = mLetterBoardBorderLeft.getWidth();
            screenWidth = metrics.widthPixels  - (mLetterBoard.getWidth()) + 50;
            Log.d("LeftboardWidth", boardWidth+"");
        }

        if ( boardWidth > screenWidth) {
            float scale = (float)screenWidth / (float)boardWidth;
            if(type.equals("letterBoard")) {
                Log.d("screenWidth ", screenWidth+"");
                Log.d("boardWidth ", boardWidth+"");
                Log.d("scale ", scale+"");
                mLetterBoard.scale(scale, scale);
            }
            else if(type.equals("topBoard")){
                Log.d("screenWidth ", screenWidth+"");
                Log.d("boardWidth ", boardWidth+"");
                Log.d("scale ", scale+"");
                mLetterBoardBorderTop.scale(scale, scale);
            }

            else {
                Log.d("screenWidth ", screenWidth+"");
                Log.d("boardWidth ", boardWidth+"");
                Log.d("scale ", scale+"");
                mLetterBoardBorderLeft.scale(scale, scale);
            }
//            mLetterBoard.animate()
//                    .scaleX(scale)
//                    .scaleY(scale)
//                    .setDuration(400)
//                    .setInterpolator(new DecelerateInterpolator())
//                    .start();
        }
    }


}
