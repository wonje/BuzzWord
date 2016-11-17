package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;

/**
 * @author Jason Kang
 */
public class GameData implements AppDataComponent{
    int engDicLevel;
    int placesLevel;
    int scienceLevel;
    int famousLevel;

    public GameData(AppTemplate appTemplate) {

    }

    @Override
    public void reset() {

    }
}
