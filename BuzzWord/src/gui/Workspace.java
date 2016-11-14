package gui;

import static buzzword.BuzzWordProperties.*;

import java.io.IOException;
import java.util.ArrayList;

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
import propertymanager.PropertyManager;
import ui.AppGUI;

/**
 * This class serves as the GUI component for the Hangman game.
 *
 * @author Jason Kang
 */
public class Workspace extends AppWorkspaceComponent {

    AppTemplate app; // the actual application
    AppGUI      gui; // the GUI inside which the application sits

    GameData    gameData;
    UserData    userData;

    BuzzWordController  controller;
    LoginController     loginController;

    BorderPane          basePane;           // main container to divide sections
    BorderPane          centerPane;
    StackPane           mainFramePane;      // container to stack grid elements and lines on the basePane
    Canvas              canvas;             // canvas to draw lines to connect each of gird elements

    GraphicsContext     drawingFrame;       // drawing lines to display at mainStagePane
    ScrollPane          helpPane;           // container to display help screen
    VBox                topPane;            // container to display labels at top
    VBox                bottomPane;         // container to display labels at bottom
    VBox                rightPane;          // container to display status at right
    VBox                rightStatusPane;
    BorderPane          pausePane;
    GridPane            mainStagePane;      // container to display all of grid elements
    GridPane            progressPane;

    Label               titleLabel;         // label to display title
    Label               modeLabel;          // label to display mode
    Label               remainingTime;      // label to display remaining time
    Label               levelLabel;         // label to display level
    Label               targetPoint;        // label to display target point

    Label[]             progress;           // labels to display progressed words
    ArrayList<Label>    matches;            // labels to display matched words
    ArrayList<Label>    matchedPoints;      // labels to display points of matched words

    Button              pauseAndPlayButton;
    Button[]            gridButtons;         // shape to make grid button design

    Accordion           modeSelection;      // button for display modes to select
    Button[]            menuButtons;        // menu buttons
    Button              closeButton;        // close button
    StackPane           closeButtonPane;
    StackPane           pauseAndPlayButtonPane;
    GridPane            remainingTimePane;

    VBox                matchedContainerPane;
    ScrollPane          matchedScrollPane;
    HBox                totalPointPane;
    HBox                matchedPane;
    VBox                matchedWordPane;
    VBox                matchedPointPane;

    VBox                targetPointPane;


