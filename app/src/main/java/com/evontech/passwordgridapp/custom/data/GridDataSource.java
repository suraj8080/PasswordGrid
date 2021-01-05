package com.evontech.passwordgridapp.custom.data;

import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;

import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public interface GridDataSource {

    interface GameRoundCallback {

        void onLoaded(GridDataEntity gameRound);

    }

    interface InfosCallback {

        void onLoaded(List<GridDataInfo> infoList);
    }

    interface StatCallback {

        void onLoaded(GridDataInfo gridDataInfo);

    }

    void getGameData(int gid, GameRoundCallback callback);

    void getGameDataInfos(InfosCallback callback);

    void getGameDataInfo(int gid, StatCallback callback);

    long saveGameData(GridDataEntity gameRound);

    void deleteGameData(int gid);

    void deleteGameDatas();

    void saveGameDataDuration(int gid, int newDuration);

    void markWordAsAnswered(UsedWord usedWord);
}
