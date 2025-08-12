package io.coursepick.coursepick.view

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.kakao.vectormap.KakaoMapSdk
import io.coursepick.coursepick.BuildConfig
import timber.log.Timber

class CoursePickApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        ClientIdProvider.init(this)
        Logger.init(FirebaseLogHandler())
        setUpCallbacks()

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        CoursePickPreferences.init(this)
    }

    private fun setUpCallbacks() {
        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?,
                ) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Created")
                    Logger.log(Logger.Event.Enter(activity.javaClass.simpleName))
                }

                override fun onActivityStarted(activity: Activity) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Started")
                }

                override fun onActivityResumed(activity: Activity) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Resumed")
                }

                override fun onActivityPaused(activity: Activity) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Paused")
                }

                override fun onActivityStopped(activity: Activity) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Stopped")
                }

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: Bundle,
                ) {
                    Timber
                        .tag("debug logger")
                        .d("${activity.javaClass.simpleName} SaveInstanceState")
                }

                override fun onActivityDestroyed(activity: Activity) {
                    Timber.tag("debug logger").d("${activity.javaClass.simpleName} Destroyed")
                    Logger.log(Logger.Event.Exit(activity.javaClass.simpleName))
                }
            },
        )
    }
}
