package gui;


import java.awt.Point;

/**
 * @author Jason Kang
 */
public class GridElement {
    Point   pos;
    char    word;
    boolean visited;
    public static final int boardSize = 4;

    public GridElement(Point pos) {
        this.pos        = pos;
        this.visited    = false;
    }

    public GridElement(Point pos, String word){
        this(pos);
        this.word = word.charAt(0);
    }

    public Point getPoint() {
        return pos;
    }

    public char getWord() {
        return word;
    }

}
