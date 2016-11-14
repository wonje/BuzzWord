package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

/**
 * @author Jason Kang
 */
public class LoginController extends Stage {
    static LoginController singleton;

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

    private LoginController() {}

    public static LoginController getSingleton()
    {
        if (singleton == null)
            singleton = new LoginController();
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
        PropertyManager propertyManager = PropertyManager.getManager();
        // MAKE MODAL FOR THIS DIALOG
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);

        // DISPLAY LOGIN UI
        mainFrame = new VBox();
        mainFrame.setAlignment(Pos.CENTER);
        mainFrame.setSpacing(30);
        mainFrame.setStyle("-fx-background-color: black");
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
        submit  = new Button("Submit");
        submit.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        cancel  = new Button("Cancel");
        cancel.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        loginFrame.add(idLabel, 0, 0);
        loginFrame.add(idField, 1, 0);
        loginFrame.add(pwLabel, 0, 1);
        loginFrame.add(pwField, 1, 1);

        submit.setOnAction(e -> {
            this.id = idField.getText();
            this.pw = pwField.getText();
            singleton.hide();
        });
        cancel.setOnAction(e -> {
            singleton.hide();
        });

        buttonBox.getChildren().addAll(submit, cancel);
        mainFrame.getChildren().addAll(messageLabel, loginFrame, buttonBox);

        // PUT IT IN THE WINDOW
        loginScene = new Scene(mainFrame,400, 300);
        this.setScene(loginScene);
        this.setResizable(false);
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
