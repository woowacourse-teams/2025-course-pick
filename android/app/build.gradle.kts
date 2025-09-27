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
    alias(libs.plugins.kotlin.compose)
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
        versionCode = 10000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "런세권(dev)")

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
                localProperties["amplitude.api.key.dev"].toString(),
            )
            buildConfigField(
                "String",
                "MIXPANEL_PROJECT_TOKEN",
                localProperties["mixpanel.project.token.dev"].toString(),
            )
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

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
                localProperties["amplitude.api.key.prod"].toString(),
            )
            buildConfigField(
                "String",
                "MIXPANEL_PROJECT_TOKEN",
                localProperties["mixpanel.project.token.prod"].toString(),
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
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
    implementation(libs.app.update)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
