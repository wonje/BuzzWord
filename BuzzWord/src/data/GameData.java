package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import controller.GameState;
import gui.GridElement;
import javafx.scene.shape.Line;
import propertymanager.PropertyManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static settings.AppPropertyType.APP_TITLE;
import static settings.AppPropertyType.WORK_FILE_EXT;
import static settings.InitializationParameters.APP_WORKDIR_PATH;

/**
 * @author Jason Kang
 */
public class GameData implements AppDataComponent {
    public AppTemplate appTemplate;
    public int maxEngDicLevel;
    public int maxBacteriaLevel;
    public int maxBiologyLevel;
    public int maxFungiLevel;
    public int totalPoints;

    private NavigableSet<String> wordFile;
    private char[][] board = new char[4][4];

    public Stack<GridElement> gridStack;
    public Stack<Line> lineStack;
    public ArrayList<String> matchedStr;

    public GameData(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        maxEngDicLevel = 1;
        maxBacteriaLevel = 1;
        maxBiologyLevel = 1;
        maxFungiLevel = 1;
        gridStack = new Stack<GridElement>();
        lineStack = new Stack<Line>();
        matchedStr = new ArrayList<String>();
    }

    @Override
    public void reset() {
        maxEngDicLevel = 1;
        maxBacteriaLevel = 1;
        maxBiologyLevel = 1;
        maxFungiLevel = 1;

    }

    public void getMaxLevels(UserData userData) {
        for (maxEngDicLevel = 1; userData.dicBestScores.containsKey(maxEngDicLevel); maxEngDicLevel++) ;
        maxEngDicLevel--;
        for (maxBacteriaLevel = 1; userData.bacteriaBestScores.containsKey(maxBacteriaLevel); maxBacteriaLevel++) ;
        maxBacteriaLevel--;
        for (maxBiologyLevel = 1; userData.biologyBestScores.containsKey(maxBiologyLevel); maxBiologyLevel++) ;
        maxBiologyLevel--;
        for (maxFungiLevel = 1; userData.fungiBestScores.containsKey(maxFungiLevel); maxFungiLevel++) ;
        maxFungiLevel--;
    }

    public void loadWordFile(GameState mode) {
        String fileName = "";
        switch (mode) {
            case ENGLISH_DICTIONARY:
                fileName = "Dictionary.txt";
                break;
            case BACTERIA:
//                fileName = "Places.txt";
                fileName = "Bacteria.txt";
                break;
            case BIOLOGY:
//                fileName = "Science.txt";
                fileName = "Biology.txt";
                break;
            case FUNGI:
//                fileName = "FamousPeople.txt";
                fileName = "Fungi.txt";
                break;
        }

        wordFile = new TreeSet<String>();
        try {
            URL wordResource = getClass().getClassLoader().getResource("words/" + fileName);
            FileReader fr = new FileReader(wordResource.toURI().toString().split(":", 2)[1]);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                wordFile.add(line.split("\n")[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading word file");
        }

    }

    public ArrayList<String> getBuzzWordSolution(GridElement[] gridElements) {
        // MAKE GRID WORD ARRAY
        int row = 0;
        int col = 0;

        for (int i = 0; i < gridElements.length; i++) {
            board[row][col] = Character.toLowerCase(gridElements[i].getWord());

            if (col == 3) {
                row++;
                col = 0;
            } else {
                col++;
            }
        }

        ArrayList<Point> pos = new ArrayList<Point>();
        // TODO IF TOTAL > TARGET, REARRANGE GRID WORDS
        final ArrayList<String> validWords = new ArrayList<String>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                solve(i, j, board[i][j] + "", validWords, pos);
            }
        }

        return validWords;

    }

    private void solve(int row, int col, String prefix, ArrayList<String> validWords, ArrayList<Point> pos) {
        for (int row1 = Math.max(0, row - 1); row1 < Math.min(board.length, row + 2); row1++) {
            for (int col1 = Math.max(0, col - 1); col1 < Math.min(board.length, col + 2); col1++) {
                // Skip the grid (row, col) itself
                if (row1 == row && col1 == col) {
                    pos.add(new Point(col1, row1));
                    continue;
                }

                // Skip the followed grids
                if(pos.contains(new Point(col1, row1)))
                    continue;

                // ARRAYLIST COPY
                ArrayList<Point> new_pos = new ArrayList<Point>(pos);
                // SAVE VISITED GRID INFO
                new_pos.add(new Point(col1, row1));

                //Skip the diagonal (row, col) value
//                if ((row1 == row - 1 && col1 == col - 1) || (row1 == row - 1 && col1 == col + 1) ||
//                        (row1 == row + 1 && col1 == col - 1) || (col1 == col + 1 && row1 == row + 1))
//                    continue;

                String word = prefix + board[row1][col1];

                if (!wordFile.subSet(word, word + Character.MAX_VALUE).isEmpty()) {
                    if (wordFile.contains(word) && !validWords.contains(word) && word.length() > 2) {
                        validWords.add(word);
                    }
                    solve(row1, col1, word, validWords, new_pos);
                }
            }
        }
    }
}
