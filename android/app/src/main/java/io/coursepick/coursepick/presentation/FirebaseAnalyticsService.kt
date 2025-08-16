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
        vararg parameters: Pair<String, Any>,
    ) {
        val bundle =
            Bundle().apply {
                parameters.forEach { (key: String, value: Any) -> putAny(key, value) }
            }
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    private fun Bundle.putAny(
        key: String,
        value: Any,
    ) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            else -> putString(key, value.toString())
        }
    }
}
