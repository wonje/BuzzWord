package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import propertymanager.PropertyManager;
import settings.InitializationParameters;
import static settings.AppPropertyType.*;

/**
 * @author Jason Kang
 */
public class HelpViewDialogSingleton extends Stage {
    
    private static HelpViewDialogSingleton singleton = null;
    
    private HelpViewDialogSingleton() {}
    
    public static HelpViewDialogSingleton getSingleton() {
        if(singleton == null) {
            singleton = new HelpViewDialogSingleton();
        }
        return singleton;
    }

    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        Button closeButton = new Button(InitializationParameters.CLOSE_LABEL.getParameter());
        closeButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        closeButton.setOnAction(e -> this.close());

        VBox helpViewPane = new VBox();
        helpViewPane.setStyle("-fx-background-color: black; -fx-border-color: wheat");
        helpViewPane.setPadding(new Insets(80, 60, 80, 60));
        helpViewPane.setSpacing(20);
        helpViewPane.setAlignment(Pos.CENTER);

        ScrollPane helpScrollPane = new ScrollPane();
        helpScrollPane.setPrefHeight(600);
        helpScrollPane.setPrefWidth(600);
        helpScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        helpScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        helpScrollPane.getStyleClass().clear();
        helpScrollPane.getStyleClass().addAll(PropertyManager.getManager().getPropertyValue(HELP_SCREEN));
        
        // MAKE CONTENT FOR HELP SCREEN
        VBox contentPane = new VBox();
        contentPane.setPrefWidth(600);
        contentPane.setAlignment(Pos.TOP_LEFT);
        contentPane.setStyle("-fx-background-color: antiquewhite; -fx-border-color: wheat");
        Label content = new Label();
        content.setStyle("-fx-text-fill: black; -fx-font-family: 'Arial'; -fx-font-weight: bolder");
        content.setText("#HOME SCREEN (BEFORE LOGIN)\n1. CREATE NEW PROFILE\n- MAKE NEW PROFILE\n- SHORTCUT : SHIFT + CTRL + P\n" +
                "2. LOGIN\n- GET THE AUTHORIZATION OF YOUR ACCOUNT\n- SHOUTCUT : CTRL + L\n\n" +
                "#HOME SCREEN (AFTER LOGIN)\n1. LOGOUT\n- CANCEL THE AUTHORIZATION OF YOUR ACCOUNT\n-SHORTCUT : CTRL + L\n" +
                "2. START PLAYING\n- GO TO LEVEL SELECTION MODE\n-SHORTCUT : CTRL + P\n" +
                "3. SELECT MODE\n- SELECT GAME PLAY MODE\n(ENGLISH DICTIONARY, BACTERIA, BIOLOGY, FUNGI)\n" +
                "4. VIEW HELP\n- POP UP HELP SCREEN\n5. QUIT APPLICATION\n- EXIT THE APPLICATION\n\n" +
                "#LEVEL SELECTION SCREEN");
        
        // ADD TO HELP VIEW PANE
        contentPane.getChildren().add(content);
        helpScrollPane.setContent(contentPane);
        helpViewPane.getChildren().addAll(helpScrollPane, closeButton);

        Scene helpViewScene = new Scene(helpViewPane, 700, 700);
        this.setScene(helpViewScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
    }
    
}
