package controller;

import java.io.IOException;

/**
 * @author Jason Kang
 */
public interface FileController {

    void handleNewProfileRequest();

    void handleLoginRequest();

    void handleGoHomeRequest();

    void handleLevelSelectRequest();

    void handlePlayRequest();

    void handleHelpRequest();

    void handlePauseRequest();

    void handleResumeRequest();

    void handleQuitRequest();

    void handleRestartRequest();

    void handleModeRequest();

    void handleModeCancelRequest();

}
