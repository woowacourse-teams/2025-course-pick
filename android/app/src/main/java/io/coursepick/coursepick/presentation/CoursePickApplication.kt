package io.coursepick.coursepick.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CoursePickApplication : Application() {
    @Inject
    lateinit var installationId: InstallationId

    var hasShownNoticeThisSession: Boolean = false
        private set

    fun markNoticeAsShown() {
        hasShownNoticeThisSession = true
    }

    override fun onCreate() {
        super.onCreate()

        val analyticsServices: List<AnalyticsService> =
            listOf(
                FirebaseAnalyticsService(installationId),
                AmplitudeAnalyticsService(this, installationId),
                MixpanelAnalyticsService(this, installationId),
            )
        Logger.init(analyticsServices)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Logger.log(Logger.Event.Enter(javaClass.simpleName))
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
