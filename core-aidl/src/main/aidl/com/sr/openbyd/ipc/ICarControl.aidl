package com.sr.openbyd.ipc;

interface ICarControl {
    int getApiVersion();
    String ping();
    String setWindow(int windowId, int state);
    String setAcPower(boolean powerOn);
    String setAuxiliaryLight(boolean powerOn);
    /** Runs a shell command via the privileged proxy process (UID 2000). Returns stdout. */
    String runShellCommand(String command);
    /**
     * Launches an app by package name. Tries multiple strategies including monkey,
     * which works even when apps don't expose a CATEGORY_LAUNCHER intent filter.
     */
    String launchApp(String packageName);

    /** Creates a virtual display in the proxy process (UID 2000). Returns a log string with RESULT_ID: at the end. */
    String createVirtualDisplay(String name, int width, int height, int density, in Surface surface, int flags);
    /** Releases a previously created virtual display. Returns a log string. */
    String releaseVirtualDisplay(int displayId);

    /** Forces a task onto a specific display. */
    String moveTaskToDisplay(int taskId, int displayId);
    /** Sets the windowing mode for a task (e.g., 5 for Freeform). */
    String setTaskWindowingMode(int taskId, int windowingMode);
    /** Sets the activity type for a task (e.g., 1 for Standard). */
    String setTaskActivityType(int taskId, int activityType);
    /** Sets the bounds for a task window. */
    String setTaskBounds(int taskId, int left, int top, int right, int bottom);

    /** Sets the focused task. */
    String setFocusedTask(int taskId);

    /** Returns the logs for finding a taskId. Returns a log string with RESULT_ID: at the end. */
    String getTaskId(String packageName);

    /** Returns the package name of the currently focused activity. */
    String getTopActivityPackage();

    /** Launches an app and forces it onto a display with specific configuration. */
    String launchAndForce(String packageName, int displayId, int width, int height);

    int getInstrumentFeatureValue(int featureId);
    String scrapBydAuto();
    String injectTouchEvent(int displayId, int action, long downTime, long eventTime, float x, float y);
    String getSettingFeatureValue(int featureId);
    String setInstrumentFeatureValue(int featureId, int value);
    String setSettingFeatureValue(int featureId, int value);
    String getSystemProperty(String key);
}
