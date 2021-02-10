package com.evontech.passwordgridapp.custom.data;

import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public interface GridDataSource {

    interface GridRoundCallback {

        void onLoaded(GridDataEntity gridDataEntity);

    }

    interface InfosCallback {

        void onLoaded(List<GridDataInfo> infoList);
    }

    interface StatCallback {

        void onLoaded(GridDataInfo gridDataInfo);

    }

    void getGridData(int gid, GridRoundCallback callback);

    void getGridDataInfos(InfosCallback callback);

    void getGridDataInfo(int gid, StatCallback callback);

    long saveGridData(GridDataEntity gridDataEntity);

    void deleteGridData(int gid);

    void deleteGridDatas();

    void saveGridDataDuration(int gid, int newDuration);

    void markWordAsAnswered(int index, UsedWord usedWord);

    void deleteAllLines(int gid);

    int updateAccountInfo(UserAccount account);
}
