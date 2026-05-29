package android.hardware;

import android.hardware.bydauto.BYDAutoEventValue;

public interface IBYDAutoListener {
    void onDataChanged(IBYDAutoEvent event);
    void onDataEventChanged(int featureId, BYDAutoEventValue value);
    void onError(int error, String msg);
}
