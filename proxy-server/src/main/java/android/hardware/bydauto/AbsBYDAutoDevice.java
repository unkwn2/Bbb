package android.hardware.bydauto;

public abstract class AbsBYDAutoDevice {
    public java.lang.String arrayToStr(float[] fArr) { return null; }
    public java.lang.String arrayToStr(int[] iArr) { return null; }
    protected int get(int i, int i2) { return 0; }
    public android.hardware.bydauto.BYDAutoEventValue get(int[] iArr, java.lang.Class<?> clazz) { return null; }
    protected byte[] getBuffer(int i, int i2) { return null; }
    public abstract int getDevicetype();
    protected double getDouble(int i, int i2) { return 0.0; }
    protected float[] getDoubleArray(int i, int[] iArr) { return null; }
    public abstract java.lang.String getGetPermission();
    protected int[] getIntArray(int i, int[] iArr) { return null; }
    public abstract java.lang.String getSetPermission();
    public boolean onError(int i, java.lang.String str) { return false; }
    public boolean onPostEvent(android.hardware.IBYDAutoEvent event) { return false; }
    public boolean postEvent(int i, int i2, float f, java.lang.Object obj) { return false; }
    public boolean postEvent(int i, int i2, int i3, java.lang.Object obj) { return false; }
    public boolean postEvent(int i, int i2, byte[] bArr, java.lang.Object obj) { return false; }
    public void registerListener(android.hardware.IBYDAutoListener listener) {}
    public void registerListener(android.hardware.IBYDAutoListener listener, int[] iArr) {}
    protected int set(int i, int i2, double d) { return 0; }
    protected int set(int i, int i2, int i3) { return 0; }
    protected int set(int i, int i2, byte[] bArr) { return 0; }
    protected int set(int i, int[] iArr, float[] fArr) { return 0; }
    protected int set(int i, int[] iArr, int[] iArr2) { return 0; }
    public int set(int[] iArr, android.hardware.bydauto.BYDAutoEventValue value) { return 0; }
    public int setMediaInfo(int i, int i2, byte[] bArr) { return 0; }
    public int setMediaState(int i, int i2, int i3) { return 0; }
    public void unregisterListener(android.hardware.IBYDAutoListener listener) {}
}
