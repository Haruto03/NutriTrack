import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

val file = rootProject.file("local.properties")
val propertiesl = Properties()
propertiesl.load(FileInputStream(file))


android {
    namespace = "com.nutritrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nutritrack"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
            //About API key
            val apiKey = propertiesl.getProperty("apiKey", "")
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
        }
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
        buildConfig = true
    }

}

dependencies {


    val nav_version = "2.8.9"
    val room_version = "2.9.0"
    implementation(libs.androidx.room.runtime)

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp(libs.androidx.room.compiler)
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    //annotationProcessor(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    // Retrofit
    implementation(libs.retrofit)
// Retrofit with Scalar Converter
    implementation(libs.converter.scalars)

    implementation (libs.converter.gson)
    // Google AI (Gemini)
    implementation(libs.androidx.google.ai.client.generativeai)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // to handle several screen size
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}



