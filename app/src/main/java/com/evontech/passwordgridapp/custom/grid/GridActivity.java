package com.evontech.passwordgridapp.custom.grid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.common.Direction;
import com.evontech.passwordgridapp.custom.common.GridIndex;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.common.generator.StringListGridGenerator;
import com.evontech.passwordgridapp.custom.mcustom.LetterBoard;
import com.evontech.passwordgridapp.custom.mcustom.StreakView;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridActivity extends FullscreenActivity {

    public static final String EXTRA_GRID_ID = "com.evontech.passwordgridapp.custom.grid.GridActivity.ID";
    public static final String EXTRA_ROW_COUNT = "com.evontech.passwordgridapp.custom.grid.GridActivity.ROW";
    public static final String EXTRA_COL_COUNT = "com.evontech.passwordgridapp.custom.grid.GridActivity.COL";
    public static final int SETTING_REQUEST_CODE = 100;
    private static final StreakLineMapper STREAK_LINE_MAPPER = new StreakLineMapper();
    @Inject ViewModelFactory mViewModelFactory;
    GridViewModel mViewModel;
    @BindView(R.id.btnReset)
    AppCompatButton mButtonReset;
    @BindView(R.id.btnSave)
    AppCompatButton mButtonSave;
    @BindView(R.id.letter_board) LetterBoard mLetterBoard;
    @BindView(R.id.top_letter_board) LetterBoard mLetterBoardTop;
    @BindView(R.id.left_letter_board) LetterBoard mLetterBoardLeft;
    @BindView(R.id.verticalscoll_left_center)
    CustomScrollView myScrollView;
    @BindView(R.id.text_sel_layout)
    View mTextSelLayout;
    @BindView(R.id.text_selection)
    TextView mTextSelection;
    @BindView(R.id.text_chooseFromBorder)
    AppCompatEditText mTextFromBorder;
    @BindView(R.id.linear_TextTyping)
    LinearLayout linearTextTyping;
    @BindView(R.id.linear_TextSelection)
    LinearLayout linearTextSelection;
    @BindView(R.id.loading)
    View mLoading;
    @BindView(R.id.loadingText)
    TextView mLoadingText;
    @BindView(R.id.content_layout)
    View mContentLayout;
    @BindColor(R.color.gray) int mGrayColor;
    @BindView(R.id.iv_settings)
    ImageView iconSetting;
    @BindView(R.id.icon_autoGenerate)
    ImageView iconAutoGenerate;
    @BindView(R.id.indicator_red)
    View indicatorRed;
    @BindView(R.id.indicator_green)
    View indicatorGreen;
    @BindView(R.id.indicator_amber)
    View indicatorAmber;
    @BindView(R.id.grid_account_name)
    TextView grid_account_name;
    @BindView(R.id.grid_user_name)
    TextView grid_user_name;
    @BindView(R.id.iv_share)
    ImageView iv_share;

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
    private int selectedColor;
    private boolean isDefaultPasswordGenerated;
    private boolean isInitialized;
    private boolean isScaled;
    private int topBorderLeftMargin;
    private int typePasswordLength;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        ButterKnife.bind(this);
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        initPwdEditText();

        //mLetterBoard.getStreakView().setEnableOverrideStreakLineColor(getPreferences().grayscale());
        mLetterBoard.getStreakView().setOverrideStreakLineColor(mGrayColor);
        mLetterBoard.getStreakView().setInteractive(true);
        if(getPreferences().showGridPattern())
            mLetterBoard.getStreakView().setRememberStreakLine(true);
        if(getPreferences().selectedDragManually())
            mLetterBoard.getStreakView().setmDraggingManually(true);
        mLetterBoard.getGridLineBackground().setVisibility(View.VISIBLE);
        mLetterBoard.setGridLineVisibility(true);
        mLetterBoard.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                streakLine.setColor(Util.getRandomColorWithAlpha(170));
                mTextSelLayout.setVisibility(View.VISIBLE);
                mLetterBoardLeft.removeAllStreakLine();
                mLetterBoardTop.removeAllStreakLine();
                selectedColor = streakLine.getColor();

                int row = streakLine.getStartIndex().row;
                int col = streakLine.getStartIndex().col;
                Log.d("GridActivity ", "start row: "+row +" start col: "+col);
                if(getPreferences().showGridPattern() || getPreferences().showWordFromBorder() || getPreferences().selectedStartEndGrid() || getPreferences().selectedTypeManually()){ }
                else  mTextSelection.setText(str);
            }
            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {
                myScrollView.setEnableScrolling(false);
                if (str.isEmpty()) {
                    if(getPreferences().showGridPattern() || getPreferences().showWordFromBorder() || getPreferences().selectedStartEndGrid() || getPreferences().selectedTypeManually()){}
                    else
                        mTextSelection.setText("...");
                } else {
                    if(getPreferences().showGridPattern()  || getPreferences().showWordFromBorder() || getPreferences().selectedStartEndGrid() || getPreferences().selectedTypeManually()){}
                    else mTextSelection.setText(str);
                }
            }
            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                int row = streakLine.getEndIndex().row;
                int col = streakLine.getEndIndex().col;
                Log.d("GridActivity ", "end row: "+row +" end col: "+col);
                Log.d("GridActivity ", "str: "+str);
                Log.d("isSingleCellSelected ", ": "+isSingleCellSelected);
                if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col && !isSingleCellSelected) {
                    Log.d("inside ", "A");
                    mainBoardStartRow = row;
                    mainBoardStartCol = col;
                    if(getPreferences().showgridDirection()){
                        Log.d("inside ", "1");
                        selectedColor = streakLine.getColor();
                        if(!TextUtils.isEmpty(getPreferences().getSelectedDirection()))
                            onDirectionSelection(mainBoardStartRow, mainBoardStartCol, "");
                     //   else
                    //    showDirectionDialog();
                    }else if(getPreferences().showGridPattern()){
                        if(!mLetterBoard.getStreakView().getmLines().contains(streakLine))
                        mLetterBoard.addStreakLine(streakLine);
                        char [][] mainboard = mLetterAdapter.getGrid();
                        StringBuilder tempStr = new StringBuilder(mTextSelection.getText().toString());
                        mTextSelection.setText(tempStr.append(mainboard[row][col]));
                        Log.d("mLetterBoard all lines ", mLetterBoard.getStreakView().getmLines().size()+"");
                        Log.d("inside ", "2");
                    }else if(getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
                        Log.d("inside ", "3");
                        mLetterBoard.popStreakLine();
                        //mLetterBoard.removeAllStreakLine();
                        //mTextSelection.setText("");
                        //mTextFromBorder.setText("");
                       // wordFromBorder = new StringBuilder();
                    }  else isSingleCellSelected = true;
                }else if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col && isSingleCellSelected && getPreferences().selectedStartEndGrid()){
                    Log.d("inside ", "B");
                    isSingleCellSelected = false;
                    if(getPreferences().selectedStartEndGrid()){
                        mLetterBoard.removeAllStreakLine();
                        mTextSelection.setText("");
                        GridIndex gridIndex = new GridIndex();
                        gridIndex.row = mainBoardStartRow;
                        gridIndex.col = mainBoardStartCol;
                        Log.d("gridIndex start "+gridIndex.row +" "+gridIndex.col, " gridIndex end "+streakLine.getEndIndex().row +" "+streakLine.getEndIndex().col);
                        Direction direction = Direction.fromLine(gridIndex, streakLine.getEndIndex());
                        Log.d("direction ", direction.name());
                        onDirectionSelection(gridIndex.row, gridIndex.col, direction.name());
                    }else {
                        StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                        newStreakLine.setColor(streakLine.getColor());
                        newStreakLine.getStartIndex().set(mainBoardStartRow, mainBoardStartCol);
                        newStreakLine.getEndIndex().set(mainBoardEndRow, mainBoardEndCol);
                        mLetterBoard.removeAllStreakLine();
                        if (Direction.fromLine(newStreakLine.getStartIndex(), newStreakLine.getEndIndex()) != Direction.NONE) {
                            String selectedPwd = Util.getStringInRange(mLetterAdapter, newStreakLine.getStartIndex(), newStreakLine.getEndIndex());
                            mLetterBoard.addStreakLine(newStreakLine);
                            mTextSelection.setText(selectedPwd);
                            List<String> pwdList = new ArrayList<>();
                            pwdList.add(selectedPwd);
                            checkPasswordCriteria(pwdList); //checkPasswordCriteria(selectedPwd, newStreakLine);
                        }
                    }
                }else if(getPreferences().showgridDirection() || getPreferences().showGridPattern() || getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
                    if(getPreferences().showGridPattern() ) {
                        mLetterBoard.popStreakLine();
                        //mLetterBoard.removeAllStreakLine();
                       //mTextSelection.setText("");
                    }else if(getPreferences().showgridDirection() ){
                        mLetterBoard.removeAllStreakLine();
                        mTextSelection.setText("");
                    }else if(getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
                        mLetterBoard.popStreakLine();
                        // mTextFromBorder.setText("");
                        //  wordFromBorder = new StringBuilder();
                        //if(getPreferences().selectedTypeManually()) mTextFromBorder.setEnabled(true);
                    }
                }
                else {
                    if (str.isEmpty()) {
                        mTextSelection.setText("");
                        mLetterBoard.popStreakLine();
                    } else {
                        if(getPreferences().selectedStartEndGrid()) {
                            mLetterBoard.popStreakLine();
                        }else {
                            isSingleCellSelected = false;
                            if(getPreferences().selectedDragManually()){
                                mLetterBoard.removeAllStreakLine();
                                mTextSelection.setText("");
                                Direction direction = Direction.fromLine(streakLine.getStartIndex(), streakLine.getEndIndex());
                                Log.d("direction ", direction.name());
                                onDirectionSelection(streakLine.getStartIndex().row, streakLine.getStartIndex().col, direction.name());
                            }else {
                                mTextSelection.setText(str);
                                List<String> pwdList = new ArrayList<>();
                                pwdList.add(str);
                                checkPasswordCriteria(pwdList);//checkPasswordCriteria(str, streakLine);
                                Log.d("passwordEntropyBits ", calculateEntropyBits(str.length(), getUsedSymbolLength()) + "");
                            }
                        }
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
                    //Log.d("GridActivity topBorder ", "end row: "+topBorderEndRow +" end col: "+topBorderEndtCol);
                    //Log.d("wordFromBorder ", str);
                    if(getPreferences().showWordFromBorder()){
                        char[][] topborder = mLetterTopAdapter.getGrid();
                        char [][] mainboard = mLetterAdapter.getGrid();
                        wordFromBorder = wordFromBorder.append(topborder[row][col]);
                        mTextFromBorder.setText(wordFromBorder.toString());
                        Log.d("wordFromBorder ", wordFromBorder.toString());
                        StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                        int occurrence = (int) wordFromBorder.chars().filter(ch -> ch == topborder[row][col]).count()-1;
                        if(occurrence>rowCount-1) occurrence = (occurrence % (rowCount));
                        newStreakLine.getStartIndex().set(occurrence, topBorderStartCol);
                        newStreakLine.getEndIndex().set(occurrence, col);
                        mLetterBoard.addStreakLine(newStreakLine);
                        StringBuilder strPwd = new StringBuilder(mTextSelection.getText().toString());
                        mTextSelection.setText(strPwd.append(mainboard[occurrence][col]).toString());
                        mLetterBoardTop.removeAllStreakLine();
                    }else { // if we want to select any col using top borders.
                        mLetterBoardTop.removeAllStreakLine();
                        /*StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                        newStreakLine.getStartIndex().set(topBorderStartRow, topBorderStartCol);
                        newStreakLine.getEndIndex().set(getPreferences().getGridCol(), col);
                        mLetterBoard.removeAllStreakLine();
                         mLetterBoard.addStreakLine(newStreakLine);*/
                    }

                    /*


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
                if(row == streakLine.getStartIndex().row && col == streakLine.getStartIndex().col) { // if we want to select any rows using left borders.
                    mLetterBoardLeft.removeAllStreakLine();
                    /*leftBorderEndRow = row;
                    leftBorderEndCol = col;
                    mLetterBoardTop.removeAllStreakLine();
                    Log.d("GridActivity leftBorder ", "end row: "+leftBorderEndRow +" end col: "+leftBorderEndCol);
                    StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                    newStreakLine.getStartIndex().set(leftBorderStartRow,leftBorderStartCol);
                    newStreakLine.getEndIndex().set(row,getPreferences().getGridCol());
                    mLetterBoard.removeAllStreakLine();
                    mLetterBoard.addStreakLine(newStreakLine);*/
                } else {
                    mLetterBoardLeft.removeAllStreakLine();
                    leftBorderEndRow = 0;
                    leftBorderEndCol = 0;
                }
            }
        });

        mLetterBoardLeft.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
            }
            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {
            }
            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) { mLetterBoardLeft.removeAllStreakLine(); }
        });


        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GridViewModel.class);
        mViewModel.getOnGridState().observe(this, this::onGridStateChanged);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_GRID_ID)) {
                int gid = extras.getInt(EXTRA_GRID_ID);
                rowCount = extras.getInt(EXTRA_ROW_COUNT);
                colCount = extras.getInt(EXTRA_COL_COUNT);
                userAccount = (UserAccount) extras.getSerializable("account");
                mViewModel.updateAccountInfo(userAccount);
                //defaultBoardWidth();
                Preferences preferences = getPreferences();
                mViewModel.setGridGenerationCriteria(preferences.showUpperCharacters(), preferences.showLowerCharacters(),preferences.showNumberCharacters(), preferences.showSpecialCharacters());
                mViewModel.setSelectedTypedWord(mTextFromBorder.getText().toString());
                //mViewModel.setGridChosenOption(getCurrentChosenOption());
                mViewModel.loadGridRound(gid);
            } else {
                rowCount = extras.getInt(EXTRA_ROW_COUNT);
                colCount = extras.getInt(EXTRA_COL_COUNT);
                userAccount = (UserAccount) extras.getSerializable("account");
                mViewModel.updateAccountInfo(userAccount);
                defaultBoardWidth();
                Preferences preferences = getPreferences();
                mViewModel.setGridGenerationCriteria(preferences.showUpperCharacters(), preferences.showLowerCharacters(),preferences.showNumberCharacters(), preferences.showSpecialCharacters());
                mViewModel.setGridChosenOption(getCurrentChosenOption());
                //mViewModel.setPasswordLength(getPreferences().getPasswordLength());
                mViewModel.setSelectedTypedWord(mTextFromBorder.getText().toString());
                mViewModel.generateNewGridRound(rowCount, colCount);
            }
            grid_account_name.setText(userAccount.getAccountName());
            grid_user_name.setText(userAccount.getUserName());
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
                resetGrid();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> pwdList = new ArrayList<>();
                pwdList.add(mTextSelection.getText().toString());
                checkPasswordCriteria(pwdList);//checkPasswordCriteria(mTextSelection.getText().toString(), null);

                /*if(ContextCompat.checkSelfPermission(GridActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(GridActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else takeScreenshot();*/

            }
        });

        iconSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GridActivity.this, GridCriteriaActivity.class);
                intent.putExtra("settingRequest", true);
                startActivityForResult(intent,SETTING_REQUEST_CODE);
            }
        });
        iconAutoGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("boardWidth ", mLetterBoard.getWidth()+"");
                resetGrid();
                generateDefaultPassword();
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(GridActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(GridActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else shareEgridCard();
            }
        });

        mTextFromBorder.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // Log.d("Editable before ", ""+s.length());
                typePasswordLength = s.length();
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Editable after ", ""+s.length());
                if(isDefaultPasswordGenerated && !getPreferences().showWordFromBorder()) {
                    if (!TextUtils.isEmpty(s)) {
                        if (s.length() > typePasswordLength || typePasswordLength > s.length()) {
                            if((s.length()-typePasswordLength)>=1 || typePasswordLength-(s.length())>=1) {
                                mTextSelection.setText("");
                                wordFromBorder = new StringBuilder();
                                mLetterBoard.removeAllStreakLine();
                                for (int i = 0; i < s.length(); i++)
                                    generatePasswordByTypeManually(Character.toUpperCase(s.charAt(i)));
                            }/*else if((s.length()-typePasswordLength)==1)
                                   generatePasswordByTypeManually(Character.toUpperCase(s.charAt(s.length() - 1)));
                             else if(typePasswordLength-(s.length())==1){
                                mLetterBoard.popStreakLine();
                                String temp = mTextSelection.getText().toString();
                                mTextSelection.setText(temp.substring(0, temp.length() - 1));
                            }*/
                        }
                    } else {
                        wordFromBorder = new StringBuilder();
                        mTextSelection.setText("");
                        mLetterBoard.removeAllStreakLine();
                    }
                }
            }
        });
    }

    private void initPwdEditText(){
        if(getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
            wordFromBorder = new StringBuilder();
            mTextFromBorder.setVisibility(View.VISIBLE);
            if(getPreferences().selectedTypeManually()) mTextFromBorder.setEnabled(true);
            else mTextFromBorder.setEnabled(false);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            linearTextTyping.setLayoutParams(param);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            linearTextSelection.setLayoutParams(param1);
        }else{
            mTextFromBorder.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 2f);
            linearTextSelection.setLayoutParams(param);
        }
    }

    private void shareEgridCard(){
        Log.d("Sharing ", " E-grid Card");
        String fileName = getFile();
        File file = new File(fileName);
        if(file.exists()){
            share(fileName);
        }
        float mHeadingFontSize = 30.0f;
        float mMediumFontSize = 30.0f;
        float mGridFontSize = 15.0f;
        float spacing = 20;
        float cellPadding = 15;
        /*com.itextpdf.kernel.color.Color colorAccent = new DeviceRgb(30, 136, 229);
        com.itextpdf.kernel.color.Color colorWhite = new DeviceRgb(255, 255, 255);
        com.itextpdf.kernel.color.Color colorBlack = new DeviceRgb(0, 0, 0);
        com.itextpdf.kernel.color.Color colorGray = new DeviceRgb(128, 128, 128);*/

        //LineSeparator lineSeparator = new LineSeparator();

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
            pdfDocument.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDocument);

            Paragraph header = new Paragraph("PasswordGrid");
            header.setBold();
            header.setFontSize(mHeadingFontSize);
            header.setFontColor(ColorConstants.BLACK);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setMargins(10f, 10f, 10f, 10f);
            document.add(header);

            /*Paragraph accountName = new Paragraph(userAccount.getAccountName());
            accountName.setFontSize(mMediumFontSize);
            accountName.setFontColor(ColorConstants.BLACK);
            accountName.setTextAlignment(TextAlignment.LEFT);
            accountName.setMargins(0f, 10f, 0f, 10f);
            document.add(accountName);

            Paragraph userName = new Paragraph(userAccount.getUserName());
            userName.setFontSize(mHeadingFontSize);
            userName.setFontColor(ColorConstants.BLACK);
            userName.setTextAlignment(TextAlignment.RIGHT);
            userName.setMargins(0f, 10f, 0f, 10f);
            document.add(userName);*/

            Paragraph p = new Paragraph(userAccount.getAccountName());
            p.add(new Tab());
            p.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
            p.add(userAccount.getUserName());
            document.add(p);


            float arr[] = {8f, 23f, 15f, 15f, 12f, 12f, 15f};
            String entries[] = {"5/03/2021", "Software Developer", "20", "Engineer", "10", "5000/-", "35000"};
            Table table = new Table(UnitValue.createPercentArray(arr)).useAllAvailableWidth();

