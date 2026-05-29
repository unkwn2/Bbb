package android.hardware.bydauto.bodywork;

import android.content.Context;
import android.hardware.bydauto.BYDAutoEventValue;

public class BYDAutoBodyworkDevice {

    public static BYDAutoBodyworkDevice getInstance(Context context) {
        return null; // The real framework will return the actual instance at runtime
    }

    // --- Base Methods ---

    public int set(int[] propertyIds, BYDAutoEventValue value) {
        return 0;
    }

    public BYDAutoEventValue get(int[] propertyIds, Class<?> type) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public int getDevicetype() {
        return 0;
    }

    // --- Getters ---

    public int getAlarmState() { return 0; }
    public int getAutoModelName() { return 0; }
    public int getAutoSystemState() { return 0; }
    public int getAutoType() { return 0; }
    public String getAutoVIN() { return null; }
    public int getAuxiliaryLightStatus() { return 0; }
    public int getAuxiliaryLightSwitchOrder() { return 0; }
    public int getBatteryCapacity() { return 0; }
    public int getBatteryCompartmentLowPower() { return 0; }
    public double getBatteryPowerHEV() { return 0.0; }
    public int getBatteryPowerValue() { return 0; }
    public int getBatteryVoltageLevel() { return 0; }
    public int getCarWindowAntiPinchConfig() { return 0; }
    public int getCenteringControlStatus() { return 0; }
    public int getCenteringMotorFaultCode() { return 0; }
    public int getChargeControllerFaultStatus() { return 0; }
    public int getChargingModeControlCommand() { return 0; }
    public int getCharingChamberTempSignal() { return 0; }
    public int getClampControlStatus() { return 0; }
    public int getDataFlag(int i) { return 0; }
    public byte[] getDjiSdkControl() { return null; }
    public byte[] getDjiSdkControlSecond() { return null; }
    public int getDoorState(int area) { return 0; }
    public int getEnergyType() { return 0; }
    public int getFanOpenCircuitFault() { return 0; }
    public int[] getFeatureList() { return null; }
    public int getFirstFanInletSpeedFeedback() { return 0; }
    public int getFirstWarehouseBatteryCharging() { return 0; }
    public int getFirstWarehouseBatteryFault() { return 0; }
    public int getFirstWarehouseBatteryInPlace() { return 0; }
    public int getFitstWarehouseBatteryPower() { return 0; }
    public int getFrequencyAlignment() { return 0; }
    public int getFuelElecLowPower() { return 0; }
    public String getGetPermission() { return null; }
    public int getHangarControllerFaultStatus() { return 0; }
    public int getHangarControllerWakeupStatus() { return 0; }
    public int getHangarMotionSelfTest() { return 0; }
    public int getHangarOvertempWarning() { return 0; }
    public int getHangarRollbackStatus() { return 0; }
    public int getHangarStatus() { return 0; }
    public int getHatchDoorMotorFaultStatus() { return 0; }
    public int getHatchDoorStatus() { return 0; }
    public int getLiftControlStatus() { return 0; }
    public int getMessage5sOnlineState(int i) { return 0; }
    public int getMoonRoofConfig() { return 0; }
    public int getMotorControllerFaultStatus() { return 0; }
    public int getPowerDayMode() { return 0; }
    public int getPowerLevel() { return 0; }
    public int getRainCloseWindow() { return 0; }
    public String getRealAutoVIN() { return null; }
    public int getReqHighVoltagePowerSupply() { return 0; }
    public int getRotateMotorControlStatus() { return 0; }
    public int getSecondWarehouseBatteryCharging() { return 0; }
    public int getSecondWarehouseBatteryFault() { return 0; }
    public int getSecondWarehouseBatteryInPlace() { return 0; }
    public int getSecondWarehouseBatteryPower() { return 0; }
    public String getSetPermission() { return null; }
    public int getSmartVoiceLimit() { return 0; }
    public double getSteeringWheelValue(int i) { return 0.0; }
    public int getSunroofCloseNotice() { return 0; }
    public int getSunroofInitState() { return 0; }
    public int getSunroofPosition() { return 0; }
    public int getSunroofState() { return 0; }
    public int getSunroofWindowblindPosition() { return 0; }
    public int getTempSensorFault() { return 0; }
    public int getThirdWarehouseBatteryCharging() { return 0; }
    public int getThirdWarehouseBatteryFault() { return 0; }
    public int getThirdWarehouseBatteryInPlace() { return 0; }
    public int getThirdWarehouseBatteryPower() { return 0; }
    public int getTripodHeadControlStatus() { return 0; }
    public int getTripodHeadMotorFaultCode() { return 0; }
    public int getUavAbnormalRollbackDoorUnclosedWarning() { return 0; }
    public int getUavBatterySecondaryPushInAction() { return 0; }
    public int getUavDelayedPowerDownStatusFeedback() { return 0; }
    public int getUavDoorMotorThermalProtectionStatus() { return 0; }
    public int getUavInpositionSignal() { return 0; }
    public int getUavLandingHangarStatus() { return 0; }
    public int getUavLandingPosition() { return 0; }
    public int getUavTakeoffHangarStatus() { return 0; }
    public int getUavTempSignal() { return 0; }
    public int getWindoblindInitState() { return 0; }
    public int getWindowOpenPercent(int area) { return 0; }
    public int getWindowPermitState() { return 0; }
    public int getWindowState(int area) { return 0; }
    public int getXAxisControlStatus() { return 0; }
    public int getXAxisMotorFaultCode() { return 0; }
    public int getYAxisControlStatus() { return 0; }
    public int getYAxisMotorFaultCode() { return 0; }

