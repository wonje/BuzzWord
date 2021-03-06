package components;

import apptemplate.AppTemplate;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This interface provides the structure for file components in
 * our applications. Note that by doing so we make it possible
 * for customly provided descendent classes to have their methods
 * called from this framework.
 *
 * @author Jason Kang
 */
public interface AppFileComponent {

    void updateProfileData(AppTemplate appTemplate) throws IOException;


    void createProfile(AppTemplate appTemplate, Path filePath) throws IOException;

    boolean loadProfile(AppTemplate appTemplate, String testPW, Path filePath) throws IOException;

    void exportData(AppDataComponent data, Path filePath) throws IOException;
}
