package io.coursepick.coursepick

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class CoursePickApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}
