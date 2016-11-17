package data;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.core.*;
import components.AppDataComponent;
import components.AppFileComponent;
import controller.GameState;
import controller.LoginController;
import ui.AppMessageDialogSingleton;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Jason Kang
 */
public class GameDataFile implements AppFileComponent {

    public static final String USER_ID              = "USER_ID";
    public static final String USER_PW              = "USER_PW";
    public static final String RECENT_MODE          = "RECENT_MODE";
    public static final String DICTIONARY_LEVEL     = "DICTIONARY_LEVEL";
    public static final String PLACES_LEVEL         = "PLACES_LEVEL";
    public static final String SCIENCE_LEVEL        = "SCIENCE_LEVEL";
    public static final String FAMOUS_LEVEL         = "FAMOUS_LEVEL";


    @Override
    public void saveData(AppDataComponent data, Path filePath) throws IOException {

    }

    @Override
    public void createProfile(AppTemplate appTemplate, Path to) throws IOException {
        GameData gameData = (GameData) appTemplate.getDataComponent();
        UserData userData = (UserData) appTemplate.getUserComponent();

        JsonFactory jsonFactory = new JsonFactory();

        try(OutputStream out = Files.newOutputStream(to)) {

            JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);

            generator.writeStartObject();

            generator.writeStringField(USER_ID, LoginController.getSingleton(appTemplate).getID());

            generator.writeStringField(USER_PW, LoginController.getSingleton(appTemplate).getPW());

            generator.writeStringField(RECENT_MODE, GameState.ENGLISH_DICTIONARY.toString());

            generator.writeNumberField(DICTIONARY_LEVEL, 1);

            generator.writeNumberField(PLACES_LEVEL, 1);

            generator.writeNumberField(SCIENCE_LEVEL, 1);

            generator.writeNumberField(FAMOUS_LEVEL, 1);

            generator.writeEndObject();

            generator.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public boolean loadProfile(AppTemplate appTemplate, String testPW, Path from) throws IOException {
        GameData gameData = (GameData) appTemplate.getDataComponent();
        UserData userData = (UserData) appTemplate.getUserComponent();

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser  jsonParser  = jsonFactory.createParser(Files.newInputStream(from));

        String tempID = "";

        while (!jsonParser.isClosed()) {
            JsonToken token = jsonParser.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldname = jsonParser.getCurrentName();
                switch (fieldname) {
                    case USER_ID:
                        jsonParser.nextToken();
                        tempID = jsonParser.getValueAsString();
                        break;
                    case USER_PW:
                        jsonParser.nextToken();
                        if(testPW.equals(jsonParser.getValueAsString())){
                            userData.reset();
                            gameData.reset();
                            userData.setUserInfo(tempID, testPW);
                        }
                        else {
                            return false;
                        }
                        break;
                    case RECENT_MODE:
                        jsonParser.nextToken();
                        GameState.loadRecentMode(jsonParser.getValueAsString());
                        break;
                    case DICTIONARY_LEVEL:
                        jsonParser.nextToken();
                        gameData.engDicLevel = jsonParser.getValueAsInt();
                        break;
                    case PLACES_LEVEL:
                        jsonParser.nextToken();
                        gameData.placesLevel = jsonParser.getValueAsInt();
                        break;
                    case SCIENCE_LEVEL:
                        jsonParser.nextToken();
                        gameData.scienceLevel = jsonParser.getValueAsInt();
                        break;
                    case FAMOUS_LEVEL:
                        jsonParser.nextToken();
                        gameData.famousLevel = jsonParser.getValueAsInt();
                        break;
                    default:
                        throw new JsonParseException(jsonParser, "Unable to load JSON data");
                }
            }
        }
        return true;
    }

    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException {

    }
}
