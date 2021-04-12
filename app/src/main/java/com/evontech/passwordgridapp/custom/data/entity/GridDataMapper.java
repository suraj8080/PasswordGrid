package com.evontech.passwordgridapp.custom.data.entity;

import android.util.Log;

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
        //Log.d("id ", ""+gr.getId());
        //Log.d("name ", ""+gr.getName());
        //Log.d("grid ", ""+gr.getGrid());
        //Log.d("row  "+obj.getGridRowCount(), "col "+obj.getGridColCount());
        if (obj.getGridData() != null && obj.getGridData().length() > 0) {
            new StringGridGenerator().setGrid(obj.getGridData(), grid.getArray());
            gr.setmSelectionCriteria(obj.getmSelectionCriteria());
            gr.setmChosenOption(obj.getmChosenOption());
            gr.setmSelectedTypedWord(obj.getmSelectedTypedWord());
            gr.setmGridPasswordLength(obj.getmGridPasswordLength());
            gr.setUpdatedPassword(obj.getUpdatedPassword());
        }

        if(obj.getUsedWords()!=null && obj.getUsedWords().size()>0)
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
            ent.setmSelectionCriteria(obj.getmSelectionCriteria());
            ent.setmChosenOption(obj.getmChosenOption());
            ent.setmSelectedTypedWord(obj.getmSelectedTypedWord());
            ent.setmGridPasswordLength(obj.getmGridPasswordLength());
            ent.setUpdatedPassword(obj.getUpdatedPassword());
        }
        if(obj.getUsedWords()!=null && obj.getUsedWords().size()>0)
        ent.setUsedWords(obj.getUsedWords());

        return ent;
    }
}
