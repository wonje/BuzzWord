package gui;

import static buzzword.BuzzWordProperties.*;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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

    GridElement[] gridElements;

    BorderPane basePane;           // main container to divide sections
    BorderPane centerPane;
    StackPane mainFramePane;      // container to stack grid elements and lines on the basePane

    ScrollPane helpPane;           // container to display help screen
    VBox topPane;            // container to display labels at top
    VBox bottomPane;         // container to display labels at bottom
    VBox rightPane;          // container to display status at right
    VBox rightStatusPane;
    BorderPane pausePane;
    GridPane mainStagePane;      // container to display all of grid elements
    GridPane progressPane;

    Label titleLabel;         // label to display title
    Label modeLabel;          // label to display mode
    Label remainingTime;      // label to display remaining time
    Label levelLabel;         // label to display level
    Label targetPoint;        // label to display target point
    Label totalPointLabel;

    Label[] progress;           // labels to display progressed words
    ArrayList<Label> matches;            // labels to display matched words
    ArrayList<Label> matchedPoints;      // labels to display points of matched words

    Button pauseAndPlayButton;
    Button[] gridButtons;         // shape to make grid button design

    Button closeButton;        // close button
    StackPane closeButtonPane;
    StackPane pauseAndPlayButtonPane;
    GridPane remainingTimePane;

    VBox matchedContainerPane;
    ScrollPane matchedScrollPane;
    HBox totalPointPane;
    HBox matchedPane;
    VBox matchedWordPane;
    VBox matchedPointPane;

    VBox targetPointPane;

    Line[] vLines;
    Line[] hLines;
    Line[] lrLines;
    Line[] rlLines;

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

    public Label getModeLabel() {
        return modeLabel;
    }

    public void setPausePane(boolean visible) {
        pausePane.setVisible(visible);
        mainStagePane.setVisible(!visible);
    }

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

        BorderPane closeButtonContainerPane = new BorderPane();
        closeButtonPane = new StackPane();
        closeButtonPane.setPrefHeight(30);
        closeButtonPane.setPrefWidth(30);
        closeButtonPane.setId(propertyManager.getPropertyValue(CLOSE_BUTTON_IMAGE));
        closeButton = new Button();
        closeButton.setPrefWidth(40);
        closeButton.setPrefHeight(40);
        closeButton.setStyle("-fx-background-color: transparent");
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO Confirm again before terminating the game
                        if (confirmBeforeExit())
                            System.exit(0);
                    }
                });
        closeButtonPane.getChildren().add(closeButton);
        closeButtonPane.setAlignment(Pos.TOP_RIGHT);
        closeButtonContainerPane.setRight(closeButtonPane);

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
        totalPointLabel = new Label();
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
        rightPane.getChildren().addAll(closeButtonContainerPane, emptyPane, remainingTimePane, rightStatusPane, targetPointPane);

        basePane.setRight(rightPane);
    }

    private GridPane initProgressPane() {
        progress = new Label[16];
        progressPane = new GridPane();
        for (int i = 0; i < progress.length; i++) {
            progress[i] = new Label();
            progress[i].setStyle("-fx-background-color: dimgray; -fx-font-family: 'Arial'; " +
                    "-fx-text-fill: antiquewhite; -fx-font-weight: bolder; -fx-font-size: 14");
            progress[i].setMinSize(30, 30);
            progress[i].setVisible(false);
            progress[i].setAlignment(Pos.CENTER);

            progressPane.add(progress[i], i % 8, i / 8);
        }
        return progressPane;
    }

    private void initMainStage() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // INIT VLINE COMPONENTS
        vLines = new Line[12];
        for (int i = 0; i < vLines.length; i++) {
            vLines[i] = new Line(0, 0, 0, 17);
            vLines[i].setFill(Paint.valueOf("#000000"));
            vLines[i].setStrokeWidth(3);
            vLines[i].setSmooth(true);
            }
            // INIT HLINE COMPONENTS
            hLines = new Line[12];
            for (int i = 0; i < hLines.length; i++) {
                hLines[i] = new Line(0, 0, 17, 0);
                hLines[i].setFill(Paint.valueOf("#000000"));
                hLines[i].setStrokeWidth(3);
                hLines[i].setSmooth(true);
            }

            // INIT LEFT TO RIGHT DIAGONAL LINE COMPONENTS
            lrLines = new Line[9];
            for (int i = 0; i < lrLines.length; i++) {
                lrLines[i] = new Line(0, 0, 15, 15);
                lrLines[i].setFill(Paint.valueOf("#000000"));
                lrLines[i].setStrokeWidth(3);
                lrLines[i].setSmooth(true);
            }

            // INIT RIGHT TO LEFT DIAGONAL LINE COMPONENTS
            rlLines = new Line[9];
            for (int i = 0; i < rlLines.length; i++) {
                rlLines[i] = new Line(15, 0, 0, 15);
                rlLines[i].setFill(Paint.valueOf("#000000"));
                rlLines[i].setStrokeWidth(3);
                rlLines[i].setSmooth(true);
            }

            // INIT GRID BUTTON COMPONENTS
            gridButtons = new Button[16];
            gridElements = new GridElement[16];
            int xPos = 0;
            int yPos = 0;
            for (int i = 0; i < gridButtons.length; i++) {
                gridButtons[i] = new Button();
                gridButtons[i].setShape(new Circle(50));
                gridButtons[i].setMinSize(100, 100);
                gridButtons[i].setMaxSize(100, 100);
                gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                gridButtons[i].setVisible(true);
                gridButtons[i].setDisable(true);

                // GRID ELEMENT CLASS INIT
                gridElements[i] = new GridElement(new Point(xPos, yPos));
                if (xPos == 6) {
                    xPos = 0;
                    yPos += 2;
                } else {
                    xPos += 2;
                }

            }

            int vLineCount = 0;
            int hLineCount = 0;
            int diagonalLineCount = 0;
            int gridCount = 0;
            StackPane tempStack;

            for (int i = 0; i < 49; i++) {
                if (i < 7 || (i >= 14 && i < 21) || (i >= 28 && i < 35) || i >= 42) {
                    if (i % 2 == 0) {
                        // CHECK GRID COMPONENT TURN
                        mainStagePane.add(gridButtons[gridCount], i % 7, i / 7);
                        // INIT GRID ELEMENT OBJECT
                        gridElements[gridCount] = new GridElement(new Point(i % 7, i / 7));
                        gridElements[gridCount].setGridButton(gridButtons[gridCount++]);
                    } else {
                        // CHECK HLINE COMPONENT TURN
                        mainStagePane.add(hLines[hLineCount], i % 7, i / 7);
                        hLines[hLineCount++].setVisible(false);
                    }
                }
                // CHECK VLINE COMPONENT TURN
                else {
                    if (i % 2 == 1) {
                        tempStack = new StackPane();
                        tempStack.setAlignment(Pos.CENTER);
                        tempStack.getChildren().add(vLines[vLineCount]);
                        vLines[vLineCount++].setVisible(false);
                        mainStagePane.add(tempStack, i % 7, i / 7);
                    } else {
                        tempStack = new StackPane();
                        tempStack.setAlignment(Pos.CENTER);
                        tempStack.getChildren().addAll(lrLines[diagonalLineCount], rlLines[diagonalLineCount]);
                        lrLines[diagonalLineCount].setVisible(false);
                        rlLines[diagonalLineCount++].setVisible(false);
                        mainStagePane.add(tempStack, i % 7, i / 7);
                    }
                }

                switch (gridCount - 1) {
                    case 0:
                        gridButtons[gridCount - 1].setText("B");
                        break;
                    case 1:
                        gridButtons[gridCount - 1].setText("U");
                        break;
                    case 4:
                    case 5:
                        gridButtons[gridCount - 1].setText("Z");
                        break;
                    case 10:
                        gridButtons[gridCount - 1].setText("W");
                        break;
                    case 11:
                        gridButtons[gridCount - 1].setText("O");
                        break;
                    case 14:
                        gridButtons[gridCount - 1].setText("R");
                        break;
                    case 15:
                        gridButtons[gridCount - 1].setText("D");
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

        // LINE UNDISPLAY BY FAKE DATA
        hLines[0].setVisible(false);
        vLines[1].setVisible(false);
        hLines[4].setVisible(false);
        hLines[5].setVisible(false);

        // UNDISPLAY RIGHT STATUS PANE
        rightStatusPane.setVisible(false);
        remainingTimePane.setVisible(false);
        targetPointPane.setVisible(false);

    }

    // SET LEVEL SELETION SCREEN
    public void setLevelSelectionScreen()
    {
        modeLabel.setVisible(true);
        gui.getPlayAndHomeButton().setText("Home");

        // CREATE GRID ELEMENT GUI
        for(int i = 0; i < 8; i++)
            gridButtons[i].setText(Integer.toString(i+1));
        for(int i = 8; i < gridButtons.length; i++)
            gridButtons[i].setVisible(false);

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
            gridButtons[i].getStyleClass().clear();
            gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_OPENLEVEL));
            gridButtons[i].setAlignment(Pos.CENTER);
            gridButtons[i].setDisable(false);
            int level = i+1;
            gridButtons[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // TODO GAMEDATA SETs
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
        for (int i = 0; i < gridButtons.length; i++)
        {
            gridButtons[i].setDisable(false);
            gridButtons[i].setVisible(true);
            gridButtons[i].getStyleClass().clear();
            gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
            gridButtons[i].setAlignment(Pos.CENTER);
            gridButtons[i].setText(Character.toString(alphabets.charAt(random.nextInt(26))));
            // GRID ELEMENT CLASS WORD SET
            gridElements[i].word = gridButtons[i].getText().charAt(0);
            // GET ACTION EVENT TO GRID BUTTONS
            gridButtons[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for (GridElement grid : gridElements) {
                        // SEARCH GRID ELEMENT OBJECT
                        if(event.getSource().equals(grid.getGridButton()))
                            // CHECKING IT WAS VISITED OR NOT
                                if(!grid.visited) {
                                    grid.getGridButton().getStyleClass().clear();
                                    grid.getGridButton().getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                                    grid.visited = true;
                                }
                    }

                }
            });
            gridButtons[i].addEventHandler(MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                }
            });
        }

        for (Label prog : progress)
            prog.setVisible(true);

        // CREATE SOLUTION WORDS ########################
        gameData.loadWordFile(GameState.currentMode);
        int target_score;
        int total_score = 0;

        // TARGET SCORE SETTING BY LEVEL
        if(GameState.currentMode.equals(GameState.ENGLISH_DICTIONARY))
            target_score = Integer.parseInt(levelLabel.getText().split(" ")[1]) * 800;
        else
            target_score = Integer.parseInt(levelLabel.getText().split(" ")[1]) * 100;
        List<String> list = gameData.getBuzzWordSolution(gridElements);

        // GET TOTAL SCORES
        for (String element : list){
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
            for(int i = 0; i < gridButtons.length; i++){
                gridButtons[i].setText(Character.toString(alphabets.charAt(random.nextInt(26))));
                gridElements[i].word = gridButtons[i].getText().charAt(0);
            }
            list = gameData.getBuzzWordSolution(gridElements);
            // GET TOTAL SCORES
            for (String element : list){
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

    private boolean confirmBeforeExit() {
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
        for(int i = 0; i < gridButtons.length; i++)
        {
            gridButtons[i].setDisable(true);
            gridButtons[i].setVisible(true);
            gridButtons[i].getStyleClass().clear();
            gridButtons[i].getStyleClass().add(PropertyManager.getManager().getPropertyValue(GRID));
            gridButtons[i].setAlignment(Pos.CENTER);
            switch (i)
            {
                case 0:
                    gridButtons[i].setText("B");
                    break;
                case 1:
                    gridButtons[i].setText("U");
                    break;
                case 4:
                case 5:
                    gridButtons[i].setText("Z");
                    break;
                case 10:
                    gridButtons[i].setText("W");
                    break;
                case 11:
                    gridButtons[i].setText("O");
                    break;
                case 14:
                    gridButtons[i].setText("R");
                    break;
                case 15:
                    gridButtons[i].setText("D");
                    break;
                default:
                    gridButtons[i].setText("");
            }

        }
    }

    private void resetScrollPane() {
        // RESET MATCHED VALUES
        matches.clear();
        matchedPoints.clear();
        matchedWordPane.getChildren().clear();
        matchedPointPane.getChildren().clear();
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
