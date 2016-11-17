package gui;


import java.awt.Point;

/**
 * @author Jason Kang
 */
public class GridElement {
    Point   pos;
    String  word;
    boolean visited;
    public static final int boardSize = 4;

    public GridElement(Point pos, String word){
        this.pos        = pos;
        this.word       = word;
        this.visited    = false;
    }


}
