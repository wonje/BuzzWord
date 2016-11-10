package components;

/**
 * This interface serves as a family of type that will initialize the style for some set of controls, like the
 * workspace, for example.
 *
 * @author Jason Kang
 * @author ?
 * @version 1.0
 */
public interface AppStyleArbiter {

    String CLASS_STACKED_PANE = "stacked_pane";

    enum BUTTON_TYPE {
        NEW, SAVE, LOAD, EXIT;
    }
    
    void initStyle();
}
