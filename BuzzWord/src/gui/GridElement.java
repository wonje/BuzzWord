package gui;


import javafx.scene.control.Button;

import java.awt.Point;


/**
 * @author Jason Kang
 */
public class GridElement {
    public static final int boardSize = 4;

    Point pos;
    char    word;
    boolean visited;
    Button gridButton;

    public GridElement(Point pos) {
        this.pos        = pos;
        this.visited    = false;
    }

    public GridElement(Point pos, String word){
        this(pos);
        this.word = word.charAt(0);
    }

    public void setGridButton(Button gridButton) {
        this.gridButton = gridButton;
    }

    public Button getGridButton() {
        return gridButton;
    }

    public Point getPoint() {
        return pos;
    }

    public char getWord() {
        return word;
    }

}
