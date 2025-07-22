package io.coursepick.coursepick.view

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

object FirebaseLogger {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init() {
        firebaseAnalytics = Firebase.analytics
    }

    fun log(
        name: String,
        params: Bundle,
    ) {
        firebaseAnalytics.logEvent(name, params)
    }

    fun log(
        name: String,
        vararg params: Pair<String, Any>,
    ) {
        val bundle =
            Bundle().apply {
                params.forEach { (k, v) -> putAny(k, v) }
            }
        firebaseAnalytics.logEvent(name, bundle)
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
