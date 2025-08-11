package io.coursepick.coursepick.view

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import io.coursepick.coursepick.util.ClientId
import io.coursepick.coursepick.view.Logger.Event

class FirebaseAnalyticsService(
    clientId: ClientId,
) : AnalyticsService {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val firebaseCrashlytics: FirebaseCrashlytics = Firebase.crashlytics

    init {
        firebaseAnalytics.setUserId(clientId.value)
        firebaseCrashlytics.setUserId(clientId.value)
    }

    override fun log(
        event: Event,
        bundle: Bundle,
    ) {
        firebaseAnalytics.logEvent(event.name, bundle)
    }
}
