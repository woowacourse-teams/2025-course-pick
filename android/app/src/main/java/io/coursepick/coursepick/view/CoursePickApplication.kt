package io.coursepick.coursepick.view

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import io.coursepick.coursepick.BuildConfig

class CoursePickApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}
