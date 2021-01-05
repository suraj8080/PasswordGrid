package com.evontech.passwordgridapp.custom.mcustom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;


import androidx.annotation.Nullable;

import com.evontech.passwordgridapp.R;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class LetterGrid extends GridBehavior implements Observer {

    private Paint mPaint;
    private Rect mCharBounds;
    private LetterGridDataAdapter mData;
    private String type;

    public LetterGrid(Context context) {
        super(context);
        init(context, null);
    }

    public LetterGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public float getLetterSize() {
        return mPaint.getTextSize();
    }

    public int getLetterColor() {
        return mPaint.getColor();
    }

    public LetterGridDataAdapter getDataAdapter() {
        return mData;
    }

    public void setLetterColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setLetterSize(float letterSize) {
        mPaint.setTextSize(letterSize);
        invalidate();
    }

    public void setDataAdapter(LetterGridDataAdapter data, String type) {
        this.type = type;
        if (data == null) {
            throw new IllegalArgumentException("Data Adapater can't be null");
        }
        else if (data != mData) {
            if (mData != null) mData.deleteObserver(this);

            mData = data;
            mData.addObserver(this);

            invalidate();
            requestLayout();
        }
    }

    @Override
    public int getColCount() {
        return mData.getColCount();
    }

    @Override
    public int getRowCount() {
        return mData.getRowCount();
    }

    @Override
    public void setColCount(int colCount) {
        // do nothing
    }

    @Override
    public void setRowCount(int rowCount) {
        // do nothing
    }

    @Override
    public void update(Observable o, Object arg) {
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int gridColCount = getColCount();
        int gridRowCount = getRowCount();

        int halfWidth = getGridWidth() / 2;
        int halfHeight = getGridHeight() / 2;

        int x;
        int y = halfHeight + getPaddingTop();

        /*if(type!=null && type.length()>0)
            Log.d("LetterGrid type "+type, "mData "+mData);
        Log.d("LetterGrid gridColCount "+gridColCount, "gridRowCount "+gridRowCount);*/

        // iterate and render all letters found in grid data adapter
        for (int i = 0; i < gridRowCount; i++) {

            x = halfWidth + getPaddingLeft();
            for (int j = 0; j < gridColCount; j++) {
                if(type!=null && type.equals("leftGrid")){
                    String letter = mData.getLetter(i, j, type);
                    mPaint.getTextBounds(letter, 0, letter.length(), mCharBounds);
                    canvas.drawText(letter,
                            x - mCharBounds.exactCenterX(), y - mCharBounds.exactCenterY(), mPaint);
                    x += getGridWidth();
                }else {
                    char letter = mData.getLetter(i, j);
                    mPaint.getTextBounds(String.valueOf(letter), 0, 1, mCharBounds);
                    canvas.drawText(String.valueOf(letter),
                            x - mCharBounds.exactCenterX(), y - mCharBounds.exactCenterY(), mPaint);
                    x += getGridWidth();
                }
            }
            y += getGridHeight();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(13.0f);
        mCharBounds = new Rect();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LetterGrid, 0, 0);

            mPaint.setTextSize(a.getDimension(R.styleable.LetterGrid_letterSize, mPaint.getTextSize()));
            mPaint.setColor(a.getColor(R.styleable.LetterGrid_letterColor, Color.GRAY));

            a.recycle();
        }

        setDataAdapter(new SampleLetterGridDataAdapter(16, 16), "");
    }

}
