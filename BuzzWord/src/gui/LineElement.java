package gui;

import apptemplate.AppTemplate;
import data.GameData;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import propertymanager.PropertyManager;

import java.awt.*;
import java.awt.Label;

import static buzzword.BuzzWordProperties.GRID;


/**
 * @author Jason Kang
 */
public class LineElement extends Line {
    Point       pos;
    boolean     visited;
    boolean     ltoR;
    AppTemplate appTemplate;
    Workspace   gameWorkspace;
    GameData    gameData;

    public LineElement(Point pos) {
        this.pos        = pos;
        this.visited    = false;
    }

    public LineElement(Point pos, AppTemplate appTemplate, Workspace gameWorkspace, boolean ltoR) {
        this(pos);
        this.appTemplate    = appTemplate;
        this.gameData       = (GameData) appTemplate.getDataComponent();
        this.gameWorkspace  = gameWorkspace;
        this.ltoR           = ltoR;
        this.setOnEventHandler();
        initLine();
    }

    private void initLine() {
        // INIT LINE OPTION
        this.setFill(Paint.valueOf("#000000"));
        this.setStrokeWidth(3);
        this.setSmooth(true);

        // DRAW H LINE
        if(pos.getY() == 0 || pos.getY() == 2 || pos.getY() == 4 || pos.getY() == 6) {
            this.setStartX(0);
            this.setEndX(17);
            this.setStartY(0);
            this.setEndY(0);
        }
        else {
            // DRAW V LINE
            if(pos.getX() == 0 || pos.getX() == 2 || pos.getX() == 4 || pos.getX() == 6) {
                this.setStartX(0);
                this.setEndX(0);
                this.setStartY(0);
                this.setEndY(17);
            }
            else {
                // DRAW LEFT TO RIGHT LINE
                if (ltoR) {
                    this.setStartX(0);
                    this.setEndX(15);
                    this.setStartY(0);
                    this.setEndY(15);
                }
                // DRAW RIGHT TO LEFT LINE
                else {
                    this.setStartX(15);
                    this.setEndX(0);
                    this.setStartY(0);
                    this.setEndY(15);
                }
            }
        }
    }

    private void setOnEventHandler() {
        PropertyManager propertyManager = PropertyManager.getManager();
        this.setOnDragDetected(mouseEvent -> {
            this.startFullDrag();
        });
        // DRAGING
        this.setOnMouseDragEntered(mouseDragEvent -> {

        });

        this.setOnMouseDragReleased(MouseDragEvent -> {
            // MATCHING PROGRESS
            StringBuilder progressStr = new StringBuilder();
            for (javafx.scene.control.Label progress : gameWorkspace.progress) {
                progressStr.append(Character.toLowerCase(progress.getText().charAt(0)));
            }
            System.out.println(progressStr.toString());
            // IF PROGRESS IS MATCHED WITH SOLUTION AND NOT DUPLICATED ONE
            if(gameWorkspace.solutions.contains(progressStr.toString()) &&
                    !gameData.matchedStr.contains(progressStr.toString())) {
                gameWorkspace.matches.add(new javafx.scene.control.Label(progressStr.toString()));
                gameData.matchedStr.add(progressStr.toString());
                gameWorkspace.matchedPoints.add(new javafx.scene.control.Label(Integer.toString(progressStr.toString().length() * 10)));
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
