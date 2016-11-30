package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
        
        ScrollPane helpScrollPane = new ScrollPane();
        helpScrollPane.setPrefHeight(500);
        helpScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        helpScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        helpScrollPane.getStyleClass().add(PropertyManager.getManager().getPropertyValue(HELP_SCREEN));
        
    
        Scene helpViewScene = new Scene(helpViewPane);
        this.setScene(helpViewScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
    }
    
    
}
