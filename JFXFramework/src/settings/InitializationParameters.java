package settings;

/**
 * @author Jason Kang
 */
public enum InitializationParameters {

    APP_PROPERTIES_XML("app-properties.xml"),
    WORKSPACE_PROPERTIES_XML("workspace-properties.xml"),
    PROPERTIES_SCHEMA_XSD("properties-schema.xsd"),
    CLOSE_LABEL("CLOSE"),
    EXIT_LABEL("EXIT APPLICATION"),
    APP_WORKDIR_PATH("logs"),
    APP_IMAGEDIR_PATH("images");

    private String parameter;

    InitializationParameters(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}