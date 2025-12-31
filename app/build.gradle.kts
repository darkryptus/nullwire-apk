
plugins {
    id("com.android.application")
    
}

android {
    namespace = "com.example.test1"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "com.example.test1"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {


    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("tech.gusavila92:java-android-websocket-client:1.2.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    implementation("com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking:1.0.4") {
    exclude(group = "com.android.support")
}



}
