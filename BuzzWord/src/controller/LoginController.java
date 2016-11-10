package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Jason Kang
 */
public class LoginController extends Stage {
    static LoginController singleton;

    Scene       loginScene;
    GridPane    loginFrame;
    HBox        buttonBox;
    VBox        mainFrame;
    Label       messageLabel;
    Label       idLabel;
    Label       pwLabel;
    TextField   idField;
    TextField   pwField;
    Button      submit;
    Button      cancel;
    String      id;
    String      pw;

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
        // MAKE MODAL FOR THIS DIALOG
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);

        // DISPLAY LOGIN UI
        mainFrame = new VBox();
        mainFrame.setAlignment(Pos.CENTER);
        mainFrame.setSpacing(10);
        buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        loginFrame = new GridPane();
        loginFrame.setHgap(10);
        loginFrame.setVgap(10);
        loginFrame.setPadding(new Insets(0, 10, 0, 10));

        messageLabel = new Label();
        idLabel = new Label("ID : ");
        pwLabel = new Label("PW : ");
        idField = new TextField();
        pwField = new TextField();
        submit  = new Button("Submit");
        cancel  = new Button("Cancel");

        loginFrame.add(idLabel, 0, 0);
        loginFrame.add(idField, 0, 1);
        loginFrame.add(pwLabel, 1, 0);
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
        mainFrame.getChildren().addAll(loginFrame, buttonBox);

        // PUT IT IN THE WINDOW
        loginScene = new Scene(mainFrame);
        this.setScene(loginScene);
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
