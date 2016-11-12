package controller;

/**
 * @author Jason Kang
 */
public enum GameState {

    UNLOGIN,
    LOGIN,
    LOGIN_MODE,
    CREATE_PROFILE,
    HOME,
    LEVEL_SELECTION,
    PLAY,
    END_FAIL,
    END_SUCCESS;

    public static GameState currentState;
}
