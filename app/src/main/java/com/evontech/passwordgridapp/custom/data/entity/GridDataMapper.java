package com.evontech.passwordgridapp.custom.data.entity;

import com.evontech.passwordgridapp.custom.common.Mapper;
import com.evontech.passwordgridapp.custom.common.generator.StringGridGenerator;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.Grid;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridDataMapper extends Mapper<GridDataEntity, GridData> {
    @Override
    public GridData map(GridDataEntity obj) {
        if (obj == null) return null;

        Grid grid = new Grid(obj.getGridRowCount(), obj.getGridColCount());
        GridData gr = new GridData();
        gr.setId(obj.getId());
        gr.setName(obj.getName());
        gr.setDuration(obj.getDuration());
        gr.setGrid(grid);

        if (obj.getGridData() != null && obj.getGridData().length() > 0) {
            new StringGridGenerator().setGrid(obj.getGridData(), grid.getArray());
        }

        gr.addUsedWords(obj.getUsedWords());

        return gr;
    }

    @Override
    public GridDataEntity revMap(GridData obj) {
        if (obj == null) return null;

        GridDataEntity ent = new GridDataEntity();
        ent.setId(obj.getId());
        ent.setName(obj.getName());
        ent.setDuration(obj.getDuration());

        if (obj.getGrid() != null) {
            ent.setGridRowCount(obj.getGrid().getRowCount());
            ent.setGridColCount(obj.getGrid().getColCount());
            ent.setGridData(obj.getGrid().toString());
        }

        ent.setUsedWords(obj.getUsedWords());

        return ent;
    }
}
