package ui;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

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

import apptemplate.AppTemplate;
import components.AppStyleArbiter;
import controller.FileController;
import controller.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

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
    protected Button         createAndSetProfileButton;
    protected Button         loginAndIDButton;
    protected Button         modeButton;
    protected Button         modeCancelButton;
    protected Button         playAndHomeButton;
    protected String         applicationTitle; // the application title
    protected StackPane[]    menuBackgrounds;
    protected VBox           modeDisplayPane;

    protected Button         engDicMode;
    protected Button         placeMode;
    protected Button         scienceMode;
    protected Button         famousPplMode;

    private int appWindowWidth;  // optional parameter for window width that can be set by the application
    private int appWindowHeight; // optional parameter for window height that can be set by the application
    
    // KEY CODE COMBINATIONS
    final KeyCombination keyCreateProfile           = new KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyLoginLogout             = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyStartPlaying            = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyQuitApplication         = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyHomeScreen              = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyReplayLevel             = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyStartNextLevel          = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
    final KeyCombination keySaveProgress            = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    
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
    
    public void setTooltipCreateIDtoProfileSetting(boolean choice) {
        if (choice)
            createAndSetProfileButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(SETTING_PROFILE_TOOLTIP)));
        else
            createAndSetProfileButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(CREATE_ID_TOOLTIP)));
    }

    public void setTooltipLogintoID(boolean choice)
    {
        if (choice)
            loginAndIDButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(DISPLAY_ID_TOOLTIP)));
        else
            loginAndIDButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(LOGIN_TOOLTIP)));
    }

    public void setTooltipPlaytoHome(boolean choice)
    {
        if (choice)
            playAndHomeButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(HOME_TOOLTIP)));
        else
            playAndHomeButton.setTooltip(new Tooltip(PropertyManager.getManager().getPropertyValue(PLAYING_TOOLTIP)));
    }

    public FileController getFileController() {
        return this.fileController;
    }

    public VBox getMenubarPane() { return menubarPane; }

    public VBox getModeDisplayPane() { return modeDisplayPane; }

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
        menubarPane.setAlignment(Pos.CENTER);
        menuBackgrounds = new StackPane[4];
        createAndSetProfileButton     = initializeChildButton(0, menubarPane, MENU_IMAGE.toString(), CREATE_ID_TOOLTIP.toString(), true, "Create New Profile");
        loginAndIDButton        = initializeChildButton(1, menubarPane, MENU_IMAGE.toString(), LOGIN_TOOLTIP.toString(), true, "Login");
        playAndHomeButton       = initializeChildButton(2, menubarPane, MENU_IMAGE.toString(), PLAYING_TOOLTIP.toString(), false, "Start Playing");
        modeButton              = initializeChildButton(3, menubarPane, MENU_MODE_IMAGE.toString(), SELECT_MODE_TOOLTIP.toString(), false, "Select Mode");
    }
    
    private void setKeyShortcut() {
        primaryScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // USE CASE 1 : CREATE PROFILE && USE CASE 17 : SETTING PROFILE DATA
                if(keyCreateProfile.match(event)) {
                    if(GameState.currentState.equals(GameState.UNLOGIN))
                        fileController.handleNewProfileRequest();
                    else if(GameState.currentState.equals(GameState.LOGIN) ||
                        GameState.currentState.equals(GameState.LOGIN_MODE))
                        fileController.handleProfileSettingRequest();
                }
                // USE CASE 2 : LOGIN / LOGOUT
                if(keyLoginLogout.match(event)) {
                    if (GameState.currentState.equals(GameState.UNLOGIN))
                        fileController.handleLoginRequest();
                    else if (!(GameState.currentState.equals(GameState.PLAY) ||
                            GameState.currentState.equals(GameState.PAUSE)))
                        fileController.handleLogoutRequest();
                }
                // USE CASE 3 : START PLAYING
                if(keyStartPlaying.match(event)) {
                    if(GameState.currentState.equals(GameState.LOGIN) || GameState.currentState.equals(GameState.LOGIN_MODE))
                        fileController.handleLevelSelectRequest();
                }
                // USE CASE 7 : QUIT APPLICATION
                if(keyQuitApplication.match(event)) {
                    fileController.handleQuitRequest();
                }
                // USE CASE 8 : RETURN TO HOME SCREEN
                if(keyHomeScreen.match(event)) {
                 fileController.handleGoHomeRequest();
                }
                // USE CASE 12 : REPLAY LEVEL
                if(keyReplayLevel.match(event)) {
                    
                }
                // USE CASE 13 : START NEXT LEVEL
                if(keyStartNextLevel.match(event)) {
                    
                }
                // USE CASE 14 : SAVE PROGRESS
                if(keySaveProgress.match(event)) {
                    
                }
                
            }
        });
        
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
        createAndSetProfileButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO Save ID data from LoginController
                        if(GameState.currentState.equals(GameState.UNLOGIN))
                            fileController.handleNewProfileRequest();
                        else if(GameState.currentState.equals(GameState.LOGIN) ||
                                GameState.currentState.equals(GameState.LOGIN_MODE))
                            fileController.handleProfileSettingRequest();
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
//                        else if (!(GameState.currentState.equals(GameState.PLAY) ||
//                                GameState.currentState.equals(GameState.PAUSE)))
                        else if(GameState.currentState.equals(GameState.LOGIN) ||
                                GameState.currentState.equals(GameState.LOGIN_MODE))
                            fileController.handleLogoutRequest();
                    }
                });

        playAndHomeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // IF GAMESTATE == LOGIN
                        if(GameState.currentState.equals(GameState.LOGIN) || GameState.currentState.equals(GameState.LOGIN_MODE))
                            fileController.handleLevelSelectRequest();
                        else
                            fileController.handleGoHomeRequest();
                    }
                });

        modeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // IF GAMESTATE == LOGIN
                        if (GameState.currentState.equals(GameState.LOGIN))
                            fileController.handleModeRequest();
                        else
                            fileController.handleGoHomeRequest();
                    }
                });
        modeCancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // IF GAMESTATE == LOGIN_MODE
                        if (GameState.currentState.equals(GameState.LOGIN_MODE))
                            fileController.handleModeCancelRequest();
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
        
        // GET KEY COMBINATIONS
        setKeyShortcut();
        
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
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

        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(50);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add(propertyManager.getPropertyValue(MENU_BUTTON));
        Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
        button.setTooltip(buttonTooltip);

        menuBackgrounds[order] = new StackPane();

        // MODE MENU BUTTON
        if(icon.equals(MENU_MODE_IMAGE.toString()))
            return makeModeMenu(menuBackgrounds[order], (VBox)menubarPane, button);


        menuBackgrounds[order].setPrefHeight(70);
        menuBackgrounds[order].setPrefWidth(200);
        menuBackgrounds[order].setId(propertyManager.getPropertyValue(icon));
        menuBackgrounds[order].setVisible(visible);

        menuBackgrounds[order].getChildren().add(button);
        menuBackgrounds[order].setAlignment(Pos.TOP_CENTER);

        menubarPane.getChildren().add(menuBackgrounds[order]);

        return button;
    }

    private Button makeModeMenu(StackPane background, VBox menubar, Button button)
    {
        PropertyManager propertyManager = PropertyManager.getManager();
        // SET MODE DISPLAY PANE
        background.setPrefHeight(300);
        background.setPrefWidth(200);
        background.setId(propertyManager.getPropertyValue(MENU_MODE_IMAGE));
        background.setVisible(false);
        background.setAlignment(Pos.TOP_CENTER);

        modeCancelButton = new Button("Select Mode");
        modeCancelButton.setPrefWidth(180);
        modeCancelButton.setPrefHeight(50);
        modeCancelButton.setAlignment(Pos.CENTER_LEFT);
        modeCancelButton.getStyleClass().add(propertyManager.getPropertyValue(MENU_BUTTON));

        modeDisplayPane = new VBox();
//            modeDisplayPane.setStyle("-fx-background-color: red");
        modeDisplayPane.setSpacing(25);
        modeDisplayPane.setPrefHeight(300);
        modeDisplayPane.setPrefWidth(200);
        modeDisplayPane.setAlignment(Pos.TOP_CENTER);
        modeDisplayPane.setId(propertyManager.getPropertyValue(MENU_MODEDISPLAY_IMAGE));

        // SET MODE LIST BUTTONS
        engDicMode = new Button("English Dictionary");
        engDicMode.setPrefWidth(170);
        engDicMode.setPrefHeight(30);
        engDicMode.setAlignment(Pos.CENTER_LEFT);
        engDicMode.getStyleClass().add(propertyManager.getPropertyValue(MODE_LIST));
        engDicMode.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fileController.handleModeSetRequest(GameState.ENGLISH_DICTIONARY);
                    }
                });

        placeMode = new Button("Bacteria");
        placeMode.setPrefWidth(170);
        placeMode.setPrefHeight(30);
        placeMode.setAlignment(Pos.CENTER_LEFT);
        placeMode.getStyleClass().add(propertyManager.getPropertyValue(MODE_LIST));
        placeMode.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fileController.handleModeSetRequest(GameState.BACTERIA);
                    }
                });

        scienceMode = new Button("Biology");
        scienceMode.setPrefWidth(170);
        scienceMode.setPrefHeight(30);
        scienceMode.setAlignment(Pos.CENTER_LEFT);
        scienceMode.getStyleClass().add(propertyManager.getPropertyValue(MODE_LIST));
        scienceMode.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fileController.handleModeSetRequest(GameState.BIOLOGY);
                    }
                });

        famousPplMode = new Button("Fungi");
        famousPplMode.setPrefWidth(170);
        famousPplMode.setPrefHeight(30);
        famousPplMode.setAlignment(Pos.CENTER_LEFT);
        famousPplMode.getStyleClass().add(propertyManager.getPropertyValue(MODE_LIST));
        famousPplMode.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fileController.handleModeSetRequest(GameState.FUNGI);
                    }
                });

        modeDisplayPane.getChildren().addAll(modeCancelButton, engDicMode, placeMode, scienceMode, famousPplMode);
        background.getChildren().addAll(button, modeDisplayPane);
            modeDisplayPane.setVisible(false);

        menubar.getChildren().add(background);

        return button;
    }

    public Button getLoginAndIDButton()
    {
        return loginAndIDButton;
    }

    public Button getPlayAndHomeButton()
    {
        return playAndHomeButton;
    }
    
    public Button getCreateAndSetProfileButton() { return createAndSetProfileButton; }
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    @Override
    public void initStyle() {
        // currently, we do not provide any stylization at the framework-level
    }
}
