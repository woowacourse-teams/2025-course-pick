package io.coursepick.coursepick.presentation

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

object Logger {
    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

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

    sealed class Event(
        target: String? = null,
    ) {
        val name: String =
            if (target == null) {
                javaClass.simpleName.lowercase()
            } else {
                "${target}_${javaClass.simpleName.lowercase()}"
            }

        class Enter(
            target: String,
        ) : Event(target)

        class Exit(
            target: String,
        ) : Event(target)

        class Click(
            target: String,
        ) : Event(target)

        object ZoomIn : Event()

        object ZoomOut : Event()

        class Search(
            target: String,
        ) : Event(target)
    }
}
