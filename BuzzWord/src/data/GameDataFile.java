package data;

import components.AppDataComponent;
import components.AppFileComponent;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jason Kang
 */
public class GameDataFile implements AppFileComponent {
    @Override
    public void saveData(AppDataComponent data, Path filePath) throws IOException {

    }

    @Override
    public void loadData(AppDataComponent data, Path filePath) throws IOException {

    }

    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException {

    }
}
