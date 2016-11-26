package gui;


import static buzzword.BuzzWordProperties.GRID;
import static buzzword.BuzzWordProperties.GRID_SELECTED;

import java.awt.*;

import apptemplate.AppTemplate;
import data.GameData;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import propertymanager.PropertyManager;


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

    // RESET PROGRESS
    private void resetProgress() {
        PropertyManager propertyManager = PropertyManager.getManager();
        // POP FROM GRID STACK AND CHANGE GRID CSS
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
        // POP FROM LINE STACK, ERASE LINES
        while(!gameData.lineStack.isEmpty()) {
            gameData.lineStack.pop().setVisible(false);
        }

        // PROGRESS RESET
        gameWorkspace.progress.clear();
        gameWorkspace.progressPane.getChildren().clear();
    }

    // MOUSE EVENT HANDLING
    public void setOnEventHandler() {
        PropertyManager propertyManager = PropertyManager.getManager();
        // START DRAG
        this.setOnDragDetected(mouseEvent -> {
            this.startFullDrag();
        });

        // DRAG
        this.setOnMouseDragEntered(mouseDragEvent -> {
            // SKIP IF THE NODE IS NOT ADJACENT NODE FROM LAST NODE
            if((!gameData.gridStack.isEmpty()) && (Math.abs(gameData.gridStack.peek().pos.getX() - this.pos.getX()) > 2 ||
                    Math.abs(gameData.gridStack.peek().pos.getY() - this.pos.getY()) > 2))
                return;
            // SKIP IF THE NODE WAS VISITED
            if(this.visited == true)
                return;
            this.visited = true;
            // MAKE PROGRESS AND CHANGE GRID CSS
            gameWorkspace.progress.add(progressLabel(Character.toString(word)));
            gameWorkspace.progressPane.add(gameWorkspace.progress.get(gameWorkspace.progress.size() - 1),
                    (gameWorkspace.progress.size() - 1) % 8, (gameWorkspace.progress.size() - 1) / 8);
            this.getStyleClass().clear();
            this.getStyleClass().addAll(propertyManager.getPropertyValue(GRID_SELECTED));
            // DRAW LINE
            LineElement temp = null;
            if(!gameData.gridStack.isEmpty()) {
                temp = new LineElement(new Point((int) (this.pos.getX() + gameData.gridStack.peek().pos.getX()) / 2,
                        (int) (this.pos.getY() + gameData.gridStack.peek().pos.getY()) / 2));
                for (int i = 0; i < gameWorkspace.lineElements.length; i++) {
                    if (gameWorkspace.lineElements[i].pos.equals(temp.pos)) {
                        // CHECK DIAGONAL RIGHT TO LEFT
                        if((gameData.gridStack.peek().pos.getY() > pos.getY() && gameData.gridStack.peek().pos.getX() < pos.getX() ||
                                (gameData.gridStack.peek().pos.getY() < pos.getY() && gameData.gridStack.peek().pos.getX() > pos.getX()))){
                            gameWorkspace.lineElements[++i].setVisible(true);
                        }
                        else {
                            gameWorkspace.lineElements[i].setVisible(true);
                        }
                        gameData.lineStack.push(gameWorkspace.lineElements[i]);
                        break;
                    }
                }
            }
            gameData.gridStack.push(this);
        });

        // FINISH DRAG
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
            resetProgress();
        });
    }

}
