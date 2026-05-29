plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sr.openbyd.proxy"
    compileSdk = 36

    defaultConfig {
        minSdk = 25
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core-aidl"))
}
