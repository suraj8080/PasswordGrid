package com.evontech.passwordgridapp.custom.common.generator;

import java.util.Arrays;

/**
 * Created by Suraj Kumar on 17/12/20.
 *
 * Base class grid generator
 */

public abstract class GridGenerator<InputType, OutputValue> {

    public abstract OutputValue setGrid(InputType dataInput, char[][] grid);

    protected void resetGrid(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            Arrays.fill(grid[i], '\0');
        }
    }

}
