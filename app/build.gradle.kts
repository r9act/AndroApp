plugins {
    alias(libs.plugins.android.application)
//    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt") // Add this line
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    // Jackson dependencies for JSON serialization/deserialization
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    // Optional: Retrofit for HTTP requests with Jackson converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    // Apache POI for working with Excel files
    implementation("org.apache.poi:poi-ooxml:5.2.3")  // Ensure to use the latest version

    // Apache POI - OOXML schemas (optional, if needed for more advanced features)
//    implementation("org.apache.poi:poi-ooxml-schemas:4.1.2")

    // Apache POI - Common libraries
    implementation("org.apache.poi:poi:5.2.3")

    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.3.1")

    // Room Database (optional, for local caching)
//    implementation("androidx.room:room-runtime:2.4.0")
//    kapt("androidx.room:room-compiler:2.4.0")
}