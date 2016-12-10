package ui;

import apptemplate.AppTemplate;
import controller.GameState;
import data.UserData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * @author Jason Kang
 */
public class ProfileSettingsDialogSingleton extends Stage {
    // HERE'S THE SINGLETON
    static ProfileSettingsDialogSingleton singleton;
    
    final KeyCombination keyESC             = new KeyCodeCombination(KeyCode.ESCAPE);
    final KeyCombination keyEnter           = new KeyCodeCombination(KeyCode.ENTER);
    
    AppTemplate appTemplate;
    UserData userData;
    
    // GUI CONTROLS FOR OUR DIALOG
    VBox            viewProfilePane;
    VBox            editProfilePane;
    StackPane       profileBasicPane;
    Scene           profileScene;
    Button          okButton;
    Button          goBackButton;
    Button          editButton;
    Button          closeButton;
    Button          quitButton;
        
    Label           viewProfileLabel;
    Label           currentIdLabel;
    GridPane        viewInfoGridPane;
    
    Label           editProfileLabel;
    Label           newPwLabel;
    PasswordField   newPwField;
    
    
    private ProfileSettingsDialogSingleton(AppTemplate appTemplate) { this.appTemplate = appTemplate;}
    
    /**
     * The static accessor method for this singleton.
     *
     * @return The singleton object for this type.
     */
    public static ProfileSettingsDialogSingleton getSingleton(AppTemplate appTemplate) {
        if (singleton == null)
            singleton = new ProfileSettingsDialogSingleton(appTemplate);
        return singleton;
    }
    
    /**
     * This method initializes the singleton for use.
     *
     * @param primaryStage The window above which this dialog will be centered.
     */
    public void init(Stage primaryStage) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();
        
        // SETTINGS FOR VIEW PROFILE PANE
        viewProfileLabel = new Label("VIEW YOUR PROFILE DATA");
        viewProfileLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
        currentIdLabel = new Label();
        currentIdLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        viewInfoGridPane = new GridPane();
        viewInfoGridPane.setPadding(new Insets(10, 10, 10, 10));
        
        // SETTINGS FOR EDIT PROFILE PANE
        editProfileLabel = new Label("EDIT YOUR PASSWORD DATA");
        editProfileLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
        newPwLabel = new Label("NEW PW : ");
        newPwLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
        newPwField = new PasswordField();
        newPwField.setStyle("-fx-background-color: wheat; -fx-background-insets: 0 -1 -1 -1, 0 0 0 0, 0 -1 3 -1; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder");
        
