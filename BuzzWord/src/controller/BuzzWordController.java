package controller;

import static settings.AppPropertyType.*;

import apptemplate.AppTemplate;
import data.GameData;
import data.UserData;
import gui.Workspace;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import propertymanager.PropertyManager;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.IOException;

/**
 * @author Jason Kang
 */
public class BuzzWordController implements FileController {
    static final int STARTTIME = 60;
    AppTemplate appTemplate;
    Workspace gameWorkspace;
    UserData userData;
    GameData gameData;
    AnimationTimer timer;

    public BuzzWordController(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        userData = (UserData) appTemplate.getUserComponent();
        gameData = (GameData) appTemplate.getDataComponent();
        GameState.currentState = GameState.UNLOGIN;
        GameState.currentMode = GameState.ENGLISH_DICTIONARY;
    }


    private void play()
    {
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();
        IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
        gameWorkspace.getRemainingTime().textProperty().bind(timeSeconds.asString());
        timer = new AnimationTimer() {
            Timeline timeline = null;
            @Override
            public void handle(long now) {
                // CHECK GAME SUCCESS END
                if(gameWorkspace.checkEndSuccess()) {
                    GameState.currentState = GameState.END_SUCCESS;
                    timer.stop();
                }



                // GET KEY TYPE
                appTemplate.getGUI().getPrimaryScene().setOnKeyTyped((KeyEvent event) -> {
                    char guess = event.getCharacter().charAt(0);
                    // TODO Handling illegal keys
                    if(Character.toString(guess).matches("[a-z]+"))
                        guess = Character.toUpperCase(guess);
                    else if (!Character.toString(guess).matches("[A-Z]+"))
                        return;
                    // KEY TYPE MODE START
                    GameState.currentPlay = GameState.KEYBOARD;




                });
            }
            @Override
            public void start() {
                if(timeline == null) {
                    timeSeconds.set(STARTTIME);
                    timeline = new Timeline();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(STARTTIME + 1),
                                    new KeyValue(timeSeconds, 0)));
                    timeline.playFromStart();
                    super.start();
                }
                else {
                    timeline.playFromStart();
                    super.start();
                }
            }
            @Override
            public void stop() {
                if(timeline == null)
                    return;
                // STOP TIMER
                timeline.stop();
                super.stop();

                // GAMESTATE IS END_SUCCESS
                if(GameState.currentState.equals(GameState.END_SUCCESS)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // TODO CHECK "PERSONAL BEST" AND UPDATE
                            try {
                                gameData.totalPoints = Integer.parseInt(gameWorkspace.getTotalPointLabel().getText());
                                checkPersonalBest();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // POPUP GAME END SCREEN
                            if(Integer.parseInt(gameWorkspace.getLevelLabel().getText().split(" ")[1]) != 8) {
                                // STOP GAME
                                yesNoCancelDialogSingleton.show("", "Level " + gameWorkspace.getLevelLabel().getText() +
                                        " is clear! \nDo you want to start Level " +
                                        Integer.toString(Integer.parseInt(gameWorkspace.getLevelLabel().getText().split(" ")[1]) + 1) + "?");
                                try {
                                    updateGameLevel();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                yesNoCancelDialogSingleton.show("", "Level " + gameWorkspace.getLevelLabel().getText() +
                                        " is clear! \nYour last stage is done!");
                            }
                            // TIMER RESET
                            timeline = null;
                        }
                    });
                }
            }
        };
        timer.start();
    }

    private void checkPersonalBest() throws IOException {
        // CHECK IT SHOULD UPDATE MAX LEVEL OR NOT
//        boolean updateLevel;
//        int maxLevel = 0;
//        switch (GameState.currentMode){
//            case ENGLISH_DICTIONARY:
//            {
//                maxLevel = gameData.maxEngDicLevel;
//                break;
//            }
//            case BACTERIA:
//            {
//                maxLevel = gameData.maxBacteriaLevel;
//                break;
//            }
//            case BIOLOGY:
//            {
//                maxLevel = gameData.maxBiologyLevel;
//                break;
//            }
//            case FUNGI:
//            {
//                maxLevel = gameData.maxFungiLevel;
//                break;
//            }
//        }
//        if(GameState.currentLevel == maxLevel && GameState.currentState.equals(GameState.END_SUCCESS))
//            updateLevel = true;
//        else
//            updateLevel = false;

        // LOAD BEST SCORE YOU DID BEFORE AND SAVE NEW BEST SCORE
        if (userData.checkAndSaveBestPoint(GameState.currentMode, Integer.parseInt(gameWorkspace.getLevelLabel().getText().split(" ")[1]),
                gameData.totalPoints)) {
            AppMessageDialogSingleton appMessageDialogSingleton = AppMessageDialogSingleton.getSingleton();
            appMessageDialogSingleton.show("", "You got the highest score!\nYour score is " + gameWorkspace.getTotalPointLabel().getText());
            // UPDATE PROFILE DATA
            appTemplate.getFileComponent().updateProfileData(appTemplate);
        }
    }

    private void updateGameLevel() throws IOException {
        switch (GameState.currentMode)
        {
            case ENGLISH_DICTIONARY:
            {
                gameData.maxEngDicLevel++;
                userData.getCurrentModeScores().put(gameData.maxEngDicLevel, 0);
                break;
            }
            case BACTERIA:
            {
                gameData.maxBacteriaLevel++;
                userData.getCurrentModeScores().put(gameData.maxBacteriaLevel, 0);
                break;
            }
            case BIOLOGY:
            {
                gameData.maxBiologyLevel++;
                userData.getCurrentModeScores().put(gameData.maxBiologyLevel, 0);
                break;
            }
            case FUNGI:
            {
                gameData.maxFungiLevel++;
                userData.getCurrentModeScores().put(gameData.maxFungiLevel, 0);
                break;
            }
        }
        // TODO SAVE UPDATED LEVEL
        appTemplate.getFileComponent().updateProfileData(appTemplate);
    }


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
        play();
    }

    @Override
    public void handleHelpRequest() {

    }

    @Override
    public void handlePauseRequest() {
        GameState.currentState = GameState.PAUSE;
        // TODO STOP TIMER
        timer.stop();
        // ###############
        gameWorkspace.setPausePane(true);
    }

    @Override
    public void handleResumeRequest() {
        GameState.currentState = GameState.PLAY;
        // TODO START TIMER AGAIN
        timer.start();
        // ######################
        gameWorkspace.setPausePane(false);

    }

    @Override
    public void handleQuitRequest() {
        timer.stop();
        if (gameWorkspace.confirmBeforeExit())
            System.exit(0);
        timer.start();
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
