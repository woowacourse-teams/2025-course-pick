import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("kotlin-kapt")
}

private val localProperties: Properties =
    Properties().apply {
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            load(localFile.inputStream())
        }
    }

android {
    namespace = "io.coursepick.coursepick"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.coursepick.coursepick"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField("String", "BASE_URL", localProperties["base.url.debug"].toString())
        }
        debug {
            buildConfigField(
                "String",
                "KAKAO_NATIVE_APP_KEY",
                localProperties["kakao.native.app.key"].toString(),
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("boolean", "DEBUG", "false")
            buildConfigField("String", "BASE_URL", localProperties["base.url.release"].toString())
            buildConfigField(
                "String",
                "KAKAO_NATIVE_APP_KEY",
                localProperties["kakao.native.app.key"].toString(),
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kakao.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
