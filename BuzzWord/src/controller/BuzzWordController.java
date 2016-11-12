package controller;

import apptemplate.AppTemplate;
import gui.Workspace;
import javafx.scene.control.Button;
import propertymanager.PropertyManager;
import ui.AppGUI;

import static buzzword.BuzzWordProperties.*;
import static settings.AppPropertyType.*;


/**
 * @author Jason Kang
 */
public class BuzzWordController implements FileController {

    private AppTemplate     appTemplate;
    private Workspace       gameWorkspace;

    public BuzzWordController(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        GameState.currentState = GameState.UNLOGIN;
    }


    @Override
    public void handleNewProfileRequest() {
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton();
        loginController.show(propertyManager.getPropertyValue(CREATE_PROFILE_TITLE), propertyManager.getPropertyValue(CREATE_PROFILE_MESSAGE));
    }

    @Override
    public void handleLoginRequest() {
        PropertyManager propertyManager = PropertyManager.getManager();
        LoginController loginController = LoginController.getSingleton();
        loginController.show(propertyManager.getPropertyValue(LOGIN_TITLE), propertyManager.getPropertyValue(LOGIN_MESSAGE));

        appTemplate.getGUI().getMenuBackground(0).setVisible(false);
        appTemplate.getGUI().getMenuBackground(2).setVisible(true);
        appTemplate.getGUI().getMenuBackground(3).setVisible(true);

        appTemplate.getGUI().getMenuBackground(1).setId(propertyManager.getPropertyValue(MENU_ID_IMAGE));

        appTemplate.getGUI().setTextLoginID("User ID");
        appTemplate.getGUI().setTextModeHome("Select Mode");
        GameState.currentState = GameState.LOGIN;
    }

    @Override
    public void handleGoHomeRequest() {
        gameWorkspace.setHomeScreen();
    }

    @Override
    public void handleLevelSelectRequest() {

    }

    @Override
    public void handlePlayRequest() {

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

    }

    @Override
    public void handleModeCancelRequest() {

    }
}
