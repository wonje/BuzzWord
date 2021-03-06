package settings;

/**
 * This enum provides properties that are to be loaded via
 * XML files to be used for setting up the application.
 *
 * @author Jason Kang
 * @author ?
 * @version 1.0
 */
public enum AppPropertyType {

    // from app-properties.xml
    APP_WINDOW_WIDTH,
    APP_WINDOW_HEIGHT,
    APP_TITLE,
    APP_LOGO,
    APP_CSS,
    APP_PATH_CSS,

    // APPLICATION TOOLTIPS FOR BUTTONS
    CREATE_ID_TOOLTIP,
    SETTING_PROFILE_TOOLTIP,
    LOGIN_TOOLTIP,
    DISPLAY_ID_TOOLTIP,
    SELECT_MODE_TOOLTIP,
    HOME_TOOLTIP,
    PLAYING_TOOLTIP,

    // BUTTON CSS
    MENU_BUTTON,
    MENU_IMAGE,
    MENU_ID_IMAGE,
    MENU_MODE_IMAGE,
    MENU_MODEDISPLAY_IMAGE,
    MODE_LIST,
    
    // SCREEN CSS
//    HELP_SCREEN,
    

    // ERROR MESSAGES
    NEW_ERROR_MESSAGE,
    SAVE_ERROR_MESSAGE,
    PROPERTIES_LOAD_ERROR_MESSAGE,
    TARGET_LOAD_ERROR,

    // ERROR TITLES
    NEW_ERROR_TITLE,
    SAVE_ERROR_TITLE,
    PROPERTIES_LOAD_ERROR_TITLE,
    TARGET_LOAD_ERROR_TITLE,

    // AND VERIFICATION MESSAGES AND TITLES
    SAVE_COMPLETED_MESSAGE,
    SAVE_COMPLETED_TITLE,
    SAVE_UNSAVED_WORK_TITLE,
    SAVE_UNSAVED_WORK_MESSAGE,
    CREATE_PROFILE_TITLE,
    CREATE_PROFILE_MESSAGE,
    LOGIN_TITLE,
    LOGIN_MESSAGE,

    SAVE_WORK_TITLE,
    LOAD_WORK_TITLE,
    WORK_FILE_EXT,
    WORK_FILE_EXT_DESC,
    LOAD_COMPLETED_MESSAGE,
    LOAD_COMPLETED_TITLE,

    GAME_WON_MESSAGE,
    GAME_LOST_MESSAGE,
    GAME_OVER_TITLE
}
