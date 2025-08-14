package io.coursepick.coursepick.presentation

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics

class FirebaseAnalyticsService(
    installationId: InstallationId,
) : AnalyticsService {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val firebaseCrashlytics: FirebaseCrashlytics = Firebase.crashlytics

    init {
        firebaseAnalytics.setUserId(installationId.value)
        firebaseCrashlytics.setUserId(installationId.value)
    }

    override fun log(
        event: Logger.Event,
        bundle: Bundle,
    ) {
        firebaseAnalytics.logEvent(event.name, bundle)
    }
}
