package com.sr.openbyd.proxy

import android.content.Intent
import android.os.Looper
import android.util.Log

object EntryPoint {
    private const val TAG = "OpenBYDProxy"

    @JvmStatic
    fun main(args: Array<String>) {
        println("OpenBYD Proxy starting...")
        Log.d(TAG, "Proxy starting with args: ${args.joinToString(", ")}")

        // Ensure we have a looper for any handlers
        Looper.prepareMainLooper()

        // 1. Setup BYD Hardware access (start this using the ADB script launch_proxy.sh or similar)
        // 2. Setup CarControl implementation
        val carControlImpl = CarControlImpl()

        // 3. Connect to the main app and pass the binder
        // We broadcast an intent to the main app with the Binder
        try {
            val intent = Intent("com.sr.openbyd.PROXY_CONNECTED")
            intent.setPackage("com.sr.openbyd")

            // Using a Parcelable wrapper to safely pass the binder
            intent.putExtra(
                "proxy_binder",
                com.sr.openbyd.ipc.ProxyBinderParcelable(carControlImpl)
            )

            // Use reflection to get System Context and broadcast
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val systemMainMethod = activityThreadClass.getMethod("systemMain")
            val activityThread = systemMainMethod.invoke(null)

            val getSystemContextMethod = activityThreadClass.getMethod("getSystemContext")
            val systemContext =
                getSystemContextMethod.invoke(activityThread) as android.content.Context

            systemContext.sendBroadcast(intent)
            Log.d(TAG, "Broadcast sent to main app with Binder.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send broadcast", e)
        }

        // 4. Loop indefinitely
        Log.d(TAG, "Proxy entering main loop")
        Looper.loop()
    }

}
