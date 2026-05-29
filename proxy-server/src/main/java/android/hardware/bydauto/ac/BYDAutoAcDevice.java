package android.hardware.bydauto.ac;

import android.content.Context;
import android.hardware.bydauto.BYDAutoEventValue;
import android.hardware.bydauto.IBYDAutoEvent;
import android.hardware.bydauto.IBYDAutoListener;
import java.util.List;

public class BYDAutoAcDevice {
    public static BYDAutoAcDevice getInstance(Context context) {
        return null;
    }
    
    public void set(int[] propertyIds, BYDAutoEventValue value) {
    }

    public BYDAutoEventValue get(int[] propertyIds, Class<?> type) {
        return null;
    }

    public String arrayToStr(float[] arr) { return null; }
    public String arrayToStr(int[] arr) { return null; }
    public int enablePurificationFunctionPrompt(int arg0) { return 0; }
    public int feelColdHot(int arg0, int arg1) { return 0; }
    public int get3daOnlineState() { return 0; }
    public int getAcCompressorManualSign() { return 0; }
    public int getAcCompressorMode() { return 0; }
    public int getAcControlMode() { return 0; }
    public int getAcCycleMode() { return 0; }
    public int getAcDefrostOnlineState() { return 0; }
    public int getAcDefrostState(int arg0) { return 0; }
    public int getAcFaultNumShownState() { return 0; }
    public int getAcKeyActionState() { return 0; }
    public int getAcMaxCoolingState() { return 0; }
    public int getAcOnlineState() { return 0; }
    public int getAcPanelLockPromptState() { return 0; }
    public int getAcPromptBoxShownState() { return 0; }
    public int getAcPtcPreheatSignal() { return 0; }
    public int getAcRearPaneNotAvailableMenuState() { return 0; }
    public int getAcRearPanelLockState() { return 0; }
    public int getAcRemoteCtrlTime() { return 0; }
    public int getAcStartState() { return 0; }
    public int getAcSubBatteryTemperature() { return 0; }
    public int getAcTemperatureControlMode() { return 0; }
    public int getAcType() { return 0; }
    public int getAcVentilationState() { return 0; }
    public int getAcWarmState() { return 0; }
    public int getAcWarmTypeOnlineState() { return 0; }
    public int getAcWindLevel() { return 0; }
    public int getAcWindLevelManualSign() { return 0; }
    public int getAcWindMode() { return 0; }
    public int getAcWindModeManualSign() { return 0; }
    public int getAcWindModeNum() { return 0; }
    public int getAcWindModeShownState() { return 0; }
    public int getAirQualityCtrlMenuState() { return 0; }
    public List<?> getAllFragranceNames(int arg0) { return null; }
    public void getAllStatus() { }
    public int getAutoCleanAirState() { return 0; }
    public int getDefrostRearConfig() { return 0; }
    public int getDevicetype() { return 0; }
    public int[] getFeatureList() { return null; }
    public String getGetPermission() { return null; }
    public int getHighTempAntivirusCountDown() { return 0; }
    public int getHighTempAntivirusState() { return 0; }
    public List<?> getInstalledFragranceNames(int arg0) { return null; }
    public int getQuickCleanAirState() { return 0; }
    public int getQuickCleanTip() { return 0; }
    public int getRearAcControlMode() { return 0; }
    public int getRearAcLockState() { return 0; }
    public int getRearAcMaxWindLevel() { return 0; }
    public int getRearAcStartState() { return 0; }
    public int getRearAcWindLevel() { return 0; }
    public int getRearAcWindMode() { return 0; }
    public String getSetPermission() { return null; }
    public int getTemperatureUnit() { return 0; }
    public int getTemprature(int arg0) { return 0; }
    public int getType() { return 0; }
    public int getVoiceCmdResult() { return 0; }
    public int hasFeature(String arg0) { return 0; }
    public boolean onError(int arg0, String arg1) { return false; }
    public boolean onPostEvent(IBYDAutoEvent arg0) { return false; }
    public boolean postEvent(int arg0, int arg1, float arg2, Object arg3) { return false; }
    public boolean postEvent(int arg0, int arg1, int arg2, Object arg3) { return false; }
    public boolean postEvent(int arg0, int arg1, byte[] arg2, Object arg3) { return false; }
    public int qurAcAllStatus() { return 0; }
    public void registerListener(IBYDAutoListener arg0) { }
    public void registerListener(AbsBYDAutoAcListener arg0) { }
    public void registerListener(IBYDAutoListener arg0, int[] arg1) { }
    public void registerListener(AbsBYDAutoAcListener arg0, int[] arg1) { }
    public int reset(int arg0, int arg1) { return 0; }
    public int setAcCompressorMode(int arg0, int arg1) { return 0; }
    public int setAcControlMode(int arg0, int arg1) { return 0; }
    public int setAcCycleMode(int arg0, int arg1) { return 0; }
    public int setAcDefrostState(int arg0, int arg1, int arg2) { return 0; }
    public int setAcMaxCoolingState(int arg0) { return 0; }
    public int setAcRearPanelLockState(int arg0) { return 0; }
    public int setAcRemoteCtrlTime(int arg0) { return 0; }
    public int setAcTemperature(int arg0, int arg1, int arg2, int arg3) { return 0; }
    public int setAcTemperatureControlMode(int arg0, int arg1) { return 0; }
    public int setAcVentilationState(int arg0, int arg1) { return 0; }
    public int setAcWarmState(int arg0) { return 0; }
    public int setAcWindLevel(int arg0, int arg1) { return 0; }
    public int setAcWindMode(int arg0, int arg1) { return 0; }
    public void setAllStatus() { }
    public int setAutoCleanAirState(int arg0) { return 0; }
    public int setFragrance(String arg0, int arg1) { return 0; }
    public int setMediaInfo(int arg0, int arg1, byte[] arg2) { return 0; }
    public int setMediaState(int arg0, int arg1, int arg2) { return 0; }
    public int setQuickCleanAirState(int arg0) { return 0; }
    public int setRearAcControlMode(int arg0, int arg1) { return 0; }
    public int setRearAcLockState(int arg0) { return 0; }
    public int setRearAcWindLevel(int arg0, int arg1) { return 0; }
    public int setRearAcWindMode(int arg0, int arg1) { return 0; }
    public int start(int arg0) { return 0; }
    public int startRearAc(int arg0) { return 0; }
    public int stop(int arg0) { return 0; }
    public int stopRearAc(int arg0) { return 0; }
    public void unregisterListener(IBYDAutoListener arg0) { }
    public void unregisterListener(AbsBYDAutoAcListener arg0) { }
}
