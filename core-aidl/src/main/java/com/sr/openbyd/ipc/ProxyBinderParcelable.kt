package com.sr.openbyd.ipc

import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable

class ProxyBinderParcelable(var binder: IBinder?) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readStrongBinder())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(binder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProxyBinderParcelable> {
        override fun createFromParcel(parcel: Parcel): ProxyBinderParcelable {
            return ProxyBinderParcelable(parcel)
        }

        override fun newArray(size: Int): Array<ProxyBinderParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
