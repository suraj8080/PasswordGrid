package com.evontech.passwordgridapp.custom.common.generator;

import android.util.Log;

import com.evontech.passwordgridapp.custom.common.Direction;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.grid.GridDataCreator;

import java.util.ArrayList;
import java.util.List;

import static com.evontech.passwordgridapp.custom.common.Direction.NORTH_EAST;
import static com.evontech.passwordgridapp.custom.common.Direction.NORTH_WEST;
import static com.evontech.passwordgridapp.custom.common.Direction.SOUTH_EAST;
import static com.evontech.passwordgridapp.custom.common.Direction.SOUTH_WEST;

/**
 * Created by Suraj Kumar on 17/12/20.
 *
 * Try to pack all string list into grid array
 */

public class StringListGridGenerator extends GridGenerator<List<String>, List<String>> {

    private static final int MIN_GRID_ITERATION_ATTEMPT = 1;

    @Override
    public List<String> setGrid(List<String> dataInput, char[][] grid) {
//        Util.sortByLength(dataInput);

        List<String> usedStrings = new ArrayList<>();
        int usedCount;
        if(GridDataCreator.type.equals("Play me")) {
            for (int i = 0; i < MIN_GRID_ITERATION_ATTEMPT; i++) {

                usedCount = 0;
                usedStrings.clear();
                resetGrid(grid);
                for (String word : dataInput) {
                    if (tryPlaceWord(word, grid)) {
                        usedCount++;
                        usedStrings.add(word);
                    }
                }

                if (usedCount >= dataInput.size())
                    break;
            }
            Util.fillNullCharWidthRandom(grid);
        }


        /*
        iterate grids here and pick each cells horizontal, verticals and diagonals set of characters
         (should contains as per selected user criteria upper, lower, special symbols, numbers if not
         replace whole set of characters with combination of all selected criteria)
         */
        if(GridDataCreator.type.equals("Play me")) {
            for (int i = 0; i < grid.length; i++) {
                String eastWestWord = getWordByDirection(i, 0, Direction.EAST, grid);
                if(!GridDataCreator.checkPasswordCriteria(eastWestWord).equals("true"))
                    placeWordAt(i, 0, Direction.EAST, grid, GridDataCreator.getRandomWords(eastWestWord.length()-1));
               // Log.d("eastWestWord ", " i "+i +" j "+0 + " " + eastWestWord);
                for (int j = 0; j < grid[i].length; j++) {
                    if(i==0) {
                        String northSouthWord = getWordByDirection(0, j, Direction.SOUTH, grid);
                        if(!GridDataCreator.checkPasswordCriteria(northSouthWord).equals("true"))
                            placeWordAt(0, j, Direction.SOUTH, grid, GridDataCreator.getRandomWords(northSouthWord.length()-1));
                      //  Log.d("northSouthWord ", " i " + 0 + " j " + j + " " + northSouthWord);
                    }if(i==0 && j==0) {
                        //String southEastNorthWestWord = getWordByDirection(i, j, SOUTH_EAST, grid);
                      //  Log.d("southEastNorthWestWord ", " i " + i + " j " + j + " " + southEastNorthWestWord);
                    }if(i==0 && j==grid[i].length-1) {
                       // String southWestNorthEastWord = getWordByDirection(i, j, SOUTH_WEST, grid);
                         //Log.d("southWestNorthEastWord ", " i " + i + " j " + j + " " + southWestNorthEastWord);
                    }
                }
            }
        }

        return usedStrings;
    }

    private Direction getRandomDirection() {
        Direction dir;
        do {
            dir = Direction.values()[ Util.getRandomInt() % Direction.values().length ];
        } while (dir == Direction.NONE);
        return dir;
    }


    private boolean tryPlaceWord(String word, char gridArr[][]) {
        Direction startDir = getRandomDirection();
        Direction currDir = startDir;

        int row;
        int col;
        int startRow;
        int startCol;

        /*

         */
        do {

            /*
                row currDir
             */
            startRow = Util.getRandomInt() % gridArr.length;
            row = startRow;
            do {

                /*
                    column row currDir
                 */
                startCol = Util.getRandomInt() % gridArr[0].length;
                col = startCol;
                do {

                    if (isValidPlacement(row, col, currDir, gridArr, word)) {
                        placeWordAt(row, col, currDir, gridArr, word);
                        return true;
                    }

                    col = (++col) % gridArr[0].length;
                } while (col != startCol);

                row = (++row) % gridArr.length;
            } while (row != startRow);

            currDir = currDir.nextDirection();
        } while (currDir != startDir);

        return false;
    }

    /**
     * Check grid col dan row dir string word
     *
     *
     * @param col starting column
     * @param row starting row
     * @param dir direction of the word
     * @param gridArr grid where the word will be placed
     * @param word the actual word to be checked
     * @return true if it is a valid placement, false otherwise
     */
    private boolean isValidPlacement(int row, int col, Direction dir, char gridArr[][], String word) {
        int wLen = word.length();
        if (dir == Direction.EAST && (col + wLen) >= gridArr[0].length) return false;
        if (dir == Direction.WEST && (col - wLen) < 0) return false;

        if (dir == Direction.NORTH && (row - wLen) < 0) return false;
        if (dir == Direction.SOUTH && (row + wLen) >= gridArr.length) return false;

        if (dir == SOUTH_EAST && ((col + wLen) >= gridArr[0].length || (row + wLen) >= gridArr.length)) return false;
        if (dir == Direction.NORTH_WEST && ((col - wLen) < 0 || (row - wLen) < 0)) return false;

        if (dir == Direction.SOUTH_WEST && ((col - wLen) < 0 || (row + wLen) >= gridArr.length)) return false;
        if (dir == Direction.NORTH_EAST && ((col + wLen) >= gridArr[0].length || (row - wLen) < 0)) return false;

        for (int i = 0; i < wLen; i++) {
            if (gridArr[row][col] != Util.NULL_CHAR && gridArr[row][col] != word.charAt(i))
                return false;
            col += dir.xOff;
            row += dir.yOff;
        }

        return true;
    }