    // --- Setters ---

    public int setAllWindowState(int fl, int fr, int rl, int rr) { return 0; }
    public int setAuxiliaryLight(int i) { return 0; }
    public int setBodyWindowCtrlState(int area, int state) { return 0; }
    public int setCancelLandingPrepare() { return 0; }
    public int setCancelTakeOffPrepare() { return 0; }
    public int setCenteringStatus(int i) { return 0; }
    public int setChargingModeControlCommand(int i) { return 0; }
    public int setCompleteLandingHangarAction() { return 0; }
    public int setDjiSdkControl(byte[] b) { return 0; }
    public int setDjiSdkControlSecond(byte[] b) { return 0; }
    public int setFrequencyAlignment() { return 0; }
    public int setFunctionSynthesisOrder(int i) { return 0; }
    public int setHangarDoorCombinationClose() { return 0; }
    public int setHangarDoorCombinationOpen() { return 0; }
    public int setHangarStart() { return 0; }
    public int setHangarStop() { return 0; }
    public int setHetchDoorStatus(int i) { return 0; }
    public int setIncludeExchangePowerSwitchOffAction() { return 0; }
    public int setIncludeExchangePowerSwitchOnAction() { return 0; }
    public int setLandingPrepare() { return 0; }
    public int setMoonRoofAndSunshadeStop() { return 0; }
    public int setMoonRoofState(int state) { return 0; }
    public int setNotIncludeExchangePowerSwitchOnAction() { return 0; }
    public int setOneKeySelfTest() { return 0; }
    public int setRainCloseWindow(int state) { return 0; }
    public int setRelayStatus(int i) { return 0; }
    public int setStartTakeOffHangarAction() { return 0; }
    public int setSunshadeState(int state) { return 0; }
    public int setTakeOffPrepare() { return 0; }
    public int setTripodHeadProtect(int i) { return 0; }
    public int setUavBatteryPower(int i) { return 0; }
    public int setUavLandingStatus(int i) { return 0; }
    public int setUavSwitchStatus(int i) { return 0; }
    public int setUavTakeoffStatus(int i) { return 0; }
    public int setVehiclePowerStatus(int i) { return 0; }
    public int setVehicleSpeed() { return 0; }
    public int voiceCtlMoonRoof(int i) { return 0; }
    public int voiceCtlSunshadePanel(int i) { return 0; }

    // --- Other ---

    public void getAllStatus() {}
    public void setAllStatus() {}
    public int hasFeature(String s) { return 0; }
    public int hasMessage(int i) { return 0; }
    public boolean postEvent(int i1, int i2, float f, Object o) { return false; }
    public boolean postEvent(int i1, int i2, int i3, Object o) { return false; }
    public int queryAutoVIN() { return 0; }
    public void queryFanStsus() {}
    public int queryFirstWarehouseStatus() { return 0; }
    public int querySecondWarehouseStatus() { return 0; }
    public int queryThirdWarehouseStatus() { return 0; }

    public void registerListener(AbsBYDAutoBodyworkListener listener) {}
    public void registerListener(AbsBYDAutoBodyworkListener listener, int[] propertyIds) {}
    public void unregisterListener(AbsBYDAutoBodyworkListener listener) {}
}
