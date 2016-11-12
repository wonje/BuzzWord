package buzzword;

import apptemplate.AppTemplate;
import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import data.GameData;
import data.GameDataFile;
import gui.Workspace;

/**
 * @author Jason Kang
 */

public class BuzzWord extends AppTemplate{
    public static void main(String[] args) {
        launch(args);
    }

    public String getFileControllerClass() {
        return "BuzzWordController";
    }

    @Override
    public AppComponentsBuilder makeAppBuilderHook() {
        return new AppComponentsBuilder() {
            @Override
            public AppDataComponent buildDataComponent() throws Exception {
//                return new GameData(BuzzWord.this);
                return new GameData(BuzzWord.this);
            }

            @Override
            public AppFileComponent buildFileComponent() throws Exception {
                return new GameDataFile();
            }

            @Override
            public AppWorkspaceComponent buildWorkspaceComponent() throws Exception {
//                return new Workspace(BuzzWord.this);
                return new Workspace(BuzzWord.this);
            }
        };
    }
}
