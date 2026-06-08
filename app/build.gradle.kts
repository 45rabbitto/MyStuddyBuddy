plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {

    namespace = "com.studdy.mystudybuddy"

    compileSdk = 35

    defaultConfig {

        applicationId =
            "com.studdy.mystudybuddy"

        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

    }

    buildFeatures {

        viewBinding = true
        dataBinding = true
        compose = true
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {

        jvmTarget = "17"
    }

    composeOptions {

        kotlinCompilerExtensionVersion =
            "1.5.14"
    }
}

kapt {

    correctErrorTypes = true
}

dependencies {

    // =====================================
    // FIREBASE
    // =====================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:33.10.0"
        )
    )

    // Auth
    implementation(
        "com.google.firebase:firebase-auth-ktx"
    )

    // Realtime Database
    implementation(
        "com.google.firebase:firebase-database-ktx"
    )

    // Firebase Storage
    implementation(
        "com.google.firebase:firebase-storage-ktx"
    )

    //pdfbox
    implementation(
        "com.tom-roush:pdfbox-android:2.0.27.0"
    )

    //firestore
    implementation(
        "com.google.firebase:firebase-firestore:25.1.0"
    )

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.database)
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // =====================================
    // CORE
    // =====================================

    implementation(
        "androidx.core:core-ktx:1.13.1"
    )

    implementation(
        "androidx.appcompat:appcompat:1.7.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.7"
    )

    implementation(
        "androidx.recyclerview:recyclerview:1.3.2"
    )

    implementation(
        "androidx.cardview:cardview:1.0.0"
    )

    implementation(
        "androidx.constraintlayout:constraintlayout:2.1.4"
    )

    // =====================================
    // COMPOSE
    // =====================================

    implementation(
        platform(
            "androidx.compose:compose-bom:2024.06.00"
        )
    )

    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2024.06.00"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.1"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )

    // =====================================
    // NAVIGATION
    // =====================================

    implementation(
        "androidx.navigation:navigation-compose:2.8.5"
    )

    // =====================================
    // VIEWMODEL
    // =====================================

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
    )

    // =====================================
    // ROOM
    // =====================================

    implementation(
        "androidx.room:room-runtime:2.7.2"
    )

    implementation(
        "androidx.room:room-ktx:2.7.2"
    )

    kapt(
        "androidx.room:room-compiler:2.7.2"
    )

    // =====================================
    // HILT
    // =====================================

    implementation(
        "com.google.dagger:hilt-android:2.56"
    )

    kapt(
        "com.google.dagger:hilt-compiler:2.56"
    )

    implementation(
        "androidx.hilt:hilt-navigation-compose:1.2.0"
    )

    // =====================================
    // RETROFIT
    // =====================================

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    // =====================================
    // DATASTORE
    // =====================================

    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )

    // =====================================
    // COIL
    // =====================================

    implementation(
        "io.coil-kt:coil-compose:2.7.0"
    )

    // =====================================
    // GLIDE
    // =====================================

    implementation(
        "com.github.bumptech.glide:glide:4.16.0"
    )

    kapt(
        "com.github.bumptech.glide:compiler:4.16.0"
    )

    // =====================================
    // COROUTINE
    // =====================================

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )

    // =====================================
    // PDF
    // =====================================

    implementation(
        "com.tom-roush:pdfbox-android:2.0.27.0"
    )

    // =====================================
    // TESTING
    // =====================================

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.6.1"
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )
}