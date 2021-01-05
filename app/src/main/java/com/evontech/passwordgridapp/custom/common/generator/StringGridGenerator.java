package com.evontech.passwordgridapp.custom.common.generator;


import com.evontech.passwordgridapp.custom.models.Grid;

/**
 * Created by Suraj Kumar on 17/12/20.
 *
 * Parse dataInput array grid[][]
 */

public class StringGridGenerator extends GridGenerator<String, Boolean> {

    @Override
    public Boolean setGrid(String dataInput, char[][] grid) {
        if (dataInput == null || grid == null) return false;

        dataInput = dataInput.trim();

        int row = 0;
        int col = 0;

        for (int i = 0; i < dataInput.length(); i++) {
            char c = dataInput.charAt(i);

            if (c == Grid.GRID_NEWLINE_SEPARATOR) {
                row++;
                col = 0;
            }
            else {
                if (row >= grid.length || col >= grid[0].length) {
                    resetGrid(grid);
                    return false;
                }

                grid[row][col] = c;

                col++;
            }
        }

        return true;
    }
}
