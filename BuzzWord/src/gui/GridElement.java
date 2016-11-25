package gui;


import apptemplate.AppTemplate;
import data.GameData;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import propertymanager.PropertyManager;

import java.awt.Point;

import static buzzword.BuzzWordProperties.GRID;
import static buzzword.BuzzWordProperties.GRID_SELECTED;


/**
 * @author Jason Kang
 */
public class GridElement extends Button{
    Point       pos;
    char        word;
    boolean     visited;
    AppTemplate appTemplate;
    Workspace   gameWorkspace;
    GameData    gameData;

    public GridElement(Point pos) {
        this.pos        = pos;
        this.visited    = false;
        this.setOnEventHandler();
    }

    public GridElement(Point pos, Workspace gameWorkspace, AppTemplate appTemplate){
        this(pos);
        this.gameWorkspace  = gameWorkspace;
        this.appTemplate    = appTemplate;
        this.gameData       = (GameData) appTemplate.getDataComponent();
    }

    public GridElement(Point pos, Workspace gameWorkspace, AppTemplate appTemplate, String word){
        this(pos, gameWorkspace, appTemplate);
        this.word = word.charAt(0);
    }

    public Point getPoint() {
        return pos;
    }

    public char getWord() {
        return word;
    }

    private Label progressLabel(String guess) {
        Label label = new Label(guess);
        label.setStyle("-fx-background-color: dimgray; -fx-font-family: 'Arial'; " +
                    "-fx-text-fill: antiquewhite; -fx-font-weight: bolder; -fx-font-size: 14");
        label.setMinSize(30, 30);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    // MOUSE EVENT HANDLING
    public void setOnEventHandler() {
        PropertyManager propertyManager = PropertyManager.getManager();
        // START DRAG
        this.setOnDragDetected(mouseEvent -> {
            this.startFullDrag();
        });

        // DRAGING
        this.setOnMouseDragEntered(mouseDragEvent -> {
            if(this.visited == true)
                return;
            this.visited = true;
            // MAKE PROGRESS AND CHANGE GRID CSS
            gameWorkspace.progress.add(progressLabel(Character.toString(word)));
            gameWorkspace.progressPane.add(gameWorkspace.progress.get(gameWorkspace.progress.size() - 1),
                    (gameWorkspace.progress.size() - 1) % 8, (gameWorkspace.progress.size() - 1) / 8);
            this.getStyleClass().clear();
            this.getStyleClass().addAll(propertyManager.getPropertyValue(GRID_SELECTED));
            // PUSH TO GRID STACK
            gameData.gridStack.push(this);
        });

        // FINISH DRAGING
        this.setOnMouseDragReleased(mouseDragEvent -> {
            // MATCHING PROGRESS
            StringBuilder progressStr = new StringBuilder();
            for (Label progress : gameWorkspace.progress) {
                progressStr.append(Character.toLowerCase(progress.getText().charAt(0)));
            }
            System.out.println(progressStr.toString());
            // IF PROGRESS IS MATCHED WITH SOLUTION AND NOT DUPLICATED ONE
            if(gameWorkspace.solutions.contains(progressStr.toString()) &&
                    !gameData.matchedStr.contains(progressStr.toString())) {
                gameWorkspace.matches.add(new Label(progressStr.toString()));
                gameData.matchedStr.add(progressStr.toString());
                gameWorkspace.matchedPoints.add(new Label(Integer.toString(progressStr.toString().length() * 10)));
                gameWorkspace.matchedWordPane.getChildren().clear();
                gameWorkspace.matchedWordPane.getChildren().addAll(gameWorkspace.matches);
                gameWorkspace.matchedPointPane.getChildren().clear();
                gameWorkspace.matchedPointPane.getChildren().addAll(gameWorkspace.matchedPoints);
                gameWorkspace.totalPointLabel.setText(Integer.toString(progressStr.toString().length() * 10 +
                        Integer.parseInt(gameWorkspace.totalPointLabel.getText())));
            }
            // POP FROM GRID STACK, CHANGE GRID CSS, AND ERASE LINES
            GridElement grid;
            while(!gameData.gridStack.isEmpty()) {
                grid = gameData.gridStack.pop();
                grid.getStyleClass().clear();
                grid.getStyleClass().addAll(propertyManager.getPropertyValue(GRID));
                grid.visited = false;
            }
            while(!gameData.lineStack.isEmpty()) {
                gameData.lineStack.pop().setVisible(false);
            }
            // PROGRESS RESET
            gameWorkspace.progress.clear();
            gameWorkspace.progressPane.getChildren().clear();

        });
    }

}
