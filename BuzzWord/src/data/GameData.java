package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import controller.GameState;
import gui.GridElement;
import propertymanager.PropertyManager;

import java.io.BufferedReader;
import java.io.FileReader;
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
public class GameData implements AppDataComponent{
    public AppTemplate appTemplate;
    public int maxEngDicLevel;
    public int maxPlacesLevel;
    public int maxScienceLevel;
    public int maxFamousLevel;

    private NavigableSet<String> wordFile;
    private char[][] board = new char[4][4];

    public GameData(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        maxEngDicLevel  = 1;
        maxPlacesLevel  = 1;
        maxScienceLevel = 1;
        maxFamousLevel  = 1;
    }

    @Override
    public void reset() {
        maxEngDicLevel  = 1;
        maxPlacesLevel  = 1;
        maxScienceLevel = 1;
        maxFamousLevel  = 1;

    }

    public void getMaxLevels(UserData userData) {
        for(maxEngDicLevel = 1; userData.dicBestScores.containsKey(maxEngDicLevel); maxEngDicLevel++);
        maxEngDicLevel--;
        for(maxPlacesLevel = 1; userData.placeBestScores.containsKey(maxPlacesLevel); maxPlacesLevel++);
        maxPlacesLevel--;
        for(maxScienceLevel = 1; userData.scienceBestScores.containsKey(maxScienceLevel); maxScienceLevel++);
        maxScienceLevel--;
        for(maxFamousLevel = 1; userData.famousBestScores.containsKey(maxFamousLevel); maxFamousLevel++);
        maxFamousLevel--;
    }

    public void loadWordFile(GameState mode){
        String fileName = "";
        switch (mode){
            case ENGLISH_DICTIONARY:
                fileName = "Dictionary.txt";
                break;
            case PLACES:
                fileName = "Places.txt";
                break;
            case SCIENCE:
                fileName = "Science.txt";
                break;
            case FAMOUS_PEOPLE:
                fileName = "FamousPeople.txt";
                break;
        }

        wordFile = new TreeSet<String>();
        try{
            URL wordResource    = getClass().getClassLoader().getResource("words/" + fileName);
            FileReader fr       = new FileReader(wordResource.toURI().toString().split(":", 2)[1]);
            BufferedReader br   = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null){
                wordFile.add(line.split("\n")[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading word file");
        }

    }

    public List<String> getBuzzWordSolution(GridElement[] gridElements){
        // MAKE GRID WORD ARRAY
        int row = 0;
        int col = 0;

        for (int i = 0; i < gridElements.length; i++) {
            board[row][col] = Character.toLowerCase(gridElements[i].getWord());

            if (col == 3) {
                row++;
                col = 0;
            }
            else {
                col++;
            }
        }

        // TODO IF TOTAL > TARGET, REARRANGE GRID WORDS
        final List<String> validWords = new ArrayList<String>();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    solve(i, j, board[i][j] + "", validWords, -1, -1);
                }
            }

        return validWords;
    }

    private void solve(int row, int col, String prefix, List<String> validWords, int followedRow, int followedCol){
        for (int row1 = Math.max(0, row - 1); row1 < Math.min(board.length, row + 2); row1++) {
            for (int col1 = Math.max(0, col - 1); col1 < Math.min(board.length, col + 2); col1++){
                // Skip the grid (row, col) itself
                if (row1 == row && col1 == col) continue;

                // Skip the followed grid
                if (row1 == followedRow && col1 == followedCol) continue;

                // Skip the diagonal (row, col) value
                if ((row1 == row - 1 && col1 == col - 1) || (row1 == row - 1 && col1 == col + 1) ||
                        (row1 == row + 1 && col1 == col - 1) || (col1 == col + 1 && row1 == row + 1))
                    continue;

                String word = prefix + board[row1][col1];

                if(!wordFile.subSet(word, word + Character.MAX_VALUE).isEmpty()) {
                    if(wordFile.contains(word) && !validWords.contains(word) && word.length() > 2){
                        validWords.add(word);
                    }
                    solve(row1, col1, word, validWords, row, col);
                }
            }
        }
    }
}
