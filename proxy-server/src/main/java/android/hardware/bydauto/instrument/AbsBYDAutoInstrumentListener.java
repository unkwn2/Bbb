package android.hardware.bydauto.instrument;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;
import android.hardware.bydauto.BYDAutoEventValue;

public abstract class AbsBYDAutoInstrumentListener implements IBYDAutoListener {
    @Override
    public void onDataChanged(IBYDAutoEvent event) {}

    @Override
    public void onDataEventChanged(int featureId, BYDAutoEventValue value) {}

    @Override
    public void onError(int error, String msg) {}
}
