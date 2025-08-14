package io.coursepick.coursepick.presentation

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.data.Services
import io.coursepick.coursepick.data.course.DefaultCourseRepository
import io.coursepick.coursepick.data.search.DefaultSearchRepository
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.search.SearchRepository
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import timber.log.Timber

class CoursePickApplication : Application() {
    val installationId: InstallationId by lazy { InstallationId(this) }
    private val services: Services by lazy { Services(installationId) }
    val courseRepository: CourseRepository by lazy { DefaultCourseRepository(services.courseService) }
    val searchRepository: SearchRepository by lazy { DefaultSearchRepository(services.searchService) }

    override fun onCreate() {
        super.onCreate()
        Logger.init(FirebaseAnalyticsService(installationId))

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        CoursePickPreferences.init(this)
    }
}
