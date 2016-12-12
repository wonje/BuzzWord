package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.security.Key;

/**
 * @author Jason Kang
 */
public class YesNoCancelDialogSingleton extends Stage {
    // HERE'S THE SINGLETON
    static YesNoCancelDialogSingleton singleton;
    
    final KeyCombination keyESC           = new KeyCodeCombination(KeyCode.ESCAPE);
    final KeyCombination keyEnter           = new KeyCodeCombination(KeyCode.ENTER);

    // GUI CONTROLS FOR OUR DIALOG
    VBox   messagePane;
    Scene  messageScene;
    Label  messageLabel;
    Button yesButton;
    Button noButton;
    Button cancelButton;
    String selection = "";

    // CONSTANT CHOICES
    public static final String YES    = "Yes";
    public static final String NO     = "No";
    public static final String CANCEL = "Cancel";
    
    public void setMessage(String message) {
        messageLabel.setText(message);
        selection = "";
    }

    /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     */
    private YesNoCancelDialogSingleton() {}

    /**
     * The static accessor method for this singleton.
     *
     * @return The singleton object for this type.
     */
    public static YesNoCancelDialogSingleton getSingleton() {
        if (singleton == null)
            singleton = new YesNoCancelDialogSingleton();
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

        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");

        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        yesButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        noButton = new Button(NO);
        noButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        cancelButton = new Button(CANCEL);
        cancelButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");

        // MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler<ActionEvent> yesNoCancelHandler = event -> {
            YesNoCancelDialogSingleton.this.selection = ((Button) event.getSource()).getText();
//            YesNoCancelDialogSingleton.this.hide();
            YesNoCancelDialogSingleton.this.close();
        };


        // AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        noButton.setOnAction(yesNoCancelHandler);
        cancelButton.setOnAction(yesNoCancelHandler);

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.getChildren().add(yesButton);
        buttonBox.getChildren().add(noButton);
        buttonBox.getChildren().add(cancelButton);

        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setSpacing(40);
        messagePane.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);
        messagePane.getChildren().add(buttonBox);

        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(80, 60, 80, 60));
        messagePane.setSpacing(10);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
//        messageScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if(keyESC.match(event)) {
//                    YesNoCancelDialogSingleton.this.selection = CANCEL;
//                    YesNoCancelDialogSingleton.this.close();
//                }
//                if(keyEnter.match(event)) {
//                    YesNoCancelDialogSingleton.this.selection = YES;
//                    YesNoCancelDialogSingleton.this.close();
//                }
//            }
//        });
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(messageScene);
    }

    /**
     * Accessor method for getting the selection the user made.
     *
     * @return Either YES, NO, or CANCEL, depending on which
     * button the user selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }
    

    /**
     * This method loads a custom message into the label
     * then pops open the dialog.
     *
     * @param title   The title to appear in the dialog window bar.
     * @param message Message to appear inside the dialog.
     */
    public void show(String title, String message) {
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
