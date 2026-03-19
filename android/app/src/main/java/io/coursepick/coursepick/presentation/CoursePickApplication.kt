package io.coursepick.coursepick.presentation

import android.app.Application
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
    }

    fun markNoticeAsShown() {
        hasShownNoticeThisSession = true
    }
}
