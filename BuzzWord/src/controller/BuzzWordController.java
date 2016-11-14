package controller;

import static settings.AppPropertyType.*;

import apptemplate.AppTemplate;
import gui.Workspace;
import propertymanager.PropertyManager;


/**
 * @author Jason Kang
 */
public class BuzzWordController implements FileController {

    private AppTemplate appTemplate;
    private Workspace gameWorkspace;

    public BuzzWordController(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        GameState.currentState = GameState.UNLOGIN;
        GameState.currentMode = GameState.ENGLISH_DICTIONARY;
    }


    @Override
    public void handleNewProfileRequest() {
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton();
        loginController.show(propertyManager.getPropertyValue(CREATE_PROFILE_TITLE), propertyManager.getPropertyValue(CREATE_PROFILE_MESSAGE));
    }

    @Override
    public void handleLoginRequest() {
        GameState.currentState = GameState.LOGIN;
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton();
        gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();

        loginController.show(propertyManager.getPropertyValue(LOGIN_TITLE), propertyManager.getPropertyValue(LOGIN_MESSAGE));

        setVisibleMenu(false, true, true, true);

        appTemplate.getGUI().getMenuBackground(1).setId(propertyManager.getPropertyValue(MENU_ID_IMAGE));

        appTemplate.getGUI().getLoginAndIDButton().setText("User ID");
        appTemplate.getGUI().getPlayAndHomeButton().setText("Start Playing");
    }

    @Override
    public void handleLogoutRequest() {
        GameState.currentState = GameState.UNLOGIN;
        // TODO DATA LOGOUT
        // #################
        setVisibleMenu(true, true, false, false);
        appTemplate.getGUI().getMenuBackground(1).setId(PropertyManager.getManager().getPropertyValue(MENU_IMAGE));
        appTemplate.getGUI().getLoginAndIDButton().setText("Login");
        gameWorkspace.setHomeScreen();
    }

    @Override
    public void handleGoHomeRequest() {
        GameState.currentState = GameState.LOGIN;
        setVisibleMenu(false, true, true, true);
        gameWorkspace.setHomeScreen();
    }

    @Override
    public void handleLevelSelectRequest() {
        GameState.currentState = GameState.LEVEL_SELECTION;
        appTemplate.getGUI().getModeDisplayPane().setVisible(false);
        setVisibleMenu(false, true, true, false);
        gameWorkspace.setLevelSelectionScreen();
    }

    @Override
    public void handlePlayRequest(int level) {
        GameState.currentState = GameState.PLAY;
        GameState.currentLevel = level;
        gameWorkspace.setGamePlayScreen(level);
    }

    @Override
    public void handleHelpRequest() {

    }

    @Override
    public void handlePauseRequest() {

    }

    @Override
    public void handleResumeRequest() {

    }

    @Override
    public void handleQuitRequest() {

    }

    @Override
    public void handleRestartRequest() {

    }

    @Override
    public void handleModeRequest() {
        GameState.currentState = GameState.LOGIN_MODE;
        appTemplate.getGUI().getModeDisplayPane().setVisible(true);
    }

    @Override
    public void handleModeCancelRequest() {
        GameState.currentState = GameState.LOGIN;
        appTemplate.getGUI().getModeDisplayPane().setVisible(false);
    }

    @Override
    public void handleModeSetRequest(GameState mode) {
        GameState.currentState = GameState.LOGIN;
        GameState.currentMode = mode;
        gameWorkspace.getModeLabel().setText(PropertyManager.getManager().getPropertyValue(mode));
        appTemplate.getGUI().getModeDisplayPane().setVisible(false);
    }

    private void setVisibleMenu(boolean first, boolean second, boolean third, boolean fourth)
    {
        appTemplate.getGUI().getMenuBackground(0).setVisible(first);
        appTemplate.getGUI().getMenuBackground(1).setVisible(second);
        appTemplate.getGUI().getMenuBackground(2).setVisible(third);
        appTemplate.getGUI().getMenuBackground(3).setVisible(fourth);
    }
}
