package io.coursepick.coursepick.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp
import io.coursepick.coursepick.BuildConfig
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CoursePickApplication : Application() {
    @Inject
    lateinit var installationId: InstallationId

    @Volatile
    var hasShownNoticeThisSession: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()

        val analyticsServices: List<AnalyticsService> =
            listOf(
                FirebaseAnalyticsService(installationId),
                AmplitudeAnalyticsService(applicationContext, installationId),
                MixpanelAnalyticsService(applicationContext, installationId),
            )
        Logger.init(analyticsServices)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Logger.log(Logger.Event.Enter(javaClass.simpleName))
        setUpCallbacks()
    }

    fun markNoticeAsShown() {
        hasShownNoticeThisSession = true
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

                override fun onActivityStopped(activity: Activity) {
                }

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
