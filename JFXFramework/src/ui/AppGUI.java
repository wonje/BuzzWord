package ui;

import apptemplate.AppTemplate;
import components.AppStyleArbiter;
import controller.FileController;
import controller.LoginController;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

import controller.GameState;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

/**
 * This class provides the basic user interface for this application, including all the file controls, but it does not
 * include the workspace, which should be customizable and application dependent.
 *
 * @author Jason Kang
 */
public class AppGUI implements AppStyleArbiter {

    protected FileController fileController;   // to react to file-related controls
    protected Stage          primaryStage;     // the application window
    protected Scene          primaryScene;     // the scene graph
    protected BorderPane     appPane;          // the root node in the scene graph, to organize the containers
    protected VBox           menubarPane;      // the left menubar
    protected Button         createProfileButton;
    protected Button         loginAndIDButton;
    protected Button         modeAndHomeButton;
    protected Button         playingButton;
    protected String         applicationTitle; // the application title
    protected StackPane[]    menuBackgrounds;

    private int appWindowWidth;  // optional parameter for window width that can be set by the application
    private int appWindowHeight; // optional parameter for window height that can be set by the application
    
    /**
     * This constructor initializes the file toolbar for use.
     *
     * @param initPrimaryStage The window for this application.
     * @param initAppTitle     The title of this application, which
     *                         will appear in the window bar.
     * @param app              The app within this gui is used.
     */
    public AppGUI(Stage initPrimaryStage, String initAppTitle, AppTemplate app) throws IOException, InstantiationException {
        this(initPrimaryStage, initAppTitle, app, -1, -1);
    }

    public AppGUI(Stage primaryStage, String applicationTitle, AppTemplate appTemplate, int appWindowWidth, int appWindowHeight) throws IOException, InstantiationException {
        this.appWindowWidth = appWindowWidth;
        this.appWindowHeight = appWindowHeight;
        this.primaryStage = primaryStage;
        this.applicationTitle = applicationTitle;
        initializeMenubar();                    // initialize the left menu bar
        initializeMenubarHandlers(appTemplate); // set the menu bar button handlers
        initializeWindow();                     // start the app window (without the application-specific workspace)

    }

    public FileController getFileController() {
        return this.fileController;
    }

    public VBox getMenubarPane() { return menubarPane; }

    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     *
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }

    public StackPane getMenuBackground(int order) { return menuBackgrounds[order]; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     *
     * @return This application's primary stage (i.e. window).
     */
    public Stage getWindow() { return primaryStage; }
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initializeMenubar() throws IOException {
        menubarPane = new VBox();
        menubarPane.setSpacing(10);
        menuBackgrounds = new StackPane[4];
        createProfileButton     = initializeChildButton(0, menubarPane, MENU_IMAGE.toString(), CREATE_ID_TOOLTIP.toString(), true, "Create New Profile");
        loginAndIDButton        = initializeChildButton(1, menubarPane, MENU_IMAGE.toString(), LOGIN_TOOLTIP.toString(), true, "Login");
        modeAndHomeButton       = initializeChildButton(2, menubarPane, MENU_MODE_IMAGE.toString(), SELECT_MODE_TOOLTIP.toString(), false, "Select Mode");
        playingButton           = initializeChildButton(3, menubarPane, MENU_IMAGE.toString(), PLAYING_TOOLTIP.toString(), false, "Start Playing");
    }

    private void initializeMenubarHandlers(AppTemplate app) throws InstantiationException {
        try {
            Method getFileControllerClassMethod = app.getClass().getMethod("getFileControllerClass");
            String fileControllerClassName = (String) getFileControllerClassMethod.invoke(app);
            Class<?> klass = Class.forName("controller." + fileControllerClassName);
            Constructor<?> constructor = klass.getConstructor(AppTemplate.class);
            fileController = (FileController) constructor.newInstance(app);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // TODO Get event only from MOUSE CLICK. --> IGNORE SPACE BAR INPUT
        createProfileButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO Save ID data from LoginController
                        fileController.handleNewProfileRequest();
                    }
                }
        );

        loginAndIDButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // IF GAMESTATE == UNLOGIN
                        if (GameState.currentState.equals(GameState.UNLOGIN))
                            fileController.handleLoginRequest();
                    }
                });

        modeAndHomeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // IF GAMESTATE == LOGIN
                        if (GameState.currentState.equals(GameState.LOGIN))
                            fileController.handleModeRequest();
                        // IF GAMESTATE == LOGIN_MODE
                        else if (GameState.currentState.equals(GameState.LOGIN_MODE))
                            fileController.handleModeCancelRequest();
                        // IF GAMESTATE != LOGIN OR LOGIN_MODE
                        else
                            fileController.handleGoHomeRequest();
                    }
                });

    }
    private void initializeWindow() throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET THE WINDOW TITLE
        primaryStage.setTitle(applicationTitle);

        // add the menubar to the constructed workspace
        appPane = new BorderPane();
        appPane.setLeft(menubarPane);
        appPane.setAlignment(menubarPane, Pos.CENTER);
        primaryScene = appWindowWidth < 1 || appWindowHeight < 1 ? new Scene(appPane)
                                                                 : new Scene(appPane,
                                                                             appWindowWidth,
                                                                             appWindowHeight);


        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resrouces folder does not exist.");
        try (InputStream appLogoStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(APP_LOGO)))) {
            primaryStage.getIcons().add(new Image(appLogoStream));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     *
     * @param menubarPane Menubar pane into which to place this button.
     * @param icon        image name for the button.
     * @param tooltip     Tooltip to appear when the user mouses over the button.
     * @param visible     true if the button is to start off visibled, false otherwise.
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initializeChildButton(int order, Pane menubarPane, String icon, String tooltip, boolean visible, String text) throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();

        menuBackgrounds[order] = new StackPane();
        menuBackgrounds[order].setPrefHeight(70);
        menuBackgrounds[order].setPrefWidth(200);
        menuBackgrounds[order].setId(propertyManager.getPropertyValue(icon));
        menuBackgrounds[order].setVisible(visible);

        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(50);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add(propertyManager.getPropertyValue(MENU_BUTTON));
        Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
        button.setTooltip(buttonTooltip);

        menuBackgrounds[order].getChildren().add(button);

        menubarPane.getChildren().add(menuBackgrounds[order]);

        return button;
    }

    public void setTextLoginID(String text)
    {
        loginAndIDButton.setText(text);
    }

    public void setTextModeHome(String text)
    {
        modeAndHomeButton.setText(text);
    }
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    @Override
    public void initStyle() {
        // currently, we do not provide any stylization at the framework-level
    }
}
