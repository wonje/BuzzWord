package controller;

import static settings.AppPropertyType.*;

import apptemplate.AppTemplate;
import data.GameData;
import data.UserData;
import gui.Workspace;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import propertymanager.PropertyManager;
import ui.YesNoCancelDialogSingleton;

import java.nio.file.Path;


/**
 * @author Jason Kang
 */
public class BuzzWordController implements FileController {

    AppTemplate appTemplate;
    Workspace gameWorkspace;
    UserData userData;
    GameData gameData;

    public BuzzWordController(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        userData = (UserData) appTemplate.getUserComponent();
        gameData = (GameData) appTemplate.getDataComponent();
        GameState.currentState = GameState.UNLOGIN;
        GameState.currentMode = GameState.ENGLISH_DICTIONARY;
    }
//
//
//    private void play()
//    {
//        AnimationTimer timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//
//            }
//        }
//    }


    @Override
    public void handleNewProfileRequest() {
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton(appTemplate);
        loginController.show(propertyManager.getPropertyValue(CREATE_PROFILE_TITLE), propertyManager.getPropertyValue(CREATE_PROFILE_MESSAGE));
    }

    @Override
    public void handleLoginRequest() {
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton(appTemplate);
        loginController.show(propertyManager.getPropertyValue(LOGIN_TITLE), propertyManager.getPropertyValue(LOGIN_MESSAGE));

        if(GameState.currentState.equals(GameState.LOGIN)) {
            appTemplate.getGUI().setTooltipLogintoID(true);
            gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();

            setVisibleMenu(false, true, true, true);

            appTemplate.getGUI().getMenuBackground(1).setId(propertyManager.getPropertyValue(MENU_ID_IMAGE));
            appTemplate.getGUI().getLoginAndIDButton().setText(loginController.getID());
            appTemplate.getGUI().getPlayAndHomeButton().setText("Start Playing");
        }
    }

    @Override
    public void handleLogoutRequest() {
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();
        yesNoCancelDialogSingleton.show("", "Do you want to logout?");

        if(yesNoCancelDialogSingleton.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
            GameState.currentState = GameState.UNLOGIN;
            appTemplate.getGUI().setTooltipLogintoID(false);
            // TODO DATA LOGOUT
            userData.reset();
            gameData.reset();
            // #################
            setVisibleMenu(true, true, false, false);
            appTemplate.getGUI().getMenuBackground(1).setId(PropertyManager.getManager().getPropertyValue(MENU_IMAGE));
            appTemplate.getGUI().getLoginAndIDButton().setText("Login");
            gameWorkspace.setHomeScreen();
        }
    }

    @Override
    public void handleGoHomeRequest() {
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();
        if(GameState.currentState.equals(GameState.PLAY) || GameState.currentState.equals(GameState.PAUSE)) {
            yesNoCancelDialogSingleton.show("", "Are you sure to terminate this stage?");
            if(!yesNoCancelDialogSingleton.getSelection().equals(yesNoCancelDialogSingleton.YES))
                return;
        }
        if(GameState.currentState.equals(GameState.PAUSE))
            gameWorkspace.setPausePane(false);
        GameState.currentState = GameState.LOGIN;
        appTemplate.getGUI().setTooltipPlaytoHome(false);
        setVisibleMenu(false, true, true, true);
        gameWorkspace.setHomeScreen();
    }

    @Override
    public void handleLevelSelectRequest() {
        GameState.currentState = GameState.LEVEL_SELECTION;
        appTemplate.getGUI().setTooltipPlaytoHome(true);
        appTemplate.getGUI().getModeDisplayPane().setVisible(false);
        setVisibleMenu(false, true, true, false);
        // GET MAX LEVEL
        gameData.getMaxLevels(userData);
        // SET LEVEL SELECTION DISPLAY
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
        GameState.currentState = GameState.PAUSE;
        // TODO STOP TIMER
        // ###############
        gameWorkspace.setPausePane(true);
    }

    @Override
    public void handleResumeRequest() {
        GameState.currentState = GameState.PLAY;
        // TODO START TIMER AGAIN
        // ######################
        gameWorkspace.setPausePane(false);

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
