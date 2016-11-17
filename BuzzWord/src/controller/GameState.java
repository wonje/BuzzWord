package controller;

/**
 * @author Jason Kang
 */
public enum GameState {

    // GAME STATE OPTIONS
    UNLOGIN,
    LOGIN,
    LOGIN_MODE,
    CREATE_PROFILE,
    HOME,
    LEVEL_SELECTION,
    PLAY,
    PAUSE,
    END_FAIL,
    END_SUCCESS,

    // GAME MODE OPTIONS
    ENGLISH_DICTIONARY,
    PLACES,
    SCIENCE,
    FAMOUS_PEOPLE;

    public static GameState currentState;
    public static GameState currentMode;
    public static int       currentLevel;
    public static void loadRecentMode(String mode)
    {
        switch (mode)
        {
            case "ENGLISH_DICTIONARY":
                currentMode = ENGLISH_DICTIONARY;
                break;
            case "PLACES":
                currentMode = PLACES;
                break;
            case "SCIENCE":
                currentMode = SCIENCE;
                break;
            case "FAMOUS_PEOPLE":
                currentMode = FAMOUS_PEOPLE;
                break;
            default:
        }

    }
}
