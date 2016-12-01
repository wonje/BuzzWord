package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import controller.GameState;

import java.util.HashMap;

/**
 * @author Jason Kang
 */
public class UserData implements AppDataComponent{
    AppTemplate appTemplate;
    GameData gameData;
    HashMap<Integer, Integer> dicBestScores;
    HashMap<Integer, Integer> bacteriaBestScores;
    HashMap<Integer, Integer> biologyBestScores;
    HashMap<Integer, Integer> fungiBestScores;
    String userID;
    String userPW;

    public UserData(AppTemplate appTemplate){
        this.appTemplate    = appTemplate;
        init();
    }

    public void init()
    {
        userID = "";
        userPW = "";
        dicBestScores = new HashMap<Integer, Integer>();
        bacteriaBestScores = new HashMap<Integer, Integer>();
        biologyBestScores = new HashMap<Integer, Integer>();
        fungiBestScores = new HashMap<Integer, Integer>();
    }

    @Override
    public void reset()
    {
        userID = "";
        userPW = "";
        dicBestScores.clear();
        bacteriaBestScores.clear();
        biologyBestScores.clear();
        fungiBestScores.clear();
    }

    public void setUserInfo(String userID, String userPW){
        this.userID = userID;
        this.userPW = userPW;
    }

    public void setGameData(GameData gameData)
    {
        this.gameData = gameData;
    }

    public int getBestPoint(GameState mode, int level){
        switch (mode)
        {
            case ENGLISH_DICTIONARY:
                return dicBestScores.get(level);
            case BACTERIA:
                return bacteriaBestScores.get(level);
            case BIOLOGY:
                return biologyBestScores.get(level);
            case FUNGI:
                return fungiBestScores.get(level);
        }
        // TODO RETURN ERROR
        return -1;
    }

    public HashMap<Integer, Integer> getCurrentModeScores() {
        switch (GameState.currentMode) {
            case ENGLISH_DICTIONARY:
                return dicBestScores;
            case BACTERIA:
                return bacteriaBestScores;
            case BIOLOGY:
                return biologyBestScores;
            case FUNGI:
                return fungiBestScores;
        }
        return null;
    }

    public boolean checkAndSaveBestPoint(GameState mode, int level, int point)
    {
        switch (mode)
        {
            case ENGLISH_DICTIONARY:
                if(point > dicBestScores.get(level))
                {
                    dicBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case BACTERIA:
                if(point > bacteriaBestScores.get(level))
                {
                    bacteriaBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case BIOLOGY:
                if(point > biologyBestScores.get(level))
                {
                    biologyBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case FUNGI:
                if(point > fungiBestScores.get(level))
                {
                    fungiBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
        }
        // TODO RETURN ERROR
        return false;
    }
}
