package ui;

import gui.Workspace;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import propertymanager.PropertyManager;
import settings.InitializationParameters;

import java.util.ArrayList;

import static buzzword.BuzzWordProperties.*;
import static settings.AppPropertyType.*;

/**
 * @author Jason Kang
 */
public class SolutionDialogSingleton extends Stage {
    private static SolutionDialogSingleton singleton = null;
    
    private Label   messageLabel1;
    private Label   messageLabel2;
    private VBox    matchedPane;
    
    private SolutionDialogSingleton() {}
    
    public static SolutionDialogSingleton getSingleton() {
        if(singleton == null) {
            singleton = new SolutionDialogSingleton();
        }
        return singleton;
    }
    
    public void init(Stage owner) {
        PropertyManager propertyManager = PropertyManager.getManager();
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        
        Button closeButton = new Button(InitializationParameters.CLOSE_LABEL.getParameter());
        closeButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        closeButton.setOnAction(e -> this.close());
        
        VBox solutionViewPane = new VBox();
        solutionViewPane.setAlignment(Pos.CENTER);
        solutionViewPane.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        solutionViewPane.setPadding(new Insets(80, 60, 80, 60));
        solutionViewPane.setSpacing(20);
        
        messageLabel1 = new Label("GAME END!");
        messageLabel1.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
    
        messageLabel2 = new Label("THESE ARE SOLUTIONS");
        messageLabel2.setStyle("-fx-text-fill: antiquewhite; -fx-font-family: 'Arial'; -fx-font-size: 22; -fx-font-weight: bolder");
        
        
        
        matchedPane = new VBox();
        
        ScrollPane solutionScrollPane = new ScrollPane();
        solutionScrollPane.setPrefHeight(200);
        solutionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        solutionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        solutionScrollPane.getStyleClass().add(propertyManager.getPropertyValue(SOLUTIONS_SCREEN));
        solutionScrollPane.setContent(matchedPane);
        
        solutionViewPane.getChildren().add(messageLabel1);
        solutionViewPane.getChildren().add(messageLabel2);
        solutionViewPane.getChildren().add(solutionScrollPane);
        solutionViewPane.getChildren().add(closeButton);
        
        Scene helpViewScene = new Scene(solutionViewPane, 400, 400);
        this.setScene(helpViewScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
    }
    
    public void setSolutions(ArrayList<Label> matches) {
        matchedPane.getChildren().clear();
        matchedPane.getChildren().addAll(matches);
    }
    
    public void show(ArrayList<Label> matches) {
        setSolutions(matches);
        showAndWait(); // opens the dialog, and waits for the user to resolve using one of the given choices
    }
}
