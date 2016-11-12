package gui;

import static buzzword.BuzzWordProperties.*;

import java.io.IOException;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
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
    StackPane           mainFramePane;      // container to stack grid elements and lines on the basePane
    Canvas              canvas;             // canvas to draw lines to connect each of gird elements

    GraphicsContext     drawingFrame;       // drawing lines to display at mainStagePane
    ScrollPane          helpPane;           // container to display help screen
    VBox                topPane;            // container to display labels at top
    BorderPane          upperTopPane;
    BorderPane          lowerTopPane;         // container to display remaining time
    VBox                bottomPane;         // container to display labels at bottom
    VBox                rightStatusPane;    // container to display status at right
    GridPane            mainStagePane;      // container to display all of grid elements
    GridPane            progressPane;
    VBox                modeDisplayPane;    // display mode selections

    Label               titleLabel;         // label to display title
    Label               modeLabel;          // label to display mode
    Label               remainingTime;      // label to display remaining time
    Label               levelLabel;         // label to display level
    Label               totalPoint;         // label to display total points
    Label               targetPoint;        // label to display target point

    Label[]             progress;           // labels to display progressed words
    Label[]             matches;            // labels to display matched words
    Label[]             matchedPoints;      // labels to display points of matched words

    Polygon             playButton;         // shape to make play button design
    Button[]            gridButtons;         // shape to make grid button design

    Accordion           modeSelection;      // button for display modes to select
    Button[]            menuButtons;        // menu buttons
    Button              closeButton;        // close button
    StackPane           closeButtonPane;

    StackPane[]         gridStackPane;




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

    private void layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET BACKGROUND AND SECTIONS
        basePane = gui.getAppPane();

        // SET TOP
        topPane = new VBox();
        topPane.setSpacing(30);

        upperTopPane = new BorderPane();
        lowerTopPane = new BorderPane();

        // UPPER TOP PANE
        titleLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_TITLE_LABEL));
        titleLabel.getStyleClass().setAll(propertyManager.getPropertyValue(TITLE_LABEL));
        
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
                        
                        System.exit(0);
                    }
                });
        closeButtonPane.getChildren().add(closeButton);
        closeButtonPane.setAlignment(Pos.TOP_RIGHT);
        upperTopPane.setCenter(titleLabel);
        upperTopPane.setRight(closeButtonPane);

        // LOWER TOP PANE
        modeLabel = new Label(propertyManager.getPropertyValue(GameState.currentMode));
        modeLabel.getStyleClass().setAll(propertyManager.getPropertyValue(MODE_LABEL));
        modeLabel.setVisible(false);

        remainingTime = new Label("REMAINING TIME : " + "60" + " seconds");
        remainingTime.getStyleClass().setAll(propertyManager.getPropertyValue(REMAINING_LABEL));
        remainingTime.setVisible(false);

        topPane.getChildren().addAll(upperTopPane, lowerTopPane);
        topPane.setAlignment(Pos.CENTER);

        lowerTopPane.setLeft(new Label("                                                           "));
        lowerTopPane.setCenter(modeLabel);
        lowerTopPane.setRight(remainingTime);
        lowerTopPane.setAlignment(modeLabel, Pos.CENTER);
        lowerTopPane.setAlignment(remainingTime, Pos.CENTER);
        lowerTopPane.setPrefHeight(80);

        basePane.setTop(topPane);
        basePane.setAlignment(topPane, Pos.CENTER);

        // SET RIGHT
        rightStatusPane = new VBox();
        rightStatusPane.setPrefWidth(250);
        rightStatusPane.getChildren().addAll(initProgressPane());

        basePane.setRight(rightStatusPane);

        // SET CENTER
        mainFramePane = new StackPane();
        mainStagePane = new GridPane();
        mainStagePane.setHgap(10);
        mainStagePane.setVgap(10);
        mainStagePane.setPadding(new Insets(0, 0, 30, 90));

        // INIT DRAW CONNECTED LINES
        drawGridLines();

        // INIT DISPLAY GRID ELEMENTS
        initGridButtons();

//        mainFramePane.setStyle("-fx-background-color: red");
        mainFramePane.getChildren().addAll(canvas, mainStagePane);
        basePane.setCenter(mainFramePane);








    }

    private GridPane initProgressPane()
    {
        progress = new Label[16];
        progressPane = new GridPane();
        for(int i = 0; i < progress.length; i++)
        {
            progress[i] = new Label("L");
            progress[i].setStyle("-fx-background-color: saddlebrown; -fx-font-family: 'Source Code Pro'; -fx-text-fill: antiquewhite; -fx-border-color: white");
            progress[i].setMinSize(30,30);
            progress[i].setVisible(false);
            progress[i].setAlignment(Pos.CENTER);
            progressPane.add(progress[i], i%8, i/8);
        }
        return progressPane;
    }

    private void drawGridLines()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        String defaultColor = "#000000";

        gc.setFill(Paint.valueOf(defaultColor));
        gc.setLineWidth(1);

        gc.strokeLine(100,100,200,200);

        gc.stroke();
    }

    private void initGridButtons() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gridStackPane = new StackPane[16];
        gridButtons = new Button[16];

        for (int i = 0; i < gridButtons.length; i++)
        {
            gridStackPane[i] = new StackPane();
            gridStackPane[i].setPrefHeight(100);
            gridStackPane[i].setPrefWidth(100);
            gridStackPane[i].setId(propertyManager.getPropertyValue(GRID_UNSELECTED_IMAGE));
            gridButtons[i] = new Button();
            gridStackPane[i].getChildren().add(gridButtons[i]);
            gridButtons[i].getStyleClass().setAll(propertyManager.getPropertyValue(GRID));
            gridButtons[i].setVisible(true);
            mainStagePane.add(gridStackPane[i], i%4, i/4);

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
        gui.getMenuBackground(0).setVisible(true);
        gui.getMenuBackground(2).setVisible(false);
        gui.getMenuBackground(3).setVisible(false);

        // TODO Init Grid Elements

        return;
    }

    // SET LEVEL SELETION SCREEN
    public void setLevelSelectionScreen()
    {
        modeLabel.setVisible(true);
        gui.getPlayAndHomeButton().setText("Home");

        // TODO Different level opend as CurrentState
        for(int i = 0; i < 8; i++)
            gridButtons[i].setText(Integer.toString(i));
        for(int i = 8; i < gridButtons.length; i++)
            gridStackPane[i].setVisible(false);

        // TODO Load data from saved data file
        int engData     = 1;
        int placeData   = 2;
        int scienceData = 3;
        int famousData  = 4;

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
            gridStackPane[i].setId(propertyManager.getPropertyValue(GRID_OPENLEVEL_IMAGE));
            gridButtons[i].getStyleClass().add(propertyManager.getPropertyValue(GRID_OPENLEVEL));
            int level = i+1;
            gridButtons[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // TODO GAMEDATA SET
                            // #################
                            gui.getFileController().handlePlayRequest(level);
                        }
                    });
        }
    }

    // SET PLAYING GAME SCREEN
    public void setGamePlayScreen()
    {

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
