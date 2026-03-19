package io.coursepick.coursepick.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CoursePickApplication : Application() {
    @Inject
    lateinit var installationId: InstallationId

    @Volatile
    var hasShownNoticeThisSession: Boolean = false
        private set

    fun markNoticeAsShown() {
        hasShownNoticeThisSession = true
    }
}