    int                 time;
    int                 totalPoint;


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
        canvas = new Canvas();
        controller = (BuzzWordController) gui.getFileController();    //new HangmanController(app, startGame); <-- THIS WAS A MAJOR BUG!??
        loginController = LoginController.getSingleton();
        layoutGUI();     // initialize all the workspace (GUI) components including the containers and their layout
    }

    public Label getModeLabel() { return modeLabel; }

    public void setPausePane(boolean visible)
    {
        pausePane.setVisible(visible);
        canvas.setVisible(!visible);
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
        mainStagePane.setHgap(10);
        mainStagePane.setVgap(10);
        mainStagePane.setPadding(new Insets(30, 0, 30, 80));

        // INIT PAUSE PANE
        pausePane = new BorderPane();
        pausePane.setStyle("-fx-background-color: transparent");
        pausePane.setVisible(false);
        Label pause = new Label("PAUSE");
        pause.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 30; -fx-font-weight: bolder; -fx-text-fill: antiquewhite; -fx-font-style: italic");
        pausePane.setCenter(pause);
        pausePane.setAlignment(pause, Pos.CENTER);

        // INIT DRAW CONNECTED LINES
        initGridLines();

        // INIT DISPLAY GRID ELEMENTS
        initGridButtons();

        mainFramePane.getChildren().addAll(canvas, mainStagePane, pausePane);
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
                        if(GameState.currentState.equals(GameState.PLAY)) {
                            GameState.currentState = GameState.PAUSE;
                            pauseAndPlayButtonPane.setId(propertyManager.getPropertyValue(PLAY_BUTTON_IMAGE));
                            gui.getFileController().handlePauseRequest();
                        }
                        else if(GameState.currentState.equals(GameState.PAUSE))
                        {
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
        matches         = new ArrayList<Label>();

        matchedPointPane    = new VBox();
        matchedPoints       = new ArrayList<Label>();

        HBox borderline1 = new HBox();
        borderline1.setPrefHeight(280);
        borderline1.setPrefWidth(3);
        borderline1.setStyle("-fx-background-color: black");

        // FAKE DATA
        matches.add(new Label("SUCCESS"));
        matches.add(new Label("CSE"));
        matchedPoints.add(new Label("70"));
        matchedPoints.add(new Label("30"));

        matchedWordPane.getChildren().addAll(matches);

        matchedPointPane.getChildren().addAll(matchedPoints);

        // TOTAL POINT
        totalPointPane = new HBox();
        totalPointPane.setStyle("-fx-background-color: dimgray");
        Label total = new Label("TOTAL");
        total.setPrefWidth(171);
        // FAKE DATA
        HBox borderline2 = new HBox();
        borderline2.setPrefWidth(3);
        borderline2.setStyle("-fx-background-color: black");

        totalPoint = 0;
        for (int i = 0; i < matchedPoints.size(); i++)
            totalPoint += Integer.parseInt(matchedPoints.get(i).getText());

        totalPointPane.getChildren().addAll(total, borderline2, new Label(Integer.toString(totalPoint)));

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
        targetPoint = new Label("150");
        targetPoint.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");

        targetPointPane.getChildren().addAll(targetDisplay, targetPoint);

        rightStatusPane.getChildren().addAll(initProgressPane(), matchedContainerPane);
        rightPane.getChildren().addAll(closeButtonContainerPane, emptyPane, remainingTimePane, rightStatusPane, targetPointPane);

        basePane.setRight(rightPane);
    }

    private GridPane initProgressPane()
    {
        progress = new Label[16];
        progressPane = new GridPane();
        for(int i = 0; i < progress.length; i++)
        {
            progress[i] = new Label();
            progress[i].setStyle("-fx-background-color: dimgray; -fx-font-family: 'Arial'; " +
                    "-fx-text-fill: antiquewhite; -fx-font-weight: bolder; -fx-font-size: 14");
            progress[i].setMinSize(30,30);
            progress[i].setVisible(false);
            progress[i].setAlignment(Pos.CENTER);
            switch (i)
            {
                case 0:
                    progress[i].setText("S");
                    break;
                case 1:
                    progress[i].setText("T");
                    break;
                case 2:
                    progress[i].setText("O");
                    break;
                case 3:
                    progress[i].setText("N");
                    break;
                case 4:
                    progress[i].setText("Y");
                    break;
            }

            progressPane.add(progress[i], i%8, i/8);
        }
        return progressPane;
    }

    private void initGridLines()
    {
        canvas.setWidth(380);
        canvas.setHeight(340);


        GraphicsContext gc = canvas.getGraphicsContext2D();
        String defaultColor = "#000000";

        gc.setFill(Paint.valueOf(defaultColor));
        gc.setLineWidth(5);

        gc.strokeLine(27,3,370,3);
        gc.strokeLine(27,110,357,110);
        gc.strokeLine(27,220,357,220);
        gc.strokeLine(27,330,357,330);
        gc.strokeLine(27, 3, 27, 330);
        gc.strokeLine(137, 3, 137, 330);
        gc.strokeLine(247, 3, 247, 330);
        gc.strokeLine(357, 3, 357, 330);
        canvas.setVisible(false);
    }

    private void initGridButtons() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gridButtons = new Button[16];

        for (int i = 0; i < gridButtons.length; i++)
        {
            gridButtons[i] = new Button();
            gridButtons[i].setShape(new Circle(50));
            gridButtons[i].setMinSize(100, 100);
            gridButtons[i].setMaxSize(100, 100);
            gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
            gridButtons[i].setVisible(true);
            gridButtons[i].setDisable(true);
            mainStagePane.add(gridButtons[i], i%4, i/4);

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
            }
        }
    }

    // SET HOME SCREEN SCREEN
    public void setHomeScreen()
    {
        gui.getPlayAndHomeButton().setText("Start Playing");
        resetGrid();
        modeLabel.setVisible(false);
        pauseAndPlayButtonPane.setVisible(false);
        levelLabel.setVisible(false);

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

        // TODO Different level opend as CurrentState
        for(int i = 0; i < 8; i++)
            gridButtons[i].setText(Integer.toString(i+1));
        for(int i = 8; i < gridButtons.length; i++)
            gridButtons[i].setVisible(false);

        // TODO Load data from saved data file
        int engData     = 4;
        int placeData   = 5;
        int scienceData = 6;
        int famousData  = 7;

        if(GameState.currentMode.equals(GameState.ENGLISH_DICTIONARY))
        {
            setOpenedGrid(engData);
        }
        else if(GameState.currentMode.equals(GameState.PLACES))
        {
            setOpenedGrid(placeData);
        }
        else if(GameState.currentMode.equals(GameState.SCIENCE))
        {
            setOpenedGrid(scienceData);
        }
        else if(GameState.currentMode.equals(GameState.FAMOUS_PEOPLE))
        {
            setOpenedGrid(famousData);
        }


    }

    private void setOpenedGrid(int data)
    {
        PropertyManager propertyManager = PropertyManager.getManager();
        for(int i=0; i < data; i++)
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

        // PANES DISPLAY SET
        canvas.setVisible(true);
        // DISPLAY RIGHT STATUS
        rightStatusPane.setVisible(true);
        remainingTimePane.setVisible(true);
        targetPointPane.setVisible(true);

        levelLabel.setVisible(true);
        pauseAndPlayButtonPane.setVisible(true);
        levelLabel.setText("Level " + Integer.toString(GameState.currentLevel));
        GameState.currentState = GameState.PLAY;
        for (Label prog : progress)
            prog.setVisible(true);

        for (int i = 0; i < gridButtons.length; i++) {
            gridButtons[i].setDisable(false);
            gridButtons[i].setVisible(true);
            gridButtons[i].getStyleClass().clear();
            gridButtons[i].setAlignment(Pos.CENTER);
            switch (i) {
                case 0:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                    gridButtons[i].setText("S");
                    break;
                case 1:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                    gridButtons[i].setText("T");
                    break;
                case 5:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                    gridButtons[i].setText("O");
                    break;
                case 6:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                    gridButtons[i].setText("N");
                    break;
                case 7:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_SELECTED));
                    gridButtons[i].setText("Y");
                    break;
                case 8:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("E");
                    break;
                case 9:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("S");
                    break;
                case 10:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("S");
                    break;
                case 12:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("C");
                    break;
                case 13:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("C");
                    break;
                case 14:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("U");
                    break;
                case 15:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("S");
                    break;
                default:
                    gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID));
                    gridButtons[i].setText("A");
            }
        }
    }

    public void resetGrid()
    {
        canvas.setVisible(false);
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
