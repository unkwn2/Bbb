plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sr.openbyd.ipc"
    compileSdk = 36

    defaultConfig {
        minSdk = 25
    }

    buildFeatures {
        aidl = true
    }
}
