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
    public static final String DICTIONARY_SCORES    = "DICTIONARY_SCORES";
    public static final String PLACES_SCORES        = "PLACES_SCORES";
    public static final String SCIENCE_SCORES       = "SCIENCE_SCORES";
    public static final String FAMOUS_SCORES        = "FAMOUS_SCORES";


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

            generator.writeFieldName(DICTIONARY_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

            generator.writeFieldName(PLACES_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

            generator.writeFieldName(SCIENCE_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

            generator.writeFieldName(FAMOUS_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

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

        String  tempID  = "";
        int     tempKey = 0;

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
                    case DICTIONARY_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.dicBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
                        break;
                    case PLACES_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.placeBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
                        break;
                    case SCIENCE_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.scienceBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
                        break;
                    case FAMOUS_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.famousBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
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
