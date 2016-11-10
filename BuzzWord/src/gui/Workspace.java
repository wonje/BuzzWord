package gui;

import static buzzword.BuzzWordProperties.*;

import java.io.IOException;

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
import javafx.scene.layout.*;
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

    StackPane           basePane;           // container to display background
    StackPane           mainFramePane;      // container to stack grid elements and lines on the basePane
    Canvas              canvas;             // canvas to draw lines to connect each of gird elements
    GraphicsContext     drawingFrame;       // drawing lines to display at mainStagePane
    BorderPane          sectionPane;        // container to divide sections
    ScrollPane          helpPane;           // container to display help screen
    VBox                topPane;            // container to display labels at top
    VBox                bottomPane;         // container to display labels at bottom
    VBox                rightStatusPane;    // container to display status at right
    VBox                leftMenuPane;       // container to display menus at left side
    GridPane            mainStagePane;      // container to display all of grid elements

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
    Circle              girdButton;         // shape to make grid button design

    Accordion           modeSelection;      // button for display modes to select
    Button[]            menuButtons;        // menu buttons
    Button              closeButton;        // close button




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
        controller = (BuzzWordController) gui.getFileController();    //new HangmanController(app, startGame); <-- THIS WAS A MAJOR BUG!??
        loginController = LoginController.getSingleton();
        layoutGUI();     // initialize all the workspace (GUI) components including the containers and their layout
    }

    private void layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET BACKGROUND
        basePane = gui.getAppPane();

        // SET SECTIONS
        sectionPane = new BorderPane();

        // SET TOP
        titleLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_TITLE_LABEL));
        titleLabel.getStyleClass().setAll(propertyManager.getPropertyValue(TITLE_LABEL));
        sectionPane.setTop(titleLabel);

        // SET LEFT
        leftMenuPane = new VBox();
        leftMenuPane.setSpacing(10);
        leftMenuPane.setPadding(new Insets(10));

        menuButtons = new Button[4];
        for(Button button : menuButtons) {
            button = new Button();
            button.setVisible(false);
        }
        // BUTTON INIT SET
        menuButtons[0].setText(propertyManager.getPropertyValue(CREATE_PROFILE_LABEL));
        menuButtons[0].setVisible(true);
        menuButtons
        menuButtons[1].setText(propertyManager.getPropertyValue(LOGIN_LABEL));
        menuButtons[1].setVisible(true);






        // DISPLAY PANES
        basePane.getChildren().add(sectionPane);



    }

    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_STACKPANE_ID));
        gui.getToolbarPane().getStyleClass().setAll(propertyManager.getPropertyValue(SEGMENTED_BUTTON_BAR));
        gui.getToolbarPane().setId(propertyManager.getPropertyValue(TOP_TOOLBAR_ID));

        ObservableList<Node> toolbarChildren = gui.getToolbarPane().getChildren();
        toolbarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(FIRST_TOOLBAR_BUTTON));
        toolbarChildren.get(toolbarChildren.size() - 1).getStyleClass().add(propertyManager.getPropertyValue(LAST_TOOLBAR_BUTTON));

        workspace.getStyleClass().add(CLASS_STACKED_PANE);
    }

}
