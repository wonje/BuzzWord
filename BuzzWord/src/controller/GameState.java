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

    // GAME PLAY WAY
    MOUSE,
    KEYBOARD,

    // GAME MODE OPTIONS
    ENGLISH_DICTIONARY,
    BACTERIA,
    BIOLOGY,
    FUNGI;

    public static GameState currentState;
    public static GameState currentMode;
    public static GameState currentPlay;
    public static int       currentLevel;
    public static void loadRecentMode(String mode)
    {
        switch (mode)
        {
            case "ENGLISH_DICTIONARY":
                currentMode = ENGLISH_DICTIONARY;
                break;
            case "BACTERIA":
                currentMode = BACTERIA;
                break;
            case "BIOLOGY":
                currentMode = BIOLOGY;
                break;
            case "FUNGI":
                currentMode = FUNGI;
                break;
            default:
        }

    }
}
