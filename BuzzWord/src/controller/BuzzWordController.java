package controller;

/**
 * @author Jason Kang
 */
public class BuzzWordController {



    public enum GameState {
        UNLOGIN,
        LOGIN,
        CREATE_PROFILE,
        HOME,
        LEVEL_SELECTION,
        PLAY,
        END_FAIL,
        END_SUCCESS
    }
}
