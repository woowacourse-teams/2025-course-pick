package io.coursepick.coursepick.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
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
        setUpCallbacks()
    }

    private fun setUpCallbacks() {
        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?,
                ) {
                    Logger.log(Logger.Event.Enter(activity.javaClass.simpleName))
                }

                override fun onActivityStarted(activity: Activity) = Unit

                override fun onActivityResumed(activity: Activity) {
                    Logger.log(Logger.Event.Resume(activity.javaClass.simpleName))
                }

                override fun onActivityPaused(activity: Activity) {
                    Logger.log(Logger.Event.Pause(activity.javaClass.simpleName))
                }

                override fun onActivityStopped(activity: Activity) = Unit

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: Bundle,
                ) = Unit

                override fun onActivityDestroyed(activity: Activity) {
                    Logger.log(Logger.Event.Exit(activity.javaClass.simpleName))
                }
            },
        )
    }
}