    /**
     * word row, col dir grid array.
     */
    private void placeWordAt(int row, int col, Direction dir, char gridArr[][], String word) {
        for (int i = 0; i < word.length(); i++) {
            gridArr[row][col] = word.charAt(i);

            col += dir.xOff;
            row += dir.yOff;
        }
    }

    public static void placeRandomWordAt(int row, int col, Direction dir, char gridArr[][], String word) {
        for (int i = 0; i < word.length(); i++) {
            gridArr[row][col] = word.charAt(i);

            col += dir.xOff;
            row += dir.yOff;
        }
    }

    private String getWordByDirection(int row, int col, Direction dir, char gridArr[][]){
        StringBuilder word = new StringBuilder();
        /*
        Horizontal direction
         */
        if(dir == Direction.EAST || dir == Direction.WEST) {
            for (int colIndex = 0; colIndex < gridArr[0].length; colIndex++)
                word.append(gridArr[row][colIndex]);
        }
        /*
        Vertical direction
         */
        if(dir == Direction.NORTH || dir == Direction.SOUTH){
            for (char[] chars : gridArr) word.append(chars[col]);
         }
        /*
        Diagonal 00 to row col direction
         */
        if(dir == SOUTH_EAST || dir == NORTH_WEST){
            int startRow = row;
            int startCol = col;
            while (startRow>0 && startCol>0){
                startRow --;
                startCol --;
            }
            //Log.d("gridLength ",""+gridArr.length);
            while (startRow<gridArr.length && startCol<gridArr.length){
                word.append(gridArr[startRow][startCol]);
                startRow ++;
                startCol ++;
            }
        }
        /*
        Diagonal 0col to row 0 direction
         */
        if(dir == SOUTH_WEST || dir == NORTH_EAST){
            int startRow = row;
            int startCol = col;
            while (startRow>0 && startCol<gridArr.length-1){
               startRow--;
               startCol++;
            }
            while (startRow<gridArr.length && startCol>=0){
                    word.append(gridArr[startRow][startCol]);
                    if(startRow<gridArr.length) {
                        startRow++;
                        startCol--;
                    }else if(startCol==0 || startRow == gridArr.length) {
                        word.append(gridArr[startRow][startCol]);
                        break;
                    }
            }
        }

        return word.toString();
    }

    public static String getWordByDirection(Direction dir, int row, int col, int endRow, int endCol, char[][] gridArr){
        StringBuilder word = new StringBuilder();


        if(dir==Direction.NONE && row==endRow && col==endCol){
            word.append(gridArr[row][col]);
        }

        /*
        Horizontal direction
         */

        if(dir == Direction.EAST || dir == Direction.WEST) {
            if(dir==Direction.EAST){
                for (int colIndex = col; colIndex <= endCol; colIndex++)
                    word.append(gridArr[row][colIndex]);
            }else {
            for (int colIndex = col; colIndex >= endCol; colIndex--)
                word.append(gridArr[row][colIndex]);
            }
        }
        /*
        Vertical direction
         */
        if(dir == Direction.NORTH || dir == Direction.SOUTH){
            if(dir==Direction.NORTH){
                for (int rowIndex = row; rowIndex >= endRow; rowIndex--)
                    word.append(gridArr[rowIndex][col]);
            }else {
                for (int rowIndex = row; rowIndex <= endRow; rowIndex++)
                    word.append(gridArr[rowIndex][col]);
            }
        }
        /*
        Diagonal 00 to row col direction
         */
        if(dir == SOUTH_EAST || dir == NORTH_WEST){
            int startRow = row;
            int startCol = col;
            if(dir == SOUTH_EAST){
                while (startRow<=endRow && startCol<=endCol){
                    word.append(gridArr[startRow][startCol]);
                    startRow ++;
                    startCol ++;
                }
            }else {
                while (startRow >= endRow && startCol >= endCol) {
                    word.append(gridArr[startRow][startCol]);
                    startRow--;
                    startCol--;
                }
            }
        }

        if(dir == SOUTH_WEST || dir == NORTH_EAST){
            int startRow = row;
            int startCol = col;
            if(dir == SOUTH_WEST){
                while (startRow<=endRow && startCol>=endCol){
                    word.append(gridArr[startRow][startCol]);
                    startRow ++;
                    startCol --;
                }
            }else {
                while (startRow >= endRow && startCol <= endCol) {
                    word.append(gridArr[startRow][startCol]);
                    startRow--;
                    startCol++;
                }
            }
        }

        /*
        Diagonal 0col to row 0 direction
         */
        /*if(dir == SOUTH_WEST || dir == NORTH_EAST){
            int startRow = row;
            int startCol = col;
            while (startRow>0 && startCol<gridArr.length-1){
                startRow--;
                startCol++;
            }
            while (startRow<gridArr.length && startCol>=0){
                word.append(gridArr[startRow][startCol]);
                if(startRow<gridArr.length) {
                    startRow++;
                    startCol--;
                }else if(startCol==0 || startRow == gridArr.length) {
                    word.append(gridArr[startRow][startCol]);
                    break;
                }
            }
        }*/
        return word.toString();
    }
}
