package gui;

import static buzzword.BuzzWordProperties.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import controller.GameState;
import controller.LoginController;
import data.GameData;
import data.UserData;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import propertymanager.PropertyManager;
import ui.AppGUI;
import ui.YesNoCancelDialogSingleton;

/**
 * This class serves as the GUI component for the Hangman game.
 *
 * @author Jason Kang
 */
public class Workspace extends AppWorkspaceComponent {

    AppTemplate app; // the actual application
    AppGUI gui; // the GUI inside which the application sits

    GameData gameData;
    UserData userData;

    BuzzWordController controller;
    LoginController loginController;

    BorderPane basePane;           // main container to divide sections
    BorderPane centerPane;
    StackPane mainFramePane;      // container to stack grid elements and lines on the basePane

    VBox topPane;            // container to display labels at top
    VBox bottomPane;         // container to display labels at bottom
    VBox rightPane;          // container to display status at right
    VBox rightStatusPane;
    BorderPane pausePane;
    GridPane mainStagePane;      // container to display all of grid elements

    Label titleLabel;         // label to display title
    Label modeLabel;          // label to display mode
    Label remainingTime;      // label to display remaining time
    Label levelLabel;         // label to display level
    Label targetPoint;        // label to display target point
    Label totalPointLabel;

    Button pauseAndPlayButton;
    Button helpButton;
    Button closeButton;        // close button
    
    StackPane helpButtonPane;
    StackPane closeButtonPane;
    StackPane pauseAndPlayButtonPane;
    GridPane remainingTimePane;

    VBox matchedContainerPane;
    ScrollPane matchedScrollPane;
    HBox totalPointPane;
    HBox matchedPane;

    VBox targetPointPane;
    
    GridElement[] gridElements;
    LineElement[] lineElements;

    public ArrayList<String> solutions;
    public ArrayList<Label> progress;           // labels to display progressed words
    public ArrayList<Label> matches;            // labels to display matched words
    public ArrayList<Label> matchedPoints;      // labels to display points of matched words
    
    public VBox matchedWordPane;
    public VBox matchedPointPane;
    public GridPane progressPane;

    int time;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     * @throws IOException Thrown should there be an error loading application
     *                     data for setting up the user interface.
     */
    public Workspace(AppTemplate initApp) throws IOException {
        app = initApp;
        gui = app.getGUI();
        gameData = (GameData) app.getDataComponent();
        userData = (UserData) app.getUserComponent();
        controller = (BuzzWordController) gui.getFileController();    //new HangmanController(app, startGame); <-- THIS WAS A MAJOR BUG!??
        loginController = LoginController.getSingleton(app);
        layoutGUI();     // initialize all the workspace (GUI) components including the containers and their layout
    }
    
    public GridElement[] getGridElements() { return gridElements; }
    
    public LineElement[] getLineElements() { return lineElements; }

    public Label getModeLabel() {
        return modeLabel;
    }

    public Label getTotalPointLabel() { return totalPointLabel; }

    public Label getLevelLabel() { return levelLabel; }
    
    public boolean checkEndFail() {
        if(Integer.parseInt(remainingTime.getText()) == 0)
            return true;
        return false;
    }
    
    public boolean checkEndSuccess() {
        if(Integer.parseInt(totalPointLabel.getText()) >= Integer.parseInt(targetPoint.getText()))
            return true;
        return false;
    }

    public void setPausePane(boolean visible) {
        pausePane.setVisible(visible);
        mainStagePane.setVisible(!visible);
    }

    public Label getRemainingTime() { return remainingTime; }

    private void layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET BACKGROUND AND SECTIONS
        basePane = gui.getAppPane();
        centerPane = new BorderPane();
        basePane.setCenter(centerPane);

        // SET TOP######################################
        topPane = new VBox();
        topPane.setSpacing(20);
        topPane.setPrefHeight(100);

        titleLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_TITLE_LABEL));
        titleLabel.getStyleClass().setAll(propertyManager.getPropertyValue(TITLE_LABEL));

        modeLabel = new Label(propertyManager.getPropertyValue(GameState.currentMode));
        modeLabel.getStyleClass().setAll(propertyManager.getPropertyValue(MODE_LABEL));
        modeLabel.setVisible(false);

        topPane.getChildren().addAll(titleLabel, modeLabel);
        topPane.setAlignment(Pos.CENTER);

        centerPane.setTop(topPane);
        centerPane.setAlignment(topPane, Pos.CENTER);

        // SET CENTER########################################
        mainFramePane = new StackPane();
        mainStagePane = new GridPane();
        mainStagePane.setPadding(new Insets(30, 0, 30, 80));

        // INIT PAUSE PANE
        pausePane = new BorderPane();
        pausePane.setStyle("-fx-background-color: transparent");
        pausePane.setVisible(false);
        Label pause = new Label("PAUSE");
        pause.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 30; -fx-font-weight: bolder; -fx-text-fill: antiquewhite; -fx-font-style: italic");
        pausePane.setCenter(pause);
        pausePane.setAlignment(pause, Pos.CENTER);

        // INIT DISPLAY GRID ELEMENTS
        initMainStage();

        mainFramePane.getChildren().addAll(mainStagePane, pausePane);
        centerPane.setCenter(mainFramePane);
        // SET BOTTOM#########################################
        bottomPane = new VBox();
        bottomPane.setAlignment(Pos.TOP_CENTER);
        bottomPane.setPrefHeight(200);
        bottomPane.setSpacing(20);

        levelLabel = new Label();
        levelLabel.setVisible(false);
        levelLabel.getStyleClass().add(propertyManager.getPropertyValue(MODE_LABEL));

        pauseAndPlayButtonPane = new StackPane();
        pauseAndPlayButtonPane.setPrefWidth(40);
        pauseAndPlayButtonPane.setPrefHeight(50);
        pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PAUSE_BUTTON_IMAGE));

        pauseAndPlayButton = new Button();
        pauseAndPlayButton.setPrefHeight(45);
        pauseAndPlayButton.setPrefWidth(45);
        pauseAndPlayButton.setStyle("-fx-background-color: transparent");
        pauseAndPlayButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (GameState.currentState.equals(GameState.PLAY)) {
                            GameState.currentState = GameState.PAUSE;
                            pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PLAY_BUTTON_IMAGE));
                            gui.getFileController().handlePauseRequest();
                        } else if (GameState.currentState.equals(GameState.PAUSE)) {
                            GameState.currentState = GameState.PLAY;
                            pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PAUSE_BUTTON_IMAGE));
                            gui.getFileController().handleResumeRequest();
                        }
                    }
                });

        pauseAndPlayButtonPane.getChildren().add(pauseAndPlayButton);
        pauseAndPlayButtonPane.setVisible(false);

        bottomPane.getChildren().addAll(levelLabel, pauseAndPlayButtonPane);

        centerPane.setBottom(bottomPane);

        // SET RIGHT##############################################
        rightPane = new VBox();
        rightPane.setPrefWidth(250);
        rightPane.setSpacing(20);
        rightStatusPane = new VBox();
        rightStatusPane.setPrefWidth(250);
        rightStatusPane.setSpacing(20);
        rightStatusPane.setVisible(false);

        FlowPane buttonsContainerPane = new FlowPane();
        closeButtonPane = new StackPane();
        closeButtonPane.setPrefHeight(30);
        closeButtonPane.setPrefWidth(30);
        closeButtonPane.setId(propertyManager.getPropertyValue(CLOSE_BUTTON_IMAGE));
        closeButton = new Button();
        closeButton.setPrefWidth(30);
        closeButton.setPrefHeight(30);
        closeButton.setStyle("-fx-background-color: transparent");
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO Confirm again before terminating the game
                        gui.getFileController().handleQuitRequest();
                    }
                });
        closeButtonPane.getChildren().add(closeButton);
    
        helpButtonPane = new StackPane();
        helpButtonPane.setPrefHeight(30);
        helpButtonPane.setPrefWidth(30);
        helpButtonPane.setId(propertyManager.getPropertyValue(HELP_BUTTON_IMAGE));
        helpButton = new Button();
        helpButton.setPrefWidth(30);
        helpButton.setPrefHeight(30);
        helpButton.setStyle("-fx-background-color: transparent");
        helpButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO Confirm again before terminating the game
                        gui.getFileController().handleHelpRequest();
                    }
                });
        helpButtonPane.getChildren().add(helpButton);
        buttonsContainerPane.getChildren().addAll(helpButtonPane, closeButtonPane);
        buttonsContainerPane.setAlignment(Pos.TOP_RIGHT);
        

        remainingTimePane = new GridPane();
        remainingTimePane.setVgap(10);
        remainingTimePane.setPrefHeight(60);
        remainingTimePane.setAlignment(Pos.CENTER_LEFT);
        remainingTimePane.setVisible(false);

        Label timeword = new Label("REMAING TIME : ");
        timeword.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18; -fx-text-fill: orangered; -fx-font-weight: bolder; -fx-underline: true");
        remainingTimePane.add(timeword, 0, 0);
        time = 40;
        remainingTime = new Label(Integer.toString(time) + " seconds");
        remainingTime.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16; -fx-text-fill: orangered; -fx-font-weight: bolder");

        BorderPane emptyPane = new BorderPane();
        emptyPane.setPrefHeight(10);

        remainingTimePane.add(remainingTime, 0, 1);
        remainingTimePane.getStyleClass().add(propertyManager.getPropertyValue(REMAINING_LABEL));

        // MATCHED STATUS
        matchedContainerPane = new VBox();

        matchedScrollPane = new ScrollPane();
        matchedScrollPane.setPrefHeight(280);
        matchedScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        matchedScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        matchedScrollPane.getStyleClass().add(propertyManager.getPropertyValue(MATCHED_STATUS));

        matchedPane = new HBox();

        matchedWordPane = new VBox();
        matchedWordPane.setPrefWidth(170);
        matches = new ArrayList<Label>();

        matchedPointPane = new VBox();
        matchedPoints = new ArrayList<Label>();

        HBox borderline1 = new HBox();
        borderline1.setPrefHeight(280);
        borderline1.setPrefWidth(3);
        borderline1.setStyle("-fx-background-color: black");

        // TOTAL POINT
        totalPointPane = new HBox();
        totalPointPane.setStyle("-fx-background-color: dimgray");
        Label total = new Label("TOTAL");
        total.setPrefWidth(171);
        totalPointLabel = new Label("0");
        // FAKE DATA
        HBox borderline2 = new HBox();
        borderline2.setPrefWidth(3);
        borderline2.setStyle("-fx-background-color: black");

        totalPointPane.getChildren().addAll(total, borderline2, totalPointLabel);

        matchedContainerPane.setStyle("-fx-background-color: black");
        matchedContainerPane.getChildren().addAll(matchedScrollPane, totalPointPane);

        matchedPane.getChildren().addAll(matchedWordPane, borderline1, matchedPointPane);
        matchedScrollPane.setContent(matchedPane);

        // TARGET POINT LABEL
        targetPointPane = new VBox();
        targetPointPane.setId(propertyManager.getPropertyValue(TARGET_POINT));
        targetPointPane.setPrefHeight(80);
        targetPointPane.setAlignment(Pos.CENTER_LEFT);
        targetPointPane.setSpacing(15);
        targetPointPane.setVisible(false);

        Label targetDisplay = new Label("TARGET");
        targetDisplay.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 20; -fx-underline: true; -fx-font-weight: bolder");
        targetPoint = new Label();
        targetPoint.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");

        targetPointPane.getChildren().addAll(targetDisplay, targetPoint);

        rightStatusPane.getChildren().addAll(initProgressPane(), matchedContainerPane);
        rightPane.getChildren().addAll(buttonsContainerPane, emptyPane, remainingTimePane, rightStatusPane, targetPointPane);
        basePane.setRight(rightPane);
    }

    private GridPane initProgressPane() {
        progress = new ArrayList<Label>();
        progressPane = new GridPane();
        progressPane.setPrefHeight(60);

        return progressPane;
    }

    private void initMainStage() {
        PropertyManager propertyManager = PropertyManager.getManager();

            int lineCount = 0;
            int gridCount = 0;
            StackPane tempStack;

            // INIT GRID BUTTON COMPONENTS
            gridElements = new GridElement[16];
            lineElements = new LineElement[42];
            for (int i = 0; i < 49; i++) {
                if (i < 7 || (i >= 14 && i < 21) || (i >= 28 && i < 35) || i >= 42) {
                    if (i % 2 == 0) {
                        // CHECK GRID COMPONENT TURN AND INIT
                        gridElements[gridCount] = new GridElement(new Point(i % 7, i / 7), this, this.app);
                        gridElements[gridCount].setShape(new Circle(50));
                        gridElements[gridCount].setMinSize(100, 100);
                        gridElements[gridCount].setMaxSize(100, 100);
                        gridElements[gridCount].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                        gridElements[gridCount].setVisible(true);
                        gridElements[gridCount].setDisable(true);
                        mainStagePane.add(gridElements[gridCount++], i % 7, i / 7);
                    } else {
                        // CHECK HLINE COMPONENT TURN
                        lineElements[lineCount] = new LineElement(new Point(i % 7, i / 7), this.app, this, false);
                        mainStagePane.add(lineElements[lineCount], i % 7, i / 7);
                        lineElements[lineCount++].setVisible(false);
                    }
                }
                else {
                    // CHECK VLINE COMPONENT TURN
                    if (i % 2 == 1) {
                        tempStack = new StackPane();
                        tempStack.setAlignment(Pos.CENTER);
                        lineElements[lineCount] = new LineElement(new Point(i % 7, i / 7), this.app, this, false);
                        tempStack.getChildren().add(lineElements[lineCount]);
                        lineElements[lineCount++].setVisible(false);
                        mainStagePane.add(tempStack, i % 7, i / 7);
                    }
                    // CHECK DIAGONAL COMPONENT TURN
                    else {
                        tempStack = new StackPane();
                        tempStack.setAlignment(Pos.CENTER);
                        lineElements[lineCount++]   = new LineElement(new Point(i % 7, i / 7), this.app, this, true);
                        lineElements[lineCount]     = new LineElement(new Point(i % 7, i / 7), this.app, this, false);
                        tempStack.getChildren().addAll(lineElements[lineCount - 1], lineElements[lineCount]);
                        lineElements[lineCount - 1].setVisible(false);
                        lineElements[lineCount++].setVisible(false);
                        mainStagePane.add(tempStack, i % 7, i / 7);
                    }
                }

                switch (gridCount - 1) {
                    case 0:
                        gridElements[gridCount - 1].setText("B");
                        break;
                    case 1:
                        gridElements[gridCount - 1].setText("U");
                        break;
                    case 4:
                    case 5:
                        gridElements[gridCount - 1].setText("Z");
                        break;
                    case 10:
                        gridElements[gridCount - 1].setText("W");
                        break;
                    case 11:
                        gridElements[gridCount - 1].setText("O");
                        break;
                    case 14:
                        gridElements[gridCount - 1].setText("R");
                        break;
                    case 15:
                        gridElements[gridCount - 1].setText("D");
                        break;
                }
            }
        }



    // SET HOME SCREEN SCREEN
    public void setHomeScreen()
    {
        gui.getPlayAndHomeButton().setText("Start Playing");
        resetGrid();
        resetScrollPane();
        modeLabel.setVisible(false);
        pauseAndPlayButtonPane.setVisible(false);
        levelLabel.setVisible(false);

        // UNDISPLAY RIGHT STATUS PANE
        rightStatusPane.setVisible(false);
        remainingTimePane.setVisible(false);
        targetPointPane.setVisible(false);

        // RESET GAME DATA
        gameData.matchedStr.clear();
        totalPointLabel.setText("0");
        
        // LINE ELEMENT CLEAR
        for(LineElement lineElement : lineElements)
            lineElement.setVisible(false);
        
        // RESET PROGRESS PANE
        progress.clear();
        progressPane.getChildren().clear();
        gameData.keySequence = "";
    }

    // SET LEVEL SELECTION SCREEN
    public void setLevelSelectionScreen()
    {
        modeLabel.setVisible(true);
        gui.getPlayAndHomeButton().setText("Home");

        // CREATE GRID ELEMENT GUI
        for(int i = 0; i < 8; i++)
            gridElements[i].setText(Integer.toString(i+1));
        for(int i = 8; i < gridElements.length; i++)
            gridElements[i].setVisible(false);

        // OPEN GRID UP TO MAX LEVEL
        if(GameState.currentMode.equals(GameState.ENGLISH_DICTIONARY))
        {
            setOpenedGrid(gameData.maxEngDicLevel);
        }
        else if(GameState.currentMode.equals(GameState.BACTERIA))
        {
            setOpenedGrid(gameData.maxBacteriaLevel);
        }
        else if(GameState.currentMode.equals(GameState.BIOLOGY))
        {
            setOpenedGrid(gameData.maxBiologyLevel);
        }
        else if(GameState.currentMode.equals(GameState.FUNGI))
        {
            setOpenedGrid(gameData.maxFungiLevel);
        }


    }

    private void setOpenedGrid(int maxLevel)
    {
        PropertyManager propertyManager = PropertyManager.getManager();
        for(int i=0; i < maxLevel; i++)
        {
            gridElements[i].getStyleClass().clear();
            gridElements[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_OPENLEVEL));
            gridElements[i].setAlignment(Pos.CENTER);
            gridElements[i].setDisable(false);
            int level = i+1;
            gridElements[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // TODO GAMEDATA SET
                            // #################
                            if(GameState.currentState.equals(GameState.LEVEL_SELECTION))
                                gui.getFileController().handlePlayRequest(level);
                        }
                    });
        }
    }

    // SET PLAYING GAME SCREEN
    public void setGamePlayScreen(int level) {
        PropertyManager propertyManager = PropertyManager.getManager();
        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();

        // DISPLAY GAME FUNCTIONS
        rightStatusPane.setVisible(true);
        remainingTimePane.setVisible(true);
        targetPointPane.setVisible(true);
        levelLabel.setVisible(true);
        pauseAndPlayButtonPane.setVisible(true);
        levelLabel.setText("Level " + Integer.toString(GameState.currentLevel));

        // MAKE GRID ELEMENTS RANDOMLY
        for (int i = 0; i < gridElements.length; i++)
        {
            gridElements[i].setDisable(false);
            gridElements[i].setVisible(true);
            gridElements[i].getStyleClass().clear();
            gridElements[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
            gridElements[i].setAlignment(Pos.CENTER);
            gridElements[i].setText(Character.toString(alphabets.charAt(random.nextInt(26))));
            // GRID ELEMENT CLASS WORD SET
            gridElements[i].word = gridElements[i].getText().charAt(0);
        }

        // CREATE SOLUTION WORDS ########################
        gameData.loadWordFile(GameState.currentMode);
        int target_score;
        int total_score = 0;

        // TARGET SCORE SETTING BY LEVEL
        if(GameState.currentMode.equals(GameState.ENGLISH_DICTIONARY))
            target_score = Integer.parseInt(levelLabel.getText().split(" ")[1]) * 200;
        else
            target_score = Integer.parseInt(levelLabel.getText().split(" ")[1]) * 50;
        solutions = gameData.getBuzzWordSolution(gridElements);

        // GET TOTAL SCORES
        for (String element : solutions){
            System.out.println(element);
            total_score += element.length() * 10;
//            matches.add(new Label(element));
//            matchedPoints.add(new Label(Integer.toString(element.length() * 10)));
        }
        System.out.println("Target Score : " + target_score);
        System.out.println("Total Score : " + total_score);
        System.out.println("---------------");

        // IF TARGET > TOTAL
        while(target_score > total_score){
            resetScrollPane();
            total_score = 0;
            for(int i = 0; i < gridElements.length; i++){
                gridElements[i].setText(Character.toString(alphabets.charAt(random.nextInt(26))));
                gridElements[i].word = gridElements[i].getText().charAt(0);
            }
            solutions = gameData.getBuzzWordSolution(gridElements);
            // GET TOTAL SCORES
            for (String element : solutions){
                System.out.println(element);
                total_score += element.length() * 10;
//                matches.add(new Label(element));
//                matchedPoints.add(new Label(Integer.toString(element.length() * 10)));
            }
            System.out.println("Target Score : " + target_score);
            System.out.println("Total Score : " + total_score);
            System.out.println("---------------");
        }
        // SET TARGET LABEL
        targetPoint.setText(Integer.toString(target_score));
        matchedWordPane.getChildren().addAll(matches);
        matchedPointPane.getChildren().addAll(matchedPoints);
//        totalPointLabel.setText(Integer.toString(total_score));
    }
    
    public ArrayList<Label> displayAllSolutions() {
        ArrayList<Label> solutionWords = new ArrayList<Label>();
        boolean contains;
        Label word;
        Label length;
        for (String solution : solutions) {
            contains = false;
            for(Label match : matches) {
                if(match.getText().equals(solution)) {
                    contains = true;
                    word = new Label(solution);
                    word.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Source Code Pro'; -fx-font-weight: bolder");
                    solutionWords.add(new Label(solution));
                    break;
                }
            }
            if(!contains) {
                System.out.println(solution);
                word = new Label(solution);
                word.setStyle("-fx-text-fill: red");
                matches.add(word);
                word = new Label(solution);
                word.setStyle("-fx-text-fill: red; -fx-font-family: 'Source Code Pro'; -fx-font-weight: bolder");
                solutionWords.add(word);
                length = new Label(Integer.toString(word.getText().length() * 10));
                length.setStyle("-fx-text-fill : red");
                matchedPoints.add(length);
                }
            }
            
            
        
        matchedWordPane.getChildren().clear();
        matchedPointPane.getChildren().clear();
        matchedWordPane.getChildren().addAll(matches);
        matchedPointPane.getChildren().addAll(matchedPoints);
        
        return solutionWords;
    }

    public boolean confirmBeforeExit() {
        PropertyManager propertyManager = PropertyManager.getManager();
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();

        if(GameState.currentState.equals(GameState.PLAY)) {
            pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PLAY_BUTTON_IMAGE));
            setPausePane(true);
        }


        yesNoCancelDialogSingleton.show("", "Are you sure to exit this application?");
        if(yesNoCancelDialogSingleton.getSelection().equals(yesNoCancelDialogSingleton.YES)) {
            return true;
        }

        if(GameState.currentState.equals(GameState.PLAY)) {
            pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PAUSE_BUTTON_IMAGE));
            setPausePane(false);
        }

        return false;
    }

    public void resetGrid()
    {
        for(int i = 0; i < gridElements.length; i++)
        {
            gridElements[i].setDisable(true);
            gridElements[i].setVisible(true);
            gridElements[i].setVisited(false);
            gridElements[i].getStyleClass().clear();
            gridElements[i].getStyleClass().add(PropertyManager.getManager().getPropertyValue(GRID));
            gridElements[i].setAlignment(Pos.CENTER);
            switch (i)
            {
                case 0:
                    gridElements[i].setText("B");
                    break;
                case 1:
                    gridElements[i].setText("U");
                    break;
                case 4:
                case 5:
                    gridElements[i].setText("Z");
                    break;
                case 10:
                    gridElements[i].setText("W");
                    break;
                case 11:
                    gridElements[i].setText("O");
                    break;
                case 14:
                    gridElements[i].setText("R");
                    break;
                case 15:
                    gridElements[i].setText("D");
                    break;
                default:
                    gridElements[i].setText("");
            }

        }
    }
    
    public void resetScrollPane() {
        // RESET MATCHED VALUES
        matches.clear();
        matchedPoints.clear();
        matchedWordPane.getChildren().clear();
        matchedPointPane.getChildren().clear();
        totalPointLabel.setText("0");
    }

    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET BACKGROUND CSS
        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));
    }

    @Override
    public void reloadWorkspace() {

    }
}