//Add Header Cells
            table.addHeaderCell(new Cell().add(new Paragraph("Date").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Job Name").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Job Size").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Job Type").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Rate").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount").setTextAlignment(TextAlignment.CENTER)));

            //for (String entry: entries) {
                for(int i=0;i<50;i++) {
                    table.addCell(new Cell().add(new Paragraph(entries[0]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[1]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[2]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[3]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[4]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[5]).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(entries[6]).setTextAlignment(TextAlignment.RIGHT)));
                }
            //}
            document.add(table);

            document.close();
            Toast.makeText(this, "E-grid card pdf generated ", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error in pdf generation ", Toast.LENGTH_SHORT).show();
        }
    }

    private void share(String fileName){

    }

    private String getFile(){
        String root = "";
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(extStorageState)){
            root = Environment.getExternalStoragePublicDirectory("").toString();
        }
        File myDir = new File(root+"/PasswordGrid/PasswordGridDocuments");
        myDir.mkdirs();
        String fname = userAccount.getAccountName()+".pdf";
        String filePath = root+"/PasswordGrid/PasswordGridDocuments/"+fname;
        return  filePath;
    }

    private void resetGrid(){
        defaultPasswordStrengthIndicator();
        mTextSelection.setText("");
        mViewModel.removeAllStreakLines(); // delete from local storage for this grid
        mLetterBoard.removeAllStreakLine();
        mLetterBoardLeft.removeAllStreakLine();
        mLetterBoardTop.removeAllStreakLine();
        if(getPreferences().showGridPattern() || getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()) mLetterBoard.getStreakView().setRememberStreakLine(true);
        else mLetterBoard.getStreakView().setRememberStreakLine(false);
        if(getPreferences().selectedDragManually()) mLetterBoard.getStreakView().setmDraggingManually(true);
        else mLetterBoard.getStreakView().setmDraggingManually(false);
        isSingleCellSelected = false;
        mTextFromBorder.setText("");
        wordFromBorder = new StringBuilder();
        topBorderStartRow = 0;
        topBorderStartCol = 0;
        topBorderEndRow = 0;
        topBorderEndtCol = 0;
        leftBorderStartRow = 0;
        leftBorderStartCol = 0;
        leftBorderEndRow = 0;
        leftBorderEndCol = 0;
        mainBoardStartRow = 0;
        mainBoardStartCol = 0;
        mainBoardEndRow = 0;
        mainBoardEndCol = 0;
        selectedColor = 0;
        //isDefaultPasswordGenerated = false;
        initPwdEditText();
    }

    private String getCurrentChosenOption(){
        if(getPreferences().selectedDragManually()) return  "selectedDragManually";
        else if(getPreferences().selectedStartEndGrid()) return  "selectedStartEndGrid";
        else if(getPreferences().showgridDirection()) return  "showgridDirection";
        else if(getPreferences().showGridPattern()) return  "showGridPattern";
        else if(getPreferences().showWordFromBorder()) return  "showWordFromBorder";
        else return  "selectedTypeManually";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                // FileOutputStream stream = null;
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PasswordGridApp");
                dir.mkdir();
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

                // create bitmap screen capture
                //View mView = getWindow().getDecorView().getRootView();
                View mView = myScrollView.getChildAt(0);//.getRootView();
                mView.setDrawingCacheEnabled(true);
                Bitmap bitmap = getBitmapFromView(mView, mView.getHeight(), mView.getWidth()); // try to get visible + invisible part.
                //Bitmap bitmap = Bitmap.createBitmap(mView.getDrawingCache()); // visible whole screen
                mView.setDrawingCacheEnabled(false);

                File imageFile = new File(mPath);
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                openScreenshot(imageFile); //open when image saved in file
            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void generateDefaultPassword(){
        if(getPreferences().selectedDragManually()){
            generateDefault_DragManual_StartEndGrid();
        }else if(getPreferences().selectedStartEndGrid()){
            generateDefault_DragManual_StartEndGrid();
        }else if(getPreferences().showgridDirection()){ //customize it: set scrollY position accordingly, add line from tail in any direction if possible.
            int randomRowIndex = Util.getRandomIntRange(0, rowCount-1);
            int randomColIndex = Util.getRandomIntRange(0, colCount-1);
            selectedColor = Util.getRandomColorWithAlpha(170);
            onDirectionSelection(randomRowIndex,randomColIndex, "");
        }else if(getPreferences().showGridPattern()){
            generateDefault_GridPattern();
        }else if(getPreferences().showWordFromBorder()){
            generateDefault_WordFromBorder();
        }else if(getPreferences().selectedTypeManually()){
            generateDefault_WordFromBorder();
        }
        isDefaultPasswordGenerated = true;
    }

    private void generateDefault_WordFromBorder(){
        isDefaultPasswordGenerated = false;
        String defaultPwd ="";
        if(getPreferences().getPasswordLength()==14) defaultPwd = "SECUREPASSWORD";
        else defaultPwd = Util.getRandomWords(getPreferences().getPasswordLength());
        String applyWord = getPreferences().getApplyWordPassword().replaceAll("\\s","")/*.toUpperCase()*/;
        if(!TextUtils.isEmpty(applyWord)){
            if(getPreferences().getPasswordLength()<=applyWord.length())
            defaultPwd = applyWord.substring(0,getPreferences().getPasswordLength());
            else {
                StringBuilder sbPwd = new StringBuilder();
                int iteration = getPreferences().getPasswordLength()/applyWord.length();
                int reminder = getPreferences().getPasswordLength()%applyWord.length();
                Log.d("iteration "+iteration, " reminder "+reminder);
                for (int i=0;i<iteration;i++)
                    sbPwd = sbPwd.append(applyWord);
                sbPwd = sbPwd.append(applyWord.substring(0, reminder));
                defaultPwd = sbPwd.toString();
            }
            Log.d("applyWord ", applyWord);
            Log.d("defaultPwd ", defaultPwd);
        }
        char [][] mainboard = mLetterAdapter.getGrid();
        char[] ch  = defaultPwd.toUpperCase().toCharArray();
        for(char c : ch){
            int temp_integer = 64; //for upper case
            int index = ((int)c -temp_integer)-1;
            if((int)c <=90 & (int)c >=65) {
               // Log.d("alphabet " + c, " at " + index);
                wordFromBorder = wordFromBorder.append(c);
                StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                int occurrence = (int) wordFromBorder.chars().filter(chr -> chr == c).count() - 1;
                if(occurrence>rowCount-1) occurrence = (occurrence % (rowCount));
               // Log.d("occurrence ", "" + occurrence);
                newStreakLine.getStartIndex().set(occurrence, index);
                newStreakLine.getEndIndex().set(occurrence, index);
                mLetterBoard.addStreakLine(newStreakLine);
                StringBuilder strPwd = new StringBuilder(mTextSelection.getText().toString());
                mTextSelection.setText(strPwd.append(mainboard[occurrence][index]).toString());
                mTextFromBorder.setText(wordFromBorder.toString());
                mLetterBoardTop.removeAllStreakLine();
            }
        }
        isDefaultPasswordGenerated = true;
        mViewModel.setSelectedTypedWord(defaultPwd);
    }

    private void generatePasswordByTypeManually(char c){
        char [][] mainboard = mLetterAdapter.getGrid();
            int temp_integer = 64; //for upper case
            int index = ((int)c -temp_integer)-1;
            if((int)c <=90 & (int)c >=65) {
                //Log.d("alphabet " + c, " at " + index);
                wordFromBorder = wordFromBorder.append(c);
                StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                int occurrence = (int) wordFromBorder.chars().filter(chr -> chr == c).count() - 1;
                if(occurrence>rowCount-1) occurrence = (occurrence % (rowCount));
               // Log.d("occurrence ", "" + occurrence);
                newStreakLine.getStartIndex().set(occurrence, index);
                newStreakLine.getEndIndex().set(occurrence, index);
                mLetterBoard.addStreakLine(newStreakLine);
                StringBuilder strPwd = new StringBuilder(mTextSelection.getText().toString());
                mTextSelection.setText(strPwd.append(mainboard[occurrence][index]).toString());
            }
           /* mTextFromBorder.setText(wordFromBorder.toString());
            mLetterBoardTop.removeAllStreakLine();*/
    }

    private void generateDefault_GridPattern(){
        char [][] mainboard = mLetterAdapter.getGrid();
        char [][] mPattern;
        int randomPattern = 6; //Util.getRandomIntRange(1,5);
        if(getPreferences().getPasswordLength()<=4) randomPattern = 5;
        else if(getPreferences().getPasswordLength()<14) randomPattern = 4;

        Log.d("randomPattern ", ""+randomPattern);
        switch (randomPattern) {
            case 1:
            mPattern = new char[][]{
                    {'2', '2', '2', '2','2'},
                    {'0', '0', '0', '2','0'},
                    {'0', '0', '2', '0','0'},
                    {'0', '2', '0', '0','0'},
                    {'2', '2', '2', '2','2'}};
                break;
            case 2:
                mPattern = new char[][]{
                        {'2', '2', '2', '2'},
                        {'2', '0', '0', '2'},
                        {'2', '2', '2', '2'},
                        {'2', '0', '0', '2'},
                        {'2', '2', '2', '2'}};
                break;
            case 3:
                mPattern = new char[][]{
                        {'2', '2', '2', '2'},
                        {'2', '0', '0', '0'},
                        {'2', '0', '0', '0'},
                        {'2', '0', '0', '0'},
                        {'2', '2', '2', '2'}};
                break;
            case 4:
                mPattern = new char[][]{
                        {'2', '2', '2', '2'},
                        {'2', '0', '0', '2'},
                        {'2', '0', '0', '2'},
                        {'2', '0', '0', '2'},
                        {'2', '2', '2', '2'}};
            break;
            case 5:
                mPattern = new char[][]{
                        {'2', '2', '2'},
                        {'2', '0', '0'},
                        {'2', '0', '0'},
                        {'2', '2', '2'}};
                break;
            default:
                mPattern = new char[][]{
                        {'2', '2', '2', '2', '2'},
                        {'2', '2', '2', '2', '2'},
                        {'2', '0', '0', '0', '2'},
                        {'2', '0', '0', '0', '2'},
                        {'0', '0', '0', '0', '0'}};
                break;
        }
        //Log.d("row ", (mPattern.length)+"");
        //Log.d("col ",  (mPattern[0].length)+"");
        if(getPreferences().getPasswordLength()>4 && getPreferences().getPasswordLength()<14){
            int diff = 14 - getPreferences().getPasswordLength();
            for(int i=mPattern.length-1;i>=0;i--){
                for (int j=0;j<mPattern[i].length;j++) {
                    if(diff>0 && mPattern[i][j]!='0'){
                        mPattern[i][j] = '0';
                        diff --;
                    }
                }
            }
        }else if(getPreferences().getPasswordLength()>14){
            int diff = getPreferences().getPasswordLength();
            int tempRow = diff/rowCount;
            if(diff%rowCount>0) tempRow++;
            mPattern = new char[tempRow+5][colCount-5];
            for(int i=0;i<mPattern.length;i++){
                    for (int j = 5; j < mPattern[i].length; j++) {
                        if(i==0 || i==mPattern.length-1 || j==5 || j==mPattern[i].length-1) {
                        if (diff >= 0 && mPattern[i][j] != '2') {
                            mPattern[i][j] = '2';
                            diff--;
                        } else {
                            mPattern[i][j] = '0';
                        }
                    }
                }
            }
            if(diff>0){
                for(int i=0;i<mPattern.length;i++){
                    for (int j = 5; j < mPattern[i].length; j++) {
                        if(i==1 || i==mPattern.length-2 || j==6 || j==mPattern[i].length-2) {
                        if (diff >= 0 && mPattern[i][j] != '2') {
                            mPattern[i][j] = '2';
                            diff--;
                        } else {
                           // mPattern[i][j] = '0';
                        }
                    }
                }
            }
            }
        }
        int randomStartRow = Util.getRandomIntRange(0,rowCount-(mPattern.length));
        int randomStartCol = Util.getRandomIntRange(0, colCount-(mPattern[0].length));
        for(int i=0;i<mPattern.length;i++){
            for (int j=0;j<mPattern[i].length;j++){
                if(mPattern[i][j]=='2') {
                    StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                    newStreakLine.getStartIndex().set(randomStartRow+i, randomStartCol+j);
                    newStreakLine.getEndIndex().set(randomStartRow+i, randomStartCol+j);
                    mLetterBoard.addStreakLine(newStreakLine);
                    StringBuilder strPwd = new StringBuilder(mTextSelection.getText().toString());
                    mTextSelection.setText(strPwd.append(mainboard[randomStartRow+i][randomStartCol+j]).toString());
                }
            }
        }
        scrollByPosition(randomStartRow);
    }

    private void generateDefault_DragManual_StartEndGrid(){
        int randomDirection = Util.getRandomIntRange(1,3);
        int passwordLength = getPreferences().getPasswordLength()-1;
        Log.d("randomDirection ", ""+randomDirection);
        int randomForwordReverse;
        int randomRowIndex;
        int randomColIndex;
        selectedColor = Util.getRandomColorWithAlpha(170);
        switch (randomDirection){
            case 1: //horizontal
                randomRowIndex = Util.getRandomIntRange(0, rowCount-1);
                randomColIndex = Util.getRandomIntRange(0, colCount-1);
                randomForwordReverse = Util.getRandomIntRange(1,2);
                if(randomForwordReverse==1) drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex, randomColIndex+passwordLength);
                else drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex, randomColIndex-passwordLength);
                scrollByPosition(randomRowIndex);
                break;
            case 2: //vertical
                randomRowIndex = Util.getRandomIntRange(0, rowCount-1);
                randomColIndex = Util.getRandomIntRange(0, colCount-1);
                randomForwordReverse = Util.getRandomIntRange(1,2);
                if(randomForwordReverse==1) drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex+passwordLength, randomColIndex);
                else drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex-passwordLength, randomColIndex);
                if(randomForwordReverse==1) scrollByPosition(randomRowIndex);
                else scrollByPosition(randomRowIndex-passwordLength);
                break;
            case 3: //diagonal
                randomRowIndex = Util.getRandomIntRange(0, rowCount-1);
                randomColIndex = Util.getRandomIntRange(0, colCount-1);
                randomForwordReverse = Util.getRandomIntRange(1,2);
                if(randomForwordReverse==1) drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex+passwordLength, randomColIndex+passwordLength);
                else drawUsingDirection(randomRowIndex, randomColIndex, randomRowIndex-passwordLength, randomColIndex-passwordLength);
                if(randomForwordReverse==1) scrollByPosition(randomRowIndex);
                else scrollByPosition(randomRowIndex-passwordLength);
                break;
        }
    }

    private void onDirectionSelection(int startRow, int startCol, String mDirection){
        String direction;
        if(TextUtils.isEmpty(mDirection)) direction = getPreferences().getSelectedDirection();
        else direction = mDirection;
        int passwordLength = getPreferences().getPasswordLength()-1;
        if(direction.equals("EAST")){
            drawUsingDirection(startRow, startCol, startRow, startCol+passwordLength);
            if(!isDefaultPasswordGenerated)
                scrollByPosition(startRow);
        }else if(direction.equals("WEST")){
            drawUsingDirection(startRow, startCol, startRow, startCol-passwordLength);
            if(!isDefaultPasswordGenerated)
                scrollByPosition(startRow);
        }else if(direction.equals("SOUTH")){
            drawUsingDirection(startRow, startCol, startRow+passwordLength, startCol);
            //if(startRow>=8)
            scrollByPosition(startRow);
        }else if(direction.equals("NORTH")){
            drawUsingDirection(startRow, startCol, startRow-passwordLength, startCol);
            scrollByPosition(startRow-passwordLength);
        }else if(direction.equals("SOUTH_EAST")){
                int endRow = startRow;
                int endCol = startCol;
                while (endRow < (startRow + passwordLength) && endCol < (startCol + passwordLength)) {
                    endRow++;
                    endCol++;
                    if ((endRow - startRow) >= passwordLength)
                        break;
                }
                Log.d("endRow " + endRow, "endRow " + endCol);
                drawUsingDirection(startRow, startCol, endRow, endCol);
                //if(startRow>=8)
                scrollByPosition(startRow);
        }else if(direction.equals("NORTH_WEST")){
                int endRow = startRow;
                int endCol = startCol;
                while (endRow > startRow - passwordLength && endCol > startCol - passwordLength) {
                    endRow--;
                    endCol--;
                    if ((startRow - endRow) >= passwordLength)
                        break;
                }
                Log.d("endRow " + endRow, "endRow " + endCol);
                drawUsingDirection(startRow, startCol, endRow, endCol);
                scrollByPosition(endRow);
            }
    }

    private void drawUsingDirection(int startRow, int startCol, int endRow, int endCol){
        int newLineStartRow = startRow, newLineStartCol = startCol, newLineEndRow = endRow, newLineEndCol = endCol;
        StreakView.StreakLine checkLine = new StreakView.StreakLine();
        checkLine.getStartIndex().set(startRow,startCol);
        checkLine.getEndIndex().set(endRow, endCol);
        Direction direction = Direction.fromLine(checkLine.getStartIndex(), checkLine.getEndIndex());
        Log.d("drawUsingDirection ", direction.name());
        Log.d("drawUsingDirection startRow "+startRow +" startCol "+startCol, " endRow "+endRow  + " endCol "+endCol);

        if(endRow>=rowCount) newLineEndRow = rowCount-1;
        else if(endRow<0) newLineEndRow = 0;
        if(endCol>=colCount ) newLineEndCol = colCount-1;
        else if(endCol<0) newLineEndCol = 0;
        if(startRow<0) newLineStartRow = 0;
        else if(startRow>=rowCount) newLineStartRow = rowCount-1;
        if(startCol>=colCount ) newLineStartCol = colCount-1;
        else if(startCol<0) newLineStartCol = 0;
        if(direction==Direction.SOUTH_EAST){
            if(endRow>=rowCount || endCol>=colCount){
                newLineEndCol = startCol + Math.min((newLineEndRow-newLineStartRow),(newLineEndCol-newLineStartCol));
                newLineEndRow = startRow + Math.min((newLineEndRow-newLineStartRow),(newLineEndCol-newLineStartCol));
            }
        }else if(direction==Direction.NORTH_WEST){
            if(endRow<0 || endCol<0){
                newLineEndCol = startCol - Math.min((newLineStartRow-newLineEndRow),(newLineStartCol-newLineEndCol));
                newLineEndRow = startRow - Math.min((newLineStartRow-newLineEndRow),(newLineStartCol-newLineEndCol));
            }
        }


        Log.d("drawUsingDirection newLineStartRow "+newLineStartRow +" newLineStartCol "+newLineStartCol, " newLineEndRow "+newLineEndRow  + " newLineEndCol "+newLineEndCol);

        StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
        newStreakLine.setColor(selectedColor);
        newStreakLine.getStartIndex().set(newLineStartRow,newLineStartCol);
        newStreakLine.getEndIndex().set(newLineEndRow,newLineEndCol);
        mLetterBoard.removeAllStreakLine();
        if(direction != Direction.NONE){
            //String firstPartPwd = "";
            List<String> lastPartPwd = new ArrayList<>();
            if(Direction.fromLine(newStreakLine.getStartIndex(), newStreakLine.getEndIndex())==Direction.NONE) {
                char mgrid[][] = mLetterAdapter.getGrid();
                lastPartPwd.add(String.valueOf(mgrid[newStreakLine.getStartIndex().row][newStreakLine.getStartIndex().col]));
            }else
                lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine.getStartIndex(), newStreakLine.getEndIndex()));
            mLetterBoard.addStreakLine(newStreakLine);
            if(direction == Direction.EAST && endCol>=colCount){
                int leftToTraverse = getPreferences().getPasswordLength() -(colCount-newLineStartCol);
                int iteration = leftToTraverse / colCount;
                int reminder = leftToTraverse % colCount;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);
                 //place a loop here and add line while passwordlength iterate.
                    for (int i=0;i<iteration;i++){
                        StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                        newStreakLine1.setColor(selectedColor);
                        newStreakLine1.getStartIndex().set(newLineStartRow, 0);
                        if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set(newLineStartRow, (reminder-1));
                        else newStreakLine1.getEndIndex().set(newLineStartRow, (colCount-1));
                        mLetterBoard.addStreakLine(newStreakLine1);
                        if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                            char mgrid[][] = mLetterAdapter.getGrid();
                            lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                            Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                        } else
                            lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                    }

            }else if(direction == Direction.WEST && endCol<0){
                int leftToTraverse = getPreferences().getPasswordLength() - (newLineStartCol+1);
                int iteration = leftToTraverse / colCount;
                int reminder = leftToTraverse % colCount;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);
                for (int i=0;i<iteration;i++) {
                    StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                    newStreakLine1.setColor(selectedColor);
                    newStreakLine1.getStartIndex().set(newLineStartRow, colCount - 1);
                    if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set(newLineStartRow, (colCount - 1)-(reminder-1));
                    else newStreakLine1.getEndIndex().set(newLineStartRow, (0));
                    mLetterBoard.addStreakLine(newStreakLine1);
                    if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                        char mgrid[][] = mLetterAdapter.getGrid();
                        lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                        Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                    } else
                        lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                }
            }else if(direction == Direction.SOUTH && endRow>=rowCount){
                int leftToTraverse = getPreferences().getPasswordLength() - (rowCount-newLineStartRow);
                int iteration = leftToTraverse / rowCount;
                int reminder = leftToTraverse % rowCount;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);
                for (int i=0;i<iteration;i++) {
                    StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                    newStreakLine1.setColor(selectedColor);
                    newStreakLine1.getStartIndex().set(0, newLineStartCol);
                    //newStreakLine1.getEndIndex().set((endRow - newLineEndRow) - 1, newLineStartCol);
                    if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set((reminder-1), newLineStartCol);
                    else newStreakLine1.getEndIndex().set((rowCount-1), newLineStartCol);
                    mLetterBoard.addStreakLine(newStreakLine1);
                    if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                        char mgrid[][] = mLetterAdapter.getGrid();
                        lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                        Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                        //Log.d("lastPartPwd", lastPartPwd);
                    } else
                        lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                }
            }else if(direction == Direction.NORTH && endRow<0){
                int leftToTraverse = getPreferences().getPasswordLength() - (newLineStartRow+1);
                int iteration = leftToTraverse / rowCount;
                int reminder = leftToTraverse % rowCount;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);
                for (int i=0;i<iteration;i++) {
                    StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                    newStreakLine1.setColor(selectedColor);
                    newStreakLine1.getStartIndex().set(rowCount - 1, newLineStartCol);
                    //newStreakLine1.getEndIndex().set((rowCount + endRow), newLineStartCol);
                    if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set((rowCount - 1)-(reminder-1), newLineStartCol);
                    else newStreakLine1.getEndIndex().set(0, newLineStartCol);
                    mLetterBoard.addStreakLine(newStreakLine1);
                    if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                        char mgrid[][] = mLetterAdapter.getGrid();
                        lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                        Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                        //Log.d("lastPartPwd", lastPartPwd);
                    } else
                        lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                }
            }else if(direction == Direction.SOUTH_EAST){
                int leftToTraverse = getPreferences().getPasswordLength() - (newLineEndRow-newLineStartRow) - 1;
                int noOfCharInIteration;
                while (newLineStartRow > 0 && newLineStartCol >0) {
                    newLineStartRow--;
                    newLineStartCol--;
                    if (newLineStartRow == 0 || newLineStartCol ==0)
                        break;
                }
                noOfCharInIteration = newLineEndRow - newLineStartRow +1;
                Log.d("tempStartRow " + newLineStartRow, "tempStartCol " + newLineStartCol);
                int iteration = leftToTraverse / noOfCharInIteration;
                int reminder = leftToTraverse % noOfCharInIteration;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);
                for (int i=0;i<iteration;i++) {
                    StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                    newStreakLine1.setColor(selectedColor);
                    newStreakLine1.getStartIndex().set(newLineStartRow, newLineStartCol);
                    //newStreakLine1.getEndIndex().set((endRow - newLineEndRow) - 1, newLineStartCol);
                    if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set(newLineStartRow + (reminder-1), newLineStartCol + (reminder-1));
                    else newStreakLine1.getEndIndex().set(newLineEndRow, newLineEndCol);
                    mLetterBoard.addStreakLine(newStreakLine1);
                    if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                        char mgrid[][] = mLetterAdapter.getGrid();
                        lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                        Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                        //Log.d("lastPartPwd", lastPartPwd);
                    } else
                        lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                }
            }else if(direction == Direction.NORTH_WEST){
                int leftToTraverse = getPreferences().getPasswordLength() - (newLineStartRow - newLineEndRow + 1);
                int noOfCharInIteration;
                while (newLineStartRow <rowCount-1 && newLineStartCol <colCount-1) {
                    newLineStartRow++;
                    newLineStartCol++;
                    if (newLineStartRow == rowCount-1 || newLineStartCol == colCount-1)
                        break;
                }
                noOfCharInIteration = newLineStartRow - newLineEndRow + 1;
                Log.d("tempStartRow " + newLineStartRow, "tempStartCol " + newLineStartCol);
                int iteration = leftToTraverse / noOfCharInIteration;
                int reminder = leftToTraverse % noOfCharInIteration;
                if(reminder>0) iteration = iteration + 1;
                Log.d("leftToTraverse "+leftToTraverse, " iteration "+iteration +" reminder "+reminder);/**/
                for (int i=0;i<iteration;i++) {
                    StreakView.StreakLine newStreakLine1 = new StreakView.StreakLine();
                    newStreakLine1.setColor(selectedColor);
                    newStreakLine1.getStartIndex().set(newLineStartRow, newLineStartCol);
                    //newStreakLine1.getEndIndex().set((endRow - newLineEndRow) - 1, newLineStartCol);
                    if(reminder>0 && i ==iteration-1) newStreakLine1.getEndIndex().set(newLineStartRow - (reminder-1), newLineStartCol - (reminder-1));
                    else newStreakLine1.getEndIndex().set(newLineEndRow, newLineEndCol);
                    mLetterBoard.addStreakLine(newStreakLine1);
                    if (Direction.fromLine(newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()) == Direction.NONE) {
                        char mgrid[][] = mLetterAdapter.getGrid();
                        lastPartPwd.add(String.valueOf(mgrid[newStreakLine1.getStartIndex().row][newStreakLine1.getStartIndex().col]));
                        Log.d("row " + newStreakLine1.getStartIndex().row, "row " + newStreakLine1.getStartIndex().col);
                        //Log.d("lastPartPwd", lastPartPwd);
                    } else
                        lastPartPwd.add(Util.getStringInRange(mLetterAdapter, newStreakLine1.getStartIndex(), newStreakLine1.getEndIndex()));
                }
            }
            String finalPwd = "";
            for (String pwd: lastPartPwd) finalPwd = finalPwd.concat(pwd);
            mTextSelection.setText(finalPwd);
            checkPasswordCriteria(lastPartPwd);
        }else {
            mLetterBoard.popStreakLine();
            mTextSelection.setText("");
        }
    }

    private void checkPasswordCriteria(List<String> lastPartPwd){
        List<StreakView.StreakLine> streakLineList = mLetterBoard.getStreakView().getmLines();
        String password = "";
        if(lastPartPwd!=null && lastPartPwd.size()>0)
        for (String pwd: lastPartPwd) password = password.concat(pwd);

        Log.d("password ", password);
        String passwordAlert = GridDataCreator.checkPasswordCriteria(password);
        if(!passwordAlert.equals("true")) {
            if(password.length()>=getPreferences().getPasswordLength()){ //replace it automatically when password select actual length but not get desired password
                if(streakLineList!=null) {
                    int randomPwdLengh =0;
                    if(lastPartPwd!=null) {
                        /*for (String pwd:lastPartPwd){
                            if(randomPwdLengh<pwd.length()) randomPwdLengh = pwd.length();
                        }*/

                        if (getPreferences().selectedTypeManually() || getPreferences().showWordFromBorder() || getPreferences().showGridPattern()) {
                            String randomPassword = GridDataCreator.getRandomWords(mTextSelection.getText().toString().length());
                            Log.d("new randomPassword ", randomPassword);
                            mTextSelection.setText(randomPassword);
                            int index = 0;
                            char[] randompwdArray = randomPassword.toCharArray();
                            for (StreakView.StreakLine streakLine : streakLineList) {
                                Direction direction = Direction.fromLine(streakLine.getStartIndex(), streakLine.getEndIndex());
                                //Log.d("password direction ", direction+"");
                                char[][] tempArray = mLetterAdapter.getGrid().clone();  // update griddata and streakline in db also
                                StringListGridGenerator.placeRandomWordAt(streakLine.getStartIndex().row, streakLine.getStartIndex().col, direction, tempArray, ""+randompwdArray[index]);
                                mLetterAdapter.setGrid(tempArray);
                                mViewModel.answerWord(index, ""+randompwdArray[index], STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                                mViewModel.updateGridData();
                                index++;
                            }
                        } else {
                            int xMin = 100, yMin = 100, xMax = 0, yMax = 0;
                            for (StreakView.StreakLine line : streakLineList) {
                                Direction direction = Direction.fromLine(line.getStartIndex(), line.getEndIndex());
                                if (direction == Direction.SOUTH_EAST || direction == Direction.SOUTH) {
                                    if (xMax < line.getEndIndex().col)
                                        xMax = line.getEndIndex().col;
                                    if (yMax < line.getEndIndex().row)
                                        yMax = line.getEndIndex().row;
                                    if (xMin > line.getStartIndex().col)
                                        xMin = line.getStartIndex().col;
                                    if (yMin > line.getStartIndex().row)
                                        yMin = line.getStartIndex().row;
                                } else {
                                    if (xMin > line.getEndIndex().col)
                                        xMin = line.getEndIndex().col;
                                    if (yMin > line.getEndIndex().row)
                                        yMin = line.getEndIndex().row;
                                    if (xMax < line.getStartIndex().col)
                                        xMax = line.getStartIndex().col;
                                    if (yMax < line.getStartIndex().row)
                                        yMax = line.getStartIndex().row;
                                }
                            }
                            int maxXDiff = xMax - xMin;
                            int maxYDiff = yMax - yMin;
                            randomPwdLengh = Math.max(maxXDiff, maxYDiff) + 1;
                            Log.d("maxDiff ", Math.max(maxXDiff, maxYDiff) + "");
                        }

                        if (randomPwdLengh >= 4) {
                            String randomPassword = GridDataCreator.getRandomWords(randomPwdLengh);
                            mTextSelection.setText("");
                            Log.d("new randomPassword " + randomPwdLengh, randomPassword + " " + randomPassword.length());
                            Log.d("streakLine length ", streakLineList.size() + "");
                            Log.d("lastPartPwd length ", lastPartPwd.size() + "");
                            int index = 0;
                            for (StreakView.StreakLine streakLine : streakLineList) {
                                Log.d("indexOf Streakline " + streakLineList.indexOf(streakLine), streakLine + "");
                                String partPwd = "";
                                if (index == 0)
                                    partPwd = randomPassword.substring(0, lastPartPwd.get(0).length());
                                else if (index == lastPartPwd.size() - 1) {
                                    if (lastPartPwd.get(index).length() > randomPassword.length() - lastPartPwd.get(0).length())
                                        partPwd = randomPassword.substring(lastPartPwd.get(0).length());
                                    else
                                        partPwd = randomPassword.substring(lastPartPwd.get(0).length(), lastPartPwd.get(0).length() + lastPartPwd.get(index).length());
                                    if (lastPartPwd.get(index).length() > partPwd.length()) {
                                        String lPart = randomPassword.substring(0, lastPartPwd.get(index).length() - partPwd.length());
                                        partPwd = partPwd.concat(lPart);
                                    }
                                } else {
                                    String fPart = randomPassword.substring(lastPartPwd.get(0).length());
                                    String lPart = randomPassword.substring(0, lastPartPwd.get(0).length());
                                    partPwd = fPart.concat(lPart);
                                }
                                Log.d("start " + lastPartPwd.get(0).length(), "end " + lastPartPwd.get(index).length());
                                Log.d("partPwd ", partPwd);
                                mTextSelection.setText(mTextSelection.getText().toString().concat(partPwd));
                                Direction direction = Direction.fromLine(streakLine.getStartIndex(), streakLine.getEndIndex());
                                //Log.d("password direction ", direction+"");
                                char[][] tempArray = mLetterAdapter.getGrid().clone();  // update griddata and streakline in db also
                                StringListGridGenerator.placeRandomWordAt(streakLine.getStartIndex().row, streakLine.getStartIndex().col, direction, tempArray, partPwd);
                                mLetterAdapter.setGrid(tempArray);
                                mViewModel.answerWord(index, partPwd, STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                                mViewModel.updateGridData();
                                index++;
                            }
                        }
                    }

                    passwordStrengthIndicator(mTextSelection.getText().toString());
                }
            }else Toast.makeText(GridActivity.this, "Alert: " + passwordAlert, Toast.LENGTH_SHORT).show();
        }
        else if(password.length()< getPreferences().getPasswordLength())
            Toast.makeText(GridActivity.this, "Alert: generated password length is less than password criteria",Toast.LENGTH_SHORT).show();
        else {//if(passwordAlert.equals("true") && mTextSelection.getText().toString().length()>= getPreferences().getPasswordLength())
            Toast.makeText(GridActivity.this, "Your grid will stored in secure wallet ", Toast.LENGTH_SHORT).show();
            if(streakLineList!=null && streakLineList.size()>0) {
                for (StreakView.StreakLine streakLine: streakLineList) {
                    int index = streakLineList.indexOf(streakLine);
                    if(lastPartPwd!=null && lastPartPwd.size() == streakLineList.size())
                    mViewModel.answerWord(index, lastPartPwd.get(index), STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                    else if(lastPartPwd!=null && lastPartPwd.size()==0) mViewModel.answerWord(0, lastPartPwd.get(0), STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                    else{
                        Direction direction = Direction.fromLine(streakLine.getStartIndex(), streakLine.getEndIndex());
                        String word = StringListGridGenerator.getWordByDirection(direction,streakLine.getStartIndex().row, streakLine.getStartIndex().col,
                                streakLine.getEndIndex().row, streakLine.getEndIndex().col, mLetterAdapter.getGrid());
                        mViewModel.answerWord(index, word, STREAK_LINE_MAPPER.revMap(streakLine), true /*getPreferences().reverseMatching()*/);
                    }
                    mViewModel.updateGridData();
                }
            }
            passwordStrengthIndicator(mTextSelection.getText().toString());
        }
    }

    private void scrollByPosition(int randomRowIndex){
        int scrollY = topBorderLeftMargin  - (int) Util.convertDpToPx(this, 10);
        myScrollView.post(new Runnable() {
            @Override
            public void run() {
                //myScrollView.fullScroll(View.FOCUS_DOWN);
                myScrollView.setScrollY((scrollY * randomRowIndex));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
            wordFromBorder = new StringBuilder();
            mTextFromBorder.setVisibility(View.VISIBLE);
            if(getPreferences().selectedTypeManually()) {
                mTextFromBorder.setEnabled(true);
                mTextFromBorder.setBackground(null);
            }
        }else mTextFromBorder.setVisibility(View.GONE);
        if(getPreferences().showGridPattern())
            mLetterBoard.getStreakView().setRememberStreakLine(true);
        if(getPreferences().selectedDragManually())
            mLetterBoard.getStreakView().setmDraggingManually(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.resumeGrid();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.pauseGrid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopGrid();
    }

    private void onGridStateChanged(GridViewModel.GridState gridState) {
        showLoading(false, null);
        Log.d("onGridStateChanged "+mLetterBoard.getWidth(), "gridState "+gridState.toString());
        if (gridState instanceof GridViewModel.Generating) {
            GridViewModel.Generating state = (GridViewModel.Generating) gridState;
            String text = "Generating " + state.rowCount + "x" + state.colCount + " grid";
            showLoading(true, text);
        } else if (gridState instanceof GridViewModel.Finished) {
          //  showFinishGrid(((GridViewModel.Finished) gridState).mGridData.getId());
        } else if (gridState instanceof GridViewModel.Paused) {

        } else if (gridState instanceof GridViewModel.Loaded) {
            if(!isInitialized)
            onGridRoundLoaded(((GridViewModel.Loaded) gridState).mGridData);
        }
    }

    private void onGridRoundLoaded(GridData gridData) {
        /* restore stored griddata selected pin/password mode, selected character case, and selected password chosen option.
           restored typed/selected word from border if chosen option is word from border or type manually,
           fix random new password generation crash on word from border and type manually chosen option.
         */


        /*

         */

            String generationCriteria = gridData.getmSelectionCriteria();
            if(generationCriteria.contains("isUpperCase")) getPreferences().setUpperCharacters(true);
            else getPreferences().setUpperCharacters(false);
            if(generationCriteria.contains("isLowerCase")) getPreferences().setLowerCharacters(true);
            else getPreferences().setLowerCharacters(false);
            if(generationCriteria.contains("isNumbers")) getPreferences().setNumberCharacters(true);
            else getPreferences().setNumberCharacters(false);
            if(generationCriteria.contains("isSpecialCharacters")) getPreferences().setSpecialCharacters(true);
            else getPreferences().setSpecialCharacters(false);

        String chosenOption = gridData.getmChosenOption();
        mViewModel.setGridChosenOption(chosenOption);
        if(chosenOption.contains("selectedDragManually")) getPreferences().setDragManually(true);
        else getPreferences().setDragManually(false);
        if(chosenOption.contains("selectedStartEndGrid")) getPreferences().setStartEndGrid(true);
        else getPreferences().setStartEndGrid(false);
        if(chosenOption.contains("showgridDirection")) getPreferences().setGridDirection(true);
        else getPreferences().setGridDirection(false);
        if(chosenOption.contains("showGridPattern")) getPreferences().setGridPattern(true);
        else getPreferences().setGridPattern(false);
        if(chosenOption.contains("showWordFromBorder")) getPreferences().setWordFromBorder(true);
        else getPreferences().setWordFromBorder(false);
        if(chosenOption.contains("selectedTypeManually")) getPreferences().setTypeManually(true);
        else getPreferences().setTypeManually(false);

        if(getPreferences().showWordFromBorder() || getPreferences().selectedTypeManually()){
            mTextFromBorder.setText(gridData.getmSelectedTypedWord());
        }

        //getPreferences().setPasswordLength(gridData.getmGridPasswordLength());
        //mViewModel.setPasswordLength(gridData.getmGridPasswordLength());

        rowCount = gridData.getGrid().getRowCount();
        colCount = gridData.getGrid().getColCount();
        defaultBoardWidth();
       // doneLoadingContent();  //call it accordingly
        userAccount.setAccountGridId(gridData.getId());
        mViewModel.updateAccountInfo(userAccount);

        showLetterGrid(gridData.getGrid().getArray());
        mLetterBoard.setVisibility(View.VISIBLE);
        mLetterBoardTop.setVisibility(View.VISIBLE);
        mLetterBoardLeft.setVisibility(View.VISIBLE);
        for (UsedWord word: gridData.getUsedWords()) {
            if(word.isAnswered()){
                Log.d("savedPassword ", word.getString());
                UsedWord.AnswerLine line = word.getAnswerLine();
                Log.d("line color ", ""+line.color);
                Log.d("line startRow startCol ", ""+line.startRow + line.startCol);
                Log.d("line endRow endCol ", ""+line.endRow + line.endCol);
                StreakView.StreakLine newStreakLine = new StreakView.StreakLine();
                newStreakLine.setColor(line.color);
                newStreakLine.getStartIndex().set(line.startRow,line.startCol);
                newStreakLine.getEndIndex().set(line.endRow,line.endCol);
                Direction direction = Direction.fromLine(newStreakLine.getStartIndex(), newStreakLine.getEndIndex());
                Log.d("direction ", direction.name());
                Log.d("all lines ", ""+mLetterBoard.getStreakView().getmLines().size());
                //if( direction!= Direction.NONE){
                    mLetterBoard.addStreakLine(newStreakLine);
                    mTextSelection.setText(mTextSelection.getText().toString().concat(word.getString()));
               // }
            }
        }

        doneLoadingContent();
    }

    private void tryScale() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int boardWidth = mLetterBoard.getWidth();  // work here for fix scale
        int screenWidth = metrics.widthPixels - (boardWidth/mLetterBoard.getGridColCount()) - (int) Util.convertDpToPx(this, 20);
        Log.d("boardWidth ", boardWidth+"");
        Log.d("screenWidth ", screenWidth+"");
        Log.d("gridColCount ", mLetterBoard.getGridColCount()+"");
        Log.d("gridRowCount ", mLetterBoard.getGridRowCount()+"");

        //Log.d("topBorderLeftMargin ", ""+topBorderLeftMargin);
        if (boardWidth > screenWidth) {
            isScaled = true;
            float scale = ((float)screenWidth / (float)boardWidth);
            Log.d("scale ", ""+scale);
            mLetterBoardLeft.scale(scale, scale);
            mLetterBoard.scale(scale, scale);
            mLetterBoardTop.scale(scale, scale);
            if(isInitialized) {
               /* mLetterBoardTop.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();*/
            }
        }else {
           /* if(!isScaled) {
                int totalWidth = metrics.widthPixels;
                int sWidth =  totalWidth - (totalWidth/colCount) - (int) Util.convertDpToPx(this, 20);
                int scale = sWidth/colCount;
                //int scale = (int)(screenWidth * 0.05);
                mLetterBoard.defaultScale(scale);
                mLetterBoardLeft.defaultScale(scale);
                mLetterBoardTop.defaultScale(scale);
            }*/
        }
         if(isInitialized){
             topBorderLeftMargin = (mLetterBoard.getWidth()/mLetterBoard.getGridColCount()) + (int) Util.convertDpToPx(this, 12);
             Log.d("topBorderLeftMargin ", topBorderLeftMargin+"");
             int dynamicStreakWidth = (mLetterBoard.getWidth()/mLetterBoard.getGridColCount());
             mLetterBoard.setStreakWidth(dynamicStreakWidth);
             mLetterBoardTop.setStreakWidth(dynamicStreakWidth);
             mLetterBoardLeft.setStreakWidth(dynamicStreakWidth);
             RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.WRAP_CONTENT,
                     RelativeLayout.LayoutParams.WRAP_CONTENT
             );
             params.setMargins(topBorderLeftMargin, 0, 0, 0);
             mLetterBoardTop.setLayoutParams(params);
             String storedPwd = mTextSelection.getText().toString();
             if(TextUtils.isEmpty(storedPwd)){
                 generateDefaultPassword();
             } else getPreferences().setPasswordLength(storedPwd.length());

         }
         /*if(!isInitialized) {
            isInitialized = true;
             new Handler().postDelayed(this::tryScale, 100);
        }*/
    }

    private void doneLoadingContent() {
        // call tryScale() on the next render frame
        isInitialized = true;
        new Handler().postDelayed(this::tryScale, 50);
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

    private void showFinishGrid(int gridId) {
        /*Intent intent = new Intent(this, GridOverActivity.class);
        intent.putExtra(GridOverActivity.EXTRA_GRID_ROUND_ID, gridId);
        startActivity(intent);
        finish();*/
    }

    private void setGridAsAlreadyFinished() {
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

    private void passwordStrengthIndicator(String password){ // use 128 entropybits for strength calculation here.
        int usedCharacters = getUsedSymbolLength();
        int passwordCharacters = passwordStrength(password);
        double enTropyBit = calculateEntropyBits(password.length(), usedCharacters);
        Log.d("enTropyBit ", String.valueOf(enTropyBit));
        if(enTropyBit>=128 && passwordCharacters>=75) { // strong pwd
        }else if(enTropyBit>=75 && passwordCharacters>=50){ // medium pwd
        }else { // weak pwd
        }
        if(usedCharacters>=94 && password.length()>=14 && passwordCharacters>=100 ||
                usedCharacters>=62 && password.length()>=20 && passwordCharacters>=75 ||
                usedCharacters>=52 && password.length()>=26 && passwordCharacters>=50 ||
                usedCharacters>=26 && password.length()>=50 && passwordCharacters>=25) { //Strong
            indicatorGreen.setBackground(ContextCompat.getDrawable(this,R.drawable.green_indicator));
            indicatorAmber.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
            indicatorRed.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
        } else if(usedCharacters>=94 && password.length()>=8 && passwordCharacters>=75 ||
                usedCharacters>=62 && password.length()>=10 && passwordCharacters>=75 ||
                usedCharacters>=52 && password.length()>=14 && passwordCharacters>=50 ||
                usedCharacters>=26 && password.length()>=26 && passwordCharacters>=26) { //Semi Strong
            indicatorAmber.setBackground(ContextCompat.getDrawable(this,R.drawable.amber_indicator));
            indicatorGreen.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
            indicatorRed.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
        } else { //Weak   //if(usedCharacters< 52 && password.length()<8 && passwordCharacters<26) return 1;
            indicatorRed.setBackground(ContextCompat.getDrawable(this,R.drawable.red_indicator));
            indicatorGreen.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
            indicatorAmber.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
        }
        if(getPreferences().showWordFromBorder() && getPreferences().getApplyWordStatus() ||
                getPreferences().selectedTypeManually() && getPreferences().getApplyWordStatus()){
            showApplyWordToAllDialog(mTextFromBorder.getText().toString());
        }
    }

    private void showApplyWordToAllDialog(String word){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Apply to all");
        dialog.setMessage("Apply this word to generate all password in future?" );
        dialog.setNeutralButton("Don't show again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPreferences().setApplyWordStatus(false);
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                getPreferences().setApplyWordPassword(word);
                //Action for "Delete".
            }
        }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void defaultPasswordStrengthIndicator(){
        indicatorAmber.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
        indicatorGreen.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
        indicatorRed.setBackground(ContextCompat.getDrawable(this,R.drawable.default_indicator));
    }

    private int passwordStrength(String word){
            char ch;
            int strength = 0;
            boolean capitalFlag = false;
            boolean lowerCaseFlag = false;
            boolean numberFlag = false;
            boolean symbolFlag = false;
            for(int i=0;i < word.length();i++) {
                ch = word.charAt(i);
                // Log.d("isUpperCase ", ""+Character.isUpperCase(ch));
                if(Character.isDigit(ch)) {
                    numberFlag = true;
                } if (Character.isUpperCase(ch)) {
                    capitalFlag = true;
                } if (Character.isLowerCase(ch)) {
                    lowerCaseFlag = true;
                }
                Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
                if(regex.matcher(""+ch).find()) { //need to change !isSpecialCharacters with pattern matching..
                    //if(isSpecialCharacters)
                    symbolFlag = true;
                }
                //if(numberFlag && capitalFlag && lowerCaseFlag && symbolFlag)
                   // strength = 100;
            }
            if(capitalFlag) strength = strength + 25;
            if(lowerCaseFlag) strength = strength + 25;
            if(numberFlag) strength = strength + 25;
            if(symbolFlag) strength = strength + 25;
            Log.d("strength ", strength+"");
            return  strength;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode == SETTING_REQUEST_CODE){
                if(data!=null){
                    Bundle extras = data.getExtras();
                    if(extras!=null && extras.getBoolean("changeInGridGeneration")) {
                        resetGrid();
                        isInitialized = false;
                        //if(!getPreferences().showWordFromBorder() && !getPreferences().selectedTypeManually())
                        isScaled = false;
                        topBorderLeftMargin = 0;
                        rowCount = extras.getInt(EXTRA_ROW_COUNT);
                        colCount = extras.getInt(EXTRA_COL_COUNT);
                        defaultBoardWidth();
                        Preferences preferences = getPreferences();
                        mViewModel.setGridGenerationCriteria(preferences.showUpperCharacters(), preferences.showLowerCharacters(), preferences.showNumberCharacters(), preferences.showSpecialCharacters());
                        mViewModel.setGridChosenOption(getCurrentChosenOption());
                        //mViewModel.setPasswordLength(getPreferences().getPasswordLength());
                        mViewModel.setSelectedTypedWord(mTextFromBorder.getText().toString());
                        mViewModel.generateNewGridRound(rowCount, colCount);
                    }else {
                        /*resetGrid();
                        if(TextUtils.isEmpty(mTextSelection.getText().toString()))
                        generateDefaultPassword();*/
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void defaultBoardWidth(){
       // Log.d("boardWidth ", ""+mLetterBoard.getWidth());
        mLetterBoard.getLetterGrid().setColCount(colCount);
        mLetterBoard.getLetterGrid().setRowCount(rowCount);
        mLetterBoardTop.getLetterGrid().setColCount(colCount);
        mLetterBoardLeft.getLetterGrid().setRowCount(rowCount);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int adjCol = colCount;
        if(colCount<=18) adjCol = 18;
        Log.d("colCount "+colCount, "adjCol "+adjCol);

        int totalWidth = metrics.widthPixels - (int) Util.convertDpToPx(this, 20);
        int screenWidth =  totalWidth - (totalWidth/adjCol);
        int gridWidth = (totalWidth/adjCol);
        if((screenWidth%adjCol>=(adjCol/2))) gridWidth = gridWidth -1;
        Log.d("before scale gridWidth ",  ""+gridWidth);
        if((gridWidth*colCount)>screenWidth){
            int diff = (gridWidth*colCount) - screenWidth +  (int)Util.convertDpToPx(this, 10);
            Log.d("boardWidth "+(gridWidth*colCount), " screenWidth "+screenWidth +" diff "+diff);
            gridWidth = gridWidth - ((diff/colCount)+1);
            Log.d("after scale gridWidth ",  ""+gridWidth);
        }
        //Log.d("gridWidth ", ""+gridWidth);
            mLetterBoard.setGridWidth(gridWidth);
            mLetterBoard.setGridHeight(gridWidth);
            mLetterBoard.setStreakWidth(gridWidth);
            mLetterBoard.setLetterSize(Util.spToPx(15f, this));
            if(colCount>18) mLetterBoard.setLetterSize(Util.spToPx(13f, this));
            //topBorderLeftMargin = (int) Util.convertDpToPx(this, 10f);

            mLetterBoardTop.setGridWidth(gridWidth);
            mLetterBoardTop.setGridHeight(gridWidth);
            mLetterBoardTop.setStreakWidth(gridWidth);
            mLetterBoardTop.setLetterSize(Util.spToPx(15f, this));
            if(colCount>18) mLetterBoardTop.setLetterSize(Util.spToPx(13f, this));

            mLetterBoardLeft.setGridWidth(gridWidth);
            mLetterBoardLeft.setGridHeight(gridWidth);
            mLetterBoardLeft.setStreakWidth(gridWidth);
            mLetterBoardLeft.setLetterSize(Util.spToPx(15f, this));
            if(colCount>18) mLetterBoardLeft.setLetterSize(Util.spToPx(13f, this));
        }
}
