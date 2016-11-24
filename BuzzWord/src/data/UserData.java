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
    HashMap<Integer, Integer> placeBestScores;
    HashMap<Integer, Integer> scienceBestScores;
    HashMap<Integer, Integer> famousBestScores;
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
        placeBestScores = new HashMap<Integer, Integer>();
        scienceBestScores = new HashMap<Integer, Integer>();
        famousBestScores = new HashMap<Integer, Integer>();
    }

    @Override
    public void reset()
    {
        userID = "";
        userPW = "";
        dicBestScores.clear();
        placeBestScores.clear();
        scienceBestScores.clear();
        famousBestScores.clear();
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
            case PLACES:
                return placeBestScores.get(level);
            case SCIENCE:
                return scienceBestScores.get(level);
            case FAMOUS_PEOPLE:
                return famousBestScores.get(level);
        }
        // TODO RETURN ERROR
        return -1;
    }

    public boolean checkBestPoint(GameState mode, int level, int point)
    {
        switch (mode)
        {
            case ENGLISH_DICTIONARY:
                if(point >= dicBestScores.get(level))
                {
                    dicBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case PLACES:
                if(point >= placeBestScores.get(level))
                {
                    placeBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case SCIENCE:
                if(point >= scienceBestScores.get(level))
                {
                    scienceBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
            case FAMOUS_PEOPLE:
                if(point >= famousBestScores.get(level))
                {
                    famousBestScores.put(level, point);
                    return true;
                }
                else
                    return false;
        }
        // TODO RETURN ERROR
        return false;
    }
}
