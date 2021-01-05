package com.evontech.passwordgridapp.custom.common;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.evontech.passwordgridapp.custom.grid.GridDataCreator;
import com.evontech.passwordgridapp.custom.mcustom.LetterGridDataAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class Util {
    public static final char NULL_CHAR = '\0';

    private static Random sRand = new Random();

    public static int getRandomColorWithAlpha(int alpha) {
        int r = getRandomInt() % 256;
        int g = getRandomInt() % 256;
        int b = getRandomInt() % 256;
        return Color.argb(alpha, r, g, b);
    }

    public static char getRandomChar() {
        Random random = new Random();
        ArrayList<Character> characterArrayList = new ArrayList<>();
        if (GridDataCreator.isSpecialCharacters) {
            characterArrayList.add((char)(random.nextInt(4) + 35));
        }if(GridDataCreator.isUpperCase) {
            // ASCII A = 65 - Z = 90
            characterArrayList.add((char) getRandomIntRange(65, 90));
        } if(GridDataCreator.isLowerCase){
            // ASCII a = 97 - z = 122
            characterArrayList.add((char) getRandomIntRange(97, 122));
        } if(GridDataCreator.isNumbers){
            // ASCII 0 - 9
            characterArrayList.add((char) Character.forDigit(random.nextInt(10), 10));
        }

        return characterArrayList.get(random.nextInt(characterArrayList.size()));
    }

    /**
     * generate random integer between min and max (inclusive)
     * example: min = 5, max = 7 output would be (5, 6, 7)
     *
     * @param min minimum integer number to be generated
     * @param max maximum integer number to be generated (inclusive)
     * @return integer between min - max
     */
    public static int getRandomIntRange(int min, int max) {
        return min + (getRandomInt() % ((max - min) + 1));
    }

    public static int getRandomInt() {
        return Math.abs(sRand.nextInt());
    }


    public static String getStringInRange(LetterGridDataAdapter mData, GridIndex start, GridIndex end) {
        Direction dir = Direction.fromLine(start, end);
        if (dir == Direction.NONE) return "";

        int count = Util.getIndexLength(start, end);
        char buff[] = new char[count];
        for (int i = 0; i < count; i++) {
            buff[i] = mData.getLetter(start.row + (dir.yOff * i), start.col + (dir.xOff * i));
        }

        return String.valueOf(buff);
    }

    public static int getIndexLength(GridIndex start, GridIndex end) {
        int x = Math.abs(start.col - end.col);
        int y = Math.abs(start.row - end.row);
        return Math.max(x, y) + 1;
    }

    public static <T> void randomizeList(List<T> list) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            int randIdx = getRandomIntRange(Math.min(i + 1, count - 1), count - 1);
            T temp = list.get(randIdx);
            list.set(randIdx, list.get(i));
            list.set(i, temp);
        }
    }

    public static String getReverseString(String str) {
        StringBuilder out = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--)
            out.append(str.charAt(i));

        return out.toString();
    }

    /**
     * Isi slot / element yang masih kosong dengan karakter acak
     *
     */
    public static void fillNullCharWidthRandom(char gridArr[][]) {
        for (int i = 0; i < gridArr.length; i++) {
            for (int j = 0; j < gridArr[i].length; j++) {
                if (gridArr[i][j] == NULL_CHAR)
                    gridArr[i][j] = getRandomChar();
            }
        }
    }

    /**
     * Urutkan list strings dari panjang string yang terbesar ke terkecil
     *
     */
    public static void sortByLength(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            for (int j = i + 1; j < strings.size(); j++) {
                if (strings.get(j).length() > strings.get(i).length()) {
                    String temp = strings.get(j);
                    strings.set(j, strings.get(i));
                    strings.set(i, temp);
                }
            }
        }
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

}
