plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.alyak.detector"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alyak.detector"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        ndk {
//            abiFilters.add("arm64-v8a")
//            abiFilters.add("armeabi-v7a")
//            abiFilters.add("x86")
//            abiFilters.add("x86_64")
//        }
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

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // vector img
    implementation(libs.androidx.appcompat.v171)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // kakao map
    implementation(libs.v2.all)
    implementation(libs.android)
    implementation("com.kakao.maps.open:android:2.12.8")
    // location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    // splash screen
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    // JUnit
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Android Instrumentation Test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // jetpack Compose Nav
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation ("io.coil-kt:coil-svg:2.4.0")

}