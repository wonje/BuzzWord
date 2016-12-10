package controller;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import data.UserData;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.parser.JSONParser;
import propertymanager.PropertyManager;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_WORKDIR_PATH;

/**
 * @author Jason Kang
 */
public class LoginController extends Stage {
    static LoginController singleton;

    AppTemplate     appTemplate;

    Scene           loginScene;
    GridPane        loginFrame;
    HBox            buttonBox;
    VBox            mainFrame;
    Label           messageLabel;
    Label           idLabel;
    Label           pwLabel;
    TextField       idField;
    PasswordField   pwField;
    Button          submit;
    Button          cancel;
    String          id;
    String          pw;
    
    final KeyCombination keyEnter           = new KeyCodeCombination(KeyCode.ENTER);
    final KeyCombination keyESC             = new KeyCodeCombination(KeyCode.ESCAPE);

    private LoginController(AppTemplate appTemplate) {
        this.appTemplate    = appTemplate;
    }

    public static LoginController getSingleton(AppTemplate appTemplate)
    {
        if (singleton == null)
            singleton = new LoginController(appTemplate);
        
        return singleton;
    }

    public String getID()
    {
        return id;
    }

    public String getPW()
    {
        return pw;
    }

    public void init(Stage primaryStage)
    {
        AppMessageDialogSingleton   appMessageDialogSingleton   = AppMessageDialogSingleton.getSingleton();
        YesNoCancelDialogSingleton  yesNoCancelDialogSingleton  = YesNoCancelDialogSingleton.getSingleton();

        // MAKE MODAL FOR THIS DIALOG
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);

        // DISPLAY LOGIN UI
        mainFrame = new VBox();
        mainFrame.setAlignment(Pos.CENTER);
        mainFrame.setSpacing(30);
        mainFrame.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        loginFrame = new GridPane();
        loginFrame.setAlignment(Pos.CENTER);
        loginFrame.setHgap(10);
        loginFrame.setVgap(10);
        loginFrame.setPadding(new Insets(0, 10, 0, 10));

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
        idLabel = new Label("ID : ");
        idLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        pwLabel = new Label("PW : ");
        pwLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        idField = new TextField();
        idField.setStyle("-fx-background-color: wheat; -fx-background-insets: 0 -1 -1 -1, 0 0 0 0, 0 -1 3 -1; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder");
        pwField = new PasswordField();
        pwField.setStyle("-fx-background-color: wheat; -fx-background-insets: 0 -1 -1 -1, 0 0 0 0, 0 -1 3 -1; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder");
        submit  = new Button("SUBMIT");
        submit.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        cancel  = new Button("CANCEL");
        cancel.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        loginFrame.add(idLabel, 0, 0);
        loginFrame.add(idField, 1, 0);
        loginFrame.add(pwLabel, 0, 1);
        loginFrame.add(pwField, 1, 1);

        submit.setOnAction(event -> {
            setSubmit();
        });
        cancel.setOnAction(event -> {
            singleton.hide();
        });

        buttonBox.getChildren().addAll(submit, cancel);
        mainFrame.getChildren().addAll(messageLabel, loginFrame, buttonBox);
        
        // PUT IT IN THE WINDOW
        loginScene = new Scene(mainFrame,400, 300);
        this.setScene(loginScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
    }
    
