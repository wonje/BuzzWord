package gui;

import static buzzword.BuzzWordProperties.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import controller.LoginController;
import data.GameData;
import data.UserData;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
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
    HBox                subTopPane;         // container to display remaining time
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

    private void layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET BACKGROUND AND SECTIONS
        basePane = gui.getAppPane();

        // SET TOP
        topPane = new VBox();
        topPane.setSpacing(30);


        subTopPane = new HBox();

        titleLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_TITLE_LABEL));
        titleLabel.getStyleClass().setAll(propertyManager.getPropertyValue(TITLE_LABEL));

        closeButtonPane = new StackPane();

        closeButton = new Button();

        modeLabel = new Label("MODE LABEL");
        modeLabel.getStyleClass().setAll(propertyManager.getPropertyValue(MODE_LABEL));
        remainingTime = new Label("REMAINING TIME : " + "40" + " seconds");
        remainingTime.getStyleClass().setAll(propertyManager.getPropertyValue(REMAINING_LABEL));

        topPane.getChildren().addAll(titleLabel, subTopPane);
        topPane.setAlignment(Pos.CENTER);

        subTopPane.getChildren().addAll(new Label("                                                                  "), modeLabel, remainingTime);
        subTopPane.setAlignment(Pos.CENTER);
        subTopPane.setSpacing(140);
        subTopPane.setPrefHeight(80);

        basePane.setTop(topPane);
        basePane.setAlignment(topPane, Pos.CENTER);

        // SET RIGHT
        rightStatusPane = new VBox();
        rightStatusPane.setPrefWidth(250);
        rightStatusPane.getChildren().addAll(makeProgressPane());

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

    private GridPane makeProgressPane()
    {
        progress = new Label[16];
        progressPane = new GridPane();
        for(int i = 0; i < progress.length; i++)
        {
            progress[i] = new Label("L");
            progress[i].setStyle("-fx-background-color: saddlebrown; -fx-font-family: 'Source Code Pro'; -fx-text-fill: antiquewhite; -fx-border-color: white");
            progress[i].setMinSize(30,30);
            progress[i].setVisible(true);
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

    // SET HOME SCREEN MODE
    public void setHomeScreen()
    {
        gui.getMenuBackground(0).setVisible(true);
        gui.getMenuBackground(2).setVisible(false);
        gui.getMenuBackground(3).setVisible(false);

        // TODO Init Grid Elements

        return;
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
