package gui;

import static buzzword.BuzzWordProperties.*;

import java.io.IOException;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
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

    Label               guiHeadingLabel;   // workspace (GUI) heading label
    HBox                headPane;          // container to display the heading
    HBox                bodyPane;          // container for the main game displays
    ToolBar             footToolbar;       // toolbar for game buttons
    VBox                gameTextsPane;     // container to display the text-related parts of the game
    HBox                guessedLetters;    // text area displaying all the letters guessed so far
    HBox                remainingGuessBox; // container to display the number of remaining guesses
    Button              startGame;         // the button to start playing a game of Hangman
    Button              hintButton;
    BuzzWordController  controller;
    Canvas              canvas;
    HBox                hintButtonBox;
    GridPane            triedButtonsPane;
    FlowPane            guessedLettersPane;
    HBox                emptyPane;

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
        layoutGUI();     // initialize all the workspace (GUI) components including the containers and their layout
        setupHandlers(); // ... and set up event handling
    }

    private void layoutGUI() {
        PropertyManager propertyManager = PropertyManager.getManager();
        guiHeadingLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADING_LABEL));

        // make a head pane
        headPane = new HBox();
        headPane.getChildren().add(guiHeadingLabel);
        headPane.setAlignment(Pos.CENTER);

        // TODO make a canvas to draw picture of Hangman
        canvas = new Canvas();
        canvas.setStyle("-fx-background-color: cyan");
        canvas.setWidth(450);
        canvas.setHeight(220);

        // TODO make GUI to display guessed letter buttons
        triedButtonsPane = new GridPane();
        triedButtonsPane.setVgap(10);
        triedButtonsPane.setHgap(10);

        // TODO make Hint button
        hintButton = new Button("Hint");
        hintButtonBox = new HBox();
        hintButton.setDisable(true);

        // make guessed letters pane to display
        guessedLettersPane = new FlowPane();
        guessedLettersPane.setPrefWidth(200);

        // make remaining guess box to display
        remainingGuessBox = new HBox();

        // TODO make game texts pane to display
        gameTextsPane = new VBox();
        gameTextsPane.setSpacing(20);
        gameTextsPane.getChildren().setAll(remainingGuessBox, guessedLettersPane, triedButtonsPane, hintButtonBox);

        // TODO make a body pane
        bodyPane = new HBox();
        bodyPane.getChildren().addAll(canvas, gameTextsPane);

        startGame = new Button("Start Playing");
        HBox blankBoxLeft  = new HBox();
        HBox blankBoxRight = new HBox();
        HBox.setHgrow(blankBoxLeft, Priority.ALWAYS);
        HBox.setHgrow(blankBoxRight, Priority.ALWAYS);
        footToolbar = new ToolBar(blankBoxLeft, startGame, blankBoxRight);

        emptyPane = new HBox();
        emptyPane.setPrefHeight(15);

        workspace = new VBox();
        workspace.getChildren().addAll(headPane, bodyPane, emptyPane, footToolbar);
    }

    private void setupHandlers() {
        startGame.setOnMouseClicked(e -> controller.start());
    }

    /**
     * This function specifies the CSS for all the UI components known at the time the workspace is initially
     * constructed. Components added and/or removed dynamically as the application runs need to be set up separately.
     */
    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));
        gui.getToolbarPane().getStyleClass().setAll(propertyManager.getPropertyValue(SEGMENTED_BUTTON_BAR));
        gui.getToolbarPane().setId(propertyManager.getPropertyValue(TOP_TOOLBAR_ID));

        ObservableList<Node> toolbarChildren = gui.getToolbarPane().getChildren();
        toolbarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(FIRST_TOOLBAR_BUTTON));
        toolbarChildren.get(toolbarChildren.size() - 1).getStyleClass().add(propertyManager.getPropertyValue(LAST_TOOLBAR_BUTTON));

        workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        guiHeadingLabel.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));

    }

    /** This function reloads the entire workspace */
    @Override
    public void reloadWorkspace() {
        /* does nothing; use reinitialize() instead */
    }

    public VBox getGameTextsPane() {
        return gameTextsPane;
    }

    public Canvas getCanvas()
    {
        return canvas;
    }

    public Button getHintButton() { return hintButton; }

    public HBox getRemainingGuessBox() {
        return remainingGuessBox;
    }

    public Button getStartGame() {
        return startGame;
    }

    public void reinitialize() {
        guessedLetters = new HBox();
        guessedLetters.setStyle("-fx-background-color: transparent;");

        remainingGuessBox = new HBox();
        gameTextsPane = new VBox();
        gameTextsPane.setSpacing(20);

        triedButtonsPane = new GridPane();
        triedButtonsPane.setVgap(10);
        triedButtonsPane.setHgap(10);

        hintButtonBox = new HBox();
        hintButton.setDisable(true);

        guessedLettersPane = new FlowPane();
        guessedLettersPane.setPrefWidth(200);

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameTextsPane.getChildren().setAll(remainingGuessBox, guessedLettersPane, triedButtonsPane, hintButtonBox);
        bodyPane.getChildren().setAll(canvas, gameTextsPane);
    }
}
