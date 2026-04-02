import com.android.build.gradle.internal.cxx.logging.lifecycleln

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cookingmagic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cookingmagic"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // Ensure you're using Java 8 language features
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    buildFeatures{
        viewBinding = true
        mlModelBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    implementation ("androidx.camera:camera-camera2:1.0.0")
    implementation ("io.github.muddz:styleabletoast:2.4.0")
    implementation ("androidx.core:core:1.7.0")  // For NotificationCompat
    implementation ("androidx.appcompat:appcompat:1.4.0")  // For AppCompatActivity


    //Material Components
    implementation(libs.material.v120)

}