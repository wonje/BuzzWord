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

    enum MENU_TYPE {
        CREATE_PROFILE, LOGIN, ID_DISPLAY, SELECT_MODE, START_PLAYING, HOME
    }
    
    void initStyle();
}
