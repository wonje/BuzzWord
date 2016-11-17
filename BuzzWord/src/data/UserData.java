package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import controller.GameState;

import java.util.HashMap;

/**
 * @author Jason Kang
 */
public class UserData implements AppDataComponent{
    public AppTemplate appTemplate;
    public GameData gameData;
    private HashMap<Integer, Integer> dicBestPoints;
    private HashMap<Integer, Integer> placeBestPoints;
    private HashMap<Integer, Integer> scienceBestPoints;
    private HashMap<Integer, Integer> famousBestPoints;
    private String userID;
    private String userPW;

    public UserData(AppTemplate appTemplate){
        this.appTemplate    = appTemplate;
        init();
    }

    public void init()
    {
        userID = "";
        userPW = "";
        dicBestPoints = new HashMap<Integer, Integer>();
        placeBestPoints = new HashMap<Integer, Integer>();
        scienceBestPoints = new HashMap<Integer, Integer>();
        famousBestPoints = new HashMap<Integer, Integer>();
    }

    @Override
    public void reset()
    {
        userID = "";
        userPW = "";
        dicBestPoints.clear();
        placeBestPoints.clear();
        scienceBestPoints.clear();
        famousBestPoints.clear();
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
                return dicBestPoints.get(level);
            case PLACES:
                return placeBestPoints.get(level);
            case SCIENCE:
                return scienceBestPoints.get(level);
            case FAMOUS_PEOPLE:
                return famousBestPoints.get(level);
        }
        // TODO RETURN ERROR
        return -1;
    }

    public boolean checkBestPoint(GameState mode, int level, int point)
    {
        switch (mode)
        {
            case ENGLISH_DICTIONARY:
                if(point >= dicBestPoints.get(level))
                {
                    dicBestPoints.put(level, point);
                    return true;
                }
                else
                    return false;
            case PLACES:
                if(point >= placeBestPoints.get(level))
                {
                    placeBestPoints.put(level, point);
                    return true;
                }
                else
                    return false;
            case SCIENCE:
                if(point >= scienceBestPoints.get(level))
                {
                    scienceBestPoints.put(level, point);
                    return true;
                }
                else
                    return false;
            case FAMOUS_PEOPLE:
                if(point >= famousBestPoints.get(level))
                {
                    famousBestPoints.put(level, point);
                    return true;
                }
                else
                    return false;
        }
        // TODO RETURN ERROR
        return false;
    }
}
