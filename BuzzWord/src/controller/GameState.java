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
    END_FAIL,
    END_SUCCESS,

    // GAME MODE OPTIONS
    ENGLISH_DICTIONARY,
    PLACES,
    SCIENCE,
    FAMOUS_PEOPLE;

    public static GameState currentState;
    public static GameState currentMode;
}
