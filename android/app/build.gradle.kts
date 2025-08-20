import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.oss.licenses.plugin)
    alias(libs.plugins.kotlin.parcelize)
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
        getByName("debug") {
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField("String", "BASE_URL", localProperties["base.url.debug"].toString())
            buildConfigField(
                "String",
                "KAKAO_BASE_URL",
                localProperties["kakao.base.url"].toString(),
            )
            buildConfigField(
                "String",
                "KAKAO_NATIVE_APP_KEY",
                localProperties["kakao.native.app.key"].toString(),
            )
            buildConfigField(
                "String",
                "KAKAO_REST_API_KEY",
                localProperties["kakao.rest.api.key"].toString(),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_API_KEY",
                localProperties["amplitude.api.key"].toString(),
            )
            buildConfigField(
                "String",
                "MIXPANEL_PROJECT_TOKEN",
                localProperties["mixpanel.project.token"].toString(),
            )
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
            buildConfigField(
                "String",
                "KAKAO_BASE_URL",
                localProperties["kakao.base.url"].toString(),
            )
            buildConfigField(
                "String",
                "KAKAO_REST_API_KEY",
                localProperties["kakao.rest.api.key"].toString(),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_API_KEY",
                localProperties["amplitude.api.key"].toString(),
            )
            buildConfigField(
                "String",
                "MIXPANEL_PROJECT_TOKEN",
                localProperties["mixpanel.project.token"].toString(),
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
    debugImplementation(libs.leakcanary.android)
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.timber)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.amplitude.android)
    implementation(libs.amplitude.android.session.replay)
    implementation(libs.mixpanel.android)
    implementation(libs.mixpanel.android.session.replay)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
