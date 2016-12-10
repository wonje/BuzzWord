package controller;

import java.io.IOException;

/**
 * @author Jason Kang
 */
public interface FileController {

    void handleNewProfileRequest();

    void handleLoginRequest();

    void handleLogoutRequest();

    void handleGoHomeRequest();

    void handleLevelSelectRequest();

    void handlePlayRequest(int level);

    void handleHelpRequest();

    void handlePauseRequest();

    void handleResumeRequest();

    void handleQuitRequest();

    void handleModeRequest();

    void handleModeCancelRequest();

    void handleModeSetRequest(GameState mode);
    
    void handleProfileSettingRequest();
    
    void handleKeyPadLevelSelection(int level);
}
