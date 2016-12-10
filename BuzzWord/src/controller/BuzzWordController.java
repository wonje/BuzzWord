package controller;

import static buzzword.BuzzWordProperties.GRID;
import static buzzword.BuzzWordProperties.GRID_SELECTED;
import static settings.AppPropertyType.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import apptemplate.AppTemplate;
import data.GameData;
import data.UserData;
import gui.GridElement;
import gui.LineElement;
import gui.Workspace;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import propertymanager.PropertyManager;
import ui.*;

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
    ArrayList<Stack<GridElement>> gridStacks;
    ArrayList<Stack<LineElement>> lineStacks;
    boolean searched;
    
    public BuzzWordController(AppTemplate appTemplate) {
        this.appTemplate        = appTemplate;
        userData                = (UserData) appTemplate.getUserComponent();
        gameData                = (GameData) appTemplate.getDataComponent();
        GameState.currentState  = GameState.UNLOGIN;
        GameState.currentMode   = GameState.ENGLISH_DICTIONARY;
    }
    
    private void progressing(String keySequence, ArrayList<Point> pos, int keyCount,
                             Stack<GridElement> gridStack) {
        PropertyManager propertyManager = PropertyManager.getManager();
        ArrayList<Point> new_pos;
        Stack<GridElement> newGridStack = new Stack<GridElement>();
        Stack<GridElement> tempStack    = new Stack<GridElement>();
        // STOP IF THE KEY SEQUENCE DONE
        if(keySequence.length() == keyCount) {
            searched = true;
            while(!gridStack.isEmpty()){
                GridElement current = gridStack.pop();
                current.setVisited(true);
                current.getStyleClass().clear();
                current.getStyleClass().addAll(propertyManager.getPropertyValue(GRID_SELECTED));
                tempStack.push(current);
                //DRAW LINE
                if(gridStack.isEmpty())
                    break;
                LineElement temp = new LineElement(new Point((int) (current.getPoint().getX() + gridStack.peek().getPoint().getX()) / 2,
                        (int) (current.getPoint().getY() + gridStack.peek().getPoint().getY()) / 2));
                for (int i = 0; i < gameWorkspace.getLineElements().length; i++) {
                    if (gameWorkspace.getLineElements()[i].getPoint().getX() == temp.getPoint().getX() &&
                            gameWorkspace.getLineElements()[i].getPoint().getY() == temp.getPoint().getY()) {
                        // CHECK DIAGONAL RIGHT TO LEFT
                        if((gridStack.peek().getPoint().getY() > current.getPoint().getY() &&
                                gridStack.peek().getPoint().getX() < current.getPoint().getX()) ||
                                (gridStack.peek().getPoint().getY() < current.getPoint().getY() &&
                                        gridStack.peek().getPoint().getX() > current.getPoint().getX())){
                            gameWorkspace.getLineElements()[++i].setVisible(true);
                        }
                        else {
                            gameWorkspace.getLineElements()[i].setVisible(true);
                        }
                        break;
                    }
                }
                
            }
//            while(!tempStack.isEmpty())
//                tempStack.pop().setVisited(false);
                
            return;
        }
        
        // SEARCH NEXT NODE
        for (int y = (int)Math.max(0, gridStack.peek().getPoint().getY()/2 - 1); y < Math.min(gameData.board.length, gridStack.peek().getPoint().getY()/2 + 2); y++) {
            for (int x = (int)Math.max(0, gridStack.peek().getPoint().getX()/2 - 1); x < Math.min(gameData.board.length, gridStack.peek().getPoint().getX()/2 + 2); x++) {
                // SKIP LAST GRID
                if (y == gridStack.peek().getPoint().getY()/2 && x == gridStack.peek().getPoint().getX()/2) {
                    pos.add(new Point(x, y));
                    continue;
                }
                // SKIP FOLLOWED GRIDS
                if(pos.contains(new Point(x,y)))
                    continue;
                
                // THIS GRID IS NEXT NODE
                if(gameData.board[y][x] == Character.toLowerCase(keySequence.charAt(keyCount))) {
                    // ARRAYLIST COPY
                    new_pos = new ArrayList<Point>(pos);
                    // SAVE VISITED GRID POINT
                    new_pos.add(new Point(x, y));
                    for(GridElement grid : gameWorkspace.getGridElements()) {
                        if(grid.getPoint().getX()/2 == x && grid.getPoint().getY()/2 == y) {
                            newGridStack.addAll(gridStack);
                            newGridStack.push(grid);
                            // RECURSION
                            progressing(keySequence, new_pos, keyCount + 1, newGridStack);
                        }
                    }
                }
            }
        }
        while(!gridStack.isEmpty())
            gridStack.pop();
    }
    
    private void enterEvent() {
        PropertyManager propertyManager = PropertyManager.getManager();
        
        if(gameWorkspace.solutions.contains(gameData.keySequence.toLowerCase()) &&
                !gameData.matchedStr.contains(gameData.keySequence.toLowerCase())) {
            gameWorkspace.matches.add(new Label(gameData.keySequence.toLowerCase()));
            gameData.matchedStr.add(gameData.keySequence.toLowerCase());
            gameWorkspace.matchedPoints.add(new Label(Integer.toString(gameData.keySequence.toLowerCase().length() * 10)));
            gameWorkspace.matchedWordPane.getChildren().clear();
            gameWorkspace.matchedWordPane.getChildren().addAll(gameWorkspace.matches);
            gameWorkspace.matchedPointPane.getChildren().clear();
            gameWorkspace.matchedPointPane.getChildren().addAll(gameWorkspace.matchedPoints);
            gameWorkspace.getTotalPointLabel().setText(Integer.toString(gameData.keySequence.toLowerCase().length() * 10 +
                    Integer.parseInt(gameWorkspace.getTotalPointLabel().getText())));
        }
        
        // TODO CLEAR
        gameData.keySequence = "";
        gameWorkspace.progress.clear();
        gameWorkspace.progressPane.getChildren().clear();
        for(GridElement grid : gameWorkspace.getGridElements()) {
            grid.setVisited(false);
            grid.getStyleClass().clear();
            grid.getStyleClass().addAll(propertyManager.getPropertyValue(GRID));
        }
        for(LineElement line : gameWorkspace.getLineElements())
            line.setVisible(false);
    }


    private void play() {
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();
        AppMessageDialogSingleton appMessageDialogSingleton = AppMessageDialogSingleton.getSingleton();
        PropertyManager propertyManager = PropertyManager.getManager();
        SolutionDialogSingleton solutionDialogSingleton = SolutionDialogSingleton.getSingleton();
        IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
        gameWorkspace.getRemainingTime().textProperty().bind(timeSeconds.asString());
        gridStacks = new ArrayList<Stack<GridElement>>();
        lineStacks = new ArrayList<Stack<LineElement>>();
        timer = new AnimationTimer() {
            Timeline timeline = null;
    
            @Override
            public void handle(long now) {
                //CHECK GAME FAIL END
                if (gameWorkspace.checkEndFail()) {
                    GameState.currentState = GameState.END_FAIL;
                    timer.stop();
                }
                // CHECK GAME SUCCESS END
                else if (gameWorkspace.checkEndSuccess()) {
//                if(gameWorkspace.checkEndSuccess()) {
                    GameState.currentState = GameState.END_SUCCESS;
                    timer.stop();
                }
        
                // GET KEY TYPE
                appTemplate.getGUI().getPrimaryScene().setOnKeyTyped((KeyEvent event) -> {
                    // TODO Handling enter key
                    if (event.getCharacter().equals("\r")) {
                        enterEvent();
                        return;
                    }
                    char guess = event.getCharacter().charAt(0);
                    // TODO Handling illegal keys
                    if (Character.toString(guess).matches("[a-z]+"))
                        guess = Character.toUpperCase(guess);
                    else if (!Character.toString(guess).matches("[A-Z]+"))
                        return;
                    // KEY TYPE MODE START
                    // TODO RESET ALL OF MOUSE PLAY GAME DATA
                    if (GameState.currentPlay == null || !GameState.currentPlay.equals(GameState.KEYBOARD)) {
                        resetMousePlayData();
                        GameState.currentPlay = GameState.KEYBOARD;
                    }
                    // TODO ALL DISPLAY DATA CLAER EXISTS BEFORE
                    for (GridElement gridElement : gameWorkspace.getGridElements()) {
                        gridElement.setVisited(false);
                        gridElement.getStyleClass().clear();
                        gridElement.getStyleClass().addAll(propertyManager.getPropertyValue(GRID));
                    }
                    for (LineElement lineElement : gameWorkspace.getLineElements()) {
                        lineElement.setVisible(false);
                    }
                    // TODO Return if the key value is not existed
                    boolean exist = false;
                    for (GridElement grid : gameWorkspace.getGridElements()) {
                        if (guess == grid.getWord())
                            exist = true;
                    }
                    if (!exist)
                        return;
                    // TODO SEARCH KEY INPUTS
                    // KEY SEQUENCE
                    gameData.keySequence += Character.toString(guess);
                    // PROGRESS UPDATE
                    Label label = new Label(Character.toString(guess));
                    label.setStyle("-fx-background-color: dimgray; -fx-font-family: 'Arial'; " +
                            "-fx-text-fill: antiquewhite; -fx-font-weight: bolder; -fx-font-size: 14");
                    label.setMinSize(30, 30);
                    label.setAlignment(Pos.CENTER);
                    gameWorkspace.progress.add(label);
                    gameWorkspace.progressPane.add(gameWorkspace.progress.get(gameWorkspace.progress.size() - 1),
                            (gameWorkspace.progress.size() - 1) % 8, (gameWorkspace.progress.size() - 1) / 8);
                    // FIND OUT FIRST ELEMENT OF KEY SEQUENCE
                    Stack<GridElement> tempGridStack;
                    ArrayList<Point> pos;
                    // START SEARCH NODES
                    searched = false;
//                    System.out.println(gameData.keySequence);
                    for (GridElement grid : gameWorkspace.getGridElements()) {
                        if (grid.getWord() == gameData.keySequence.charAt(0)) {
                            tempGridStack = new Stack<GridElement>();
                            pos = new ArrayList<Point>();
                            if (gameData.keySequence.length() > 1) {
                                tempGridStack.push(grid);
                                progressing(gameData.keySequence, pos, 1, tempGridStack);
                            } else {
                                grid.getStyleClass().clear();
                                grid.getStyleClass().addAll(propertyManager.getPropertyValue(GRID_SELECTED));
                                grid.setVisited(true);
                            }
                        }
                    }
                    if (!searched && gameData.keySequence.length() > 1) {
                        gameData.keySequence = "";
                        gameWorkspace.progress.clear();
                        gameWorkspace.progressPane.getChildren().clear();
                    }
                });
            }
    
            @Override
            public void start() {
                if (timeline == null) {
                    timeSeconds.set(STARTTIME);
                    timeline = new Timeline();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(STARTTIME + 1),
                                    new KeyValue(timeSeconds, 0)));
                    timeline.playFromStart();
                    super.start();
                } else {
                    timeline.playFromStart();
                    super.start();
                }
            }
    
            @Override
            public void stop() {
                if (timeline == null)
                    return;
                // STOP TIMER
                timeline.stop();
                super.stop();
                if(!(GameState.currentState.equals(GameState.END_FAIL) ||
                        GameState.currentState.equals(GameState.END_SUCCESS)))
                    return;
                
                
                Platform.runLater(() -> {
                    ArrayList<Label> solutionWords = gameWorkspace.displayAllSolutions();
                    // GAMESTATE IS END_FAIL
                    if (GameState.currentState.equals(GameState.END_FAIL)) {
                        // TODO DISPLAY ALL OF SOLUTIONS
                        if (solutionDialogSingleton.isShowing()) {
                            solutionDialogSingleton.setSolutions(solutionWords);
                            solutionDialogSingleton.toFront();
                        } else {
                            solutionDialogSingleton.show(solutionWords);
                        }
                        // TODO POP UP GAME FAIL MESSAGE
                        if (appMessageDialogSingleton.isShowing()) {
                            appMessageDialogSingleton.setMessageLabel("GAME FAIL!");
                            appMessageDialogSingleton.toFront();
                        } else {
                            appMessageDialogSingleton.show("", "GAME FAIL!");
                        }
                        // TODO CHECK "PERSONAL BEST" AND UPDATE
                        try {
                            gameData.totalPoints = Integer.parseInt(gameWorkspace.getTotalPointLabel().getText());
                            checkPersonalBest();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
    
                        // TODO GAME FAIL.
                    }
                    // GAMESTATE IS END_SUCCESS
                    else if (GameState.currentState.equals(GameState.END_SUCCESS)) {
                        if (solutionDialogSingleton.isShowing()) {
                            solutionDialogSingleton.setSolutions(solutionWords);
                            solutionDialogSingleton.toFront();
                        } else {
                            solutionDialogSingleton.show(solutionWords);
                        }
    
                        // TODO POP UP GAME SUCCESS MESSAGE
                        if (appMessageDialogSingleton.isShowing()) {
                            appMessageDialogSingleton.setMessageLabel("GAME SUCCESS!");
                            appMessageDialogSingleton.toFront();
                        } else {
                            appMessageDialogSingleton.show("", "GAME SUCCESS!");
                        }
    
                        // TODO CHECK "PERSONAL BEST" AND UPDATE
                        try {
                            gameData.totalPoints = Integer.parseInt(gameWorkspace.getTotalPointLabel().getText());
                            checkPersonalBest();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // TODO UPDATE DATA
                        try {
                            updateGameLevel();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
    
                        // TODO GAME IS SUCCESSFULLY END
                        if (GameState.currentLevel != 8)
                            gameWorkspace.nextGameButton.setDisable(false);
                        else {
                            if (appMessageDialogSingleton.isShowing()) {
                                appMessageDialogSingleton.setMessageLabel("Congratulation! All of level are cleared!");
                                appMessageDialogSingleton.toFront();
                            } else
                                appMessageDialogSingleton.show("", "Congratulation! All of level are cleared!");
                        }
                    }
                    // TIMER RESET
                    timeline = null;
                });
            }
        };
        timer.start();
    }
        

    private void checkPersonalBest() throws IOException {
        AppMessageDialogSingleton appMessageDialogSingleton = AppMessageDialogSingleton.getSingleton();
        // LOAD BEST SCORE YOU DID BEFORE AND SAVE NEW BEST SCORE
        if (userData.checkAndSaveBestPoint(GameState.currentMode, Integer.parseInt(gameWorkspace.getLevelLabel().getText().split(" ")[1]),
                Integer.parseInt(gameWorkspace.getTotalPointLabel().getText()))) {
            if(appMessageDialogSingleton.isShowing()) {
                appMessageDialogSingleton.setMessageLabel("You got the highest score!\nYour score is " + gameWorkspace.getTotalPointLabel().getText());
                appMessageDialogSingleton.toFront();
            }
            else {
                appMessageDialogSingleton.show("", "You got the highest score!\nYour score is " + gameWorkspace.getTotalPointLabel().getText());
            }
            // UPDATE PROFILE DATA
            appTemplate.getFileComponent().updateProfileData(appTemplate);
        }
    }

    private void updateGameLevel() throws IOException {
        switch (GameState.currentMode)
        {
            case ENGLISH_DICTIONARY:
            {
                if(GameState.currentLevel != gameData.maxEngDicLevel)
                    return;
                gameData.maxEngDicLevel++;
                userData.getCurrentModeScores().put(gameData.maxEngDicLevel, 0);
                break;
            }
            case BACTERIA:
            {
                if(GameState.currentLevel != gameData.maxBacteriaLevel)
                    return;
                gameData.maxBacteriaLevel++;
                userData.getCurrentModeScores().put(gameData.maxBacteriaLevel, 0);
                break;
            }
            case BIOLOGY:
            {
                if(GameState.currentLevel != gameData.maxBiologyLevel)
                    return;
                gameData.maxBiologyLevel++;
                userData.getCurrentModeScores().put(gameData.maxBiologyLevel, 0);
                break;
            }
            case FUNGI:
            {
                if(GameState.currentLevel != gameData.maxFungiLevel)
                    return;
                gameData.maxFungiLevel++;
                userData.getCurrentModeScores().put(gameData.maxFungiLevel, 0);
                break;
            }
        }
        // TODO SAVE UPDATED LEVEL
        appTemplate.getFileComponent().updateProfileData(appTemplate);
    }
    
    private void resetMousePlayData() {
        PropertyManager propertyManager = PropertyManager.getManager();
        // POP FROM GRID STACK AND CHANGE GRID CSS
        GridElement grid;
        while(!gameData.gridStack.isEmpty()) {
            grid = gameData.gridStack.pop();
            grid.getStyleClass().clear();
            grid.getStyleClass().addAll(propertyManager.getPropertyValue(GRID));
            grid.setVisited(false);
        }
        // POP FROM LINE STACK, ERASE LINES
        while(!gameData.lineStack.isEmpty()) {
            gameData.lineStack.pop().setVisible(false);
        }
    
        // PROGRESS RESET
        gameWorkspace.progress.clear();
        gameWorkspace.progressPane.getChildren().clear();
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
            appTemplate.getGUI().setTooltipCreateIDtoProfileSetting(true);
            gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();

            setVisibleMenu(true, true, true, true);

            appTemplate.getGUI().getMenuBackground(1).setId(propertyManager.getPropertyValue(MENU_ID_IMAGE));
            appTemplate.getGUI().getCreateAndSetProfileButton().setText("Profile Setting");
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
            appTemplate.getGUI().setTooltipCreateIDtoProfileSetting(false);
            // TODO DATA LOGOUT
            userData.reset();
            gameData.reset();
            // #################
            setVisibleMenu(true, true, false, false);
            appTemplate.getGUI().getMenuBackground(1).setId(PropertyManager.getManager().getPropertyValue(MENU_IMAGE));
            appTemplate.getGUI().getCreateAndSetProfileButton().setText("Create New Profile");
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
        // IF GAME IS PLAYING, TIMER SHOULD BE STOPPED
        if(GameState.currentState.equals(GameState.PLAY))
            timer.stop();
        if(GameState.currentState.equals(GameState.PAUSE))
            gameWorkspace.setPausePane(false);
        GameState.currentState = GameState.LOGIN;
        gameWorkspace.helpButtonPane.setVisible(true);
        appTemplate.getGUI().setTooltipPlaytoHome(false);
        setVisibleMenu(true, true, true, true);
        gameWorkspace.setHomeScreen();
    }

    @Override
    public void handleLevelSelectRequest() {
        GameState.currentState = GameState.LEVEL_SELECTION;
        gameWorkspace.getModeLabel().setText(PropertyManager.getManager().getPropertyValue(GameState.currentMode));
        appTemplate.getGUI().setTooltipPlaytoHome(true);
        appTemplate.getGUI().getModeDisplayPane().setVisible(false);
        gameWorkspace.helpButtonPane.setVisible(false);
        // LOAD WORDS FILE
        gameData.loadWordFile(GameState.currentMode);
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
        if(GameState.currentState.equals(GameState.UNLOGIN))
            gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        HelpViewDialogSingleton helpViewDialogSingleton = HelpViewDialogSingleton.getSingleton();
        helpViewDialogSingleton.showAndWait();
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
        if(GameState.currentState.equals(GameState.UNLOGIN))
            gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        if(GameState.currentState.equals(GameState.PLAY))
            timer.stop();
        if (gameWorkspace.confirmBeforeExit())
            System.exit(0);
        if(GameState.currentState.equals(GameState.PLAY))
            timer.start();
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
    
    @Override
    public void handleProfileSettingRequest() {
        ProfileSettingsDialogSingleton profileSettingsDialogSingleton = ProfileSettingsDialogSingleton.getSingleton(appTemplate);
        profileSettingsDialogSingleton.viewProfileshow();
    }
    
    private void setVisibleMenu(boolean first, boolean second, boolean third, boolean fourth)
    {
        appTemplate.getGUI().getMenuBackground(0).setVisible(first);
        appTemplate.getGUI().getMenuBackground(1).setVisible(second);
        appTemplate.getGUI().getMenuBackground(2).setVisible(third);
        appTemplate.getGUI().getMenuBackground(3).setVisible(fourth);
    }
}
