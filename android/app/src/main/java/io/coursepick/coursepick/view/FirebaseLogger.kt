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
        event: Event,
        parameters: Bundle,
    ) {
        firebaseAnalytics.logEvent(event.name, parameters)
    }

    fun log(
        event: Event,
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

    sealed interface Event {
        val type: String
        val target: String
        val name: String get() = "${type}_$target"

        class View(
            override val target: String,
        ) : Event {
            override val type: String = "view"
        }

        class Click(
            override val target: String,
        ) : Event {
            override val type: String = "click"
        }
    }
}
