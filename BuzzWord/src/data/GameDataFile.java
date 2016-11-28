package data;

import static settings.AppPropertyType.APP_TITLE;
import static settings.AppPropertyType.WORK_FILE_EXT;
import static settings.InitializationParameters.APP_WORKDIR_PATH;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.*;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import components.AppFileComponent;
import controller.GameState;
import controller.LoginController;
import propertymanager.PropertyManager;

/**
 * @author Jason Kang
 */
public class GameDataFile implements AppFileComponent {

    public static final String USER_ID              = "USER_ID";
    public static final String USER_PW              = "USER_PW";
    public static final String RECENT_MODE          = "RECENT_MODE";
    public static final String DICTIONARY_SCORES    = "DICTIONARY_SCORES";
    public static final String BACTERIA_SCORES      = "BACTERIA_SCORES";
    public static final String BIOLOGY_SCORES       = "BIOLOGY_SCORES";
    public static final String FUNGI_SCORES         = "FUNGI_SCORES";


    @Override
    public void updateProfileData(AppTemplate appTemplate) throws IOException {
        GameData        gameData    = (GameData) appTemplate.getDataComponent();
        UserData        userData    = (UserData) appTemplate.getUserComponent();
        PropertyManager propertyManager = PropertyManager.getManager();
        Path            appDirPath  = Paths.get(propertyManager.getPropertyValue(APP_TITLE)).toAbsolutePath();
        Path            targetPath  = appDirPath.resolve(APP_WORKDIR_PATH.getParameter());
        Path            to          = Paths.get(targetPath.toString() + "\\" + userData.userID + "." + propertyManager.getPropertyValue(WORK_FILE_EXT));

        JsonFactory jsonFactory = new JsonFactory();

        try(OutputStream out = Files.newOutputStream(to)) {

            JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);

            generator.writeStartObject();

            generator.writeStringField(USER_ID, LoginController.getSingleton(appTemplate).getID());

            generator.writeStringField(USER_PW, LoginController.getSingleton(appTemplate).getPW());

            generator.writeStringField(RECENT_MODE, GameState.currentMode.toString());

            // UPDATE PERSONAL BEST POINTS
            generator.writeFieldName(DICTIONARY_SCORES);
            generator.writeStartArray(userData.dicBestScores.size() * 2);
            for (int i = 0; i < userData.dicBestScores.size(); i++) {
                generator.writeNumber(i + 1);
                generator.writeNumber(userData.dicBestScores.get(i + 1));
            }
            generator.writeEndArray();

            generator.writeFieldName(BACTERIA_SCORES);
            generator.writeStartArray(userData.bacteriaBestScores.size() * 2);
            for (int i = 0; i < userData.bacteriaBestScores.size(); i++) {
                generator.writeNumber(i + 1);
                generator.writeNumber(userData.bacteriaBestScores.get(i + 1));
            }
            generator.writeEndArray();

            generator.writeFieldName(BIOLOGY_SCORES);
            generator.writeStartArray(userData.biologyBestScores.size() * 2);
            for (int i = 0; i < userData.biologyBestScores.size(); i++) {
                generator.writeNumber(i + 1);
                generator.writeNumber(userData.biologyBestScores.get(i + 1));
            }
            generator.writeEndArray();

            generator.writeFieldName(FUNGI_SCORES);
            generator.writeStartArray(userData.fungiBestScores.size() * 2);
            for (int i = 0; i < userData.fungiBestScores.size(); i++) {
                generator.writeNumber(i + 1);
                generator.writeNumber(userData.fungiBestScores.get(i + 1));
            }
            generator.writeEndArray();

            generator.writeEndObject();

            generator.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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

            generator.writeFieldName(BACTERIA_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

            generator.writeFieldName(BIOLOGY_SCORES);
            generator.writeStartArray(2);
            generator.writeNumber(1);
            generator.writeNumber(0);
            generator.writeEndArray();

            generator.writeFieldName(FUNGI_SCORES);
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
                    case BACTERIA_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.bacteriaBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
                        break;
                    case BIOLOGY_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.biologyBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
                        }
                        break;
                    case FUNGI_SCORES:
                        jsonParser.nextToken();
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                            tempKey = Character.getNumericValue(jsonParser.getText().charAt(0));
                            jsonParser.nextToken();
                            userData.fungiBestScores.put(tempKey, Character.getNumericValue(jsonParser.getText().charAt(0)));
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
