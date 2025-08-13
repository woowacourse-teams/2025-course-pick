package io.coursepick.coursepick.presentation

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.presentation.view.preference.CoursePickPreferences
import timber.log.Timber

class CoursePickApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        CoursePickPreferences.init(this)
    }
}