        HBox newPwBox = new HBox();
        newPwBox.setAlignment(Pos.CENTER);
        newPwBox.getChildren().addAll(newPwLabel, newPwField);
        
        
        // OK, EDIT, AND CLOSE BUTTONS
        okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    setNewPassword();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        goBackButton = new Button("GO BACK");
        goBackButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        goBackButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setViewProfilePane();
            }
        });
        
        editButton = new Button("EDIT PROFILE");
        editButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setEditProfilePane();
            }
        });
        
        closeButton = new Button("CLOSE");
        closeButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ProfileSettingsDialogSingleton.this.close();
            }
        });
    
        quitButton = new Button("QUIT APPLICATION");
        quitButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        quitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                yesNoCancelDialogSingleton.show("", "Are you sure to exit this application?");
                if(yesNoCancelDialogSingleton.getSelection().equals(yesNoCancelDialogSingleton.YES)) {
                    System.exit(0);
                }
                yesNoCancelDialogSingleton.close();
            }
        });
        
        // NOW ORGANIZE OUR BUTTONS
        HBox viewButtonBox = new HBox();
        HBox editButtonBox = new HBox();
        viewButtonBox.setAlignment(Pos.CENTER);
        editButtonBox.setAlignment(Pos.CENTER);
        viewButtonBox.setSpacing(10);
        editButtonBox.setSpacing(10);
        viewButtonBox.getChildren().addAll(editButton, closeButton);
        editButtonBox.getChildren().addAll(okButton, goBackButton);
        
        // WE'LL PUT EVERYTHING HERE
        viewProfilePane = new VBox();
        viewProfilePane.setSpacing(40);
        viewProfilePane.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        viewProfilePane.setAlignment(Pos.CENTER);
        viewProfilePane.getChildren().addAll(viewProfileLabel, currentIdLabel, viewInfoGridPane, viewButtonBox, quitButton);
    
        editProfilePane = new VBox();
        editProfilePane.setSpacing(60);
        editProfilePane.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        editProfilePane.setAlignment(Pos.CENTER);
        editProfilePane.getChildren().addAll(editProfileLabel, newPwBox, editButtonBox);
        
        // MAKE IT LOOK NICE
        viewProfilePane.setPadding(new Insets(80, 60, 80, 60));
        viewProfilePane.setSpacing(10);
        viewProfilePane.setVisible(true);
        viewProfilePane.setAlignment(Pos.CENTER);
        
        editProfilePane.setPadding(new Insets(80, 60, 80, 60));
        editProfilePane.setSpacing(10);
        editProfilePane.setVisible(false);
        
        // AND PUT IT IN THE WINDOW
        profileBasicPane = new StackPane();
        profileBasicPane.getChildren().addAll(viewProfilePane, editProfilePane);
        
        
        profileScene = new Scene(profileBasicPane, 600, 600);
        profileScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(keyEnter.match(event)) {
                    if(viewProfilePane.isVisible()){
                        // GO TO EDIT PROFILE
                        setEditProfilePane();
                    }
                    else {
                        // SUBMIT TO CHANGE
                        try {
                            setNewPassword();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(keyESC.match(event)) {
                    if(viewProfilePane.isVisible())
                        ProfileSettingsDialogSingleton.this.close();
                    else {
                        // RETURN TO VIEW PROFILE
                        setViewProfilePane();
                    }
                }
            }
        });
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(profileScene);
    }
    
    private void setNewPassword() throws NoSuchAlgorithmException, IOException {
        YesNoCancelDialogSingleton  yesNoCancelDialogSingleton  = YesNoCancelDialogSingleton.getSingleton();
        AppMessageDialogSingleton   appMessageDialogSingleton   = AppMessageDialogSingleton.getSingleton();
        yesNoCancelDialogSingleton.show("","\"" + newPwField.getText() + "\" is your new password?");
        
        // CHANGE PASSWORD
        if(yesNoCancelDialogSingleton.selection.equals(yesNoCancelDialogSingleton.YES)) {
            userData.setUserInfo(userData.getUserID(), convertToMD5(newPwField.getText()));
            appTemplate.getFileComponent().updateProfileData(appTemplate);
            appMessageDialogSingleton.show("", "Your password is changed!");
            newPwField.clear();
        }
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
    
    private void setViewProfilePane() {
        viewInfoGridPane.getChildren().clear();
        userData = (UserData) appTemplate.getUserComponent();
        // SET CURRENT USER ID
        currentIdLabel.setText("ID : " + userData.getUserID());
        
        // SET EACH OF GAME DATAS TO DISPLAY
        Label engLabel = new Label("DICTIONARY  ");
        engLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        GridPane.setHalignment(engLabel, HPos.CENTER);
        Label bacLabel = new Label("BACTERIA  ");
        bacLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        GridPane.setHalignment(bacLabel, HPos.CENTER);
        Label bioLabel = new Label("BIOLOGY  ");
        bioLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        GridPane.setHalignment(bioLabel, HPos.CENTER);
        Label fungiLabel = new Label("FUNGI");
        fungiLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
        GridPane.setHalignment(fungiLabel, HPos.CENTER);
        // (NODE, X POS, Y POS)
        viewInfoGridPane.add(engLabel, 1, 0);
        viewInfoGridPane.add(bacLabel, 2, 0);
        viewInfoGridPane.add(bioLabel, 3, 0);
        viewInfoGridPane.add(fungiLabel, 4, 0);
        
        // LEVEL LABELS
        for(int i = 1; i < 9; i++) {
            Label temp = new Label("LEVEL " + Integer.toString(i) + " ");
            temp.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
            temp.setAlignment(Pos.CENTER);
            viewInfoGridPane.add(temp, 0, i);
        }
        
        // ITERATE ENGLISH DICTIONARY SCORES
        HashMap<Integer, Integer> tempHash = userData.getModeScores(GameState.ENGLISH_DICTIONARY);
        for(int i = 1; i < 9; i++) {
            Label score = new Label();
            score.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
            if(tempHash.get(i) != null)
                score.setText(Integer.toString(tempHash.get(i)));
            else
                score.setText("X");
            viewInfoGridPane.add(score, 1, i);
            GridPane.setHalignment(score, HPos.CENTER);
        }
        
        // ITERATE BACTERAI SCORES
        tempHash = userData.getModeScores(GameState.BACTERIA);
        for(int i = 1; i < 9; i++) {
            Label score = new Label();
            score.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
            score.setAlignment(Pos.CENTER);
            if(tempHash.get(i) != null)
                score.setText(Integer.toString(tempHash.get(i)));
            else
                score.setText("X");
            viewInfoGridPane.add(score, 2, i);
            GridPane.setHalignment(score, HPos.CENTER);
        }
        
        // ITERATE BIOLOGY SCORES
        tempHash = userData.getModeScores(GameState.BIOLOGY);
        for(int i = 1; i < 9; i++) {
            Label score = new Label();
            score.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
            score.setAlignment(Pos.CENTER);
            if(tempHash.get(i) != null)
                score.setText(Integer.toString(tempHash.get(i)));
            else
                score.setText("X");
            viewInfoGridPane.add(score, 3, i);
            GridPane.setHalignment(score, HPos.CENTER);
        }
        
        
        // ITERATE FUNGI SCORES
        tempHash = userData.getModeScores(GameState.FUNGI);
        for(int i = 1; i < 9; i++) {
            Label score = new Label();
            score.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 18; -fx-font-weight: bolder");
            score.setAlignment(Pos.CENTER);
            if(tempHash.get(i) != null)
                score.setText(Integer.toString(tempHash.get(i)));
            else
                score.setText("X");
            viewInfoGridPane.add(score, 4, i);
            GridPane.setHalignment(score, HPos.CENTER);
        }
        
        
        
        editProfilePane.setVisible(false);
        viewProfilePane.setVisible(true);
    }
    
    private void setEditProfilePane() {
        viewProfilePane.setVisible(false);
        editProfilePane.setVisible(true);
    }
    
    
    public void viewProfileshow() {
        // SET VIEW PROFILE
        setViewProfilePane();
        
        // AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
        // WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
        // DO MORE WORK.
        showAndWait();
    }
    
}
