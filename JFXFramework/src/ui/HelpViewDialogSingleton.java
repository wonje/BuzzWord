package ui;

import controller.GameState;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
    
    final KeyCombination keyQuitApplication         = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    
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
        
        YesNoCancelDialogSingleton yesNoCancelDialogSingleton = YesNoCancelDialogSingleton.getSingleton();

        Button closeButton = new Button(InitializationParameters.CLOSE_LABEL.getParameter());
        closeButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        closeButton.setOnAction(e -> this.close());
        
        Button exitButton = new Button(InitializationParameters.EXIT_LABEL.getParameter());
        exitButton.setStyle("-fx-background-color: black; -fx-border-color: wheat; -fx-border-width: 3; -fx-font-family: 'Arial';" +
                "-fx-font-weight: bolder;-fx-text-fill: wheat;-fx-font-size: 14; -fx-opacity: 1");
        exitButton.setOnAction(e -> {
            yesNoCancelDialogSingleton.show("", "Are you sure to exit this application?");
            if(yesNoCancelDialogSingleton.getSelection().equals(yesNoCancelDialogSingleton.YES)) {
                System.exit(0);
            }
            yesNoCancelDialogSingleton.close();
        });
        
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
        content.setStyle("-fx-text-fill: black; -fx-font-family: 'Source Code Pro'; -fx-font-weight: bolder");
        content.setText("#HOME SCREEN (BEFORE LOGIN)\n1. CREATE NEW PROFILE\n- MAKE NEW PROFILE\n- SHORTCUT : SHIFT + CTRL + P\n" +
                "2. LOGIN\n- GET THE AUTHORIZATION OF YOUR ACCOUNT\n- SHOUTCUT : CTRL + L\n" +
                "3. VIEW HELP\n- POP UP HELP SCREEN\n4. QUIT APPLICATION\n- EXIT THE APPLICATION\n- SHORTCUT : CTRL + Q\n\n" +
                "#HOME SCREEN (AFTER LOGIN)\n1. LOGOUT\n- CANCEL THE AUTHORIZATION OF YOUR ACCOUNT\n-SHORTCUT : CTRL + L\n" +
                "2. START PLAYING\n- GO TO LEVEL SELECTION MODE\n-SHORTCUT : CTRL + P\n" +
                "3. SELECT MODE\n- SELECT GAME PLAY MODE\n(ENGLISH DICTIONARY, BACTERIA, BIOLOGY, FUNGI)\n" +
                "4. VIEW HELP\n- POP UP HELP SCREEN\n5. QUIT APPLICATION\n- EXIT THE APPLICATION\n- SHORTCUT : CTRL + Q\n\n" +
                "#LEVEL SELECTION SCREEN\n1. GAME LEVEL SELECT AND PLAY\n-GAME START WITH PLAYER SELECTED LEVEL\n" +
                "2. RETURN TO HOME SCREEN\n- GO TO HOME SCREEN\n- SHORTCUT : CTRL + H\n3. QUIT APPLICATION\n- EXIT THE APPLICATION\n- SHORTCUT : CTRL + Q\n\n" +
                "#HELP SCREEN\n1. DISPLAY HELP CONTENT TO PLAYER\n- IT IS USEFUL TO PLAY BUZZWORD GAME\n" +
                "2. RETURN TO HOME SCREEN\n- GO TO HOME SCREEN\n- SHORTCUT : CTRL + H\n3. SCROLL THROUGH HELP\n- SCROLL UP AND DOWN BY USING MOUSE OR KEY PRESS\n" +
                "4. QUIT APPLICATION\n- EXIT THE APPLICATION\n- SHORTCUT : CTRL + Q\n\n" +
                "#GAMEPLAY SCREEN\n1. RETURN TO HOME SCREEN\n- GO TO HOME SCREEN\n- SHORTCUT : CTRL + H\n" +
                "2. PAUSE/RESUME GAME\n- PAUSE PLAYING GAME OR RESUME PLAYING GAME\n" +
                "3. SELECT WORD BY MOUSE DRAG\n- GAME PLAY BY USING MOUSE\n- CLICK GRID : START\n- DRAG GRID : ADD SEQUENCE WORD\n" +
                "- DRAG OFF GRID : SEARCH THE SEQUENCE WORDS IN THE WORD FILE.\nIF IT MATCHED, IT WILL BE POSTED TO RIGHT SCROLL PANE\n" +
                "4. SELECT WORD BY TYPING\n- GAME PLAY BY USING KEY BOARD\n- ALPHABET KEY TYPE : ADD SEQUENCE WORD\n" +
                "- ENTER TYPE : SEARCH THE SEQUENCE WORDS IN THE WORD FILE.\nIF IT MATCHED, IT WILL BE POSTED TO RIGHT SCROLL PANE\n" +
                "5. REPLAY GAME\n- RESTART CURRENT LEVEL GAME\n- SHORTCUT : CTRL + R\n" +
                "6. START NEXT LEVEL\n- PLAY NEXT LEVEL GAME IF THE GAME SUCCESSFULLY FINISH\n- SHORTCUT : CTRL + >(RIGHT ARROW KEY)" +
                "7. SAVE PROGRESS\n- SAVE PROGRESSED DATA\n- SHORTCUT : CTRL + S\n" +
                "8. CLOSE \"PERSONAL BEST\" DIALOG\n- CLOSE PERSONAL BEST POPUP IF YOU ATTAIN BEST SCORE\n" +
                "9. LEVEL GAME PLAY ENDS\n- DISPLAY ALL OF SOLUTIONS WHAT YOU DID AND YOU DIDN'T\n" +
                "10. QUIT APPLICATION\n- EXIT THE APPLICATION\n- SHORTCUT : CTRL + Q\n\n");
        
        // ADD TO HELP VIEW PANE
        contentPane.getChildren().add(content);
        helpScrollPane.setContent(contentPane);
        helpViewPane.getChildren().addAll(helpScrollPane, closeButton, exitButton);

        Scene helpViewScene = new Scene(helpViewPane, 700, 700);
    
        helpViewScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // USE CASE 7 : QUIT APPLICATION
                if(keyQuitApplication.match(event)) {
                    yesNoCancelDialogSingleton.show("", "Are you sure to exit this application?");
                    if(yesNoCancelDialogSingleton.getSelection().equals(yesNoCancelDialogSingleton.YES)) {
                        System.exit(0);
                    }
                    yesNoCancelDialogSingleton.close();
                }
            
            }
        });
        
        this.setScene(helpViewScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
    }
    
}