    private void setSubmit() {
        AppMessageDialogSingleton   appMessageDialogSingleton   = AppMessageDialogSingleton.getSingleton();
        YesNoCancelDialogSingleton  yesNoCancelDialogSingleton  = YesNoCancelDialogSingleton.getSingleton();
        // CRAETE PROFILE MODE
        if(messageLabel.getText().equals(PropertyManager.getManager().getPropertyValue(CREATE_PROFILE_MESSAGE))) {
            try {
                isExistedID(idField.getText());
                appMessageDialogSingleton.setMessageLabel("The ID already exists!");
                appMessageDialogSingleton.show();
            
            } catch (IOException e) {
                if (!(idField.getText().equals("") || pwField.getText().equals(""))) {
                    yesNoCancelDialogSingleton.show("", "Do you want to make '" + idField.getText() + "'?");
                
                    if (yesNoCancelDialogSingleton.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
                        this.id = idField.getText();
                        try {
                            this.pw = convertToMD5(pwField.getText());
                        } catch (NoSuchAlgorithmException e1) {
                            e1.printStackTrace();
                        }
                    
                        // TODO SAVE PROFILE DATA WITH JSON FILE
                        try {
                            createNewProfile();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    
                        appMessageDialogSingleton.setMessageLabel("'" + id + "' is created!");
                        appMessageDialogSingleton.show();
                        singleton.hide();
                    }
                } else {
                    appMessageDialogSingleton.setMessageLabel("It is not correct type of ID & PW!");
                    appMessageDialogSingleton.show();
                }
            }
        }
        // LOGIN MODE
        else
        {
            if(!(idField.getText().equals("") || pwField.getText().equals(""))) {
                try {
                    isExistedID(idField.getText());
                    if(loadProfile(idField.getText())) {
                        id = idField.getText();
                        pw = convertToMD5(pwField.getText());
                        GameState.currentState = GameState.LOGIN;
                        singleton.hide();
                    }
                    else {
                        appMessageDialogSingleton.setMessageLabel("Invalid Password!");
                        appMessageDialogSingleton.show();
                    }
                
                } catch (IOException e) {
                    appMessageDialogSingleton.setMessageLabel("'" + idField.getText() + "' is not existed ID!");
                    appMessageDialogSingleton.show();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                appMessageDialogSingleton.setMessageLabel("It is not correct type of ID & PW!");
                appMessageDialogSingleton.show();
            }
        }
    }

    private  void setKeyShortcut() {
        loginScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(keyEnter.match(event)) {
                    setSubmit();
                }
                if(keyESC.match(event)) {
                    singleton.hide();
                }
        
            }
        });
    }
    
    private void createNewProfile() throws IOException {
        PropertyManager propertyManager         = PropertyManager.getManager();
        Path appDirPath                         = Paths.get(propertyManager.getPropertyValue(APP_TITLE)).toAbsolutePath();
        Path targetPath                         = appDirPath.resolve(APP_WORKDIR_PATH.getParameter());
        Path target                             = Paths.get(targetPath.toString() + "\\" + id + "." + propertyManager.getPropertyValue(WORK_FILE_EXT));

        appTemplate.getFileComponent().createProfile(appTemplate, target);
    }

    private boolean loadProfile(String userID) throws IOException, NoSuchAlgorithmException {
        PropertyManager propertyManager         = PropertyManager.getManager();
        Path appDirPath                         = Paths.get(propertyManager.getPropertyValue(APP_TITLE)).toAbsolutePath();
        Path targetPath                         = appDirPath.resolve(APP_WORKDIR_PATH.getParameter());
        Path target                             = Paths.get(targetPath.toString() + "\\" + userID + "." + propertyManager.getPropertyValue(WORK_FILE_EXT));

        return appTemplate.getFileComponent().loadProfile(appTemplate, convertToMD5(pwField.getText()), target);
    }

    private void isExistedID(String testID) throws IOException {
        PropertyManager propertyManager         = PropertyManager.getManager();
        Path appDirPath                         = Paths.get(propertyManager.getPropertyValue(APP_TITLE)).toAbsolutePath();
        Path targetPath                         = appDirPath.resolve(APP_WORKDIR_PATH.getParameter());
        Path idCheckerPath                      = Paths.get(targetPath.toString() + "\\" + testID + "." + propertyManager.getPropertyValue(WORK_FILE_EXT));

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser  jsonParser  = new JsonFactory().createParser(Files.newInputStream(idCheckerPath));
    }
    
    private String convertToMD5(String original) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest)
            sb.append(String.format("%02x", b & 0xff));
        
        return sb.toString();
    }

    private void reset()
    {
        id = "";
        pw = "";
        idField.clear();
        pwField.clear();
    }

    public void show(String title, String message)
    {
        // SET KEY SHORTCUT
        setKeyShortcut();
        
        // RESET VARIABLES
        reset();

        // SET THE DIALOG TITLE BAR TITLE
        setTitle(title);

        // SET THE MESSAGE TO DISPLAY TO THE USER
        messageLabel.setText(message);

        // AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
        // WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
        // DO MORE WORK.
        showAndWait();
    }

}
