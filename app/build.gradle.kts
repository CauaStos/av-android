plugins {
    id("com.android.application")
}

android {
    namespace = "com.av.pesquisa"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.av.pesquisa"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
